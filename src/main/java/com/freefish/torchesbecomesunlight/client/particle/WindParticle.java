package com.freefish.torchesbecomesunlight.client.particle;

import com.freefish.torchesbecomesunlight.client.render.model.tools.MathUtils;
import com.freefish.torchesbecomesunlight.client.util.render.MMRenderType;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
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

public class WindParticle extends TextureSheetParticle {
    private final SpriteSet sprite;
    private float scale;
    private float rotX;
    private float rotY;

    public WindParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd,int time, float scale, float pitch, float yaw,SpriteSet sprite) {
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
        this.friction = 0.7f;
        this.setSpriteFromAge(sprite);
    }

    public void tick() {
        super.tick();
        this.setSprite(sprite.get(age,lifetime));
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

        Quaternionf quatX = MathUtils.quatFromRotationXYZ(rotX,0, 0, false);

        Vec3 vec3 = pRenderInfo.getPosition();
        float f = (float) (Mth.lerp((double) pPartialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) pPartialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) pPartialTicks, this.zo, this.z) - vec3.z());

        float f3 = this.getQuadSize(pPartialTicks);
        if(age<20){
            f3 *= com.freefish.torchesbecomesunlight.server.util.MathUtils.easeOutExpo((age+pPartialTicks)/20);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        PoseStack poseStack = new PoseStack();

        renderSphere(f,f1,f2,f3,quatX,poseStack,1,16,8,f6,f7,f4,f5,pPartialTicks,pBuffer);
    }

    public void renderSphere(float f, float f1, float f2, float f3,Quaternionf quatX,PoseStack poseStack, float radius, int lonSegments, int latSegments,
                             float u0, float u1, float v0, float v1, float partialTicks, VertexConsumer buffer) {

        for (int lat = 0; lat < latSegments; lat++) {
            float theta1 = (float) (Math.PI * lat / latSegments);
            float theta2 = (float) (Math.PI * (lat + 1) / latSegments);

            for (int lon = 0; lon < lonSegments; lon++) {
                float phi1 = (float) (2 * Math.PI * lon / lonSegments);
                float phi2 = (float) (2 * Math.PI * (lon + 1) / lonSegments);

                // 当前四边形的四个顶点（球面坐标）
                Vector3f[] quadVertices = new Vector3f[] {
                        sphericalToCartesian(radius, theta1, phi1), // 顶点0
                        sphericalToCartesian(radius, theta1, phi2), // 顶点1
                        sphericalToCartesian(radius, theta2, phi2), // 顶点2
                        sphericalToCartesian(radius, theta2, phi1)  // 顶点3
                };

                // 应用模型变换
                for (Vector3f vert : quadVertices) {
                    vert.rotateY(-(age+partialTicks)/5);
                    vert.rotate(quatX);
                    vert.rotateY(rotY);
                    vert.mul(f3);
                    vert.add(f, f1, f2);
                }

                float[][] quadUVs = new float[][] {
                        {Mth.lerp((float)lon / lonSegments      ,u0,u1), Mth.lerp((float) lat / latSegments      ,v0,v1)}, // 顶点3
                        {Mth.lerp((float)(lon + 1) / lonSegments,u0,u1), Mth.lerp((float) lat / latSegments      ,v0,v1)}, // 顶点0
                        {Mth.lerp((float)(lon + 1) / lonSegments,u0,u1), Mth.lerp((float) (lat + 1) / latSegments,v0,v1)}, // 顶点1
                        {Mth.lerp((float)lon / lonSegments      ,u0,u1), Mth.lerp((float) (lat + 1) / latSegments,v0,v1)} // 顶点2
                };

                int light = getLightColor(partialTicks);
                buffer.vertex(quadVertices[0].x(), quadVertices[0].y(), quadVertices[0].z()).uv(quadUVs[0][0], quadUVs[0][1]).color(1.f,1.f,1.f,alpha).uv2(light).endVertex();
                buffer.vertex(quadVertices[1].x(), quadVertices[1].y(), quadVertices[1].z()).uv(quadUVs[1][0], quadUVs[1][1]).color(1.f,1.f,1.f,alpha).uv2(light).endVertex();
                buffer.vertex(quadVertices[2].x(), quadVertices[2].y(), quadVertices[2].z()).uv(quadUVs[2][0], quadUVs[2][1]).color(1.f,1.f,1.f,alpha).uv2(light).endVertex();
                buffer.vertex(quadVertices[3].x(), quadVertices[3].y(), quadVertices[3].z()).uv(quadUVs[3][0], quadUVs[3][1]).color(1.f,1.f,1.f,alpha).uv2(light).endVertex();
            }
        }
    }

    // 球面坐标转笛卡尔坐标
    private Vector3f sphericalToCartesian(float r, float theta, float phi) {
        float x = r * (float) (Math.sin(theta) * Math.cos(phi));
        float y = r * (float) Math.cos(theta);
        float z = r * (float) (Math.sin(theta) * Math.sin(phi));
        return new Vector3f(x, y, z);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return MMRenderType.PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<WindParticle.WindData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle createParticle(WindParticle.WindData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            WindParticle particle = new WindParticle(worldIn, x, y, z,xSpeed,ySpeed,zSpeed,typeIn.duration,typeIn.scale,typeIn.rotX,typeIn.rotY,spriteSet);
            return particle;
        }
    }


    public static class WindData implements ParticleOptions {
        public static final Deserializer<WindParticle.WindData> DESERIALIZER = new Deserializer<WindParticle.WindData>() {
            public WindParticle.WindData fromCommand(ParticleType<WindParticle.WindData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                int duration =  reader.readInt();
                reader.expect(' ');
                float scale = reader.readFloat();
                reader.expect(' ');
                float rotX = reader.readFloat();
                reader.expect(' ');
                float rotY = reader.readFloat();
                reader.expect(' ');
                return new WindParticle.WindData(duration,scale,rotX,rotY);
            }

            public WindParticle.WindData fromNetwork(ParticleType<WindParticle.WindData> particleTypeIn, FriendlyByteBuf buffer) {
                return new WindParticle.WindData(buffer.readInt(),buffer.readFloat(),buffer.readFloat(),buffer.readFloat());
            }
        };

        private final int duration;
        private final float scale;
        private final float rotX;
        private final float rotY;

        public WindData(int duration,float scale,float rotX,float rotY) {
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
        public ParticleType<WindParticle.WindData> getType() {
            return ParticleHandler.WIND.get();
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
        public static Codec<WindParticle.WindData> CODEC(ParticleType<WindParticle.WindData> particleType) {
            return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                            Codec.INT.fieldOf("duration").forGetter(WindParticle.WindData::getDuration),
                            Codec.FLOAT.fieldOf("scale").forGetter(WindParticle.WindData::getScale),
                            Codec.FLOAT.fieldOf("rotX").forGetter(WindParticle.WindData::getRotX),
                            Codec.FLOAT.fieldOf("rotY").forGetter(WindParticle.WindData::getRotY)
                    ).apply(codecBuilder, WindParticle.WindData::new)
            );
        }
    }
}
