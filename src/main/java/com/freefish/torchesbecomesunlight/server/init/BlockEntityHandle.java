package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.blockentity.PotBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityHandle {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<BlockEntityType<PotBlockEntity>> POT =
            BLOCK_ENTITY.register("pot", () ->
                    BlockEntityType.Builder.of(PotBlockEntity::new,
                            BlockHandle.POT.get()).build(null));
}
