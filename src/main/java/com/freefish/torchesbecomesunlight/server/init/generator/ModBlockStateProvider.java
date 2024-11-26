package com.freefish.torchesbecomesunlight.server.init.generator;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.BlockHandle;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TorchesBecomeSunlight.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(BlockHandle.GEM_POLISHING_STATION.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/gem_polishing_station")));

        simpleBlockWithItem(BlockHandle.POT.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/pot")));
    }
}
