package com.freefish.torchesbecomesunlight.server.partner;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.partner.self.FrostNovaPartner;
import com.freefish.torchesbecomesunlight.server.partner.self.PatriotPartner;
import com.freefish.torchesbecomesunlight.server.partner.self.RosmontisPartner;
import com.freefish.torchesbecomesunlight.server.partner.vanilla.SanktaPartner;
import com.freefish.torchesbecomesunlight.server.partner.vanilla.WolfPartner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public class PartnerHandler {
    public static final BiMap<ResourceLocation,PartnerType<?>> PARTNER_TYPES = HashBiMap.create();

    public static PartnerType<WolfPartner> WOLF_PARTNER;
    public static PartnerType<SanktaPartner> SANKTA_PARTNER;
    public static PartnerType<RosmontisPartner> ROSMONTIS_PARTNER;
    public static PartnerType<PatriotPartner> PATRIOT_PARTNER;
    public static PartnerType<FrostNovaPartner> FROSTNOVA_PARTNER;

    public static <T extends Partner<?>> PartnerType<T> init(ResourceLocation resourceLocation, EntityType<?> entityType, Supplier<T> supplier){
        PartnerType<T> tPartnerType = new PartnerType<>(entityType,supplier);
        PARTNER_TYPES.put(resourceLocation,tPartnerType);
        return tPartnerType;
    }

    public static void init(){
        WOLF_PARTNER = init(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"wolf"),EntityType.WOLF,WolfPartner::new);
        ROSMONTIS_PARTNER = init(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"rosmontis"), EntityHandle.ROSMONTIS.get(),RosmontisPartner::new);
        PATRIOT_PARTNER = init(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"patriot"), EntityHandle.PATRIOT.get(),PatriotPartner::new);
        FROSTNOVA_PARTNER = init(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"frostnova"), EntityHandle.FROST_NOVA.get(),FrostNovaPartner::new);
    }
}
