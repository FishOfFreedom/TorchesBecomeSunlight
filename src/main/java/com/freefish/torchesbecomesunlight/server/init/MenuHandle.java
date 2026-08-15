package com.freefish.torchesbecomesunlight.server.init;

import net.minecraft.core.registries.BuiltInRegistries;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.gui.screen.cuttingborad.CuttingBoardMenu;
import com.freefish.torchesbecomesunlight.client.render.gui.screen.oven.OvenMenu;
import com.freefish.torchesbecomesunlight.client.render.gui.screen.stewpot.StewPotMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MenuHandle {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<StewPotMenu>> STEW_POT_MENU =
            registerMenuType("stew_pot_menu", StewPotMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<CuttingBoardMenu>> CUTTING_BORAD_MENU =
            registerMenuType("cutting_board_menu", CuttingBoardMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<OvenMenu>> OVEN_MENU =
            registerMenuType("oven_menu", OvenMenu::new);


    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
