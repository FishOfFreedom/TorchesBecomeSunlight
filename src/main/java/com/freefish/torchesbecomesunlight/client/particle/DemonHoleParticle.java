package com.freefish.torchesbecomesunlight.client.particle;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
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

public class DemonHoleParticle extends Particle {
    private float scale;
    private float rotX;
    private float rotY;
    private final int fadeTime = 20;

    private static ResourceLocation HOLE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/demonhole.png");

    public DemonHoleParticle(ClientLevel world, double x, double y, double z, int time, float scale, float pitch, float yaw) {
        super(world, x, y, z);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.lifetime = time;
        this.gravity = 0.0F;
        this.scale = scale;
        this.rotX = pitch;
        this.rotY = yaw;
    }

    public void tick() {
        super.tick();
    }


    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.getPosition();
        float fadeScale = 1;
        float fadecolor = 1;
        if(age<fadeTime)
            fadeScale = MathUtils.easeOutBack((age+partialTick)/20);
        else if(age>=(lifetime-fadeTime)){
            fadeScale = (age+partialTick - (lifetime-fadeTime))/210f + 1;
            fadecolor = (-(age+partialTick-(lifetime+1)))/21f;
        }

        Quaternionf quatX = com.bobmowzie.mowziesmobs.client.model.tools.MathUtils.quatFromRotationXYZ(0, rotX, 0, false);
        Quaternionf quatY = com.bobmowzie.mowziesmobs.client.model.tools.MathUtils.quatFromRotationXYZ(rotY, 0, 0, false);

        float f = (float) (Mth.lerp((double) partialTick, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTick, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTick, this.zo, this.z) - vec3.z());
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer portalStatic = multibuffersource$buffersource.getBuffer(ForgeRenderTypes.getUnlitTranslucent(HOLE));
        PoseStack posestack = new PoseStack();
        PoseStack.Pose posestack$pose = posestack.last();
        Matrix3f matrix3f = posestack$pose.normal();

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1, -1.0F, 0), new Vector3f(-1, 1.0F, 0), new Vector3f(1, 1.0F, 0), new Vector3f(1, -1.0F, 0)};
        float f4 = 0.5F;
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotateZ(-(age+partialTick)/5);

            vector3f.rotate(quatY);
            vector3f.rotate(quatX);
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
    public static class Factory implements ParticleProvider<DemonHoleData> {
        public Factory(SpriteSet sprite){}

        public Particle createParticle(DemonHoleData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new DemonHoleParticle(worldIn, x, y, z,typeIn.duration,typeIn.scale,typeIn.rotX,typeIn.rotY);
        }
    }


    public static class DemonHoleData implements ParticleOptions {
        public static final Deserializer<DemonHoleParticle.DemonHoleData> DESERIALIZER = new Deserializer<DemonHoleParticle.DemonHoleData>() {
            public DemonHoleParticle.DemonHoleData fromCommand(ParticleType<DemonHoleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                int duration =  reader.readInt();
                reader.expect(' ');
                float scale = reader.readFloat();
                reader.expect(' ');
                float rotX = reader.readFloat();
                reader.expect(' ');
                float rotY = reader.readFloat();
                return new DemonHoleParticle.DemonHoleData(duration,scale,rotX,rotY);
            }

            public DemonHoleParticle.DemonHoleData fromNetwork(ParticleType<DemonHoleParticle.DemonHoleData> particleTypeIn, FriendlyByteBuf buffer) {
                return new DemonHoleParticle.DemonHoleData(buffer.readInt(),buffer.readFloat(),buffer.readFloat(),buffer.readFloat());
            }
        };

        private final int duration;
        private final float scale;
        private final float rotX;
        private final float rotY;

        public DemonHoleData(int duration,float scale,float rotX,float rotY) {
            this.duration = duration;
            this.scale = scale;
            this.rotX = rotX;
            this.rotY = rotY;
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buffer) {
            buffer.writeInt(this.duration);
            buffer.writeFloat(this.scale);
            buffer.writeFloat(this.rotX);
            buffer.writeFloat(this.rotY);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.duration,this.scale,this.rotX,this.rotY);
        }

        @Override
        public ParticleType<DemonHoleParticle.DemonHoleData> getType() {
            return ParticleHandler.DEMONHOLE.get();
        }

        @OnlyIn(Dist.CLIENT)
        public int getDuration() {
            return this.duration;
        }

        @OnlyIn(Dist.CLIENT)
        public float getScale() {
            return this.scale;
        }

        @OnlyIn(Dist.CLIENT)
        public float getRotX() {
            return this.rotX;
        }
        @OnlyIn(Dist.CLIENT)
        public float getRotY() {
            return this.rotY;
        }
        public static Codec<DemonHoleData> CODEC(ParticleType<DemonHoleParticle.DemonHoleData> particleType) {
            return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                            Codec.INT.fieldOf("duration").forGetter(DemonHoleParticle.DemonHoleData::getDuration),
                            Codec.FLOAT.fieldOf("scale").forGetter(DemonHoleParticle.DemonHoleData::getScale),
                            Codec.FLOAT.fieldOf("rotX").forGetter(DemonHoleParticle.DemonHoleData::getRotX),
                            Codec.FLOAT.fieldOf("rotY").forGetter(DemonHoleParticle.DemonHoleData::getRotY)
                    ).apply(codecBuilder, DemonHoleData::new)
            );
        }
    }
}
