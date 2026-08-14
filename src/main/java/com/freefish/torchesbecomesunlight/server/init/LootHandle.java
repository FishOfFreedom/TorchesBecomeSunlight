package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.generator.lootmodifier.EndCityLootModifier;
import com.freefish.torchesbecomesunlight.server.init.generator.lootmodifier.NetherFortressLootModifier;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class LootHandle {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<EndCityLootModifier>> END_CITY_LOOT = GLOBAL_LOOT.register("end_city_loot",()->EndCityLootModifier.CODEC);
    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<NetherFortressLootModifier>> NETHER_FORTRESS_LOOT = GLOBAL_LOOT.register("nether_fortress_loot",()->NetherFortressLootModifier.CODEC);

}