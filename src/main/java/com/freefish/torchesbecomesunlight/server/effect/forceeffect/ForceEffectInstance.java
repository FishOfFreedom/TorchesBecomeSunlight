package com.freefish.torchesbecomesunlight.server.effect.forceeffect;

import com.freefish.rosmontislib.sync.ITagSerializable;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SynCapabilityMessage;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;

@Getter
@Setter
public class ForceEffectInstance implements ITagSerializable<CompoundTag> {
    private ForceEffect singleEffect;
    private int level;
    private int time;

    public ForceEffectInstance(ForceEffectType<?> singleEffect, int level, int time) {
        this.singleEffect = singleEffect.create();
        this.singleEffect.setForceEffectInstance(this);
        this.level = level;
        this.time = time;
    }

    public ForceEffectInstance() {
    }

    public void tick(LivingEntity living){
        singleEffect.tick(living,level);
        time-=1;
    }

    public void discard(LivingEntity living){
        singleEffect.isRemoved = true;
        if(!living.level().isClientSide){
            CompoundTag all = new CompoundTag();
            all.putString("removeforceeffct",ForceEffectHandle.getKey(singleEffect.getType()).toString());
            TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> living), new SynCapabilityMessage(living,all));
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("type",ForceEffectHandle.getKey(singleEffect.getType()).toString());
        compoundTag.putInt("level",level);
        compoundTag.putInt("time",time);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        String[] split = compoundTag.getString("type").split(":");
        ResourceLocation type = new ResourceLocation(split[0],split[1]);
        ForceEffectType<?> value = ForceEffectHandle.getValue(type);
        singleEffect = value.create();
        this.singleEffect.setForceEffectInstance(this);
        level = compoundTag.getInt("level");
        time  = compoundTag.getInt("time");
    }
}
