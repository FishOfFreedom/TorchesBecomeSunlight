package com.freefish.torchesbecomesunlight.server.entity.ai.attribute;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AttributeRegistry {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<Attribute> ARMOR_DURABILITY = ATTRIBUTES.register("armor_durability",
            () -> new RangedAttribute("attribute.name.generic.max_health", 0.0, 0.0, 1024.0).setSyncable(true));
}
