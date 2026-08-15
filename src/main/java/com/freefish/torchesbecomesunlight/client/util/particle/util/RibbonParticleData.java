package com.freefish.torchesbecomesunlight.client.util.particle.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**Be from https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/client/particle/util/RibbonParticleData.java
 * @author bobmowzie
 */
public class RibbonParticleData extends AdvancedParticleData {
    /**
     * 1.21 移除了 ParticleOptions.Deserializer（命令解析改由 codec 承担）。
     * 这里保留原 fromNetwork 的读取逻辑，供 STREAM_CODEC 复用。
     */
    private static RibbonParticleData readFromNetwork(ParticleType<RibbonParticleData> particleTypeIn, RegistryFriendlyByteBuf buffer) {
            double airDrag = buffer.readFloat();
            double red = buffer.readFloat();
            double green = buffer.readFloat();
            double blue = buffer.readFloat();
            double alpha = buffer.readFloat();
            String rotationMode = buffer.readUtf();
            double scale = buffer.readFloat();
            double yaw = buffer.readFloat();
            double pitch = buffer.readFloat();
            double roll = buffer.readFloat();
            boolean emissive = buffer.readBoolean();
            double duration = buffer.readFloat();
            buffer.readFloat();
            int length = buffer.readInt();
            ParticleRotation rotation;
            if (rotationMode.equals("face_camera")) rotation = new ParticleRotation.FaceCamera((float) 0);
            else if (rotationMode.equals("euler")) rotation = new ParticleRotation.EulerAngles((float)yaw, (float)pitch, (float)roll);
            else rotation = new ParticleRotation.OrientVector(new Vec3(yaw, pitch, roll));
            return new RibbonParticleData(particleTypeIn, rotation, scale, red, green, blue, alpha, airDrag, duration, emissive, length);
        }


    private final int length;

    public RibbonParticleData(ParticleType<? extends RibbonParticleData> type, ParticleRotation rotation, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, int length) {
        this(type, rotation, scale, r, g, b, a, drag, duration, emissive, length, new ParticleComponent[]{});
    }

    public RibbonParticleData(ParticleType<? extends RibbonParticleData> type, ParticleRotation rotation, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, int length, ParticleComponent[] components) {
        super(type, rotation, scale, r, g, b, a, drag, duration, emissive, false, components);
        this.length = length;
    }

        public void writeToNetwork(RegistryFriendlyByteBuf buffer) {
        super.writeToNetwork(buffer);
        buffer.writeInt(this.length);
    }

    @SuppressWarnings("deprecation")
        public String writeToString() {
        return super.writeToString() + " " + this.length;
    }

    @OnlyIn(Dist.CLIENT)
    public int getLength() {
        return this.length;
    }

    public static MapCodec<RibbonParticleData> CODEC_RIBBON(ParticleType<RibbonParticleData> particleType) {
        return RecordCodecBuilder.mapCodec((codecBuilder) -> codecBuilder.group(
                Codec.DOUBLE.fieldOf("scale").forGetter(RibbonParticleData::getScale),
                Codec.DOUBLE.fieldOf("r").forGetter(RibbonParticleData::getRed),
                Codec.DOUBLE.fieldOf("g").forGetter(RibbonParticleData::getGreen),
                Codec.DOUBLE.fieldOf("b").forGetter(RibbonParticleData::getBlue),
                Codec.DOUBLE.fieldOf("a").forGetter(RibbonParticleData::getAlpha),
                Codec.DOUBLE.fieldOf("drag").forGetter(RibbonParticleData::getAirDrag),
                Codec.DOUBLE.fieldOf("duration").forGetter(RibbonParticleData::getDuration),
                Codec.BOOL.fieldOf("emissive").forGetter(RibbonParticleData::isEmissive),
                Codec.INT.fieldOf("length").forGetter(RibbonParticleData::getLength)
                ).apply(codecBuilder, (scale, r, g, b, a, drag, duration, emissive, length) ->
                    new RibbonParticleData(particleType, new ParticleRotation.FaceCamera(0), scale, r, g, b, a, drag, duration, emissive, length, new ParticleComponent[]{}))
        );
    }

    /** 复用原 writeToNetwork / fromNetwork 的字节布局，未改动序列化格式 */
    public static StreamCodec<RegistryFriendlyByteBuf, RibbonParticleData> STREAM_CODEC_RIBBON(ParticleType<RibbonParticleData> particleType) {
        return StreamCodec.of((buf, data) -> data.writeToNetwork(buf), buf -> readFromNetwork(particleType, buf));
    }
}
