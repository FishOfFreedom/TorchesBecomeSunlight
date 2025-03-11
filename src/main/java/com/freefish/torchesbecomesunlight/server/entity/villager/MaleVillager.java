package com.freefish.torchesbecomesunlight.server.entity.villager;

import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
public class MaleVillager extends UrsusVillager{

    public MaleVillager(EntityType<? extends UrsusVillager> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public AnimationAct getDeathAnimation() {
        return null;
    }

    @Override
    public Dialogue getDialogue() {
        return DialogueStore.pursuer_d_1;
    }

    @Override
    public boolean getHasDialogue() {
        return getDialogue()!=null;
    }
}
