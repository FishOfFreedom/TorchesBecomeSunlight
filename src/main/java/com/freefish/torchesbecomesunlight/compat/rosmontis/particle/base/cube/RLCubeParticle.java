/**
 * SPDX-License-Identifier: GPL-3.0
 * Copyright (c) 2023 KilaBash
 *
 * === Modification History ===
 * 2025-5-26 - Pass particle data to the render type to implement a dynamic shader
 */

package com.freefish.torchesbecomesunlight.compat.rosmontis.particle.base.cube;

import com.freefish.rosmontislib.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.rosmontislib.client.particle.advance.RLParticleQueueRenderType;
import com.freefish.rosmontislib.client.particle.advance.RLParticleRenderType;
import com.freefish.rosmontislib.client.particle.advance.base.IParticle;
import com.freefish.rosmontislib.client.particle.advance.base.IParticleEmitter;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

@ParametersAreNonnullByDefault
public class RLCubeParticle extends AdvancedRLParticleBase{
    public static int VERSION = 2;

    public final RLCubeParticleConfig config;

    public Map<RLParticleRenderType, Queue<IParticle>> getParticles() {
        return particles;
    }

    // runtime
    protected final Map<RLParticleRenderType, Queue<IParticle>> particles = new LinkedHashMap<>();
    public final Queue<IParticle> waitToAdded = Queues.newArrayDeque();

    public RLCubeParticle(Level level) {
        this(level,new RLCubeParticleConfig());
    }

    protected RLCubeParticle(Level level, RLCubeParticleConfig config) {
        super((ClientLevel) level);
        this.config = config;
        this.config.setRlParticle(this);
    }

    //////////////////////////////////////
    //*****     particle logic     *****//
    //////////////////////////////////////
    protected CubeParticle createNewParticle() {
        return new CubeParticle(this, config, getThreadSafeRandomSource());
    }

    @Override
    public void update() {
        var available = config.maxParticles - getParticleAmount();
        if (!removed && getParticleAmount() < config.maxParticles) {
            available = Math.min(config.emission.getEmissionCount(this.age, t, getRandomSource()), available);
            for (int i = 0; i < available; i++) {
                emitParticle(createNewParticle());
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
            if (config.parallelUpdate && (!config.physics.isEnable() || !config.physics.isHasCollision())) { // parallel stream for particles tick.
                queue.removeIf(p -> !p.isAlive());
                queue.parallelStream().forEach(IParticle::tick);
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

        super.update();
    }

    @Override
    public boolean isLooping() {
        return config.isLooping();
    }

    @Override
    public float getT() {
        return t;
    }

    public void emitParticle(IParticle particle) {
        waitToAdded.add(particle);
    }

    @Override
    public int getLifetime() {
        return config.duration;
    }

    @Override
    protected void updateOrigin() {
        super.updateOrigin();
        setLifetime(config.duration);
    }

    @Override
    public void reset() {
        super.reset();
        this.particles.clear();
    }

    @Override
    public void render(@Nonnull VertexConsumer buffer, Camera camera, float pPartialTicks) {
        super.render(buffer, camera, pPartialTicks);
        if (!RLParticleQueueRenderType.INSTANCE.isRenderingQueue() && delay <= 0 && isVisible() &&
                //RLParticleRenderType.checkLayer(config.renderer.getLayer()) &&
                (!config.renderer.getCull().isEnable() ||
                        RLParticleRenderType.checkFrustum(config.renderer.getCull().getCullAABB(this, pPartialTicks)))) {
            for(var entry : this.particles.entrySet()) {
                var type = entry.getKey();
                if (type == ParticleRenderType.NO_RENDER) continue;
                var queue = entry.getValue();
                if (type == RLParticleQueueRenderType.INSTANCE) {
                    // TODO sub emitters
                    for (var emitter : queue) {
                        emitter.render(buffer, camera, pPartialTicks);
                    }
                } else if (!queue.isEmpty()) {
                    RLParticleQueueRenderType.INSTANCE.pipeQueue(type, queue, camera, pPartialTicks);
                }
            }
        }
    }


    //////////////////////////////////////
    //********      Emitter    *********//
    //////////////////////////////////////

    @Override
    public int getParticleAmount() {
        var sum = 0;
        for (var entry : getParticles().entrySet()) {
            if (entry.getKey() == RLParticleQueueRenderType.INSTANCE) {
                for (var particle : entry.getValue()) {
                    sum += ((IParticleEmitter) particle).getParticleAmount();
                }
            }
            sum += entry.getValue().size();
        }
        return sum + waitToAdded.size();
    }

    @Override
    @Nullable
    public AABB getCullBox(float partialTicks) {
        return config.renderer.getCull().isEnable() ? config.renderer.getCull().getCullAABB(this, partialTicks) : null;
    }

    @Override
    public void remove(boolean force) {
        super.remove(force);
        if (force) {
            particles.clear();
        }
    }
}
