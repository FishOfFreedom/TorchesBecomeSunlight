package com.freefish.torchesbecomesunlight.server.event;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.mixin.MixinStructureTemplatePool;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VillageAddEvent {
    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent ev)
    {
        if(ev.getUpdateCause()!= TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD)
            return;

        for(String biome : new String[]{"snowy"})
            for(String type : new String[]{"villager"}) {
                addToPool(
                        new ResourceLocation("village/" + biome + "/houses"),
                        new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "village/" + biome + "_" + type),
                        ev.getRegistryAccess()
                );
            }
    }

    private static void addToPool(ResourceLocation poolId, ResourceLocation toAdd, RegistryAccess regAccess)
    {
        Registry<StructureTemplatePool> registry = regAccess.registryOrThrow(Registries.TEMPLATE_POOL);
        StructureTemplatePool pool = Objects.requireNonNull(registry.get(poolId), poolId.getPath());
        MixinStructureTemplatePool poolAccess = (MixinStructureTemplatePool)pool;
        if(!(poolAccess.getRawTemplates() instanceof ArrayList))
            poolAccess.setRawTemplates(new ArrayList<>(poolAccess.getRawTemplates()));

        SinglePoolElement addedElement = SinglePoolElement.single(toAdd.toString()).apply(StructureTemplatePool.Projection.RIGID);
        poolAccess.getRawTemplates().add(Pair.of(addedElement, 1));
        poolAccess.getTemplates().add(addedElement);
    }
}
