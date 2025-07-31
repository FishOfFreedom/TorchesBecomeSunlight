package com.freefish.torchesbecomesunlight.server.item.weapon;

import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.TriggerHandler;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerHandler;
import com.freefish.torchesbecomesunlight.server.partner.PartnerType;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import com.freefish.torchesbecomesunlight.server.story.PlayerStoryStoneData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class Talisman extends SummonItem<Patriot>{

    public Talisman(Properties pProperties) {
        super(pProperties,Patriot.class);
    }

    @Override
    public Patriot create(Level level) {
        return new Patriot(EntityHandle.PATRIOT.get(), level);
    }

    @Override
    public PartnerType getPartnerType() {
        return PartnerHandler.PATRIOT_PARTNER;
    }

    @Override
    public void whenSummonSuccess(Player player,Patriot patriot) {
        if(player instanceof ServerPlayer serverPlayer){
            TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(serverPlayer, "partner_patriot");
        }
    }

    @Override
    public boolean testPlayerStory(PlayerStoryStoneData storyStoneData) {
        return storyStoneData.isCanSummonOPatriot();
    }

    @Override
    public boolean test(Patriot entity) {
        Partner<?> partner = PartnerUtil.getPartner(entity);
        if (partner != null||(entity.challengePlayer!=null&&entity.challengePlayer.isAlive())||entity.getDialogueEntity()!=null) {
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.talisman"));
    }
}
