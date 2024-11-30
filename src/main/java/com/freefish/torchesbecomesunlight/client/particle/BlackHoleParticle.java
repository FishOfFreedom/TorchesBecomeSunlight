package com.freefish.torchesbecomesunlight.client.particle;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
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
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Locale;

public class BlackHoleParticle extends Particle {
    private float scale;
    private final int fadeTime = 12;

    public BlackHoleParticle(ClientLevel world, double x, double y, double z, int time, float scale) {
        super(world, x, y, z);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.lifetime = time;
        this.gravity = 0.0F;
        this.scale = scale;
    }

    public void tick() {
        super.tick();
    }


    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.getPosition();
        float fadeScale = 1;
        float fadecolor = 1;

        Quaternionf quaternion;
        if (this.roll == 0.0F) {
            quaternion = camera.rotation();
        } else {
            quaternion = new Quaternionf(camera.rotation());
            float f3 = Mth.lerp(partialTick, this.oRoll, this.roll);
            quaternion.mul(Axis.ZP.rotation(f3));
        }

        if(age<fadeTime)
            fadeScale = MathUtils.easeOutBack((age+partialTick)/12);
        else if(age>=(lifetime-fadeTime)){
            fadeScale = (age+partialTick - (lifetime-fadeTime))/130f + 1;
            fadecolor = (-(age+partialTick-(lifetime+1)))/13f;
        }

        float f = (float) (Mth.lerp((double) partialTick, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTick, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTick, this.zo, this.z) - vec3.z());
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer portalStatic = multibuffersource$buffersource.getBuffer(FFRenderTypes.HOLE);
        PoseStack posestack = new PoseStack();
        PoseStack.Pose posestack$pose = posestack.last();
        Matrix3f matrix3f = posestack$pose.normal();

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1, -1.0F, 0), new Vector3f(-1, 1.0F, 0), new Vector3f(1, 1.0F, 0), new Vector3f(1, -1.0F, 0)};
        float f4 = 0.5F;
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotateZ(-(age+partialTick)/25);
            vector3f.rotate(quaternion);
            vector3f.mul(f4*scale*fadeScale);
            vector3f.add(f, f1, f2);
        }

        float f7 = 0;
        float f8 = 1;
        float f5 = 0;
        float f6 = 1;
        int j = 240;
        portalStatic.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).color(this.rCol, this.gCol, this.bCol, fadecolor).uv(f8, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).color(this.rCol, this.gCol, this.bCol, fadecolor).uv(f8, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).color(this.rCol, this.gCol, this.bCol, fadecolor).uv(f7, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).color(this.rCol, this.gCol, this.bCol, fadecolor).uv(f7, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

        multibuffersource$buffersource.endBatch();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<BlackHoleData> {
        public Factory(SpriteSet sprite){}

        public Particle createParticle(BlackHoleData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new BlackHoleParticle(worldIn, x, y, z,typeIn.duration,typeIn.scale);
        }
    }


    public static class BlackHoleData implements ParticleOptions {
        public static final Deserializer<BlackHoleParticle.BlackHoleData> DESERIALIZER = new Deserializer<BlackHoleParticle.BlackHoleData>() {
            public BlackHoleParticle.BlackHoleData fromCommand(ParticleType<BlackHoleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                int duration =  reader.readInt();
                reader.expect(' ');
                float scale = reader.readFloat();
                return new BlackHoleParticle.BlackHoleData(duration,scale);
            }

            public BlackHoleParticle.BlackHoleData fromNetwork(ParticleType<BlackHoleParticle.BlackHoleData> particleTypeIn, FriendlyByteBuf buffer) {
                return new BlackHoleParticle.BlackHoleData(buffer.readInt(),buffer.readFloat());
            }
        };

        private final int duration;
        private final float scale;

        public BlackHoleData(int duration,float scale) {
            this.duration = duration;
            this.scale = scale;
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buffer) {
            buffer.writeInt(this.duration);
            buffer.writeFloat(this.scale);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.duration,this.scale);
        }

        @Override
        public ParticleType<BlackHoleParticle.BlackHoleData> getType() {
            return ParticleHandler.BLACKHOLE.get();
        }

        @OnlyIn(Dist.CLIENT)
        public int getDuration() {
            return this.duration;
        }

        @OnlyIn(Dist.CLIENT)
        public float getScale() {
            return this.scale;
        }
        public static Codec<BlackHoleData> CODEC(ParticleType<BlackHoleParticle.BlackHoleData> particleType) {
            return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                            Codec.INT.fieldOf("duration").forGetter(BlackHoleParticle.BlackHoleData::getDuration),
                            Codec.FLOAT.fieldOf("scale").forGetter(BlackHoleParticle.BlackHoleData::getScale)
                    ).apply(codecBuilder, BlackHoleData::new)
            );
        }
    }
}
