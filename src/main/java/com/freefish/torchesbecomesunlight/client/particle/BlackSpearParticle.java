package com.freefish.torchesbecomesunlight.client.particle;

import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Locale;

public class BlackSpearParticle extends Particle {
    private static ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/darkspear.png");
    private boolean isIn;
    private float rotX;
    private float rotY;
    public BlackSpearParticle(ClientLevel world, double x, double y, double z, float pitch, float yaw,int time,boolean isline) {
        super(world, x, y, z);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.lifetime = time;
        this.gravity = 0.0F;
        this.isIn = isline;
        this.rotX = pitch;
        this.rotY = yaw;
    }

    public void tick() {
        super.tick();
    }


    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp((double) partialTick, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTick, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTick, this.zo, this.z) - vec3.z());
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer portalStatic = multibuffersource$buffersource.getBuffer(FFRenderTypes.getGlowingEffect(TEXTURE));
        PoseStack posestack = new PoseStack();
        PoseStack.Pose posestack$pose = posestack.last();
        Matrix3f matrix3f = posestack$pose.normal();

        Quaternionf quatX = MathUtils.quatFromRotationXYZ(0, rotX, 0, false);
        Quaternionf quatY = MathUtils.quatFromRotationXYZ(rotY, 0, 0, false);

        float f8 = MathUtils.easeInQuint(((float)age+partialTick)/(lifetime+1));
        Vector3f[] avector3f;
        if(isIn)
            avector3f = new Vector3f[]{new Vector3f(0, -1.0F, 0), new Vector3f(0, 1.0F, 0), new Vector3f(f8*2, 1.0F, 0), new Vector3f(f8*2, -1.0F, 0)};
        else
            avector3f = new Vector3f[]{new Vector3f(f8*2-2, -1.0F, 0), new Vector3f(f8*2-2, 1.0F, 0), new Vector3f(0, 1.0F, 0), new Vector3f(0, -1.0F, 0)};
        float f4 = 2F;
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];

            vector3f.mul(f4);
            vector3f.rotate(quatY);
            vector3f.rotate(quatX);

            vector3f.add(f, f1, f2);
        }
        float f7 = 0;
        float f5 = 0;
        float f6 = 1;
        int j = 240;
        if(isIn) {
            portalStatic.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f8, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            portalStatic.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f8, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            portalStatic.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f7, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            portalStatic.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f7, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        }
        else {
            portalStatic.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f7, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            portalStatic.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f7, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            portalStatic.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(1-f8, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            portalStatic.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(1-f8, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        }

        multibuffersource$buffersource.endBatch();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<BlackSpearData> {
        public Factory(SpriteSet sprite){}

        public Particle createParticle(BlackSpearData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new BlackSpearParticle(worldIn, x, y, z, typeIn.rotX, typeIn.rotY,typeIn.duration,typeIn.swirls);
        }
    }


    public static class BlackSpearData implements ParticleOptions {
        public static final ParticleOptions.Deserializer<BlackSpearParticle.BlackSpearData> DESERIALIZER = new ParticleOptions.Deserializer<BlackSpearParticle.BlackSpearData>() {
            public BlackSpearParticle.BlackSpearData fromCommand(ParticleType<BlackSpearParticle.BlackSpearData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                int duration =  reader.readInt();
                reader.expect(' ');
                boolean swirls = reader.readBoolean();
                reader.expect(' ');
                float rotX = reader.readFloat();
                reader.expect(' ');
                float rotY = reader.readFloat();
                return new BlackSpearParticle.BlackSpearData(duration, swirls,rotX,rotY);
            }

            public BlackSpearParticle.BlackSpearData fromNetwork(ParticleType<BlackSpearParticle.BlackSpearData> particleTypeIn, FriendlyByteBuf buffer) {
                return new BlackSpearParticle.BlackSpearData(buffer.readInt(), buffer.readBoolean(),buffer.readFloat(),buffer.readFloat());
            }
        };

        private final int duration;
        private final boolean swirls;
        private final float rotX;
        private final float rotY;

        public BlackSpearData(int duration, boolean isline,float rotX,float rotY) {
            this.duration = duration;
            this.swirls = isline;
            this.rotX = rotX;
            this.rotY = rotY;
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buffer) {
            buffer.writeInt(this.duration);
            buffer.writeBoolean(this.swirls);
            buffer.writeFloat(this.rotX);
            buffer.writeFloat(this.rotY);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %b %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.duration, this.swirls,this.rotX,this.rotY);
        }

        @Override
        public ParticleType<BlackSpearParticle.BlackSpearData> getType() {
            return ParticleHandler.BLACK_SPEAR.get();
        }

        @OnlyIn(Dist.CLIENT)
        public int getDuration() {
            return this.duration;
        }

        @OnlyIn(Dist.CLIENT)
        public boolean getSwirls() {
            return this.swirls;
        }

        @OnlyIn(Dist.CLIENT)
        public float getRotX() {
            return this.rotX;
        }

        @OnlyIn(Dist.CLIENT)
        public float getRotY() {
            return this.rotY;
        }

        public static Codec<BlackSpearParticle.BlackSpearData> CODEC(ParticleType<BlackSpearParticle.BlackSpearData> particleType) {
            return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                            Codec.INT.fieldOf("duration").forGetter(BlackSpearParticle.BlackSpearData::getDuration),
                            Codec.BOOL.fieldOf("swirls").forGetter(BlackSpearParticle.BlackSpearData::getSwirls),
                    Codec.FLOAT.fieldOf("rotX").forGetter(BlackSpearParticle.BlackSpearData::getRotX),
                    Codec.FLOAT.fieldOf("rotY").forGetter(BlackSpearParticle.BlackSpearData::getRotY)
                    ).apply(codecBuilder, BlackSpearData::new)
            );
        }
    }
}
