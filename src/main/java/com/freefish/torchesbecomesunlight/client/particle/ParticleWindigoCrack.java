package com.freefish.torchesbecomesunlight.client.particle;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.render.MMRenderType;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Locale;

public class ParticleWindigoCrack extends TextureSheetParticle {
    private int swirlTick;
    private final float spread;
    boolean swirls;

    public ParticleWindigoCrack(ClientLevel world, double x, double y, double z, double vX, double vY, double vZ, double duration, boolean swirls) {
        super(world, x, y, z);
        setSize(1, 1);
        xd = vX;
        yd = vY;
        zd = vZ;
        lifetime = (int) duration;
        swirlTick = random.nextInt(120);
        spread = random.nextFloat();
        this.swirls = swirls;
    }

    @Override
    protected float getU1() {
        return super.getU1() - (super.getU1() - super.getU0())/8f;
    }

    @Override
    protected float getV1() {
        return super.getV1() - (super.getV1() - super.getV0())/8f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return MMRenderType.PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH;
    }

    @Override
    public void tick() {
        super.tick();

        if (swirls) {
            Vector3f motionVec = new Vector3f((float)xd, (float)yd, (float)zd);
            motionVec.normalize();
            float yaw = (float) Math.atan2(motionVec.x(), motionVec.z());
            float pitch = (float) Math.atan2(motionVec.y(), 1);
            float swirlRadius = 4f * (age / (float) lifetime) * spread;
            Quaternionf quatSpin = new Quaternionf(new AxisAngle4f(swirlTick * 0.2f, motionVec));
            Quaternionf quatOrient = MathUtils.quatFromRotationXYZ(pitch, yaw, 0, false);
            Vector3f vec = new Vector3f(swirlRadius, 0, 0);
            quatOrient.transform(vec);
            quatSpin.transform(vec);
            x += vec.x();
            y += vec.y();
            z += vec.z();
        }

        if (age >= lifetime) {
            remove();
        }
        age++;
        swirlTick++;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        float var = (age + partialTicks)/(float)lifetime;
        alpha = (float) (1 - Math.exp(10 * (var - 1)) - Math.pow(2000, -var));
        if (alpha < 0.01) alpha = 0.01f;
        super.render(buffer, renderInfo, partialTicks);
    }

    @OnlyIn(Dist.CLIENT)
    public static final class WindigoCrackFactory implements ParticleProvider<WindigoCrackData> {
        private final SpriteSet spriteSet;

        public WindigoCrackFactory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle createParticle(ParticleWindigoCrack.WindigoCrackData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleWindigoCrack particle = new ParticleWindigoCrack(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getDuration(), typeIn.getSwirls());
            particle.pickSprite(spriteSet);
            return particle;
        }
    }

    public static class WindigoCrackData implements ParticleOptions {
        public static final ParticleOptions.Deserializer<ParticleWindigoCrack.WindigoCrackData> DESERIALIZER = new ParticleOptions.Deserializer<ParticleWindigoCrack.WindigoCrackData>() {
            public ParticleWindigoCrack.WindigoCrackData fromCommand(ParticleType<WindigoCrackData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                float duration = (float) reader.readDouble();
                reader.expect(' ');
                boolean swirls = reader.readBoolean();
                return new ParticleWindigoCrack.WindigoCrackData(duration, swirls);
            }

            public ParticleWindigoCrack.WindigoCrackData fromNetwork(ParticleType<ParticleWindigoCrack.WindigoCrackData> particleTypeIn, FriendlyByteBuf buffer) {
                return new ParticleWindigoCrack.WindigoCrackData(buffer.readFloat(), buffer.readBoolean());
            }
        };

        private final float duration;
        private final boolean swirls;

        public WindigoCrackData(float duration, boolean spins) {
            this.duration = duration;
            this.swirls = spins;
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buffer) {
            buffer.writeFloat(this.duration);
            buffer.writeBoolean(this.swirls);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %b", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.duration, this.swirls);
        }

        @Override
        public ParticleType<ParticleWindigoCrack.WindigoCrackData> getType() {
            return ParticleHandler.WINDIGO_CRACK.get();
        }

        @OnlyIn(Dist.CLIENT)
        public float getDuration() {
            return this.duration;
        }

        @OnlyIn(Dist.CLIENT)
        public boolean getSwirls() {
            return this.swirls;
        }

        public static Codec<WindigoCrackData> CODEC(ParticleType<WindigoCrackData> particleType) {
            return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                            Codec.FLOAT.fieldOf("duration").forGetter(WindigoCrackData::getDuration),
                            Codec.BOOL.fieldOf("swirls").forGetter(WindigoCrackData::getSwirls)
                    ).apply(codecBuilder, WindigoCrackData::new)
            );
        }
    }
}
