package com.freefish.torchesbecomesunlight.server.capability;

import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.minecraft.core.HolderLookup;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.story.IDialogueEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class DialogueCapability {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "dialogue_cap");

    public interface IDialogueCapability extends INBTSerializable<CompoundTag> {
        int getDialogueNeedTime();

        void setDialogueNeedTime(int dialogueNeedTime);

        void tick(EntityTickEvent.Post event);
    }

    public static class DialogueCapabilityImp implements IDialogueCapability {
        int dialogueNeedTime = 0;

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registries) {
            CompoundTag compound = new CompoundTag();
            return compound;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider registries, CompoundTag compound) {
        }

        @Override
        public int getDialogueNeedTime() {
            return dialogueNeedTime;
        }

        @Override
        public void setDialogueNeedTime(int dialogueNeedTime) {
            this.dialogueNeedTime = dialogueNeedTime;
        }

        public void tick(EntityTickEvent.Post event) {
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

    
}
