package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.command.customargument.PartnerTypeArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CommandArgumentHandle {
    public static final DeferredRegister<ArgumentTypeInfo<?,?>> ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<ArgumentTypeInfo<PartnerTypeArgument, ?>> PARTNER_TYPE = ARGUMENT_TYPES.register("partner_type", () -> SingletonArgumentInfo.contextFree(PartnerTypeArgument::new));
}
