package com.freefish.torchesbecomesunlight.server.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SpeedEntity extends Entity {
    private PathfinderMob pathfinderMob;
    final int maxMove = 20;
    Vec3 oldPosition;
    double amount;

    public SpeedEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SpeedEntity(EntityType<?> pEntityType, Level pLevel, PathfinderMob pathfinderMob) {
        super(pEntityType, pLevel);
        this.pathfinderMob = pathfinderMob;
        this.oldPosition = pathfinderMob.position();
        this.amount = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount>maxMove) {
            //todo
            System.out.println(amount);
            kill();
        }
        if(!level().isClientSide()&&pathfinderMob!=null){
            Vec3 vec3 = position().add(40,0,0);
            pathfinderMob.getNavigation().moveTo(vec3.x,vec3.y,vec3.z,0.71f);
            //pathfinderMob.getMoveControl().strafe(0,0.5f);
            if(tickCount>=1){
                double temp = pathfinderMob.position().distanceTo(oldPosition);
                amount+=temp;
                oldPosition=pathfinderMob.position();
            }
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }
}
