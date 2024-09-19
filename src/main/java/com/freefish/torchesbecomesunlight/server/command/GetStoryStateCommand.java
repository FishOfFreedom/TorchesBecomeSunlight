package com.freefish.torchesbecomesunlight.server.command;

import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStoneProvider;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class GetStoryStateCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("getstorystate").executes((context) -> {
            context.getSource().getPlayer().getCapability(PlayerStoryStoneProvider.PLAYER_STORY_STONE_CAPABILITY).ifPresent((storystate)->{
                context.getSource().sendSuccess(()-> Component.literal("state:" + storystate.getStoryState()),false);
            });
        return 0;
        }));
    }
}
