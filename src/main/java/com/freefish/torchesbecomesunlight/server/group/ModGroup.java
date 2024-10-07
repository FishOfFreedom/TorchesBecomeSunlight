package com.freefish.torchesbecomesunlight.server.group;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.item.ItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModGroup {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MOD_GROUP = CREATIVE_MODE_TAB.register("torches_become_sunlight",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(Items.IRON_SWORD))
                    .title(Component.translatable("creativegroup.torchesbecomesunlight"))
                    .displayItems((parameters,output) ->{
                        output.accept(ItemRegistry.TEST_ITEM.get());
                        output.accept(ItemRegistry.SPEED_ITEM.get());
                        output.accept(ItemRegistry.Animation_ITEM.get());
                        output.accept(ItemRegistry.PATRIOT_EGG.get());
                        output.accept(ItemRegistry.SNOW_NOVA_EGG.get());
                    }).build());
}
