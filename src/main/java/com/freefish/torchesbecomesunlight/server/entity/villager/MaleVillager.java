package com.freefish.torchesbecomesunlight.server.entity.villager;

import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import net.minecraft.world.entity.EntityType;
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
}
