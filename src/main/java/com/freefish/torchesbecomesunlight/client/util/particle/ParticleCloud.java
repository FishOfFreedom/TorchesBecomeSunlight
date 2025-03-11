package com.freefish.torchesbecomesunlight.client.util.particle;

import com.freefish.torchesbecomesunlight.client.util.render.MMRenderType;
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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Locale;

/**Be from https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/client/particle/ParticleCloud.java
 * @author bobmowzie
 */
public class ParticleCloud extends TextureSheetParticle {
    private final float scale;
    private final EnumCloudBehavior behavior;
    private final float airDrag;
    private float offset;
    private float finalGray = 0.4f;

    public enum EnumCloudBehavior {
        SHRINK,
        GROW,
        CONSTANT,
        FIRE,
        LEN
    }

    public ParticleCloud(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, double r, double g, double b, double scale, int duration, EnumCloudBehavior behavior, double airDrag) {
        super(world, x, y, z);
        this.scale = (float) scale * 0.5f * 0.1f;
        lifetime = duration;
        xd = vx * 0.5;
        yd = vy * 0.5;
        zd = vz * 0.5;
        this.behavior = behavior;
        if(behavior == EnumCloudBehavior.FIRE){
            offset = (float) r;
            finalGray = 0.3f + world.random.nextFloat()*0.2f;
        }
        setColor((float) r,(float) g,(float) b);
        roll = oRoll = (float) (random.nextInt(4) * Math.PI/2);
        this.airDrag = (float) airDrag;
        this.friction = 0.9f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return MMRenderType.PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH;
    }

    @Override
    public void tick() {
        super.tick();
        xd *= airDrag;
        yd *= airDrag;
        zd *= airDrag;
        if(age>lifetime-20){
            xd = 0.06;
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {

        float var = (age + partialTicks)/(float)lifetime;
        if(behavior!=EnumCloudBehavior.FIRE)
            alpha = 0.4f * ((float) (1 - Math.exp(5 * (var - 1)) - Math.pow(2000, -var)));
        else
            alpha = 1 - var* var* var;
        if (alpha < 0.01) alpha = 0.01f;

        if(behavior == EnumCloudBehavior.FIRE){
            if(age<40) {
                float scale = MathUtils.easeOutCubic(Math.min(1, (age + partialTicks) / 40 * offset));
                setColor(Mth.lerp((MathUtils.easeInExpo(scale)), 1f, finalGray), Mth.lerp(scale, 0.86f, finalGray), Mth.lerp(scale, 0.12f, finalGray));
            }
        }

        if (behavior == EnumCloudBehavior.SHRINK||behavior == EnumCloudBehavior.FIRE) this.quadSize = scale * ((1 - 0.7f * var) + 0.3f);
        else if (behavior == EnumCloudBehavior.GROW) this.quadSize = scale * ((0.7f * var) + 0.3f);
        else this.quadSize = scale;

        Vec3 vec3 = renderInfo.getPosition();
        float f = (float)(Mth.lerp((double)partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp((double)partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp((double)partialTicks, this.zo, this.z) - vec3.z());
        Quaternionf quaternionf;
        if (this.roll == 0.0F) {
            quaternionf = renderInfo.rotation();
        } else {
            quaternionf = new Quaternionf(renderInfo.rotation());
            quaternionf.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
        }

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f3 = this.getQuadSize(partialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(partialTicks);
        buffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    public static final class CloudFactory implements ParticleProvider<CloudData> {
        private final SpriteSet spriteSet;

        public CloudFactory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle createParticle(CloudData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleCloud particleCloud = new ParticleCloud(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getR(), typeIn.getG(), typeIn.getB(), typeIn.getScale(), typeIn.getDuration(), typeIn.getBehavior(), typeIn.getAirDrag());
            particleCloud.setSpriteFromAge(spriteSet);
            return particleCloud;
        }
    }

    public static class CloudData implements ParticleOptions {
        public static final Deserializer<CloudData> DESERIALIZER = new Deserializer<CloudData>() {
            public CloudData fromCommand(ParticleType<CloudData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                float r = (float) reader.readDouble();
                reader.expect(' ');
                float g = (float) reader.readDouble();
                reader.expect(' ');
                float b = (float) reader.readDouble();
                reader.expect(' ');
                float scale = (float) reader.readDouble();
                reader.expect(' ');
                int duration = reader.readInt();
                reader.expect(' ');
                float airDrag = (float) reader.readDouble();
                return new CloudData(particleTypeIn, r, g, b, scale, duration, EnumCloudBehavior.CONSTANT, airDrag);
            }

            public CloudData fromNetwork(ParticleType<CloudData> particleTypeIn, FriendlyByteBuf buffer) {
                return new CloudData(particleTypeIn, buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), EnumCloudBehavior.CONSTANT, buffer.readFloat());
            }
        };

        private final ParticleType<CloudData> type;

        private final float r;
        private final float g;
        private final float b;
        private final float scale;
        private final int duration;
        private final EnumCloudBehavior behavior;
        private final float airDrag;

        public CloudData(ParticleType<CloudData> type, float r, float g, float b, float scale, int duration, EnumCloudBehavior behavior, float airDrag) {
            this.type = type;
            this.r = r;
            this.g = g;
            this.b = b;
            this.scale = scale;
            this.behavior = behavior;
            this.airDrag = airDrag;
            this.duration = duration;
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buffer) {
            buffer.writeFloat(this.r);
            buffer.writeFloat(this.g);
            buffer.writeFloat(this.b);
            buffer.writeFloat(this.scale);
            buffer.writeInt(this.duration);
            buffer.writeFloat(this.airDrag);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.r, this.g, this.b, this.scale, this.duration, this.airDrag);
        }

        @Override
        public ParticleType<CloudData> getType() {
            return type;
        }

        @OnlyIn(Dist.CLIENT)
        public float getR() {
            return this.r;
        }

        @OnlyIn(Dist.CLIENT)
        public float getG() {
            return this.g;
        }

        @OnlyIn(Dist.CLIENT)
        public float getB() {
            return this.b;
        }

        @OnlyIn(Dist.CLIENT)
        public float getScale() {
            return this.scale;
        }

        @OnlyIn(Dist.CLIENT)
        public EnumCloudBehavior getBehavior() {
            return this.behavior;
        }

        @OnlyIn(Dist.CLIENT)
        public int getDuration() {
            return this.duration;
        }

        @OnlyIn(Dist.CLIENT)
        public float getAirDrag() {
            return this.airDrag;
        }

        public static Codec<CloudData> CODEC(ParticleType<CloudData> particleType) {
            return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                            Codec.FLOAT.fieldOf("r").forGetter(CloudData::getR),
                            Codec.FLOAT.fieldOf("g").forGetter(CloudData::getG),
                            Codec.FLOAT.fieldOf("b").forGetter(CloudData::getB),
                            Codec.FLOAT.fieldOf("scale").forGetter(CloudData::getScale),
                            Codec.STRING.fieldOf("behavior").forGetter((cloudData) -> cloudData.getBehavior().toString()),
                            Codec.INT.fieldOf("duration").forGetter(CloudData::getDuration),
                            Codec.FLOAT.fieldOf("airdrag").forGetter(CloudData::getAirDrag)
                    ).apply(codecBuilder, (r, g, b, scale, behavior, duration, airdrag) ->
                            new CloudData(particleType, r, g, b, scale, duration, EnumCloudBehavior.valueOf(behavior), airdrag))
            );
        }
    }
}