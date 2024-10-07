package com.freefish.torchesbecomesunlight.server.entity;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityFallingBlock;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceTuft;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceWallEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.StompEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova1;
import com.freefish.torchesbecomesunlight.server.entity.help.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.help.SpeedEntity;
import com.freefish.torchesbecomesunlight.server.entity.projectile.BigIceCrystal;
import com.freefish.torchesbecomesunlight.server.entity.projectile.HalberdOTIEntity;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceBlade;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import com.freefish.torchesbecomesunlight.server.entity.villager.Man;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.units.qual.Speed;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<EntityType<SnowNova>> SNOWNOVA = ENTITY_TYPE.register("snownova",
            () -> EntityType.Builder.<SnowNova>of(SnowNova::new, MobCategory.CREATURE)
                    .sized(0.5F, 2F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "snownova").toString()));

    public static final RegistryObject<EntityType<SnowNova1>> SNOWNOVA1 = ENTITY_TYPE.register("snownova1",
            () -> EntityType.Builder.<SnowNova1>of(SnowNova1::new, MobCategory.CREATURE)
                    .sized(0.5F, 2F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "snownova1").toString()));

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

    public static final RegistryObject<EntityType<EntityCameraShake>> CAMERA_SHAKE = ENTITY_TYPE.register("camera_shake",
            () -> EntityType.Builder.<EntityCameraShake>of(EntityCameraShake::new, MobCategory.MISC).sized(1, 1).
            setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "camera_shake").toString()));

    public static final RegistryObject<EntityType<StompEntity>> STOMP_ENTITY = ENTITY_TYPE.register("stomp_entity",
            () -> EntityType.Builder.<StompEntity>of(StompEntity::new, MobCategory.MISC).sized(1, 1).
                    setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "stomp_entity").toString()));

    public static final RegistryObject<EntityType<IceWallEntity>> ICE_WALL_ENTITY = ENTITY_TYPE.register("ice_wall",
            () -> EntityType.Builder.<IceWallEntity>of(IceWallEntity::new, MobCategory.MISC).sized(1, 1).
                    setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "ice_wall").toString()));

    public static final RegistryObject<EntityType<IceBlade>> ICE_BLADE = ENTITY_TYPE.register("ice_blade",
                    () -> EntityType.Builder.<IceBlade>of(IceBlade::new, MobCategory.MISC).sized(3f, 0.8f)
                            .setUpdateInterval(1).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "ice_blade").toString()));

    public static final RegistryObject<EntityType<HalberdOTIEntity>> HALBERD_OTI_ENTITY = ENTITY_TYPE.register("halberd_of_the_infected",
            () -> EntityType.Builder.<HalberdOTIEntity>of(HalberdOTIEntity::new, MobCategory.MISC)
                    .sized(1F, 1F).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "halberd_of_the_infected").toString()));

    public static final RegistryObject<EntityType<EntityFallingBlock>> FALLING_BLOCK = ENTITY_TYPE.register("falling_block",
            () -> EntityType.Builder.<EntityFallingBlock>of(EntityFallingBlock::new, MobCategory.MISC)
                    .sized(1, 1).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "falling_block").toString()));

    public static final RegistryObject<EntityType<Patriot>> PATRIOT = ENTITY_TYPE.register("patriot",
            () -> EntityType.Builder.<Patriot>of(Patriot::new, MobCategory.CREATURE)
                    .sized(1F, 3.5F).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "patriot").toString()));


    @SubscribeEvent
    public static void onCreateAttributes(EntityAttributeCreationEvent event){
        event.put(EntityRegistry.SNOWNOVA.get(), SnowNova.createAttributes().build());
        event.put(EntityRegistry.SNOWNOVA1.get(), SnowNova1.createAttributes().build());
        event.put(EntityRegistry.MAN.get(), Man.createAttributes().build());
        event.put(EntityRegistry.PATRIOT.get(), Patriot.createAttributes().build());
    }
}
