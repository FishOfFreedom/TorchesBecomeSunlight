package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.generator.lootmodifier.EndCityLootModifier;
import com.freefish.torchesbecomesunlight.server.init.generator.lootmodifier.NetherFortressLootModifier;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LootHandle {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<Codec<EndCityLootModifier>> END_CITY_LOOT = GLOBAL_LOOT.register("end_city_loot",()->EndCityLootModifier.CODEC);
    public static final RegistryObject<Codec<NetherFortressLootModifier>> NETHER_FORTRESS_LOOT = GLOBAL_LOOT.register("nether_fortress_loot",()->NetherFortressLootModifier.CODEC);

}