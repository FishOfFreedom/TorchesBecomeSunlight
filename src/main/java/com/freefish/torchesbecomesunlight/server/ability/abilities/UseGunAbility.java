package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.entity.effect.SacredRealmEntity;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.RawAnimation;


public class UseGunAbility extends PlayerAbility {

    public UseGunAbility(AbilityType<Player, UseGunAbility> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 20),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 20)
        });
    }

    private static final RawAnimation USE_HALBERD = RawAnimation.begin().thenPlay("use_gun");

    @Override
    public void start() {
        super.start();
        heldItemMainHandVisualOverride = getUser().getMainHandItem();
        if (!getUser().level().isClientSide()) {
        }
        else {
            playAnimation(USE_HALBERD);
        }
    }

    @Override
    public void tickUsing() {
        super.tickUsing();
        if (getTicksInUse() == 21) {
            Player player = getUser();
            ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            Vec3 start = FFEntityUtils.getBodyRotVec(player, new Vec3(-0.45, 3, 1.0));
            Level level = getLevel();
            if(level.isClientSide){
                ParticleComponent.KeyTrack keyTrack1 = ParticleComponent.KeyTrack.oscillate(0, 2, 24);
                ParticleComponent.KeyTrack keyTrack3 = new ParticleComponent.KeyTrack(new float[]{0, 0,0, 200, 200,10}, new float[]{0, 0.4f,0.5f, 0.6f,0.9f, 1});
                ParticleComponent.KeyTrack keyTrack31 = new ParticleComponent.KeyTrack(new float[]{0, 15, 15,0, 0}, new float[]{0, 0.4f,0.5f, 0.6f, 1});
                AdvancedParticleBase.spawnParticle(level, ParticleHandler.ICEBOMB_1.get(), start.x, start.y, start.z, 0, 0, 0, true, 0, 0, 0, 0, 0F, 1, 1, 0, 1, 1, 50, true, false, new ParticleComponent[]{
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack3, false),
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack1, true),
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.MOTION_Y, ParticleComponent.KeyTrack.startAndEnd(4f,0), false)
                });
                AdvancedParticleBase.spawnParticle(level, ParticleHandler.SUN.get(), start.x, start.y, start.z, 0, 0, 0, true, 0, 0, 0, 0, 0F, 1, 1, 0, 1, 1, 50, true, false, new ParticleComponent[]{
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack31, false),
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack1, true),
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.MOTION_Y, ParticleComponent.KeyTrack.startAndEnd(4f,0), false)
                });
            }else {
                SacredRealmEntity sacredRealmEntity= new SacredRealmEntity(EntityHandle.SACRED_REALM.get(),level);
                sacredRealmEntity.setCaster(getUser());
                Vec3 pos = MathUtils.getFirstBlockAbove(level,FFEntityUtils.getBodyRotVec(player,new Vec3(0,0,20)).add(0,-4,0),8);
                sacredRealmEntity.setPos((int)(pos.x) + 0.5,(int)(pos.y)+0.1,(int)(pos.z)+0.5);
                level.addFreshEntity(sacredRealmEntity);
            }
        }
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public boolean preventsAttacking() {
        return false;
    }
}
