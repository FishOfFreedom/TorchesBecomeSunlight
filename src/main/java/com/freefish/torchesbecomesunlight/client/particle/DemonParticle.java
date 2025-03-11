package com.freefish.torchesbecomesunlight.client.particle;

import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Locale;

public class DemonParticle extends Particle {
    private float scale;
    private float rotX;
    private float rotY;

    public DemonParticle(ClientLevel world, double x, double y, double z, int time, float scale,float pitch,float yaw) {
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
        float yScale = 1;
        int grow = lifetime/20;
        if(age<=grow){
            yScale = MathUtils.fade((age+partialTick)/grow);
        }
        else if(age>=lifetime - grow){
            yScale = MathUtils.fade((lifetime-(age+partialTick))/grow);
        }
        Quaternionf quatX = MathUtils.quatFromRotationXYZ(0, rotX, 0, false);
        Quaternionf quatY = MathUtils.quatFromRotationXYZ(rotY, 0, 0, false);

        float f = (float) (Mth.lerp((double) partialTick, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTick, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTick, this.zo, this.z) - vec3.z());
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer portalStatic = multibuffersource$buffersource.getBuffer(FFRenderTypes.DEMON);
        PoseStack posestack = new PoseStack();
        PoseStack.Pose posestack$pose = posestack.last();
        Matrix3f matrix3f = posestack$pose.normal();

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(0, 0.5F*yScale, -0.05F), new Vector3f(1.0F, 0, -0.05F), new Vector3f(0, -0.5F*yScale, -0.05F), new Vector3f(-1f, 0, -0.05F)};
        float f4 = 0.5F;
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];

            vector3f.rotate(quatY);
            vector3f.rotate(quatX);
            vector3f.mul(f4*scale);
            vector3f.add(f, f1, f2);
        }

        float f7 = 0;
        float f8 = 1;
        float f5 = 0;
        float f6 = 1;
        int j = 240;
        portalStatic.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f8, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f8, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f7, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f7, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

        multibuffersource$buffersource.endBatch();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<DemonData> {
        public Factory(SpriteSet sprite){}

        public Particle createParticle(DemonData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new DemonParticle(worldIn, x, y, z,typeIn.duration,typeIn.scale,typeIn.rotX,typeIn.rotY);
        }
    }


    public static class DemonData implements ParticleOptions {
        public static final Deserializer<DemonData> DESERIALIZER = new Deserializer<DemonData>() {
            public DemonData fromCommand(ParticleType<DemonData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                int duration =  reader.readInt();
                reader.expect(' ');
                float scale = reader.readFloat();
                reader.expect(' ');
                float rotX = reader.readFloat();
                reader.expect(' ');
                float rotY = reader.readFloat();
                return new DemonData(duration,scale,rotX,rotY);
            }

            public DemonData fromNetwork(ParticleType<DemonData> particleTypeIn, FriendlyByteBuf buffer) {
                return new DemonData(buffer.readInt(),buffer.readFloat(),buffer.readFloat(),buffer.readFloat());
            }
        };

        private final int duration;
        private final float scale;
        private final float rotX;
        private final float rotY;

        public DemonData(int duration,float scale,float rotX,float rotY) {
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
        public ParticleType<DemonData> getType() {
            return ParticleHandler.DEMON.get();
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
        public static Codec<DemonData> CODEC(ParticleType<DemonData> particleType) {
            return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                            Codec.INT.fieldOf("duration").forGetter(DemonData::getDuration),
                            Codec.FLOAT.fieldOf("scale").forGetter(DemonData::getScale),
                            Codec.FLOAT.fieldOf("rotX").forGetter(DemonData::getRotX),
                            Codec.FLOAT.fieldOf("rotY").forGetter(DemonData::getRotY)
                    ).apply(codecBuilder, DemonData::new)
            );
        }
    }
}
