package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.gui.screen.cuttingborad.CuttingBoardMenu;
import com.freefish.torchesbecomesunlight.client.render.gui.screen.oven.OvenMenu;
import com.freefish.torchesbecomesunlight.client.render.gui.screen.stewpot.StewPotMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuHandle {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<MenuType<StewPotMenu>> STEW_POT_MENU =
            registerMenuType("stew_pot_menu", StewPotMenu::new);

    public static final RegistryObject<MenuType<CuttingBoardMenu>> CUTTING_BORAD_MENU =
            registerMenuType("cutting_board_menu", CuttingBoardMenu::new);

    public static final RegistryObject<MenuType<OvenMenu>> OVEN_MENU =
            registerMenuType("oven_menu", OvenMenu::new);


    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
