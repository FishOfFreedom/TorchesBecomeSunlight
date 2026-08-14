package com.freefish.torchesbecomesunlight.server.init;

import net.minecraft.core.registries.BuiltInRegistries;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.command.customargument.PartnerTypeArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CommandArgumentHandle {
    public static final DeferredRegister<ArgumentTypeInfo<?,?>> ARGUMENT_TYPES = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<ArgumentTypeInfo<?,?>, ArgumentTypeInfo<PartnerTypeArgument, ?>> PARTNER_TYPE = ARGUMENT_TYPES.register("partner_type", () -> SingletonArgumentInfo.contextFree(PartnerTypeArgument::new));
}
