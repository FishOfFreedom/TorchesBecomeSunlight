package com.freefish.torchesbecomesunlight.client.util.particle.util;

import net.minecraft.network.codec.StreamCodec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Locale;

/**Be from https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/client/particle/util/AdvancedParticleData.java
 * @author bobmowzie
 */
public class AdvancedParticleData implements ParticleOptions {
    /**
     * 1.21 移除了 ParticleOptions.Deserializer（命令解析改由 codec 承担）。
     * 这里保留原 fromNetwork 的读取逻辑，供 STREAM_CODEC 复用。
     */
    private static AdvancedParticleData readFromNetwork(ParticleType<AdvancedParticleData> particleTypeIn, RegistryFriendlyByteBuf buffer) {
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
            double faceCameraAngle = buffer.readFloat();
            boolean canCollide = buffer.readBoolean();
            ParticleRotation rotation;
            if (rotationMode.equals("face_camera")) rotation = new ParticleRotation.FaceCamera((float) faceCameraAngle);
            else if (rotationMode.equals("euler")) rotation = new ParticleRotation.EulerAngles((float)yaw, (float)pitch, (float)roll);
            else rotation = new ParticleRotation.OrientVector(new Vec3(yaw, pitch, roll));
            return new AdvancedParticleData(particleTypeIn, rotation, scale, red, green, blue, alpha, airDrag, duration, emissive, canCollide);
        }


    private final ParticleType<? extends AdvancedParticleData> type;

    private final float airDrag;
    private final float red, green, blue, alpha;
    private final ParticleRotation rotation;
    private final float scale;
    private final boolean emissive;
    private final float duration;
    private final boolean canCollide;

    private final ParticleComponent[] components;

    public AdvancedParticleData(ParticleType<? extends AdvancedParticleData> type, ParticleRotation rotation, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, boolean canCollide) {
        this(type, rotation, scale, r, g, b, a, drag, duration, emissive, canCollide, new ParticleComponent[]{});
    }

    public AdvancedParticleData(ParticleType<? extends AdvancedParticleData> type, ParticleRotation rotation, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, boolean canCollide, ParticleComponent[] components) {
        this.type = type;

        this.rotation = rotation;

        this.scale = (float) scale;

        this.red = (float) r;
        this.green = (float) g;
        this.blue = (float) b;
        this.alpha = (float) a;
        this.emissive = emissive;

        this.airDrag = (float) drag;

        this.duration = (float) duration;

        this.canCollide = canCollide;

        this.components = components;
    }

        public void writeToNetwork(RegistryFriendlyByteBuf buffer) {
        String rotationMode;
        float faceCameraAngle = 0;
        float yaw = 0;
        float pitch = 0;
        float roll = 0;
        if (rotation instanceof ParticleRotation.FaceCamera) {
            rotationMode = "face_camera";
            faceCameraAngle = ((ParticleRotation.FaceCamera) rotation).faceCameraAngle;
        }
        else if (rotation instanceof ParticleRotation.EulerAngles) {
            rotationMode = "euler";
            yaw = ((ParticleRotation.EulerAngles) rotation).yaw;
            pitch = ((ParticleRotation.EulerAngles) rotation).pitch;
            roll = ((ParticleRotation.EulerAngles) rotation).roll;
        }
        else {
            rotationMode = "orient";
            Vec3 vec = ((ParticleRotation.OrientVector)rotation).orientation;
            yaw = (float) vec.x;
            pitch = (float) vec.y;
            roll = (float) vec.z;
        }

        buffer.writeFloat(this.airDrag);
        buffer.writeFloat(this.red);
        buffer.writeFloat(this.green);
        buffer.writeFloat(this.blue);
        buffer.writeFloat(this.alpha);
        buffer.writeUtf(rotationMode);
        buffer.writeFloat(this.scale);
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
        buffer.writeFloat(roll);
        buffer.writeBoolean(this.emissive);
        buffer.writeFloat(this.duration);
        buffer.writeFloat(faceCameraAngle);
        buffer.writeBoolean(canCollide);
    }

    @SuppressWarnings("deprecation")
        public String writeToString() {
        String rotationMode;
        float faceCameraAngle = 0;
        float yaw = 0;
        float pitch = 0;
        float roll = 0;
        if (rotation instanceof ParticleRotation.FaceCamera) {
            rotationMode = "face_camera";
            faceCameraAngle = ((ParticleRotation.FaceCamera) rotation).faceCameraAngle;
        }
        else if (rotation instanceof ParticleRotation.EulerAngles) {
            rotationMode = "euler";
            yaw = ((ParticleRotation.EulerAngles) rotation).yaw;
            pitch = ((ParticleRotation.EulerAngles) rotation).pitch;
            roll = ((ParticleRotation.EulerAngles) rotation).roll;
        }
        else {
            rotationMode = "orient";
            Vec3 vec = ((ParticleRotation.OrientVector)rotation).orientation;
            yaw = (float) vec.x;
            pitch = (float) vec.y;
            roll = (float) vec.z;
        }

        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %s %.2f %.2f %.2f %.2f %b %.2f %.2f %b", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                this.airDrag, this.red, this.green, this.blue, this.alpha, rotationMode, this.scale, yaw, pitch, roll, this.emissive, this.duration, faceCameraAngle, canCollide);
    }

    @Override
    public ParticleType<? extends AdvancedParticleData> getType() {
        return type;
    }

    @OnlyIn(Dist.CLIENT)
    public double getRed() {
        return this.red;
    }

    @OnlyIn(Dist.CLIENT)
    public double getGreen() {
        return this.green;
    }

    @OnlyIn(Dist.CLIENT)
    public double getBlue() {
        return this.blue;
    }

    @OnlyIn(Dist.CLIENT)
    public double getAlpha() {
        return this.alpha;
    }

    @OnlyIn(Dist.CLIENT)
    public double getAirDrag() {
        return airDrag;
    }
    
    @OnlyIn(Dist.CLIENT)
    public ParticleRotation getRotation() {
        return rotation;
    }

    @OnlyIn(Dist.CLIENT)
    public double getScale() {
        return scale;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isEmissive() {
        return emissive;
    }

    @OnlyIn(Dist.CLIENT)
    public double getDuration() {
        return duration;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getCanCollide() {
        return canCollide;
    }

    @OnlyIn(Dist.CLIENT)
    public ParticleComponent[] getComponents() {
        return components;
    }

    public static MapCodec<AdvancedParticleData> CODEC(ParticleType<AdvancedParticleData> particleType) {
        return RecordCodecBuilder.mapCodec((codecBuilder) -> codecBuilder.group(
                Codec.DOUBLE.fieldOf("scale").forGetter(AdvancedParticleData::getScale),
                Codec.DOUBLE.fieldOf("r").forGetter(AdvancedParticleData::getRed),
                Codec.DOUBLE.fieldOf("g").forGetter(AdvancedParticleData::getGreen),
                Codec.DOUBLE.fieldOf("b").forGetter(AdvancedParticleData::getBlue),
                Codec.DOUBLE.fieldOf("a").forGetter(AdvancedParticleData::getAlpha),
                Codec.DOUBLE.fieldOf("drag").forGetter(AdvancedParticleData::getAirDrag),
                Codec.DOUBLE.fieldOf("duration").forGetter(AdvancedParticleData::getDuration),
                Codec.BOOL.fieldOf("emissive").forGetter(AdvancedParticleData::isEmissive),
                Codec.BOOL.fieldOf("canCollide").forGetter(AdvancedParticleData::getCanCollide)
                ).apply(codecBuilder, (scale, r, g, b, a, drag, duration, emissive, canCollide) ->
                        new AdvancedParticleData(particleType, new ParticleRotation.FaceCamera(0), scale, r, g, b, a, drag, duration, emissive, canCollide, new ParticleComponent[]{}))
        );
    }

    /** 复用原 writeToNetwork / fromNetwork 的字节布局，未改动序列化格式 */
    public static StreamCodec<RegistryFriendlyByteBuf, AdvancedParticleData> STREAM_CODEC(ParticleType<AdvancedParticleData> particleType) {
        return StreamCodec.of((buf, data) -> data.writeToNetwork(buf), buf -> readFromNetwork(particleType, buf));
    }
}
