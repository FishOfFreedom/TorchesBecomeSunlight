package com.freefish.torchesbecomesunlight.mixin.accessor;

import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PoolElementStructurePiece.class)
public interface PoolElementStructurePieceAccessor {
    @Accessor
    StructurePoolElement getElement();
}
