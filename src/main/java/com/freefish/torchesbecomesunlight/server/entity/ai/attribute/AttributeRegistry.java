package com.freefish.torchesbecomesunlight.server.entity.ai.attribute;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AttributeRegistry {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TorchesBecomeSunlight.MOD_ID);
}
