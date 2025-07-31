package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.compat.rosmontis.particle.base.cube.RLCubeParticle;
import com.freefish.torchesbecomesunlight.compat.rosmontis.particle.base.cube.setting.RendererSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ActRangeSignMessage {
    private int entityID;
    private CompoundTag component;

    public ActRangeSignMessage() {

    }

    public ActRangeSignMessage(int entityID, CompoundTag component) {
        this.entityID = entityID;
        this.component = component;
    }

    public static void serialize(final ActRangeSignMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeNbt(message.component);
    }

    public static ActRangeSignMessage deserialize(final FriendlyByteBuf buf) {
        final ActRangeSignMessage message = new ActRangeSignMessage();
        message.entityID = buf.readVarInt();
        message.component = buf.readNbt();
        return message;
    }

    public static class Handler implements BiConsumer<ActRangeSignMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final ActRangeSignMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(message.entityID);
                if (entity != null) {
                    CompoundTag component1 = message.component;
                    if(component1.contains("type")){
                        int type = component1.getInt("type");
                        if(type==0){
                            BlockPos blockPos = NbtUtils.readBlockPos(component1.getCompound("block"));
                            showBlock(blockPos,entity.level());
                        }else if (type==1){
                            Vec3 v = new Vec3(component1.getFloat("vx"),component1.getFloat("vy"),component1.getFloat("vz"));
                            showSector(v,component1.getFloat("scale"),component1.getFloat("radius"),component1.getFloat("roffset"),entity.level());
                        }
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }

    public static final boolean FLAD = false;

    public static void showBlockInter(Entity entity,BlockPos vec3, Level level){
        if(FLAD){
            if (!entity.level().isClientSide) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.put("block", NbtUtils.writeBlockPos(vec3));
                compoundTag.putInt("type", 0);
                TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new ActRangeSignMessage(entity.getId(), compoundTag));
            } else {
                showBlock(vec3, level);
            }
        }
    }

    private static void showBlock(BlockPos vec3, Level level){
        RLCubeParticle rlCubeParticle = new RLCubeParticle(level);
        rlCubeParticle.config.setStartColor(new Gradient(new GradientColor(0X1FFFFFFF)));
        rlCubeParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        rlCubeParticle.config.setStartSize(new NumberFunction3(0.5));
        rlCubeParticle.config.getMaterial().setDepthTest(false);
        EmissionSetting.Burst burst = new EmissionSetting.Burst();burst.setCount(NumberFunction.constant(1));
        rlCubeParticle.config.getEmission().addBursts(burst);
        rlCubeParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Vertical);
        rlCubeParticle.config.getMaterial().setMaterial(TBSMaterialHandle.NO_GLOW_PIXEL.create());
        BlockEffect effect = new BlockEffect(level,vec3.getCenter());
        rlCubeParticle.emmit(effect);
    }

    public static void showSectorInter(Entity entity,Vec3 vec3,float scale,float radius,float roffset, Level level){
        if(FLAD){
            if (!entity.level().isClientSide) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putFloat("vx", (float) vec3.x);
                compoundTag.putFloat("vy", (float) vec3.y);
                compoundTag.putFloat("vz", (float) vec3.z);
                compoundTag.putFloat("scale", scale);
                compoundTag.putFloat("radius", radius);
                compoundTag.putFloat("roffset", roffset);
                compoundTag.putInt("type", 1);
                TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new ActRangeSignMessage(entity.getId(), compoundTag));
            } else {
                showSector(vec3, scale, radius, roffset, level);
            }
        }
    }

    private static void showSector(Vec3 vec3,float scale,float radius,float roffset, Level level){
        RLParticle rlCubeParticle = new RLParticle(level);
        rlCubeParticle.config.setStartColor(new Gradient(new GradientColor(0X9FFFFFFF)));
        rlCubeParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        rlCubeParticle.config.getShape().setShape(new Dot());
        rlCubeParticle.config.setStartSize(new NumberFunction3(scale));
        EmissionSetting.Burst burst = new EmissionSetting.Burst();burst.setCount(NumberFunction.constant(1));
        rlCubeParticle.config.getEmission().addBursts(burst);
        rlCubeParticle.config.getRenderer().setRenderMode(com.freefish.rosmontislib.client.particle.advance.data.RendererSetting.Particle.Mode.Horizontal);
        rlCubeParticle.config.getMaterial().setMaterial(TBSMaterialHandle.getSector(radius).create());
        rlCubeParticle.config.getRotationOverLifetime().open();
        rlCubeParticle.config.getRotationOverLifetime().setYaw(NumberFunction.constant(roffset));
        BlockEffect effect = new BlockEffect(level,vec3);
        rlCubeParticle.emmit(effect);
    }
}
