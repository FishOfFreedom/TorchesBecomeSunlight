package com.freefish.torchesbecomesunlight.server.item;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.event.ServerNetwork;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SynCapabilityMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class SanktaRing extends Item {
    public SanktaRing(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.sankta_ring"));
        tooltip.add(Component.translatable("text.torchesbecomesunlight.sankta_ring_tool"));
        tooltip.add(Component.translatable("text.torchesbecomesunlight.sankta_ringtoold"));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(!pLevel.isClientSide&&pPlayer.isCreative()){
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(pPlayer, CapabilityHandle.PLAYER_CAPABILITY);
            if(capability!=null){
                if(capability.isSankta()){
                    capability.setIsSankta(false);
                    ServerNetwork.toClientMessage(pPlayer, new SynCapabilityMessage(pPlayer, capability.writePlaySkillMessage()));
                }else {
                    capability.setIsSankta(true);
                    ServerNetwork.toClientMessage(pPlayer, new SynCapabilityMessage(pPlayer, capability.writePlaySkillMessage()));
                }
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
