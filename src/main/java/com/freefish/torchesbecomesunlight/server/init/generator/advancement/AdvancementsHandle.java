package com.freefish.torchesbecomesunlight.server.init.generator.advancement;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.AdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdvancementsHandle extends AdvancementProvider {
    public AdvancementsHandle(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new TBSAdvancements()));
    }
}