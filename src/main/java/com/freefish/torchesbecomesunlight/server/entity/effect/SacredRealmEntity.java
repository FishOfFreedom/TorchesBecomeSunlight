package com.freefish.torchesbecomesunlight.server.entity.effect;

import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.entity.projectile.Bullet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
                }else if(getCaster() instanceof Player){
                    Bullet abstractarrow = new Bullet(level(),getCaster(),2);
                    Vec3 add = this.position().add(16.9, 94, 0);
                    abstractarrow.setPos(add);
                    abstractarrow.setIsHoly(true);

                    double d0 = position().x  - add.x;
                    double d1 = position().y  - add.y;
                    double d2 = position().z  - add.z;

                    abstractarrow.shoot(d0, d1 , d2, 10.66f, 1);
                    this.level().addFreshEntity(abstractarrow);
                }
            }
        }

        if(tickCount>100){
            discard();
        }
    }


}
