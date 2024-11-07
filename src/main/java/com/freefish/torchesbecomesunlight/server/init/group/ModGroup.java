package com.freefish.torchesbecomesunlight.server.init.group;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
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
                        output.accept(ItemHandle.TEST_ITEM.get());
                        output.accept(ItemHandle.SPEED_ITEM.get());
                        output.accept(ItemHandle.Animation_ITEM.get());
                        output.accept(ItemHandle.PATRIOT_EGG.get());
                        output.accept(ItemHandle.SNOW_NOVA_EGG.get());
                    }).build());
}
