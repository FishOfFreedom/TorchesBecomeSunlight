package com.freefish.torchesbecomesunlight.server.command;

import com.freefish.torchesbecomesunlight.server.init.recipe.FoodCategory;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodValuesDefinition;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class TBSCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("torchesbecomesunlight").requires((require) -> require.hasPermission(2))
                .then(Commands.literal("checkfoodattribute")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if(player!=null){
                                ItemStack mainHandItem = player.getMainHandItem();
                                if(!mainHandItem.isEmpty()){
                                    context.getSource().sendSuccess(() -> Component.literal(String.valueOf(FoodValuesDefinition.getFoodValues(mainHandItem,context.getSource().getLevel()).get(FoodCategory.FRUIT))),false);
                                }
                            }
                            return 0;
                        })));
    }
}
