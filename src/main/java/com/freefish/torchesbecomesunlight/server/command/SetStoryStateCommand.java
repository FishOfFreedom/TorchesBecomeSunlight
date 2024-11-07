package com.freefish.torchesbecomesunlight.server.command;

import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStoneProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class SetStoryStateCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("setstorystate")
                .then(Commands.argument("value", IntegerArgumentType.integer())
                        .executes(context -> {
                            int value = IntegerArgumentType.getInteger(context, "value");
                            Player player = context.getSource().getPlayer();
                            player.getCapability(PlayerStoryStoneProvider.PLAYER_STORY_STONE_CAPABILITY).ifPresent((storystate)->{
                                storystate.setStoryState(value);
                            });
                            return 0;
                        })));
    }
}
