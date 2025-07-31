package com.freefish.torchesbecomesunlight.server.init.generator;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.TBSTagKey;
import com.freefish.torchesbecomesunlight.server.world.structure.StructureHandle;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class EMStructureTagsProvider extends StructureTagsProvider {
    public EMStructureTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, TorchesBecomeSunlight.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(TBSTagKey.EYE_OF_RHODES_ISLAND_BANSHICHU).add(StructureHandle.RHODE_ISLAND_OFFICE_K);
        this.tag(TBSTagKey.EYE_OF_SANKTA_STATUE).add(StructureHandle.SANKTA_STATUE_K);
    }
}