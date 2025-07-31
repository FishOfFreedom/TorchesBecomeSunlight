package com.freefish.torchesbecomesunlight.server.command;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.command.customargument.PartnerTypeArgument;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodCategory;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodValues;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodValuesDefinition;
import com.freefish.torchesbecomesunlight.server.item.food.DishAttribute;
import com.freefish.torchesbecomesunlight.server.item.food.TBSFood;
import com.freefish.torchesbecomesunlight.server.partner.PartnerType;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import com.freefish.torchesbecomesunlight.server.story.PlayerStoryStoneData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Collectors;

public class TBSCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
            Commands.literal("torchesbecomesunlight").requires((require) -> require.hasPermission(2))
                .then(Commands.literal("checkfoodattribute")
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayer();
                        if(player!=null){
                            ItemStack mainHandItem = player.getMainHandItem();
                            if(!mainHandItem.isEmpty()){
                                FoodValues foodValues;
                                DishAttribute dishAttribute;

                                if(mainHandItem.getItem() instanceof TBSFood tbsFood){
                                    dishAttribute = tbsFood.getItemAdditionData(mainHandItem,DishAttribute::new);
                                    foodValues = dishAttribute.getFoodValues();
                                }else {
                                    dishAttribute = null;
                                    foodValues = FoodValuesDefinition.getFoodValues(mainHandItem, context.getSource().getLevel());
                                }

                                context.getSource().sendSuccess(() -> Component.literal("meat:"+foodValues.get(FoodCategory.MEAT)+" fish:"+foodValues.get(FoodCategory.FISH)+" egg:"+foodValues.get(FoodCategory.EGG)+" veggie:"+foodValues.get(FoodCategory.VEGGIE)),false);
                                context.getSource().sendSuccess(() -> Component.literal(((dishAttribute!=null)?dishAttribute.getIntegratedNutrition():0)+" "+
                                        foodValues.getEffects().stream()
                                        .map(FoodValues.MobEffectInstance::toString)
                                        .collect(Collectors.joining(","))
                                ),false);
                            }
                        }
                        return 0;
                    })
                )
                .then(Commands.literal("partner")
                    .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.argument("partner_type", PartnerTypeArgument.partnerType())
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayer();
                                Entity targetEntity = EntityArgument.getEntity(context, "target");
                                PartnerType<?> partnerType = PartnerTypeArgument.getPartnerType(context, "partner_type");

                                if(targetEntity instanceof Mob mob&& partnerType.getEntityType()==targetEntity.getType()){
                                    PartnerUtil.startPartner(player,mob,partnerType);
                                    context.getSource().sendSuccess(() -> Component.literal("Success partner"+ PartnerUtil.getKey(partnerType)),false);
                                }else {
                                    context.getSource().sendSuccess(() -> Component.literal("fail partner"+ PartnerUtil.getKey(partnerType)),false);
                                }
                                return 1;
                            })
                        )
                    )
                )
                .then(Commands.literal("remove_partner")
                    .then(Commands.argument("target", EntityArgument.entity())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            Entity targetEntity = EntityArgument.getEntity(context, "target");
                            if(targetEntity instanceof Mob mob){
                                PartnerUtil.removePartner(mob,player);
                            }
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("storyLine")
                        .then(Commands.argument("isclear", BoolArgumentType.bool())
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayer();
                                boolean targetEntity = BoolArgumentType.getBool(context, "isclear");
                                PlayerStoryStoneData playerStory;
                                PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                                if(capability!=null){
                                    playerStory = capability.getPlayerStory();
                                } else {
                                    playerStory = null;
                                }

                                if(playerStory!=null) {
                                    context.getSource().sendSuccess(() -> Component.literal(playerStory.toString()), false);
                                }

                                if(targetEntity){
                                    if(playerStory!=null){
                                        playerStory.clear();
                                    }
                                }
                                return 1;
                            })
                        )
                )
        );
    }
}
