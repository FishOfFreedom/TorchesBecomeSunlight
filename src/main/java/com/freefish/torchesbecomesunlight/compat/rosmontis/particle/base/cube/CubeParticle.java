/**
 * SPDX-License-Identifier: GPL-3.0
 * Copyright (c) 2023 KilaBash
 */

package com.freefish.torchesbecomesunlight.compat.rosmontis.particle.base.cube;

import com.freefish.rosmontislib.client.particle.advance.RLParticleRenderType;
import com.freefish.rosmontislib.client.particle.advance.base.IParticle;
import com.freefish.rosmontislib.client.particle.advance.base.IParticleEmitter;
import com.freefish.rosmontislib.client.particle.advance.data.InheritVelocitySetting;
import com.freefish.rosmontislib.client.utils.ColorUtils;
import com.freefish.rosmontislib.client.utils.Vector3fHelper;
import com.freefish.torchesbecomesunlight.compat.rosmontis.particle.base.cube.setting.RendererSetting;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class CubeParticle implements IParticle {
    public static final Direction[] MODEL_SIDES = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN, null};
    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);
    /**
     * Basic data
     */
    protected float localX, localY, localZ; // local position
    protected float localXo, localYo, localZo;
    protected float rotationX = 180, rotationY = 180, rotationZ = 180; // rotation
    protected float rotationXo = 180, rotationYo = 180, rotationZo = 180;
    protected float sizeX = 1, sizeY = 1, sizeZ = 1; // size
    protected float sizeXo = 1, sizeYo = 1, sizeZo = 1;
    protected float r = 1, g = 1, b = 1, a = 1; // color
    protected float ro = 1, go = 1, bo = 1, ao = 1;
    protected float velocityX, velocityY, velocityZ; // velocity
    protected int light = -1;
    protected AABB boundingBox = new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);

    public boolean isCollided() {
        return collided;
    }

    public void setCollided(boolean collided) {
        this.collided = collided;
    }

    /**
     * Physics
     */
    protected boolean collided;
    /**
     * Life cycle
     */
    protected int delay;
    protected int age;
    protected int lifetime;
    protected boolean isRemoved;

    @Override
    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }


    @Override
    public float getT() {
        return t;
    }

    // runtime
    protected float t;
    protected Matrix4f initialTransform;
    protected Matrix4f initialTransformInverse;

    public void setInitialScale(Vector3f initialScale) {
        this.initialScale = initialScale;
    }

    protected Vector3f initialScale;
    protected Vector3f initialSize;
    protected Vector3f initialRotation;
    protected Vector4f initialColor;
    protected boolean isFirstCollision;
    protected RLCubeParticleConfig config;

    public IParticleEmitter getEmitter() {
        return emitter;
    }

    public ConcurrentHashMap<Object, Float> getMemRandom() {
        return memRandom;
    }

    @Override
    public RandomSource getRandomSource() {
        return randomSource;
    }

    protected IParticleEmitter emitter;
    protected ConcurrentHashMap<Object, Float> memRandom = new ConcurrentHashMap<>();
    public RandomSource randomSource;

    public CubeParticle(IParticleEmitter emitter, RLCubeParticleConfig config, RandomSource randomSource) {
        this.emitter = emitter;
        this.config = config;
        this.randomSource = randomSource;
        setup();
    }

    public void setup() {
        this.initialTransform = emitter.transform().localToWorldMatrix();
        this.initialTransformInverse = emitter.transform().worldToLocalMatrix();
        this.initialScale = emitter.transform().scale();
        var emitterT = emitter.getT();

        setDelay(config.getStartDelay().get(randomSource, emitterT).intValue());
        if (config.lifetimeByEmitterSpeed.isEnable()) {
            setLifetime(config.lifetimeByEmitterSpeed.getLifetime(this, emitter,
                    config.getStartLifetime().get(randomSource, emitterT).intValue()));
        } else {
            setLifetime(config.getStartLifetime().get(randomSource, emitterT).intValue());
        }

        if (config.inheritVelocity.isEnable() && config.inheritVelocity.getMode() == InheritVelocitySetting.Mode.INITIAL) {
            addInternalVelocity(getSpaceTransformInverse().transformDirection(emitter.getVelocity()));
        }
        mulInternalVelocity(config.getStartSpeed().get(randomSource, emitterT).floatValue());
        this.initialSize = config.getStartSize().get(randomSource, emitterT);
        this.initialRotation = config.getStartRotation().get(randomSource, emitterT).mul(Mth.TWO_PI / 360);
        var color = config.getStartColor().get(randomSource, emitterT).intValue();
        this.initialColor = new Vector4f(ColorUtils.red(color), ColorUtils.green(color), ColorUtils.blue(color), ColorUtils.alpha(color));
        setSize(initialSize);
        setRotation(initialRotation);
        setColor(initialColor);
        update();
        updateOrigin();
    }

    @Override
    public RLParticleRenderType getRenderType() {
        return config.particleRenderType;
    }

    @Override
    public float getT(float partialTicks) {
        return t + partialTicks / getLifetime();
    }

    @Override
    public float getMemRandom(Object object) {
        return getMemRandom(object, RandomSource::nextFloat);
    }

    @Override
    public float getMemRandom(Object object, Function<RandomSource, Float> randomFunc) {
        var value = memRandom.get(object);
        if (value == null) return memRandom.computeIfAbsent(object, o -> randomFunc.apply(randomSource));
        return value;
    }

    public void setLocalPos(float x, float y, float z, boolean setOrigin) {
        this.localX = x;
        this.localY = y;
        this.localZ = z;
        if (setOrigin) {
            this.localXo = x;
            this.localYo = y;
            this.localZo = z;
        }
    }

    public void setLocalPos(Vector3f realPos, boolean origin) {
        setLocalPos(realPos.x, realPos.y, realPos.z, origin);
    }

    public void setInternalVelocity(Vector3f vec) {
        this.velocityX = vec.x;
        this.velocityY = vec.y;
        this.velocityZ = vec.z;
    }

    public void mulInternalVelocity(float mul) {
        this.velocityX *= mul;
        this.velocityY *= mul;
        this.velocityZ *= mul;
    }

    public void addInternalVelocity(Vector3f vec) {
        this.velocityX += vec.x;
        this.velocityY += vec.y;
        this.velocityZ += vec.z;
    }

    public void setRotation(Vector3f rotation) {
        this.rotationX = rotation.x;
        this.rotationY = rotation.y;
        this.rotationZ = rotation.z;
    }

    public void setSize(Vector3f size) {
        this.sizeX = size.x;
        this.sizeY = size.y;
        this.sizeZ = size.z;
        boundingBox = new AABB(-sizeX / 2, -sizeY / 2, -sizeZ / 2, sizeX / 2, sizeY / 2, sizeZ / 2);
    }

    public void mulSize(float size) {
        this.sizeX *= size;
        this.sizeY *= size;
        this.sizeZ *= size;
        boundingBox = new AABB(-sizeX / 2, -sizeY / 2, -sizeZ / 2, sizeX / 2, sizeY / 2, sizeZ / 2);
    }

    public void setColor(Vector4f color) {
        this.r = color.x();
        this.g = color.y();
        this.b = color.z();
        this.a = color.w();
    }

    public void setARGBColor(int color) {
        this.a = ColorUtils.alpha(color);
        this.r = ColorUtils.red(color);
        this.g = ColorUtils.green(color);
        this.b = ColorUtils.blue(color);
    }

    public Vector3f getRealRotation(float partialTicks) {
        var rotation = new Vector3f(
                Mth.lerp(partialTicks, rotationXo, rotationX),
                Mth.lerp(partialTicks, rotationYo, rotationY),
                Mth.lerp(partialTicks, rotationZo, rotationZ));
        return rotation;
    }

    public Vector3f getRealSize(float partialTicks) {
        return new Vector3f(
                Mth.lerp(partialTicks, sizeXo, sizeX),
                Mth.lerp(partialTicks, sizeYo, sizeY),
                Mth.lerp(partialTicks, sizeZo, sizeZ));
    }

    public Vector3f getLocalPos() {
        return getLocalPos(0);
    }

    public Vector3f getLocalPos(float partialTicks) {
        if (isRemoved) {
            return new Vector3f(localX, localY, localZ);
        }
        var pos = new Vector3f(Mth.lerp(partialTicks, this.localXo, this.localX),
                Mth.lerp(partialTicks, this.localYo, this.localY),
                Mth.lerp(partialTicks, this.localZo, this.localZ));

        if (config.noise.isEnable()) {
            pos.add(config.noise.getPosition(this, partialTicks));
        }

        return pos;
    }

    public Vector3f getWorldPos() {
        return getWorldPos(0);
    }

    /**
     * from local to world
     */
    public Matrix4f getSpaceTransform() {
        return config.getSimulationSpace() == RLCubeParticleConfig.Space.Local ?
                emitter.transform().localToWorldMatrix() :
                initialTransform;
    }

    /**
     * from world to local
u     */
    public Matrix4f getSpaceTransformInverse() {
        return config.getSimulationSpace() == RLCubeParticleConfig.Space.Local ?
                emitter.transform().worldToLocalMatrix() :
                initialTransformInverse;
    }

    public Vector3f getSpaceScale() {
        return config.getSimulationSpace() == RLCubeParticleConfig.Space.Local ?
                emitter.transform().scale() :
                initialScale;
    }

    public Vector3f getWorldPos(float partialTicks) {
        var localPosition = getLocalPos(partialTicks);
        return new Vector3f(localPosition).mulPosition(getSpaceTransform());
    }

    public AABB getRealBoundingBox(float partialTicks) {
        var pos = getWorldPos(partialTicks);
        return boundingBox.move(pos.x, pos.y, pos.z);
    }

    public Vector4f getRealColor(float partialTicks) {
        var emitterColor = emitter.getRGBAColor();
        var a = Mth.lerp(partialTicks, this.ao, this.a);
        var r = Mth.lerp(partialTicks, this.ro, this.r);
        var g = Mth.lerp(partialTicks, this.go, this.g);
        var b = Mth.lerp(partialTicks, this.bo, this.b);
        return emitterColor.mul(r, g, b, a);
    }

    public Vector4f getRealUVs(float partialTicks) {
        if (config.uvAnimation.isEnable()) {
            return config.uvAnimation.getUVs(this, partialTicks);
        } else {
            return new Vector4f(0, 0, 1, 1);
        }
    }

    public int getRealLight(float partialTicks) {
        if (config.renderer.isBloomEffect()) {
            return LightTexture.FULL_BRIGHT;
        }
        if (config.lights.isEnable()) {
            return config.lights.getLight(this, partialTicks);
        }
        return light;
    }

    public int getLightColor() {
        var pos = getWorldPos();
        var blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
        return emitter.getLightColor(blockPos);
    }

    /**
     * should always be called per tick
     */
    public void tick() {
        if (delay > 0) {
            delay--;
            return;
        }

        //if (config.subEmitters.isEnable() && emitter.getScene() instanceof FXRuntime fxRuntime) {
        //    config.subEmitters.triggerEvent(fxRuntime.getFx(), this, SubEmittersSetting.Event.Birth);
        //}

        // update origin data
        updateOrigin();

        // update life cycle
        if (this.age++ >= this.lifetime && lifetime > 0) {
            setRemoved(true);
            //if (config.subEmitters.isEnable() && emitter.getScene() instanceof FXRuntime fxRuntime) {
            //    config.subEmitters.triggerEvent(fxRuntime.getFx(), this, SubEmittersSetting.Event.Death);
            //}
        }

        // update data
        update();

        //if (config.subEmitters.isEnable() && emitter.getScene() instanceof FXRuntime fxRuntime) {
        //    config.subEmitters.triggerEvent(fxRuntime.getFx(), this, SubEmittersSetting.Event.Tick);
        //}

        if (lifetime > 0) {
            t = 1.0f * age / lifetime;
        }
    }

    protected void updateOrigin() {
        this.localXo = this.localX;
        this.localYo = this.localY;
        this.localZo = this.localZ;
        this.rotationXo = this.rotationX;
        this.rotationYo = this.rotationY;
        this.rotationZo = this.rotationZ;
        this.sizeXo = this.sizeX;
        this.sizeYo = this.sizeY;
        this.sizeZo = this.sizeZ;
        this.ro = this.r;
        this.go = this.g;
        this.bo = this.b;
        this.ao = this.a;
    }

    protected void update() {
        updateChanges();
    }

    protected void updateChanges() {
        this.updatePositionAndInternalVelocity();
        this.updateColor();
        this.updateSize();
        this.updateRotation();
        this.updateLight();
    }

    protected void updatePositionAndInternalVelocity() {
        var velocity = getRealVelocity();
        var moveX = velocity.x;
        var moveY = velocity.y;
        var moveZ = velocity.z;

        var level = emitter.getLevel();
        if (config.physics.isEnable() && config.physics.isHasCollision() && level != null &&
                (moveX != 0.0 || moveY != 0.0 || moveZ != 0.0) && moveX * moveX + moveY * moveY + moveZ * moveZ < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
            var vec3 = Entity.collideBoundingBox(null, new Vec3(moveX, moveY, moveZ), getRealBoundingBox(0), level, List.of());
            moveX = (float) vec3.x;
            moveY = (float) vec3.y;
            moveZ = (float) vec3.z;
        }

        // update bounding box and position
        if (moveX != 0.0 || moveY != 0.0 || moveZ != 0.0) {
            var moveLocal = getSpaceTransformInverse().transformDirection(new Vector3f(moveX, moveY, moveZ));
            setLocalPos(localX + moveLocal.x, localY + moveLocal.y, localZ + moveLocal.z, false);
        }

        // update internal velocity
        if (!config.physics.isEnable()) return;
        if (config.physics.isHasCollision() && !this.collided) {
            var bounceChance = config.physics.getBounceChance(this);
            var bounceRate = config.physics.getBounceRate(this);
            var bounceSpreadRate = config.physics.getBounceSpreadRate(this);
            if (Math.abs(velocity.x) >= 1.0E-5 && Math.abs(moveX) < 1.0E-5) {
                updateCollisionBounce(bounceChance, velocity, bounceRate, bounceSpreadRate, Direction.Axis.X);
            } else if (Math.abs(velocity.y) >= 1.0E-5 && Math.abs(moveY) < 1.0E-5) {
                updateCollisionBounce(bounceChance, velocity, bounceRate, bounceSpreadRate, Direction.Axis.Y);
            } else if (Math.abs(velocity.z) >= 1.0E-5 && Math.abs(moveZ) < 1.0E-5) {
                updateCollisionBounce(bounceChance, velocity, bounceRate, bounceSpreadRate, Direction.Axis.Z);
            }
        }

        var gravity = config.physics.getGravity(this);
        if (gravity != 0) {
            this.addInternalVelocity(getSpaceTransformInverse().transformDirection(new Vector3f(0, -gravity * 0.04f, 0)));
        }

        var friction = config.physics.getFriction(this);
        this.velocityX *= friction;
        this.velocityY *= friction;
        this.velocityZ *= friction;

        if (this.collided && friction != 1.0) {
            this.velocityX *= 0.7F;
            this.velocityY *= 0.7F;
            this.velocityZ *= 0.7F;
        }
    }

    private void updateCollisionBounce(float bounceChance, Vector3f velocity, float bounceRate, float bounceSpreadRate, Direction.Axis axis) {
        if (bounceChance < 1 && bounceChance < randomSource.nextFloat()) {
            this.collided = true;
        } else {
            var newVelocity = getSpaceTransformInverse().transformDirection(new Vector3f(
                    axis == Direction.Axis.X ? -velocity.x * bounceRate :
                            (velocity.x + (bounceSpreadRate > 0 ?
                                    (float) (bounceSpreadRate * randomSource.nextGaussian()) : 0)),
                    axis == Direction.Axis.Y ? -velocity.y * bounceRate :
                            (velocity.y + (bounceSpreadRate > 0 ?
                                    (float) (bounceSpreadRate * randomSource.nextGaussian()) : 0)),
                    axis == Direction.Axis.Z ? -velocity.z * bounceRate :
                            (velocity.z + (bounceSpreadRate > 0 ?
                                    (float) (bounceSpreadRate * randomSource.nextGaussian()) : 0))
            ));
            setInternalVelocity(newVelocity);
        }
        if (config.physics.isEnable() && config.physics.isRemovedWhenCollided()) {
            this.setRemoved(true);
            //if (config.subEmitters.isEnable() && emitter.getScene() instanceof FXRuntime fxRuntime) {
            //    config.subEmitters.triggerEvent(fxRuntime.getFx(), this, SubEmittersSetting.Event.Death);
            //}
        }
        //if (config.subEmitters.isEnable() && emitter.getScene() instanceof FXRuntime fxRuntime) {
        //    config.subEmitters.triggerEvent(fxRuntime.getFx(), this, SubEmittersSetting.Event.Collision);
        //    if (!isFirstCollision) {
        //        isFirstCollision = true;
        //        config.subEmitters.triggerEvent(fxRuntime.getFx(), this, SubEmittersSetting.Event.FirstCollision);
        //    }
        //}
    }

    /**
     * It's the internal velocity, which is the local velocity
     */
    public Vector3f getInternalVelocity() {
        var velocity = new Vector3f(velocityX, velocityY, velocityZ);
        if (config.velocityOverLifetime.isEnable()) {
            var velocityAddition = config.velocityOverLifetime.getVelocityAddition(this);
            velocity.add(velocityAddition);
        }
        return velocity;
    }

    /**
     * It's the total velocity, which is the world velocity
     */
    public Vector3f getRealVelocity() {
        var velocity = getSpaceTransform().transformDirection(getInternalVelocity());
        if (config.inheritVelocity.isEnable() && config.inheritVelocity.getMode() == InheritVelocitySetting.Mode.CURRENT) {
            velocity.add(emitter.getVelocity());
        }
        if (config.velocityOverLifetime.isEnable()) {
            var velocityMultiplier = config.velocityOverLifetime.getVelocityMultiplier(this);
            velocity.mul(velocityMultiplier);
        }
        return velocity;
    }

    protected void updateSize() {
        if ( config.sizeOverLifetime.isEnable() || config.noise.isEnable()) {
            var size = new Vector3f(initialSize);
            var mul = new Vector3f(1, 1, 1);

            if (config.noise.isEnable()) {
                size.add(config.noise.getSize(this, 0));
            }

            if (config.sizeOverLifetime.isEnable()) {
                mul.mul(config.sizeOverLifetime.getSize(this, 0));
            }

            setSize(size.mul(mul));
        }
    }

    protected void updateRotation() {
        if (config.rotationOverLifetime.isEnable() || config.noise.isEnable()) {
            var rotation = new Vector3f(initialRotation);

            if (config.rotationOverLifetime.isEnable()) {
                rotation.add(config.rotationOverLifetime.getRotation(this, 0));
            }

            if (config.noise.isEnable()) {
                rotation.add(config.noise.getRotation(this, 0));
            }

            setRotation(rotation);
        }
    }

    protected void updateColor() {
        if (config.colorOverLifetime.isEnable()) {
            var color = new Vector4f(initialColor);

            if (config.colorOverLifetime.isEnable()) {
                color.mul(config.colorOverLifetime.getColor(this, 0));
            }

            setColor(color);
        }
    }

    protected void updateLight() {
        if (config.lights.isEnable() || config.renderer.isBloomEffect()) return;
        light = getLightColor();
    }

    public void render(@Nonnull VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        if (delay <= 0 && this.emitter.isVisible()) {
            renderInternal(pBuffer, pRenderInfo, pPartialTicks);
        }
    }

    public void renderInternal(@Nonnull VertexConsumer buffer, Camera camera, float partialTicks) {
        var vec3 = camera.getPosition();

        var localPos = getLocalPos(partialTicks).mulPosition(getSpaceTransform());
        var x = (float) (localPos.x - vec3.x);
        var y = (float) (localPos.y - vec3.y);
        var z = (float) (localPos.z - vec3.z);

        var color = getRealColor(partialTicks);
        var r = color.x();
        var g = color.y();
        var b = color.z();
        var a = color.w();

        var light = getRealLight(partialTicks);

        var rotation = getRealRotation(partialTicks);
        var renderMode = config.renderer.getRenderMode();
        var quaternion = renderMode.quaternion.apply(this, camera, partialTicks);
        if (!Vector3fHelper.isZero(rotation)) {
            quaternion = new Quaternionf(quaternion).rotateXYZ(rotation.x, rotation.y, rotation.z);
        }

        var size = getRealSize(partialTicks);

        if (renderMode == RendererSetting.Particle.Mode.Model) {
            var transform = new Matrix4f().translate(x, y ,z)
                    .rotate(new Quaternionf().rotateXYZ(rotation.x, rotation.y, rotation.z))
                    .scale(size)
                    .translate(-0.5f, -0.5f, -0.5f);
            // draw 3d model
            //var model = config.renderer.getModel();
            var uvs = getRealUVs(partialTicks);
            for (var side : MODEL_SIDES) {
                var brightness = (side != null && config.renderer.isShade()) ? switch (side) {
                    case DOWN, UP:
                        yield 0.9F;
                    case NORTH:
                    case SOUTH:
                        yield 0.8F;
                    case WEST:
                    case EAST:
                        yield 0.6F;
                    default:
                        yield 1.0F;
                } : 1f;
                //var quads = model.renderModel(null, null, null, side, randomSource);
                //for (var quad : quads) {
                //    putBulkData(transform, buffer, quad, brightness, uvs, r, g, b, a, light);
                //}
            }
        } else {
            var rawVertexes = new Vector3f[]{
                    new Vector3f(-1.0F, -1.0F, -1.0F),
                    new Vector3f(-1.0F, -1.0F, 1.0F),
                    new Vector3f(1.0F, -1.0F, 1.0F),
                    new Vector3f(1.0F, -1.0F, -1.0F),
                    new Vector3f(-1.0F, 1.0F, -1.0F),
                    new Vector3f(-1.0F, 1.0F, 1.0F),
                    new Vector3f(1.0F, 1.0F, 1.0F),
                    new Vector3f(1.0F, 1.0F, -1.0F)
            };

            int[][] faces = new int[][]{
                    {0, 1, 2, 3},
                    {4, 7, 6, 5},
                    {0, 4, 5, 1},
                    {3, 2, 6, 7},
                    {1, 5, 6, 2},
                    {0, 3, 7, 4}
            };

            for (var i = 0; i < 8; ++i) {
                var vertex = rawVertexes[i];
                vertex.mul(size.x, size.y, size.z);
                vertex = quaternion.transform(vertex);
                vertex.mul(getSpaceScale());
                vertex.add(x, y, z);
            }

            var uvs = getRealUVs(partialTicks);
            var u0 = uvs.x();
            var v0 = uvs.y();
            var u1 = uvs.z();
            var v1 = uvs.w();

            //buffer.vertex(rawVertexes[0].x(), rawVertexes[0].y(), rawVertexes[0].z()).uv(u1, v1).color(r, g, b, a).uv2(light).endVertex();
            //buffer.vertex(rawVertexes[1].x(), rawVertexes[1].y(), rawVertexes[1].z()).uv(u1, v0).color(r, g, b, a).uv2(light).endVertex();
            //buffer.vertex(rawVertexes[2].x(), rawVertexes[2].y(), rawVertexes[2].z()).uv(u0, v0).color(r, g, b, a).uv2(light).endVertex();
            //buffer.vertex(rawVertexes[3].x(), rawVertexes[3].y(), rawVertexes[3].z()).uv(u0, v1).color(r, g, b, a).uv2(light).endVertex();
            for (int faceIndex = 0; faceIndex < 6; faceIndex++) {
                Vector3f[] faceVertexes = new Vector3f[4];

                for (int i = 0; i < 4; i++) {
                    int vertexIndex = faces[faceIndex][i];
                    faceVertexes[i] = new Vector3f(rawVertexes[vertexIndex]);
                }

                renderFace(buffer, faceVertexes, u0, v0, u1, v1, r, g, b, a, light);
            }
        }
    }

    private void renderFace(VertexConsumer buffer,Vector3f[] rawVertexes ,float u0,float v0,float u1,float v1,float r,float g,float b,float a,int light){
        buffer.vertex(rawVertexes[0].x(), rawVertexes[0].y(), rawVertexes[0].z()).uv(u1, v1).color(r, g, b, a).uv2(light).endVertex();
        buffer.vertex(rawVertexes[1].x(), rawVertexes[1].y(), rawVertexes[1].z()).uv(u1, v0).color(r, g, b, a).uv2(light).endVertex();
        buffer.vertex(rawVertexes[2].x(), rawVertexes[2].y(), rawVertexes[2].z()).uv(u0, v0).color(r, g, b, a).uv2(light).endVertex();
        buffer.vertex(rawVertexes[3].x(), rawVertexes[3].y(), rawVertexes[3].z()).uv(u0, v1).color(r, g, b, a).uv2(light).endVertex();
    }
}