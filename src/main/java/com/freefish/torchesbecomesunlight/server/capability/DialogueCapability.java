package com.freefish.torchesbecomesunlight.server.capability;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.story.IDialogueEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class DialogueCapability {
    public static ResourceLocation ID = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "dialogue_cap");

    public interface IDialogueCapability extends INBTSerializable<CompoundTag> {
        int getDialogueNeedTime();

        void setDialogueNeedTime(int dialogueNeedTime);

        void tick(LivingEvent.LivingTickEvent event);
    }

    public static class DialogueCapabilityImp implements IDialogueCapability {
        int dialogueNeedTime = 0;

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag compound = new CompoundTag();
            return compound;
        }

        @Override
        public void deserializeNBT(CompoundTag compound) {
        }

        @Override
        public int getDialogueNeedTime() {
            return dialogueNeedTime;
        }

        @Override
        public void setDialogueNeedTime(int dialogueNeedTime) {
            this.dialogueNeedTime = dialogueNeedTime;
        }

        public void tick(LivingEvent.LivingTickEvent event) {
            LivingEntity entity = event.getEntity();
            Level level = entity.level();

            if(entity instanceof IDialogueEntity dialogue){
                int f1 = this.getDialogueNeedTime();

                if(dialogue.canDialogue()&&ConfigHandler.COMMON.GLOBALSETTING.canDialogue.get()){
                    List<Player> livingEntities = level.getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(5), livingEntity ->
                            livingEntity.distanceTo(entity) < 3 + livingEntity.getBbWidth() / 2 && livingEntity != entity);
                    boolean flad = false;
                    for (LivingEntity livingEntity : livingEntities) {
                        if (isLookingAtMe1(entity, livingEntity))
                            flad = true;
                    }
                    if (flad && f1 < 50) this.setDialogueNeedTime(f1 + 1);
                    if (!flad && f1 > 0) this.setDialogueNeedTime(f1 - 1);
                }else {
                    if (f1 > 0) this.setDialogueNeedTime(f1 - 1);
                }
            }
        }

        public boolean isLookingAtMe1(LivingEntity livingEntity, LivingEntity pPlayer) {
            return pPlayer.hasLineOfSight(livingEntity);
        }
    }

    public static class Provider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag>
    {
        private final LazyOptional<IDialogueCapability> instance = LazyOptional.of(DialogueCapabilityImp::new);

        @Override
        public CompoundTag serializeNBT() {
            return instance.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
            return CapabilityHandle.DIALOGUE_CAPABILITY.orEmpty(cap, instance.cast());
        }
    }
}
