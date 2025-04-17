package com.freefish.torchesbecomesunlight.client.particle;

import com.freefish.torchesbecomesunlight.client.util.render.MMRenderType;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.projectile.BulletRenderer;
import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Locale;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class CycleWindParticle extends Particle {
    public static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/trail_2.png");
    private Entity locateEntity;
    private Vec3[] trailPositions = new Vec3[16];
    private int trailPointer = -1;
    private float rot11;

    public CycleWindParticle(ClientLevel world, double x, double y, double z, int time) {
        super(world, x, y, z);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.lifetime = 60;
        this.gravity = 0.0F;
        locateEntity = level.getEntity(time);
        rot11 = level.random.nextFloat()*6;
    }

    public static void spawnParticle(Level level, Entity entity){
        level.addParticle(new CycleWindData(entity.getId()),entity.getX(),entity.getY(),entity.getZ(),0,0,0);
    }

    public void tick() {
        super.tick();
        if(locateEntity!=null){
            setPos(locateEntity.getX(),locateEntity.getY(),locateEntity.getZ());

            Vec3 trailAt = locateEntity.position().add(new Vec3(0, age/(float)lifetime*2+rot11/8, 0.5+age/(float)lifetime).yRot(rot11+age / 6f * 3.14f));
            if (trailPointer == -1) {
                Vec3 backAt = trailAt;
                for (int i = 0; i < trailPositions.length; i++) {
                    trailPositions[i] = backAt;
                }
            }
            if (++this.trailPointer == this.trailPositions.length) {
                this.trailPointer = 0;
            }
            this.trailPositions[this.trailPointer] = trailAt;
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.getPosition();
        if (trailPointer != -1) {
            float f = (float) (Mth.lerp((double) partialTick, this.xo, this.x) );
            float f1 = (float) (Mth.lerp((double) partialTick, this.yo, this.y) );
            float f2 = (float) (Mth.lerp((double) partialTick, this.zo, this.z) );
            PoseStack poseStack = new PoseStack();
            poseStack.pushPose();
            poseStack.translate(f- vec3.x(),f1- vec3.y(),f2- vec3.z());
            renderTrail(partialTick, poseStack,0.15f,f,f1,f2);
            poseStack.popPose();
        }
    }

    private void renderTrail(float partialTicks, PoseStack poseStack, float trailHeight,float x1,float y1,float z1) {
        int samples = 0;
        int len = 8;
        int sampleSize = age >= lifetime -len?lifetime - age: len;
        Vec3 topAngleVec = new Vec3(0, trailHeight, 0);
        Vec3 bottomAngleVec = new Vec3(0, -trailHeight, 0);
        Vec3 drawFrom = getTrailPosition(0, partialTicks);
        MultiBufferSource.BufferSource bufferIn = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexconsumer = bufferIn.getBuffer(FFRenderTypes.getGlowingEffect((BulletRenderer.TRAIL_TEXTURE)));

        while (samples < sampleSize) {
            Vec3 sample = getTrailPosition(samples + 1, partialTicks);
            float u1 = samples / (float) sampleSize;
            float u2 = u1 + 1 / (float) sampleSize;

            Vec3 draw1 = drawFrom;
            Vec3 draw2 = sample;

            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertexconsumer.vertex(matrix4f, (float) (draw1.x +  bottomAngleVec.x-x1), (float) (draw1.y + bottomAngleVec.y-y1), (float) (draw1.z + bottomAngleVec.z-z1)).color(1f,1f,1f, 0.8f).uv(u1, 1F).overlayCoords(NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) (draw2.x +  bottomAngleVec.x-x1), (float) (draw2.y + bottomAngleVec.y-y1), (float) (draw2.z + bottomAngleVec.z-z1)).color(1f,1f,1f, 0.8f).uv(u2, 1F).overlayCoords(NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) (draw2.x +  topAngleVec.x   -x1), (float) (draw2.y + topAngleVec.y   -y1), (float) (draw2.z + topAngleVec.z   -z1)).color(1f,1f,1f, 0.8f).uv(u2, 0).overlayCoords(NO_OVERLAY) .uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) (draw1.x +  topAngleVec.x   -x1), (float) (draw1.y + topAngleVec.y   -y1), (float) (draw1.z + topAngleVec.z   -z1)).color(1f,1f,1f, 0.8f).uv(u1, 0).overlayCoords(NO_OVERLAY) .uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            samples++;
            drawFrom = sample;
        }
        bufferIn.endBatch();
    }

    public Vec3 getTrailPosition(int pointer, float partialTick) {
        if (locateEntity!=null&&locateEntity.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 15;
        int j = this.trailPointer - pointer - 1 & 15;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.scale(partialTick));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return MMRenderType.PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<CycleWindData> {
        public Factory(SpriteSet sprite){}

        public Particle createParticle(CycleWindData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new CycleWindParticle(worldIn, x, y, z,typeIn.locateEntity);
        }
    }


    public static class CycleWindData implements ParticleOptions {
        public static final Deserializer<CycleWindData> DESERIALIZER = new Deserializer<CycleWindData>() {
            public CycleWindData fromCommand(ParticleType<CycleWindData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                int duration =  reader.readInt();
                return new CycleWindData(duration);
            }

            public CycleWindData fromNetwork(ParticleType<CycleWindData> particleTypeIn, FriendlyByteBuf buffer) {
                return new CycleWindData(buffer.readInt());
            }
        };

        private final int locateEntity;

        public CycleWindData(int locateEntity) {
            this.locateEntity = locateEntity;
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buffer) {
            buffer.writeInt(this.locateEntity);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String writeToString() {
            return String.format(Locale.ROOT, "%s %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.locateEntity);
        }

        @Override
        public ParticleType<CycleWindData> getType() {
            return ParticleHandler.CYCLE_WIND.get();
        }

        @OnlyIn(Dist.CLIENT)
        public int getLocateEntity() {
            return this.locateEntity;
        }

        public static Codec<CycleWindData> CODEC(ParticleType<CycleWindData> particleType) {
            return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                            Codec.INT.fieldOf("locate").forGetter(CycleWindData::getLocateEntity)
                    ).apply(codecBuilder, CycleWindData::new)
            );
        }
    }
}
