package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.animal.Mangler;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.*;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.ShieldGuard;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.effect.SpeedEntity;
import com.freefish.torchesbecomesunlight.server.entity.projectile.*;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.entity.villager.Man;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityHandle {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<EntityType<FrostNova>> FROST_NOVA = ENTITY_TYPE.register("frost_nova",
            () -> EntityType.Builder.<FrostNova>of(FrostNova::new, MobCategory.CREATURE)
                    .sized(0.5F, 2F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "frost_nova").toString()));

    public static final RegistryObject<EntityType<Man>> MAN = ENTITY_TYPE.register("man",
            () -> EntityType.Builder.<Man>of(Man::new, MobCategory.CREATURE)
                    .sized(0.5F, 2F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "man").toString()));

    public static final RegistryObject<EntityType<DialogueEntity>> DIALOGUE = ENTITY_TYPE.register("dialogue",
            () -> EntityType.Builder.<DialogueEntity>of(DialogueEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "dialogue").toString()));

    public static final RegistryObject<EntityType<SpeedEntity>> SPEED_ENTITY = ENTITY_TYPE.register("speed_entity",
            () -> EntityType.Builder.<SpeedEntity>of(SpeedEntity::new, MobCategory.MISC)
                    .sized(1F, 1F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "speed_entity").toString()));

    public static final RegistryObject<EntityType<IceCrystal>> ICE_CRYSTAL = ENTITY_TYPE.register("ice_crystal",
            ()->EntityType.Builder.<IceCrystal>of(IceCrystal::new, MobCategory.MISC)
                    .sized(0.8F, 0.8F).clientTrackingRange(4).updateInterval(20)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"ice_crystal").toString()));

    public static final RegistryObject<EntityType<BigIceCrystal>> BIG_ICE_CRYSTAL = ENTITY_TYPE.register("big_ice_crystal",
            ()->EntityType.Builder.<BigIceCrystal>of(BigIceCrystal::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"big_ice_crystal").toString()));

    public static final RegistryObject<EntityType<IceTuft>> ICE_TUFT = ENTITY_TYPE.register("ice_tuft",
            ()->EntityType.Builder.<IceTuft>of(IceTuft::new, MobCategory.MISC)
                    .sized(0.2F, 0.2F).clientTrackingRange(4).updateInterval(20)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"ice_tuft").toString()));

    public static final RegistryObject<EntityType<BlackTuft>> BLACK_TUFT = ENTITY_TYPE.register("black_tuft",
            ()->EntityType.Builder.<BlackTuft>of(BlackTuft::new, MobCategory.MISC)
                    .sized(0.2F, 0.2F).updateInterval(20)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"black_tuft").toString()));

    public static final RegistryObject<EntityType<EntityCameraShake>> CAMERA_SHAKE = ENTITY_TYPE.register("camera_shake",
            () -> EntityType.Builder.<EntityCameraShake>of(EntityCameraShake::new, MobCategory.MISC).sized(1, 1).
            setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "camera_shake").toString()));

    public static final RegistryObject<EntityType<StompEntity>> STOMP_ENTITY = ENTITY_TYPE.register("stomp_entity",
            () -> EntityType.Builder.<StompEntity>of(StompEntity::new, MobCategory.MISC).sized(1, 1).
                    setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "stomp_entity").toString()));

    public static final RegistryObject<EntityType<PursuerEffectEntity>> PEE = ENTITY_TYPE.register("pee",
            () -> EntityType.Builder.<PursuerEffectEntity>of(PursuerEffectEntity::new, MobCategory.MISC).sized(1, 1).
                    setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "pee").toString()));

    public static final RegistryObject<EntityType<BlackHoleEntity>> BLACKHE = ENTITY_TYPE.register("black_hole",
            () -> EntityType.Builder.<BlackHoleEntity>of(BlackHoleEntity::new, MobCategory.MISC).sized(1, 1).
                    setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "black_hole").toString()));

    public static final RegistryObject<EntityType<IceWallEntity>> ICE_WALL_ENTITY = ENTITY_TYPE.register("ice_wall",
            () -> EntityType.Builder.<IceWallEntity>of(IceWallEntity::new, MobCategory.MISC).sized(1, 1).
                    setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "ice_wall").toString()));

    public static final RegistryObject<EntityType<IceBlade>> ICE_BLADE = ENTITY_TYPE.register("ice_blade",
                    () -> EntityType.Builder.<IceBlade>of(IceBlade::new, MobCategory.MISC).sized(3f, 0.8f)
                            .setUpdateInterval(1).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "ice_blade").toString()));

    public static final RegistryObject<EntityType<BlackSpear>> BLACK_SPEAR = ENTITY_TYPE.register("black_spear",
            () -> EntityType.Builder.<BlackSpear>of(BlackSpear::new, MobCategory.MISC).sized(0.5f, 0.5f)
                    .setUpdateInterval(1).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "black_spear").toString()));

    public static final RegistryObject<EntityType<HalberdOTIEntity>> HALBERD_OTI_ENTITY = ENTITY_TYPE.register("halberd_of_the_infected",
            () -> EntityType.Builder.<HalberdOTIEntity>of(HalberdOTIEntity::new, MobCategory.MISC)
                    .sized(1F, 1F).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "halberd_of_the_infected").toString()));

    public static final RegistryObject<EntityType<EntityFallingBlock>> FALLING_BLOCK = ENTITY_TYPE.register("falling_block",
            () -> EntityType.Builder.<EntityFallingBlock>of(EntityFallingBlock::new, MobCategory.MISC)
                    .sized(1, 1).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "falling_block").toString()));

    public static final RegistryObject<EntityType<Patriot>> PATRIOT = ENTITY_TYPE.register("patriot",
            () -> EntityType.Builder.<Patriot>of(Patriot::new, MobCategory.CREATURE)
                    .sized(1F, 3.5F).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "patriot").toString()));

    public static final RegistryObject<EntityType<ShieldGuard>> SHIELD_GUARD = ENTITY_TYPE.register("shield_guard",
            () -> EntityType.Builder.<ShieldGuard>of(ShieldGuard::new, MobCategory.CREATURE)
                    .sized(1F, 2.6F).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "shield_guard").toString()));

    public static final RegistryObject<EntityType<Mangler>> MANGLER = ENTITY_TYPE.register("mangler",
            () -> EntityType.Builder.<Mangler>of(Mangler::new, MobCategory.AMBIENT)
                    .sized(1F, 1F).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "mangler").toString()));

    public static final RegistryObject<EntityType<Pursuer>> PURSUER = ENTITY_TYPE.register("pursuer",
            () -> EntityType.Builder.<Pursuer>of(Pursuer::new, MobCategory.CREATURE)
                    .sized(1F, 3.5F).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "pursuer").toString()));


    @SubscribeEvent
    public static void onCreateAttributes(EntityAttributeCreationEvent event){
        event.put(EntityHandle.FROST_NOVA.get(), FrostNova.createAttributes().build());
        event.put(EntityHandle.MAN.get(), Man.createAttributes().build());
        event.put(EntityHandle.PATRIOT.get(), Patriot.createAttributes().build());
        event.put(EntityHandle.PURSUER.get(), Pursuer.createAttributes().build());
        event.put(EntityHandle.SHIELD_GUARD.get(), ShieldGuard.createAttributes().build());
        event.put(EntityHandle.MANGLER.get(), Mangler.createAttributes().build());
    }
}
