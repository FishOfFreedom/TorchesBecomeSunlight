package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.blockentity.StewPotBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityHandle {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<BlockEntityType<StewPotBlockEntity>> STEW_POT =
            BLOCK_ENTITY.register("stew_pot_tile", () ->
                    BlockEntityType.Builder.of(StewPotBlockEntity::new,
                            BlockHandle.STEW_POT.get()).build(null));
}
