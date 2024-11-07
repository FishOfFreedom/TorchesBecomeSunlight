package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.blockentity.ShaderBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityHandle {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TorchesBecomeSunlight.MOD_ID);

    public static RegistryObject<BlockEntityType<ShaderBlockEntity>> SHADER = BLOCK_ENTITY.register("shader", () -> BlockEntityType.Builder.of(ShaderBlockEntity::new, BlockHandle.SHADER.get()).build(null));
}
