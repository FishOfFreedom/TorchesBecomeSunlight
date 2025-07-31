package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.TriggerHandler;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import com.freefish.torchesbecomesunlight.server.partner.vanilla.SanktaPartner;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class UseSanktaRingAbility extends PlayerAbility {

    public UseSanktaRingAbility(AbilityType<Player, UseSanktaRingAbility> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 20),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 20)
        });
    }

    List<ItemEntity> itemEntities;

    @Override
    public void start() {
        super.start();
        Player player = getUser();
        Level level = getLevel();
        if(player instanceof ServerPlayer serverPlayer){
            TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(serverPlayer, "gunPatriot_5_control");
        }
        ItemStack mainHandItem = player.getMainHandItem();
        heldItemMainHandVisualOverride = mainHandItem;

        if (!player.level().isClientSide()) {
        }
        else {
        }
        Vec3 pStartVec = player.getEyePosition();
        Vec3 pEndVec = FFEntityUtils.getHeadRotVec(player,new Vec3(0,0,20)).add(0,player.getEyeHeight(),0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(player.level(), player, pStartVec, pEndVec, player.getBoundingBox().expandTowards(pEndVec.subtract(pStartVec)).inflate(1.0D), (e) -> true);
        if(entityHitResult!=null){
            Entity entity = entityHitResult.getEntity();
            if(entity instanceof Mob mob){
                SanktaPartner sanktaPartner = new SanktaPartner();
                sanktaPartner.init(player,mob);
                PartnerUtil.setPartner(mob,sanktaPartner);
            }
        }
    }

    @Override
    public void tickUsing() {
        super.tickUsing();
        Player player = getUser();
        Level level = getLevel();
        int ticksInUse = getTicksInUse();

    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public boolean preventsAttacking() {
        return false;
    }

    @Override
    public boolean isAnimating() {
        return false;
    }
}
