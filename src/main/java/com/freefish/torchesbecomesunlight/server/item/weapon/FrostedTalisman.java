package com.freefish.torchesbecomesunlight.server.item.weapon;

import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
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

public class FrostedTalisman extends SummonItem<FrostNova> {

    public FrostedTalisman(Properties pProperties) {
        super(pProperties,FrostNova.class);
    }

    @Override
    public FrostNova create(Level level) {
        return new FrostNova(EntityHandle.FROST_NOVA.get(), level);
    }

    @Override
    public PartnerType getPartnerType() {
        return PartnerHandler.FROSTNOVA_PARTNER;
    }

    @Override
    public void whenSummonSuccess(Player player,FrostNova patriot) {
        if(player instanceof ServerPlayer serverPlayer){
            TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(serverPlayer, "partner_frostnova");
        }
    }

    @Override
    public boolean testPlayerStory(PlayerStoryStoneData storyStoneData) {
        return storyStoneData.isCanSummonFrostNova();
    }

    @Override
    public boolean test(FrostNova entity) {
        Partner<?> partner = PartnerUtil.getPartner(entity);
        if (partner != null||(entity.challengePlayer!=null&&entity.challengePlayer.isAlive())||entity.getDialogueEntity()!=null) {
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.frosted_talisman"));
    }
}
