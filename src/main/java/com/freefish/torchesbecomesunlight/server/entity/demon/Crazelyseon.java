package com.freefish.torchesbecomesunlight.server.entity.demon;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Crazelyseon extends AnimatedEntity {
    public Crazelyseon(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    @Override
    public AnimationAct getDeathAnimation() {
        return null;
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide){
            int lightTick = tickCount%10;
            float len = 4;
            Vec3 move = new Vec3(0, 0, len).yRot(6.28f*lightTick/10);
            Vec3 move1 = new Vec3(0, 0, len).yRot(6.28f*(lightTick+1)/10);
            Vec3 finalMove = move1.subtract(move);
            level().addParticle(ParticleHandler.TESLA_BULB_LIGHTNING.get(), this.getX()+move.x, this.getY()+move.y, this.getZ()+move.z, finalMove.x, finalMove.y, finalMove.z);
        }
    }
}
