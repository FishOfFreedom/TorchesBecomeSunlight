package com.freefish.torchesbecomesunlight.client.particle;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Locale;

public class BladeParticle extends TextureSheetParticle {
    private final SpriteSet sprite;
    private float scale;
    private float rotX;
    private float rotY;
    private float hw;

    public BladeParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd,int time, float scale, float pitch, float yaw,float hw,SpriteSet sprite) {
        super(world, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.sprite = sprite;
        this.lifetime = time;
        this.gravity = 0.0F;
        this.scale = scale;
        this.rotX = pitch;
        this.rotY = yaw;
        this.hw = hw;
        this.friction = 0.7f;
        this.setSpriteFromAge(sprite);
    }

    public void tick() {
        super.tick();
        if(age<=6) {
            this.setSprite(sprite.get((int)(this.lifetime*(age/6f)),lifetime));
        }
    }

    @Override
    public float getQuadSize(float pScaleFactor) {
        return scale;
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        if(age<lifetime&&age>=lifetime-10){
            alpha = 1 - (age-(lifetime-10)+pPartialTicks) /10f;
        }

        Quaternionf quatX = com.bobmowzie.mowziesmobs.client.model.tools.MathUtils.quatFromRotationXYZ(0, rotX, 0, false);
        Quaternionf quatY = com.bobmowzie.mowziesmobs.client.model.tools.MathUtils.quatFromRotationXYZ(rotY, 0, 0, false);

        Vec3 vec3 = pRenderInfo.getPosition();
        float f = (float)(Mth.lerp((double)pPartialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp((double)pPartialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp((double)pPartialTicks, this.zo, this.z) - vec3.z());

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -hw, 0.0F), new Vector3f(-1.0F, hw, 0.0F), new Vector3f(1.0F, hw, 0.0F), new Vector3f(1.0F, -hw, 0.0F)};
        float f3 = this.getQuadSize(pPartialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quatY);
            vector3f.rotate(quatX);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(pPartialTicks);
        pBuffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        pBuffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        pBuffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        pBuffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<BladeData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle createParticle(BladeData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            BladeParticle particle = new BladeParticle(worldIn, x, y, z,xSpeed,ySpeed,zSpeed,typeIn.duration,typeIn.scale,typeIn.rotX,typeIn.rotY,typeIn.hw,spriteSet);
            return particle;
        }
    }


    public static class BladeData implements ParticleOptions {
        public static final Deserializer<BladeParticle.BladeData> DESERIALIZER = new Deserializer<BladeParticle.BladeData>() {
            public BladeParticle.BladeData fromCommand(ParticleType<BladeData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                int duration =  reader.readInt();
                reader.expect(' ');
                float scale = reader.readFloat();
                reader.expect(' ');
                float rotX = reader.readFloat();
                reader.expect(' ');
                float rotY = reader.readFloat();
                reader.expect(' ');
                float hw = reader.readFloat();
                return new BladeParticle.BladeData(duration,scale,rotX,rotY,hw);
            }

            public BladeParticle.BladeData fromNetwork(ParticleType<BladeParticle.BladeData> particleTypeIn, FriendlyByteBuf buffer) {
                return new BladeParticle.BladeData(buffer.readInt(),buffer.readFloat(),buffer.readFloat(),buffer.readFloat(),buffer.readFloat());
            }
        };

        private final int duration;
        private final float scale;
        private final float rotX;
        private final float rotY;
        private final float hw;

        public BladeData(int duration,float scale,float rotX,float rotY,float hw) {
            this.duration = duration;
            this.scale = scale;
            this.rotX = rotX;
            this.rotY = rotY;
            this.hw = hw;
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buffer) {
            buffer.writeInt(this.duration);
            buffer.writeFloat(this.scale);
            buffer.writeFloat(this.rotX);
            buffer.writeFloat(this.rotY);
            buffer.writeFloat(this.hw);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.duration,this.scale,this.rotX,this.rotY,this.hw);
        }

        @Override
        public ParticleType<BladeParticle.BladeData> getType() {
            return ParticleHandler.BLADE.get();
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
        @OnlyIn(Dist.CLIENT)
        public float getHw() {
            return this.hw;
        }
        public static Codec<BladeData> CODEC(ParticleType<BladeParticle.BladeData> particleType) {
            return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                            Codec.INT.fieldOf("duration").forGetter(BladeParticle.BladeData::getDuration),
                            Codec.FLOAT.fieldOf("scale").forGetter(BladeParticle.BladeData::getScale),
                            Codec.FLOAT.fieldOf("rotX").forGetter(BladeParticle.BladeData::getRotX),
                            Codec.FLOAT.fieldOf("rotY").forGetter(BladeParticle.BladeData::getRotY),
                            Codec.FLOAT.fieldOf("hw").forGetter(BladeParticle.BladeData::getHw)
                    ).apply(codecBuilder, BladeData::new)
            );
        }
    }
}
