package com.freefish.torchesbecomesunlight.client.particle.advance.base.particle;

import com.freefish.torchesbecomesunlight.client.particle.advance.RLParticleQueueRenderType;
import com.freefish.torchesbecomesunlight.client.particle.advance.RLParticleRenderType;
import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.torchesbecomesunlight.client.particle.advance.base.IParticleEmitter;
import com.freefish.torchesbecomesunlight.client.particle.advance.data.Transform;
import com.freefish.torchesbecomesunlight.client.particle.advance.effect.IEffect;
import com.freefish.torchesbecomesunlight.client.util.ColorUtils;
import com.freefish.torchesbecomesunlight.mixin.accessor.BlendModeAccessor;
import com.freefish.torchesbecomesunlight.mixin.accessor.ShaderInstanceAccessor;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ParametersAreNonnullByDefault
public class RLParticle extends AdvancedRLParticleBase implements IParticleEmitter {
    public static int VERSION = 1;

    protected final RLParticleConfig config;
    protected final RLParticleRenderType renderType;

    protected String name = "particle emitter";

    protected boolean isSubEmitter = false;

    protected final Transform transform = new Transform();

    // runtime
    private final Queue<AdvancedRLParticleBase> waitToAdded = Queues.newArrayDeque();

    protected final Map<RLParticleRenderType, Queue<AdvancedRLParticleBase>> particles = new LinkedHashMap<>();

    protected boolean visible = true;
    @Nullable
    protected IEffect effect;

    public RLParticle() {
        this(new RLParticleConfig());
    }

    public RLParticle(RLParticleConfig config) {
        super(null, 0, 0, 0);
        setCull(false);
        setMoveless(true);
        this.config = config;
        this.renderType = new RenderType(config);
    }

    //////////////////////////////////////
    //*****     particle logic     *****//
    //////////////////////////////////////
    protected AdvancedRLParticleBase createNewParticle() {
        var randomSource= getRandomSource();
        var particle = new Basic(level, 0, 0, 0, renderType);
        particle.setLevel(getLevel());
        // start value
        particle.setDelay(config.startDelay.get(randomSource, t).intValue());
        if (config.lifetimeByEmitterSpeed.isEnable()) {
            particle.setLifetime(config.lifetimeByEmitterSpeed.getLifetime(particle, this, config.startLifetime.get(randomSource, t).intValue()));
        } else {
            particle.setLifetime(config.startLifetime.get(randomSource, t).intValue());
        }
        config.shape.setupParticle(particle, this);
        particle.setSpeed(config.startSpeed.get(randomSource, t).floatValue());
        var sizeScale = config.startSize.get(randomSource, t).floatValue();

        var startedSize = new Vector3f(sizeScale * this.getQuadSize().x, sizeScale * this.getQuadSize().y, sizeScale * this.getQuadSize().z);
        particle.setSize(sizeScale);
        var rotation = config.startRotation.get(randomSource, t).mul(Mth.TWO_PI / 360);
        particle.setRoll(rotation.x);
        particle.setPitch((float) rotation.y);
        particle.setYaw((float) rotation.z);
        var startColor = config.startColor.get(randomSource, t).intValue();
        particle.setARGBColor(ColorUtils.color(
                getAlpha(0) * ColorUtils.alpha(startColor),
                getRed(0) * ColorUtils.red(startColor),
                getGreen(0) * ColorUtils.green(startColor),
                getBlue(0) * ColorUtils.blue(startColor)));
        config.renderer.setupQuaternion(this, particle);
        if (config.physics.isEnable()) {
            particle.setPhysics(config.physics.isHasCollision());
            particle.setGravity(config.physics.getGravity().get(randomSource, 0).floatValue());
            particle.setFriction(config.physics.getFriction().get(randomSource, 0).floatValue());
        }

        if (config.physics.isEnable()) {
            particle.setOnCollision(p -> {
                if (config.physics.isEnable() && config.physics.isRemovedWhenCollided()) {
                    p.removeWithEvent();
                }
            });
        }

        // particle logic

        if (config.velocityOverLifetime.isEnable() || config.inheritVelocity.isEnable()) {
            particle.setVelocityAddition(p -> {
                var addition = new Vector3f(0, 0, 0);
                if (config.velocityOverLifetime.isEnable()) {
                    addition.add(config.velocityOverLifetime.getVelocityAddition(p, this));
                }
                if (config.inheritVelocity.isEnable()) {
                    addition.add(config.inheritVelocity.getVelocityAddition(p, this));
                }
                return addition;
            });
        }

        if (config.velocityOverLifetime.isEnable()) {
            particle.setVelocityMultiplier(p -> {
                float multiplier = 1;
                if (config.velocityOverLifetime.isEnable()) {
                    multiplier *= config.velocityOverLifetime.getVelocityMultiplier(p);
                }
                return multiplier;
            });
        }

        if (config.forceOverLifetime.isEnable() || config.sizeOverLifetime.isEnable() || config.physics.isEnable()) {
            particle.setOnUpdate(p -> {
                if (config.forceOverLifetime.isEnable()) {
                    p.setSpeed(p.getVelocity().add(config.forceOverLifetime.getForce(p)));
                }
                if (config.sizeOverLifetime.isEnable()) {
                    p.setQuadSize(config.sizeOverLifetime.getSize(startedSize, p, 0));
                }
                if (config.physics.isEnable()) {
                    config.physics.setupParticlePhysics(p);
                }
            });
        }


        if (config.colorOverLifetime.isEnable() || config.colorBySpeed.isEnable()) {
            particle.setDynamicColor((p, partialTicks) -> {
                float a = 1f;
                float r = 1f;
                float g = 1f;
                float b = 1f;
                if (config.colorOverLifetime.isEnable()) {
                    int color = config.colorOverLifetime.getColor(p, partialTicks);
                    a *= ColorUtils.alpha(color);
                    r *= ColorUtils.red(color);
                    g *= ColorUtils.green(color);
                    b *= ColorUtils.blue(color);
                }
                if (config.colorBySpeed.isEnable()) {
                    int color = config.colorBySpeed.getColor(p);
                    a *= ColorUtils.alpha(color);
                    r *= ColorUtils.red(color);
                    g *= ColorUtils.green(color);
                    b *= ColorUtils.blue(color);
                }
                return new Vector4f(r, g, b, a);
            });
        }

        if (config.sizeOverLifetime.isEnable() || config.noise.isEnable()) {
            particle.setDynamicSize((p, partialTicks) -> {
                var size = p.getQuadSize(partialTicks);
                if (config.sizeOverLifetime.isEnable()) {
                    size = config.sizeOverLifetime.getSize(startedSize, p, partialTicks);
                }
                if (config.noise.isEnable()) {
                    size = new Vector3f(size).add(config.noise.getSize(p, partialTicks));
                }
                return size;
            });
        }

        if (config.rotationOverLifetime.isEnable() || config.rotationBySpeed.isEnable() || config.noise.isEnable()) {
            particle.setRotationAddition((p, partialTicks) -> {
                var addition = new Vector3f(0, 0, 0);
                if (config.rotationOverLifetime.isEnable()) {
                    addition.add(config.rotationOverLifetime.getRotation(p, partialTicks));
                }
                if (config.rotationBySpeed.isEnable()) {
                    addition.add(config.rotationBySpeed.getRotation(p));
                }
                if (config.noise.isEnable()) {
                    addition.add(config.noise.getRotation(p, partialTicks));
                }
                return addition;
            });
        }

        if (config.noise.isEnable() || config.inheritVelocity.isEnable()) {
            var initialPos = this.getPos();
            particle.setPositionAddition((p, partialTicks) -> {
                var addition = new Vector3f(0, 0, 0);
                if (config.noise.isEnable()) {
                    addition.add(config.noise.getPosition(p, partialTicks));
                }
                if (config.inheritVelocity.isEnable()) {
                    addition.add(config.inheritVelocity.getPosition(this, initialPos, partialTicks));
                }
                return addition;
            });
        }

        if (config.uvAnimation.isEnable()) {
            particle.setDynamicUVs((p, partialTicks) -> {
                if (config.uvAnimation.isEnable()) {
                    return config.uvAnimation.getUVs(p, partialTicks);
                }
                return new Vector4f(p.getU0(partialTicks), p.getV0(partialTicks), p.getU1(partialTicks), p.getV1(partialTicks));
            });
        }

        particle.setDynamicLight((p, partialTicks) -> {
            if (usingBloom()) {
                return LightTexture.FULL_BRIGHT;
            }
            if (config.lights.isEnable()) {
                return config.lights.getLight(p, partialTicks);
            }
            return p.getLight(partialTicks);
        });

        if (config.trails.isEnable()) {
            config.trails.setup(this, particle);
        }

        return particle;
    }

    @Override
    public void tick() {
        // effect first
        if (effect != null && effect.updateEmitter(this)) {
            return;
        }

        // delay
        if (delay > 0) {
            delay--;
            return;
        }

        // emit new particle
        if (!isRemoved() && getParticleAmount() < config.maxParticles) {
            var number = config.emission.getEmissionCount(this.age, t, getRandomSource());
            for (int i = 0; i < number; i++) {
                if (!emitParticle(createNewParticle())) {
                    break;
                }
            }
        }

        // particles life cycle
        if (!waitToAdded.isEmpty()) {
            for (var p : waitToAdded) {
                particles.computeIfAbsent(p.getRenderType(), type -> new ArrayDeque<>(config.maxParticles)).add(p);
            }
            waitToAdded.clear();
        }

        for (var queue : particles.values()) {
            if (config.parallelUpdate) { // parallel stream for particles tick.
                queue.removeIf(p -> !p.isAlive());
                queue.parallelStream().forEach(LParticle::tick);
            } else {
                var iter = queue.iterator();
                while (iter.hasNext()) {
                    var particle = iter.next();
                    if (!particle.isAlive()) {
                        iter.remove();
                    } else {
                        particle.tick();
                    }
                }
            }
        }

        // is sub emitter
        if (getEmitter() != null) {
            if (this.age >= lifetime) {
                this.remove();
            }
            this.age++;
            t = this.age * 1f / lifetime;
        } else {
            if (this.age >= config.duration && !config.isLooping()) {
                this.remove();
            }
            this.age++;
            t = (this.age % config.duration) * 1f / config.duration;
        }

        update();

    }

    public void updatePos(Vector3f newPos) {
        var lastPos = getPos(1);
        self().setPos(newPos, true);
        self().setSpeed(new Vector3f(newPos).sub(lastPos));
    }

    @Override
    public int getLifetime() {
        return config.duration;
    }

    @Override
    public boolean isAlive() {
        return !removed || getParticleAmount() != 0;
    }

    @Override
    protected void updateOrigin() {
        super.updateOrigin();
        setLifetime(config.duration);
    }

    @Override
    public void resetParticle() {
        super.resetParticle();
        this.particles.clear();
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, @Nonnull VertexConsumer buffer, Camera camera, float pPartialTicks) {
        if (!RLParticleQueueRenderType.INSTANCE.isRenderingQueue() && delay <= 0 && isVisible() &&
                RLParticleRenderType.checkLayer(config.renderer.getLayer()) &&
                (!config.renderer.getCull().isEnable() ||
                        RLParticleRenderType.checkFrustum(config.renderer.getCull().getCullAABB(this, pPartialTicks)))) {
            poseStack.pushPose();
            poseStack.mulPoseMatrix(transform.getMatrix());
            for(var entry : this.particles.entrySet()) {
                var type = entry.getKey();
                if (type == ParticleRenderType.NO_RENDER) continue;
                var queue = entry.getValue();
                if (type == RLParticleQueueRenderType.INSTANCE) {
                    for (var emitter : queue) {
                        emitter.render(poseStack, buffer, camera, pPartialTicks);
                    }
                } else if (!queue.isEmpty()) {
                    RLParticleQueueRenderType.INSTANCE.pipeQueue(type, queue, new Matrix4f(poseStack.last().pose()), camera, pPartialTicks);
                }
            }
            poseStack.popPose();
        }
    }

    @Override
    public Transform getTransform() {
        return null;
    }

    private static class RenderType extends RLParticleRenderType {
        protected final RLParticleConfig config;
        private BlendMode lastBlend = null;

        public RenderType(RLParticleConfig config) {
            this.config = config;
        }

        @Override
        public void prepareStatus() {
            //if (config.renderer.isBloomEffect()) {
            //    beginBloom();
            //}
            config.material.pre();
            config.material.getMaterial().begin(false);
            if (RenderSystem.getShader() instanceof ShaderInstanceAccessor shader) {
                lastBlend = BlendModeAccessor.getLastApplied();
                BlendModeAccessor.setLastApplied(shader.getBlend());
            }
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        }

        @Override
        public void begin(@Nonnull BufferBuilder bufferBuilder) {
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void releaseStatus() {
            config.material.getMaterial().end(false);
            config.material.post();
            if (lastBlend != null) {
                lastBlend.apply();
                lastBlend = null;
            }
            //if (config.renderer.isBloomEffect()) {
            //    endBloom();
            //}
        }

        @Override
        public boolean isParallel() {
            //return config.isParallelRendering();
            return super.isParallel();
        }

        @Override
        public int hashCode() {
            return config.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RenderType renderType) {
                return renderType.config.equals(config);
            }
            return super.equals(obj);
        }
    }

    @Override
    public @NotNull RLParticleRenderType getRenderType() {
        return RLParticleQueueRenderType.INSTANCE;
    }

    //////////////////////////////////////
    //********      Emitter    *********//
    //////////////////////////////////////
    public boolean emitParticle(AdvancedRLParticleBase particle) {
        if (emitter != null) { // find root
            return emitter.emitParticle(particle);
        } else {
            particle.prepareForEmitting(this);
            waitToAdded.add(particle);
            return getParticleAmount() <= config.maxParticles;
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public int getParticleAmount() {
        return IParticleEmitter.super.getParticleAmount() + waitToAdded.size();
    }

    @Override
    @Nullable
    public AABB getCullBox(float partialTicks) {
        return config.renderer.getCull().isEnable() ? config.renderer.getCull().getCullAABB(this, partialTicks) : null;
    }

    @Override
    public boolean usingBloom() {
        return config.renderer.isBloomEffect();
    }

    @Override
    public void setEffect(IEffect effect) {
        this.effect = effect;
    }

    @Override
    public IEffect getEffect() {
        return effect;
    }

    @Override
    public void remove(boolean force) {
        remove();
        if (force) {
            particles.clear();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isSubEmitter() {
        return isSubEmitter;
    }

    @Override
    public void setSubEmitter(boolean isSubEmitter) {
        this.isSubEmitter = isSubEmitter;
    }

    @Override
    public Map<RLParticleRenderType, Queue<AdvancedRLParticleBase>> getParticles() {
        return particles;
    }
}
