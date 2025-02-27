package com.freefish.torchesbecomesunlight.client.particle;

import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Math;
import org.joml.Vector3f;

import java.util.Locale;

public class BlackFlatParticle extends TextureSheetParticle {
    private Vec3 target;
    private float scale;

    public BlackFlatParticle(ClientLevel level, double x, double y, double z, double toX, double toY, double toZ,int time,float scale,SpriteSet sprite) {
        super(level, x, y, z, 0, 0, 0);
        this.friction = 0.96F;
        this.gravity = 0;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yd =  0.0F;
        this.xd =  0.0F;
        this.zd =  0.0F;
        this.lifetime = time;
        this.hasPhysics = false;
        this.scale = scale;
        this.target = new Vec3(toX, toY, toZ);
        this.setBoundingBox(new AABB(x - 1, y, z - 1, x + 1, y + 10, z + 1));
        setSprite(sprite.get(lifetime,lifetime));
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        Vec3 vec3 = renderInfo.getPosition();
        float scale1 = 0.1f;
        if(age>=lifetime-76&&age<lifetime-66){
            scale1 = Math.lerp(0.1f,scale,MathUtils.easeOutExpo((age-(lifetime-76)+partialTicks)/10f));
        }
        else if(age>=lifetime-66&&age<lifetime-20){
            scale1 = scale;
        }
        else if(age>=lifetime-20){
            scale1 = Math.lerp(0,scale,MathUtils.easeOutExpo( 1 - (age-(lifetime-20)+partialTicks)/21f));
        }

        Vec3 pos = new Vec3(x,y,z);
        Vec3 line = target.subtract(pos);
        Vec3 cross = line.cross(pos.subtract(vec3)).normalize().scale(scale1);

        Vector3f[] avector3f = new Vector3f[]{new Vector3f((float) cross.x, (float) cross.y, (float) cross.z)
                , new Vector3f((float) -cross.x, (float) -cross.y, (float) -cross.z), new Vector3f((float) -cross.x, (float) -cross.y, (float) -cross.z), new Vector3f((float) cross.x, (float) cross.y, (float) cross.z)};
        float f3 = this.getQuadSize(partialTicks);

        for(int i = 0; i < 2; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.mul(f3);
            vector3f.add((float) (x-vec3.x), (float) (y-vec3.y), (float) (z-vec3.z));
        }
        for(int i = 2; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.mul(f3);
            vector3f.add((float) (target.x-vec3.x), (float) (target.y-vec3.y), (float) (target.z-vec3.z));
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(partialTicks);

        buffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f7, f5).color(0, 0, 0, this.alpha).uv2(j).endVertex();
        buffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f7, f4).color(0, 0, 0, this.alpha).uv2(j).endVertex();
        buffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f6, f4).color(0, 0, 0, this.alpha).uv2(j).endVertex();
        buffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f6, f5).color(0, 0, 0, this.alpha).uv2(j).endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<BlackFlatData> {
        private final SpriteSet sprite;

        public Factory(SpriteSet p_107868_) {
            this.sprite = p_107868_;
        }

        public Particle createParticle(BlackFlatData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            BlackFlatParticle spellparticle = new BlackFlatParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed,typeIn.duration,typeIn.scale,sprite);
            return spellparticle;
        }
    }

    public static class BlackFlatData implements ParticleOptions {
        public static final Deserializer<BlackFlatParticle.BlackFlatData> DESERIALIZER = new Deserializer<BlackFlatParticle.BlackFlatData>() {
            public BlackFlatParticle.BlackFlatData fromCommand(ParticleType<BlackFlatParticle.BlackFlatData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                int duration =  reader.readInt();
                reader.expect(' ');
                float scale = reader.readFloat();
                return new BlackFlatParticle.BlackFlatData(duration,scale);
            }

            public BlackFlatParticle.BlackFlatData fromNetwork(ParticleType<BlackFlatParticle.BlackFlatData> particleTypeIn, FriendlyByteBuf buffer) {
                return new BlackFlatParticle.BlackFlatData(buffer.readInt(),buffer.readFloat());
            }
        };

        private final int duration;
        private final float scale;

        public BlackFlatData(int duration,float scale) {
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
            return String.format(Locale.ROOT, "%s %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.duration,this.scale);
        }

        @Override
        public ParticleType<BlackFlatParticle.BlackFlatData> getType() {
            return ParticleHandler.BLACK_FLAT.get();
        }

        @OnlyIn(Dist.CLIENT)
        public int getDuration() {
            return this.duration;
        }

        @OnlyIn(Dist.CLIENT)
        public float getScale() {
            return this.scale;
        }
        public static Codec<BlackFlatParticle.BlackFlatData> CODEC(ParticleType<BlackFlatParticle.BlackFlatData> particleType) {
            return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                            Codec.INT.fieldOf("duration").forGetter(BlackFlatParticle.BlackFlatData::getDuration),
                            Codec.FLOAT.fieldOf("scale").forGetter(BlackFlatParticle.BlackFlatData::getScale)
                    ).apply(codecBuilder, BlackFlatParticle.BlackFlatData::new)
            );
        }
    }
}
