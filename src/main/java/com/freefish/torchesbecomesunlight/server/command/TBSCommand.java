package com.freefish.torchesbecomesunlight.server.command;

import com.freefish.torchesbecomesunlight.server.init.recipe.FoodCategory;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodValues;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodValuesDefinition;
import com.freefish.torchesbecomesunlight.server.item.food.DishAttribute;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Collectors;

public class TBSCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("torchesbecomesunlight").requires((require) -> require.hasPermission(2))
                .then(Commands.literal("checkfoodattribute")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if(player!=null){
                                ItemStack mainHandItem = player.getMainHandItem();
                                if(!mainHandItem.isEmpty()){
                                    FoodValues foodValues;
                                    CompoundTag orCreateTag = mainHandItem.getOrCreateTag();
                                    if(orCreateTag.contains("tbsdish")){
                                        foodValues =new DishAttribute(orCreateTag.getCompound("tbsdish")).getFoodValues();
                                    }else {
                                        foodValues = FoodValuesDefinition.getFoodValues(mainHandItem, context.getSource().getLevel());
                                    }

                                    context.getSource().sendSuccess(() -> Component.literal("meat:"+foodValues.get(FoodCategory.MEAT)+" fish:"+foodValues.get(FoodCategory.FISH)+" egg:"+foodValues.get(FoodCategory.EGG)+" veggie:"+foodValues.get(FoodCategory.VEGGIE)),false);
                                    context.getSource().sendSuccess(() -> Component.literal(
                                            foodValues.getEffects().stream()
                                            .map(mobEffect -> ForgeRegistries.MOB_EFFECTS.getKey(mobEffect).toString())
                                            .collect(Collectors.joining(","))
                                    ),false);
                                }
                            }
                            return 0;
                        })));
    }
}
