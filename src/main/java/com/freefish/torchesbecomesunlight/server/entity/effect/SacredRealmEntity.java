package com.freefish.torchesbecomesunlight.server.entity.effect;

import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SacredRealmEntity extends EffectEntity{
    public SacredRealmEntity(EntityType<?> type, Level level) {
        super(type, level);
    }


    @Override
    public void tick() {
        super.tick();

        if(tickCount==90){
            if(!level().isClientSide){
                if(getCaster() instanceof GunKnightPatriot gunKnightPatriot){
                    gunKnightPatriot.shootBulletWithoutFace(this.position(),this.position().add(16.9,94,0),2,10.66f,true);
                }
            }
        }

        if(tickCount>100){
            discard();
        }
    }


}
