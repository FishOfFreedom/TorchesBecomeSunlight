package com.freefish.torchesbecomesunlight.client.particle.advance;

import com.freefish.torchesbecomesunlight.client.particle.advance.base.IParticleEmitter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public abstract class AdvancedRLParticleBase extends Particle {
    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);
    /**
     * Quad Size of Particles.
     */
    protected Vector3f quadSize = new Vector3f(1, 1, 1);
    /**
     * Can particle move by speed.
     */
    protected boolean moveless;
    /**
     * Delay time before tick and rendering
     */
    protected int delay;
    /**
     * Lighting map value, -1: get light from world.
     */
    protected int light = -1;
    /**
     * Should we do cull check
     */
    protected boolean cull = true;
    /**
     * Rotation of yaw, pitch
     */
    protected float yaw, pitch;
    /**
     * possibility of bounce when it has physics.
     */
    protected float bounceChance = 1;
    /**
     * bounce rate of speed when collision happens.
     */
    protected float bounceRate = 1;
    /**
     * addition speed for other two axis gaussian noise when collision happens.
     */
    protected float bounceSpreadRate = 0;

    protected Supplier<Quaternionf> quaternionSupplier = () -> null;

    private Level realLevel;

    @Nullable
    protected Consumer<AdvancedRLParticleBase> onUpdate;

    @Nullable
    protected Function<AdvancedRLParticleBase, Vector3f> velocityAddition = null;

    @Nullable
    protected Function<AdvancedRLParticleBase, Float> velocityMultiplier = null;

    @Nullable
    protected BiFunction<AdvancedRLParticleBase, Float, Vector4f> dynamicColor = null;

    @Nullable
    protected BiFunction<AdvancedRLParticleBase, Float, Vector3f> dynamicSize = null;

    @Nullable
    protected BiFunction<AdvancedRLParticleBase, Float, Vector3f> rotationAddition = null;

    @Nullable
    protected BiFunction<AdvancedRLParticleBase, Float, Vector3f> positionAddition = null;

    @Nullable
    protected BiFunction<AdvancedRLParticleBase, Float, Vector4f> dynamicUVs = null;

    @Nullable
    protected BiFunction<AdvancedRLParticleBase, Float, Integer> dynamicLight = null;

    @Nullable
    protected Consumer<AdvancedRLParticleBase> onBirth = null;

    @Nullable
    protected Consumer<AdvancedRLParticleBase> onCollision = null;

    @Nullable
    protected Consumer<AdvancedRLParticleBase> onDeath = null;
    // runtime
    protected float t;
    @Nullable
    protected IParticleEmitter emitter;

    protected ConcurrentHashMap<Object, Float> memRandom = new ConcurrentHashMap<>();

    protected AdvancedRLParticleBase(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.realLevel = level;
        this.hasPhysics = false;
        this.friction = 1;
    }

    protected AdvancedRLParticleBase(ClientLevel level, double x, double y, double z, double sX, double sY, double sZ) {
        super(level, x, y, z, sX, sY, sZ);
        this.realLevel = level;
        this.hasPhysics = false;
        this.friction = 1;
    }

    public RandomSource getRandomSource() {
        return random;
    }

    @Nullable
    public Level getLevel() {
        return realLevel == null ? super.level : realLevel;
    }

    public void setLevel(@Nullable Level level) {
        this.realLevel = level;
    }

    public void setPhysics(boolean hasPhysics) {
        this.hasPhysics = hasPhysics;
    }

    public void setFullLight() {
        setLight(0xf000f0);
    }

    @Nonnull
    @Deprecated
    public AdvancedRLParticleBase scale(float pScale) {
        this.quadSize = new Vector3f(pScale, pScale, pScale);
        this.setSize(pScale, pScale);
        return this;
    }

    public void setQuadSize(Vector3f size) {
        this.quadSize = size;
        this.setSize(size.x, size.y);
    }

    public void setPos(double pX, double pY, double pZ, boolean setOrigin) {
        this.x = pX;
        this.y = pY;
        this.z = pZ;
        if (setOrigin) {
            this.xo = x;
            this.yo = y;
            this.zo = z;
        }
        float f = this.bbWidth / 2.0F;
        float f1 = this.bbHeight;
        this.setBoundingBox(new AABB(pX - (double)f, pY, pZ - (double)f, pX + (double)f, pY + (double)f1, pZ + (double)f));
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setImmortal() {
        setLifetime(-1);
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void setColor(int color) {
        this.setColor((float) FastColor.ARGB32.red(color) / 255, (float)FastColor.ARGB32.green(color) / 255, (float)FastColor.ARGB32.blue(color) / 255);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setARGBColor(int color) {
        this.setColor((float) FastColor.ARGB32.red(color) / 255, (float)FastColor.ARGB32.green(color) / 255, (float)FastColor.ARGB32.blue(color) / 255);
        setAlpha((float) FastColor.ARGB32.alpha(color) / 255);
    }

    public void setSize(float size) {
        scale(size);
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    @Override
    public void tick() {
        if (delay > 0) {
            delay--;
            return;
        }

        if (onBirth != null && this.age == 0) {
            onBirth.accept(this);
        }

        updateOrigin();

        if (this.age++ >= getLifetime() && getLifetime() > 0) {
            removeWithEvent();
        }
        update();

        if (getLifetime() > 0) {
            t = 1.0f * age / getLifetime();
        }
    }

    public void removeWithEvent() {
        this.remove();
        if (onDeath != null) {
            onDeath.accept(this);
        }
    }

    protected void updateOrigin() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.oRoll = this.roll;
    }

    protected void update() {
        updateChanges();
        if (onUpdate != null) {
            onUpdate.accept(this);
        }
    }

    protected void updateChanges() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (!moveless) {
            var velocity = getVelocity();
            this.move(velocity.x, velocity.y, velocity.z);

            this.yd -= 0.04D * this.gravity;
            if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
                this.xd *= 1.1D;
                this.zd *= 1.1D;
            }
            this.xd *= this.friction;
            this.yd *= this.friction;
            this.zd *= this.friction;
            if (this.onGround && this.friction != 1.0) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
                this.yd *= 0.7F;
            }
        }
    }

    @Override
    public void move(double x, double y, double z) {
        double moveX = x;
        double moveY = y;
        double moveZ = z;
        if (this.hasPhysics && getLevel() != null && (x != 0.0 || y != 0.0 || z != 0.0) && x * x + y * y + z * z < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
            Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(x, y, z), this.getBoundingBox(), getLevel(), List.of());
            x = vec3.x;
            y = vec3.y;
            z = vec3.z;
        }
        if (x != 0.0 || y != 0.0 || z != 0.0) {
            this.setBoundingBox(this.getBoundingBox().move(x, y, z));
            this.setLocationFromBoundingbox();
        }
        if (!this.onGround && this.hasPhysics) {
            if (Math.abs(moveY) >= 1.0E-5 && Math.abs(y) < 1.0E-5) {
                if (bounceChance < 1 && bounceChance < random.nextFloat()) {
                    this.onGround = true;
                } else {
                    this.yd = -this.yd * bounceRate;
                    if (bounceSpreadRate > 0) {
                        this.xd += bounceSpreadRate * random.nextGaussian();
                        this.zd += bounceSpreadRate * random.nextGaussian();
                    }
                }
                if (onCollision != null) {
                    onCollision.accept(this);
                }
            } else if (Math.abs(moveX) >= 1.0E-5 && Math.abs(x) < 1.0E-5) {
                if (bounceChance < 1 && bounceChance < random.nextFloat()) {
                    this.onGround = true;
                } else {
                    this.xd = -this.xd * bounceRate;
                    if (bounceSpreadRate > 0) {
                        this.yd += bounceSpreadRate * random.nextGaussian();
                        this.zd += bounceSpreadRate * random.nextGaussian();
                    }
                }
                if (onCollision != null) {
                    onCollision.accept(this);
                }
            } else if (Math.abs(moveZ) >= 1.0E-5 && Math.abs(z) < 1.0E-5) {
                if (bounceChance < 1 && bounceChance < random.nextFloat()) {
                    this.onGround = true;
                } else {
                    this.zd = -this.zd * bounceRate;
                    if (bounceSpreadRate > 0) {
                        this.xd += bounceSpreadRate * random.nextGaussian();
                        this.yd += bounceSpreadRate * random.nextGaussian();
                    }
                }
                if (onCollision != null) {
                    onCollision.accept(this);
                }
            }
        }
    }

    protected int getLightColor(float partialTick) {
        BlockPos blockPos = new BlockPos((int) this.x, (int) this.y, (int) this.z);
        var level = getLevel();
        if (level != null && (level.hasChunkAt(blockPos))) {
            return LevelRenderer.getLightColor(level, blockPos);
        }
        return 0;
    }

    public final void render(@Nonnull VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        render(new PoseStack(), pBuffer, pRenderInfo, pPartialTicks);
    }

    public void render(@Nonnull PoseStack poseStack, @Nonnull VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        if (delay <= 0 && (this.emitter == null || this.emitter.isVisible())) {
            renderInternal(poseStack.last().pose(), pBuffer, pRenderInfo, pPartialTicks);
        }
    }

    public void renderInternal(@Nonnull Matrix4f matrix, @Nonnull VertexConsumer buffer, Camera camera, float partialTicks) {
        var pos = getPos(partialTicks);
        float x = pos.x;
        float y = pos.y;
        float z = pos.z;

        float a = getAlpha(partialTicks);
        float r = getRed(partialTicks);
        float g = getGreen(partialTicks);
        float b = getBlue(partialTicks);
        if (dynamicColor != null){
            var color = dynamicColor.apply(this, partialTicks);
            a *= color.w();
            r *= color.x();
            g *= color.y();
            b *= color.z();
        }

        Vector3f size, rotation = getRotation(partialTicks);
        if (dynamicSize != null) {
            size = dynamicSize.apply(this, partialTicks);
        } else {
            size = this.getQuadSize(partialTicks);
        }
        if (this.rotationAddition != null) {
            rotation = rotation.add(this.rotationAddition.apply(this, partialTicks));
        }

        Quaternionf quaternion = this.getQuaternionSupplier().get();
        if (quaternion == null) {
            quaternion = camera.rotation();
        }
        if (!(rotation.x == 0 && rotation.y == 0 && rotation.z == 0)) {
            quaternion = new Quaternionf(quaternion);
            if (rotation.y != 0) {
                quaternion.rotateX(rotation.y);
            }
            if (rotation.z != 0) {
                quaternion.rotateY(rotation.z);
            }
            quaternion.rotateZ(rotation.x);
        }

        Vector3f[] rawVertexes = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

        for(int i = 0; i < 4; ++i) {
            Vector3f vertex = rawVertexes[i];
            vertex = quaternion.transform(vertex);
            vertex.mul(size.x, size.y, size.z);
            vertex.add(x, y, z);
        }

        float u0, u1, v0, v1;
        if (dynamicUVs != null) {
            var uvs = dynamicUVs.apply(this, partialTicks);
            u0 = uvs.x();
            v0 = uvs.y();
            u1 = uvs.z();
            v1 = uvs.w();
        } else {
            u0 = this.getU0(partialTicks);
            u1 = this.getU1(partialTicks);
            v0 = this.getV0(partialTicks);
            v1 = this.getV1(partialTicks);
        }

        int light = dynamicLight == null ? this.getLight(partialTicks) : dynamicLight.apply(this, partialTicks);

        buffer.vertex(matrix, rawVertexes[0].x(), rawVertexes[0].y(), rawVertexes[0].z()).uv(u1, v1).color(r, g, b, a).uv2(light).endVertex();
        buffer.vertex(matrix, rawVertexes[1].x(), rawVertexes[1].y(), rawVertexes[1].z()).uv(u1, v0).color(r, g, b, a).uv2(light).endVertex();
        buffer.vertex(matrix, rawVertexes[2].x(), rawVertexes[2].y(), rawVertexes[2].z()).uv(u0, v0).color(r, g, b, a).uv2(light).endVertex();
        buffer.vertex(matrix, rawVertexes[3].x(), rawVertexes[3].y(), rawVertexes[3].z()).uv(u0, v1).color(r, g, b, a).uv2(light).endVertex();
    }

    public boolean shouldCull() {
        return cull;
    }

    public float getAlpha(float partialTicks) {
        return this.alpha;
    }

    public float getRed(float partialTicks) {
        return this.rCol;
    }

    public float getGreen(float partialTicks) {
        return this.gCol;
    }

    public float getBlue(float partialTicks) {
        return this.bCol;
    }

    public Vector3f getQuadSize(float partialTicks) {
        return this.quadSize;
    }

    protected float getRoll(float partialTicks) {
        return Mth.lerp(partialTicks, this.oRoll, this.roll);
    }

    public float getGravity() {
        return gravity;
    }

    public float getRoll() {
        return roll;
    }

    public int getLight(float pPartialTick) {
        if (light >= 0) return light;
        if (getLevel() == null) return 0xf000f0;
        return getLightColor(pPartialTick);
    }

    public float getU0(float pPartialTicks) {
        return 0;
    }

    public float getU1(float pPartialTicks) {
        return 1;
    }

    public float getV0(float pPartialTicks) {
        return 0;
    }

    public float getV1(float pPartialTicks) {
        return 1;
    }

    public int getAge() {
        return age;
    }

    public Vector3f getVelocity() {
        var speed = new Vector3f((float) this.xd, (float) this.yd, (float) this.zd);
        if (velocityAddition != null) {
            speed.add(velocityAddition.apply(this));
        }
        if (velocityMultiplier != null) {
            speed.mul(velocityMultiplier.apply(this));
        }
        return speed;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void prepareForEmitting(@Nullable IParticleEmitter emitter) {
        updateOrigin();
        this.emitter = emitter;
    }

    public void resetAge() {
        this.age = 0;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSpeed(Vector3f vec) {
        super.setParticleSpeed(vec.x, vec.y, vec.z);
    }

    public void setSpeed(float mul) {
        this.xd *= mul;
        this.yd *= mul;
        this.zd *= mul;
    }

    public Vector3f getVecPos() {
        return getPos(0);
    }

    public Vector3f getPos(float partialTicks) {
        var pos = new Vector3f((float)Mth.lerp(partialTicks, this.xo, this.x),
                (float)Mth.lerp(partialTicks, this.yo, this.y),
                (float)Mth.lerp(partialTicks, this.zo, this.z));
        if (positionAddition != null) {
            var addition = positionAddition.apply(this, partialTicks);
            pos.add(addition);
        }
        return pos;
    }

    public Vector3f getRotation(float partialTicks) {
        var rotation = new Vector3f(getRoll(partialTicks), getPitch(), getYaw());
        if (rotationAddition != null) {
            var addition = rotationAddition.apply(this, partialTicks);
            rotation.add(addition);
        }
        return rotation;
    }

    public float getT(float partialTicks) {
        return t + partialTicks / getLifetime();
    }

    public void setPos(Vector3f realPos, boolean origin) {
        setPos(realPos.x, realPos.y, realPos.z, origin);
    }

    public float getMemRandom(Object object) {
        return getMemRandom(object, RandomSource::nextFloat);
    }

    public float getMemRandom(Object object, Function<RandomSource, Float> randomFunc) {
        var value = memRandom.get(object);
        if (value == null) return memRandom.computeIfAbsent(object, o -> randomFunc.apply(random));
        return value;
    }

    public void setRotation(Vector3f rotation) {
        setRoll(rotation.x);
        setPitch(rotation.y);
        setYaw(rotation.z);
    }

    public ClientLevel getClientLevel() {
        return level;
    }

    public Vector4f getColor(float partialTicks) {
        float a = getAlpha(partialTicks);
        float r = getRed(partialTicks);
        float g = getGreen(partialTicks);
        float b = getBlue(partialTicks);
        if (dynamicColor != null){
            var color = dynamicColor.apply(this, partialTicks);
            a *= color.w();
            r *= color.x();
            g *= color.y();
            b *= color.z();
        }
        return new Vector4f(r, g, b, a);
    }

    public void resetParticle() {
        resetAge();
        this.memRandom.clear();
        this.removed = false;
        this.onGround = false;
        this.emitter = null;
        this.t = 0;
    }

    @Override
    @Nonnull
    public abstract RLParticleRenderType getRenderType();

    @Nullable
    public Consumer<AdvancedRLParticleBase> getOnDeath() {
        return onDeath;
    }

    public void setOnDeath(@Nullable Consumer<AdvancedRLParticleBase> onDeath) {
        this.onDeath = onDeath;
    }

    public Vector3f getQuadSize() {
        return quadSize;
    }

    public boolean isMoveless() {
        return moveless;
    }

    public void setMoveless(boolean moveless) {
        this.moveless = moveless;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public boolean isCull() {
        return cull;
    }

    public void setCull(boolean cull) {
        this.cull = cull;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getBounceChance() {
        return bounceChance;
    }

    public void setBounceChance(float bounceChance) {
        this.bounceChance = bounceChance;
    }

    public float getBounceRate() {
        return bounceRate;
    }

    public void setBounceRate(float bounceRate) {
        this.bounceRate = bounceRate;
    }

    public float getBounceSpreadRate() {
        return bounceSpreadRate;
    }

    public void setBounceSpreadRate(float bounceSpreadRate) {
        this.bounceSpreadRate = bounceSpreadRate;
    }

    public Supplier<Quaternionf> getQuaternionSupplier() {
        return quaternionSupplier;
    }

    public void setQuaternionSupplier(Supplier<Quaternionf> quaternionSupplier) {
        this.quaternionSupplier = quaternionSupplier;
    }

    public Level getRealLevel() {
        return realLevel;
    }

    public void setRealLevel(Level realLevel) {
        this.realLevel = realLevel;
    }

    @Nullable
    public Consumer<AdvancedRLParticleBase> getOnUpdate() {
        return onUpdate;
    }

    public void setOnUpdate(@Nullable Consumer<AdvancedRLParticleBase> onUpdate) {
        this.onUpdate = onUpdate;
    }

    @Nullable
    public Function<AdvancedRLParticleBase, Vector3f> getVelocityAddition() {
        return velocityAddition;
    }

    public void setVelocityAddition(@Nullable Function<AdvancedRLParticleBase, Vector3f> velocityAddition) {
        this.velocityAddition = velocityAddition;
    }

    @Nullable
    public Function<AdvancedRLParticleBase, Float> getVelocityMultiplier() {
        return velocityMultiplier;
    }

    public void setVelocityMultiplier(@Nullable Function<AdvancedRLParticleBase, Float> velocityMultiplier) {
        this.velocityMultiplier = velocityMultiplier;
    }

    @Nullable
    public BiFunction<AdvancedRLParticleBase, Float, Vector4f> getDynamicColor() {
        return dynamicColor;
    }

    public void setDynamicColor(@Nullable BiFunction<AdvancedRLParticleBase, Float, Vector4f> dynamicColor) {
        this.dynamicColor = dynamicColor;
    }

    @Nullable
    public BiFunction<AdvancedRLParticleBase, Float, Vector3f> getDynamicSize() {
        return dynamicSize;
    }

    public void setDynamicSize(@Nullable BiFunction<AdvancedRLParticleBase, Float, Vector3f> dynamicSize) {
        this.dynamicSize = dynamicSize;
    }

    @Nullable
    public BiFunction<AdvancedRLParticleBase, Float, Vector3f> getRotationAddition() {
        return rotationAddition;
    }

    public void setRotationAddition(@Nullable BiFunction<AdvancedRLParticleBase, Float, Vector3f> rotationAddition) {
        this.rotationAddition = rotationAddition;
    }

    @Nullable
    public BiFunction<AdvancedRLParticleBase, Float, Vector3f> getPositionAddition() {
        return positionAddition;
    }

    public void setPositionAddition(@Nullable BiFunction<AdvancedRLParticleBase, Float, Vector3f> positionAddition) {
        this.positionAddition = positionAddition;
    }

    @Nullable
    public BiFunction<AdvancedRLParticleBase, Float, Vector4f> getDynamicUVs() {
        return dynamicUVs;
    }

    public void setDynamicUVs(@Nullable BiFunction<AdvancedRLParticleBase, Float, Vector4f> dynamicUVs) {
        this.dynamicUVs = dynamicUVs;
    }

    @Nullable
    public BiFunction<AdvancedRLParticleBase, Float, Integer> getDynamicLight() {
        return dynamicLight;
    }

    public void setDynamicLight(@Nullable BiFunction<AdvancedRLParticleBase, Float, Integer> dynamicLight) {
        this.dynamicLight = dynamicLight;
    }

    @Nullable
    public Consumer<AdvancedRLParticleBase> getOnBirth() {
        return onBirth;
    }

    public void setOnBirth(@Nullable Consumer<AdvancedRLParticleBase> onBirth) {
        this.onBirth = onBirth;
    }

    @Nullable
    public Consumer<AdvancedRLParticleBase> getOnCollision() {
        return onCollision;
    }

    public void setOnCollision(@Nullable Consumer<AdvancedRLParticleBase> onCollision) {
        this.onCollision = onCollision;
    }

    public float getT() {
        return t;
    }

    public void setT(float t) {
        this.t = t;
    }

    @Nullable
    public IParticleEmitter getEmitter() {
        return emitter;
    }

    public void setEmitter(@Nullable IParticleEmitter emitter) {
        this.emitter = emitter;
    }

    public ConcurrentHashMap<Object, Float> getMemRandom() {
        return memRandom;
    }

    public void setMemRandom(ConcurrentHashMap<Object, Float> memRandom) {
        this.memRandom = memRandom;
    }

    public static class Basic extends AdvancedRLParticleBase {
        final RLParticleRenderType renderType;

        public Basic(ClientLevel level, double x, double y, double z, RLParticleRenderType renderType) {
            super(level, x, y, z);
            this.renderType = renderType;
        }

        public Basic(ClientLevel level, double x, double y, double z, double sX, double sY, double sZ, RLParticleRenderType renderType) {
            super(level, x, y, z, sX, sY, sZ);
            this.renderType = renderType;
        }

        @Override
        @NotNull
        public RLParticleRenderType getRenderType() {
            return renderType;
        }
    }
}
