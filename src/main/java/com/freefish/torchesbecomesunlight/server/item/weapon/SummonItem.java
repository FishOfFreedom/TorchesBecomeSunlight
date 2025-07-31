package com.freefish.torchesbecomesunlight.server.item.weapon;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerType;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import com.freefish.torchesbecomesunlight.server.story.PlayerStoryStoneData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class SummonItem<T extends Mob> extends Item {
    public SummonItem(Properties pProperties,Class<T> clazz) {
        super(pProperties);
        this.clazz = clazz;
    }

    private final Class<T> clazz;

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
        if(pUsedHand == InteractionHand.MAIN_HAND){
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(pPlayer, CapabilityHandle.PLAYER_CAPABILITY);

            if(capability!=null&&!pLevel.isClientSide) {
                PlayerStoryStoneData playerStory = capability.getPlayerStory();
                if(testPlayerStory(playerStory)||pPlayer.isCreative()){
                    List<Partner<?>> partnerList = capability.getPartnerManager().getPartnerList();
                    if (partnerList.isEmpty()) {
                        Iterable<Entity> all = ((ServerLevel) pLevel).getEntities().getAll();
                        T rosmontis = null;
                        boolean isSummoned = false;

                        for(Entity e:all){
                            if(clazz.isInstance(e)){
                                rosmontis = (T) e;
                                if(test(rosmontis)){
                                    isSummoned = true;
                                }
                                break;
                            }
                        }
                        if(!pPlayer.isCreative()&&getCurrentTotalDays(pLevel)==capability.getSummonTime()/24000L){
                            isSummoned = true;
                            pPlayer.sendSystemMessage(Component.translatable("text.torchesbecomesunlight.talisman13"));
                        }
                        if(isSummoned){
                            //todo
                            EntityType entityType = getPartnerType().getEntityType();
                            Component description = entityType.getDescription();
                            pPlayer.sendSystemMessage(Component.translatable("text.torchesbecomesunlight.talisman1",description));
                        }
                        else {
                            capability.setSummonTime(pLevel.getDayTime());
                            whenSummonSuccess(pPlayer,rosmontis);
                            if(rosmontis==null){
                                rosmontis = create(pLevel);
                                rosmontis.setPos(pPlayer.position());
                                pLevel.addFreshEntity(rosmontis);
                                PartnerUtil.startPartner(pPlayer, rosmontis, getPartnerType());
                                pPlayer.getCooldowns().addCooldown(itemInHand.getItem(), 60);
                            } else {
                                rosmontis.setPos(pPlayer.position());
                                PartnerUtil.startPartner(pPlayer, rosmontis, getPartnerType());
                                pPlayer.getCooldowns().addCooldown(itemInHand.getItem(), 60);
                            }
                        }
                    }else {
                        Partner<?> partner = partnerList.get(0);
                        if(partner.getPartnerType()==getPartnerType()){
                            PartnerUtil.removePartner(partner,pPlayer);
                        }else {
                            pPlayer.sendSystemMessage(Component.translatable("text.torchesbecomesunlight.talisman14"));
                        }
                    }
                }
                else {
                    EntityType entityType = getPartnerType().getEntityType();
                    Component description = entityType.getDescription();

                    pPlayer.sendSystemMessage(Component.translatable("text.torchesbecomesunlight.talisman12",description));
                }
            }
            return InteractionResultHolder.success(itemInHand);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public void whenSummonSuccess(Player player,T mob){

    };

    public abstract boolean testPlayerStory(PlayerStoryStoneData storyStoneData);

    public abstract boolean test(T entity);

    public abstract T create(Level level);

    public abstract PartnerType getPartnerType();

    private long getCurrentTotalDays(Level world) {
        return world.getDayTime() / 24000L;
    }


    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.summonitem"));
    }
}
