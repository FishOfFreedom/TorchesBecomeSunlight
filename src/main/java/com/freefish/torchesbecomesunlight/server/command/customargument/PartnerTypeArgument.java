package com.freefish.torchesbecomesunlight.server.command.customargument;

import com.freefish.torchesbecomesunlight.server.partner.PartnerHandler;
import com.freefish.torchesbecomesunlight.server.partner.PartnerType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PartnerTypeArgument implements ArgumentType<ResourceLocation> {
    private static final DynamicCommandExceptionType INVALID_PARTNER = new DynamicCommandExceptionType(
            id -> Component.literal("Unknown partner type: " + id)
    );

    private Collection<String> getAvailableIds() {
        return PartnerHandler.PARTNER_TYPES.keySet().stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.toList());
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);

        if (!PartnerHandler.PARTNER_TYPES.containsKey(id)) {
            throw INVALID_PARTNER.create(id);
        }
        
        return id;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(getAvailableIds(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return getAvailableIds().stream().limit(3).collect(Collectors.toList());
    }

    public static PartnerTypeArgument partnerType() {
        return new PartnerTypeArgument();
    }

    public static PartnerType<?> getPartnerType(CommandContext<?> context, String name) {
        ResourceLocation id = context.getArgument(name, ResourceLocation.class);
        return PartnerHandler.PARTNER_TYPES.get(id);
    }
}