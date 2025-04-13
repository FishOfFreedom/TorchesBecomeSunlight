package com.freefish.torchesbecomesunlight.client.particle.advance;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RLParticleQueueRenderType extends RLParticleRenderType {
    public static final RLParticleQueueRenderType INSTANCE = new RLParticleQueueRenderType();
    private static final BufferBuilder[] BUFFERS = new BufferBuilder[ForkJoinPool.getCommonPoolParallelism() + 1];
    static {
        for (int i = 0; i < BUFFERS.length; i++) {
            BUFFERS[i] = new BufferBuilder(256);
        }
    }

    // runtime
    protected final Map<RLParticleRenderType, Pair<Matrix4f, Queue<AdvancedRLParticleBase>>> particles = new HashMap<>();
    private Camera camera;
    private float pPartialTicks;
    private boolean isRenderingQueue;

    @Override
    public void begin(BufferBuilder builder) {
        particles.clear();
        camera = null;
        isRenderingQueue = false;
    }

    @Override
    public void end(BufferBuilder builder) {
        isRenderingQueue = true;
        for (var entry : particles.entrySet()) {
            var type = entry.getKey();
            var matrix = entry.getValue().left();
            var list = entry.getValue().right();
            if (!list.isEmpty()) {
                RenderSystem.setShader(GameRenderer::getParticleShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                type.prepareStatus();

                if (type.isParallel()) {
                    final var forkJoinPool = ForkJoinPool.commonPool();
                    final var task = forkJoinPool.submit(new ParallelRenderingTask(BUFFERS, type, list.spliterator()));
                    try {
                        for (var buffer : task.get()) {
                            type.end(buffer);
                        }
                    } catch (Throwable ignored) {
                        ignored.printStackTrace();
                    } finally {
                        forkJoinPool.shutdown();
                    }
                } else {
                    type.begin(builder);
                    var poseStack = new PoseStack();
                    Vec3 vec3 = camera.getPosition();
                    poseStack.translate(-vec3.x, -vec3.y, -vec3.z);
                    poseStack.mulPoseMatrix(matrix);
                    for (var particle : list) {
                        particle.render(poseStack, builder, camera, pPartialTicks);
                    }
                    type.end(builder);
                }

                type.releaseStatus();
            }
        }
        isRenderingQueue = false;
    }

    public void pipeQueue(@Nonnull RLParticleRenderType type, @Nonnull Queue<AdvancedRLParticleBase> queue, Matrix4f matrix, Camera camera, float pPartialTicks) {
        particles.computeIfAbsent(type, t -> Pair.of(matrix, queue));
        if (this.camera == null) {
            this.camera = camera;
            this.pPartialTicks = pPartialTicks;
        }
    }

    class ParallelRenderingTask extends RecursiveTask<List<BufferBuilder>> {
        private final BufferBuilder[] buffers;
        private final RLParticleRenderType type;
        private final Spliterator<AdvancedRLParticleBase> particles;

        public ParallelRenderingTask(BufferBuilder[] buffers, RLParticleRenderType type, Spliterator<AdvancedRLParticleBase> particles) {
            this.buffers = buffers;
            this.type = type;
            this.particles = particles;
        }

        @Override
        protected List<BufferBuilder> compute() {
            if(buffers.length > 1){
                var split = particles.trySplit();
                var task1 = new ParallelRenderingTask(ArrayUtils.subarray(buffers, 0, buffers.length / 2), type, particles).fork();
                if (split != null) {
                    var task2 = new ParallelRenderingTask(ArrayUtils.subarray(buffers, buffers.length / 2, buffers.length), type, split).fork();
                    var result = new ArrayList<>(task1.join());
                    result.addAll(task2.join());
                    return result;
                }
                return task1.join();
            } else {
                type.begin(buffers[0]);
                particles.forEachRemaining(particle -> particle.render(buffers[0], camera, pPartialTicks));
                return List.of(buffers[0]);
            }
        }

    }
}