package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.EntityEyeOfStructure;
import com.freefish.torchesbecomesunlight.server.entity.animal.Burdenbeast;
import com.freefish.torchesbecomesunlight.server.entity.animal.Mangler;
import com.freefish.torchesbecomesunlight.server.entity.demon.Crazelyseon;
import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.entity.dlc.PathfinderBallistarius;
import com.freefish.torchesbecomesunlight.server.entity.dlc.SaintGuard;
import com.freefish.torchesbecomesunlight.server.entity.dlc.Turret;
import com.freefish.torchesbecomesunlight.server.entity.effect.*;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.ShieldGuard;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.YetiIcecleaver;
import com.freefish.torchesbecomesunlight.server.entity.projectile.*;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.PreparationOp;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.Rosmontis;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.RosmontisBlock;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.RosmontisInstallation;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.RosmontisLivingInstallation;
import com.freefish.torchesbecomesunlight.server.entity.ursus.PatrolCaptain;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.entity.villager.FemaleVillager;
import com.freefish.torchesbecomesunlight.server.entity.villager.MaleVillager;
import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
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

    //邪魔
    public static final RegistryObject<EntityType<Crazelyseon>> CRAZELYSEON = ENTITY_TYPE.register("crazelyseon",
            () -> EntityType.Builder.<Crazelyseon>of(Crazelyseon::new, MobCategory.MONSTER)
                    .sized(32f, 32f).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "crazelyseon").toString()));

    //罗德岛
    public static final RegistryObject<EntityType<Rosmontis>> ROSMONTIS = ENTITY_TYPE.register("rosmontis",
            () -> EntityType.Builder.<Rosmontis>of(Rosmontis::new, MobCategory.MONSTER).sized(0.45F, 1.6F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "rosmontis").toString()));
    public static final RegistryObject<EntityType<RosmontisInstallation>> ROSMONTIS_INSTALLATION = ENTITY_TYPE.register("rosmontis_installation",
            () -> EntityType.Builder.<RosmontisInstallation>of(RosmontisInstallation::new, MobCategory.MISC).sized(1F, 3F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "rosmontis_installation").toString()));
    public static final RegistryObject<EntityType<RosmontisLivingInstallation>> ROSMONTIS_LIVING_INSTALLATION = ENTITY_TYPE.register("rosmontis_living_installation",
            () -> EntityType.Builder.<RosmontisLivingInstallation>of(RosmontisLivingInstallation::new, MobCategory.CREATURE).sized(1F, 3F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "rosmontis_living_installation").toString()));
    public static final RegistryObject<EntityType<PreparationOp>> PREPARATION_OP = ENTITY_TYPE.register("preparation_op",
            () -> EntityType.Builder.<PreparationOp>of(PreparationOp::new, MobCategory.MONSTER).sized(0.45F, 1.8F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "preparation_op").toString()));

    //游击队
    public static final RegistryObject<EntityType<FrostNova>> FROST_NOVA = ENTITY_TYPE.register("frost_nova",
            () -> EntityType.Builder.<FrostNova>of(FrostNova::new, MobCategory.MONSTER).sized(0.5F, 2F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "frost_nova").toString()));

    public static final RegistryObject<EntityType<Patriot>> PATRIOT = ENTITY_TYPE.register("patriot",
            () -> EntityType.Builder.<Patriot>of(Patriot::new, MobCategory.MONSTER).sized(1.8F, 4F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "patriot").toString()));

    public static final RegistryObject<EntityType<ShieldGuard>> SHIELD_GUARD = ENTITY_TYPE.register("shield_guard",
            () -> EntityType.Builder.<ShieldGuard>of(ShieldGuard::new, MobCategory.MONSTER).sized(1F, 2.6F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "shield_guard").toString()));

    public static final RegistryObject<EntityType<YetiIcecleaver>> YETI_ICE_LEAVER = ENTITY_TYPE.register("yeti_ice_leaver",
            () -> EntityType.Builder.<YetiIcecleaver>of(YetiIcecleaver::new, MobCategory.MONSTER).sized(1F, 2.6F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "yeti_ice_leaver").toString()));
    //ursus
    public static final RegistryObject<EntityType<Pursuer>> PURSUER = ENTITY_TYPE.register("pursuer",
            () -> EntityType.Builder.<Pursuer>of(Pursuer::new, MobCategory.MONSTER).sized(1F, 3.5F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "pursuer").toString()));
    public static final RegistryObject<EntityType<PatrolCaptain>> PATROL_CAPTAIN = ENTITY_TYPE.register("patrol_captain",
            () -> EntityType.Builder.<PatrolCaptain>of(PatrolCaptain::new, MobCategory.MONSTER).sized(0.45F, 1.8F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "patrol_captain").toString()));

    //dlc
    public static final RegistryObject<EntityType<GunKnightPatriot>> GUN_KNIGHT_PATRIOT = ENTITY_TYPE.register("gun_knight_patriot",
            () -> EntityType.Builder.<GunKnightPatriot>of(GunKnightPatriot::new, MobCategory.MONSTER).sized(1.8F, 4F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "gun_knight_patriot").toString()));

    public static final RegistryObject<EntityType<SaintGuard>> SAINT_GUARD = ENTITY_TYPE.register("saint_guard",
            () -> EntityType.Builder.<SaintGuard>of(SaintGuard::new, MobCategory.MONSTER).sized(0.6F, 0.6F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "saint_guard").toString()));

    public static final RegistryObject<EntityType<PathfinderBallistarius>> PATHFINDER_BALL = ENTITY_TYPE.register("pathfinder_ballistarius",
            () -> EntityType.Builder.<PathfinderBallistarius>of(PathfinderBallistarius::new, MobCategory.MONSTER).sized(1.2F, 3F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "pathfinder_ballistarius").toString()));

    public static final RegistryObject<EntityType<Turret>> TURRET = ENTITY_TYPE.register("turret",
            ()->EntityType.Builder.<Turret>of(Turret::new, MobCategory.CREATURE).sized(1F, 2F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"turret").toString()));
    //animal
    public static final RegistryObject<EntityType<Mangler>> MANGLER = ENTITY_TYPE.register("mangler",
            () -> EntityType.Builder.<Mangler>of(Mangler::new, MobCategory.AMBIENT).sized(1F, 1F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "mangler").toString()));
    public static final RegistryObject<EntityType<Burdenbeast>> BURDENBEAST = ENTITY_TYPE.register("burdenbeast",
            () -> EntityType.Builder.<Burdenbeast>of(Burdenbeast::new, MobCategory.AMBIENT).sized(2.8F, 2.8F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "burdenbeast").toString()));

    //villager
    public static final RegistryObject<EntityType<MaleVillager>> MALE = ENTITY_TYPE.register("male_villager",
            () -> EntityType.Builder.<MaleVillager>of(MaleVillager::new, MobCategory.MONSTER).sized(0.5F, 1.8F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "male_villager").toString()));

    public static final RegistryObject<EntityType<FemaleVillager>> FEMALE = ENTITY_TYPE.register("female_villager",
            () -> EntityType.Builder.<FemaleVillager>of(FemaleVillager::new, MobCategory.MONSTER).sized(0.5F, 1.8F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "female_villager").toString()));

    public static final RegistryObject<EntityType<DialogueEntity>> DIALOGUE = ENTITY_TYPE.register("dialogue",
            () -> EntityType.Builder.<DialogueEntity>of(DialogueEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).updateInterval(20)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "dialogue").toString()));

    public static final RegistryObject<EntityType<IceCrystal>> ICE_CRYSTAL = ENTITY_TYPE.register("ice_crystal",
            ()->EntityType.Builder.<IceCrystal>of(IceCrystal::new, MobCategory.MISC)
                    .sized(0.8F, 0.8F).setUpdateInterval(1)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"ice_crystal").toString()));

    public static final RegistryObject<EntityType<LightingBoom>> LIGHT_BOOM = ENTITY_TYPE.register("light_boom",
            ()->EntityType.Builder.<LightingBoom>of(LightingBoom::new, MobCategory.MISC)
                    .sized(2F, 2F).setUpdateInterval(1)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"light_boom").toString()));

    public static final RegistryObject<EntityType<LightingHalberd>> LIGHT_HALBERD = ENTITY_TYPE.register("light_halberd",
            ()->EntityType.Builder.<LightingHalberd>of(LightingHalberd::new, MobCategory.MISC)
                    .sized(1F, 1F)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"light_halberd").toString()));

    public static final RegistryObject<EntityType<BigIceCrystal>> BIG_ICE_CRYSTAL = ENTITY_TYPE.register("big_ice_crystal",
            ()->EntityType.Builder.<BigIceCrystal>of(BigIceCrystal::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"big_ice_crystal").toString()));

    public static final RegistryObject<EntityType<IceTuft>> ICE_TUFT = ENTITY_TYPE.register("ice_tuft",
            ()->EntityType.Builder.<IceTuft>of(IceTuft::new, MobCategory.MISC)
                    .sized(0.2F, 0.2F).clientTrackingRange(4).updateInterval(20)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"ice_tuft").toString()));

    public static final RegistryObject<EntityType<FXEntity>> FX_ENTITY = ENTITY_TYPE.register("fx_entity",
            ()->EntityType.Builder.<FXEntity>of(FXEntity::new, MobCategory.MISC)
                    .sized(1F, 1F).clientTrackingRange(4).updateInterval(20)
                    .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"fx_entity").toString()));

    public static final RegistryObject<EntityType<ChairEntity>> CHAIR_ENTITY =
            ENTITY_TYPE.register("chair_entity", () -> EntityType.Builder.<ChairEntity>of(ChairEntity::new, MobCategory.MISC)
                            .sized(0.1f, 0.1f).build("chair_entity"));

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

    public static final RegistryObject<EntityType<PursuerEffectEntity>> PEE = ENTITY_TYPE.register("pursuer_effect",
            () -> EntityType.Builder.<PursuerEffectEntity>of(PursuerEffectEntity::new, MobCategory.MISC).sized(1, 1).
                    setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "pursuer_effect").toString()));

    public static final RegistryObject<EntityType<SacredRealmEntity>> SACRED_REALM = ENTITY_TYPE.register("sacred_realm",
            () -> EntityType.Builder.<SacredRealmEntity>of(SacredRealmEntity::new, MobCategory.MISC).sized(1, 1).
                    setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "sacred_realm").toString()));

    public static final RegistryObject<EntityType<BlackHoleEntity>> BLACKHE = ENTITY_TYPE.register("black_hole",
            () -> EntityType.Builder.<BlackHoleEntity>of(BlackHoleEntity::new, MobCategory.MISC).sized(1, 1).
                    setUpdateInterval(Integer.MAX_VALUE).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "black_hole").toString()));


    public static final RegistryObject<EntityType<EntityEyeOfStructure>> EYE_OF_STRUCTURE =
            ENTITY_TYPE.register("eye_of_structure",
                    () -> EntityType.Builder.<EntityEyeOfStructure>of(EntityEyeOfStructure::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(4)
                            .build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "eye_of_structure").toString()));

    public static final RegistryObject<EntityType<IceBlade>> ICE_BLADE = ENTITY_TYPE.register("ice_blade",
                    () -> EntityType.Builder.<IceBlade>of(IceBlade::new, MobCategory.MISC).sized(3f, 0.8f)
                            .setUpdateInterval(1).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "ice_blade").toString()));

    public static final RegistryObject<EntityType<BlackSpear>> BLACK_SPEAR = ENTITY_TYPE.register("black_spear",
            () -> EntityType.Builder.<BlackSpear>of(BlackSpear::new, MobCategory.MISC).sized(0.5f, 0.5f)
                    .setUpdateInterval(1).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "black_spear").toString()));

    public static final RegistryObject<EntityType<PlayerSkillHelpEntity>> PLAYER_SKILL_ENTITY = ENTITY_TYPE.register("player_skill_entity",
            () -> EntityType.Builder.<PlayerSkillHelpEntity>of(PlayerSkillHelpEntity::new, MobCategory.MISC).sized(0.5f, 0.5f)
                    .setUpdateInterval(1).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "player_skill_entity").toString()));

    public static final RegistryObject<EntityType<Bullet>> BULLET = ENTITY_TYPE.register("bullet",
            () -> EntityType.Builder.<Bullet>of(Bullet::new, MobCategory.MISC).sized(0.2f, 0.2f)
                    .setUpdateInterval(1).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "bullet").toString()));

    public static final RegistryObject<EntityType<HalberdOTIEntity>> HALBERD_OTI_ENTITY = ENTITY_TYPE.register("halberd_of_the_infected",
            () -> EntityType.Builder.<HalberdOTIEntity>of(HalberdOTIEntity::new, MobCategory.MISC)
                    .sized(1F, 1F).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "halberd_of_the_infected").toString()));

    public static final RegistryObject<EntityType<EntityFallingBlock>> FALLING_BLOCK = ENTITY_TYPE.register("falling_block",
            () -> EntityType.Builder.<EntityFallingBlock>of(EntityFallingBlock::new, MobCategory.MISC)
                    .sized(1, 1).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "falling_block").toString()));

    public static final RegistryObject<EntityType<RosmontisBlock>> ROS_MULTI_BLOCK = ENTITY_TYPE.register("ros_multi_block",
            () -> EntityType.Builder.<RosmontisBlock>of(RosmontisBlock::new, MobCategory.MISC)
                    .sized(1, 1).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "ros_multi_block").toString()));

    public static final RegistryObject<EntityType<AnimationBlock>> ANIMATION_BLOCK = ENTITY_TYPE.register("animation_block",
            () -> EntityType.Builder.<AnimationBlock>of(AnimationBlock::new, MobCategory.MISC)
                    .sized(1, 1).build(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animation_block").toString()));


    @SubscribeEvent
    public static void onCreateAttributes(EntityAttributeCreationEvent event){
        event.put(EntityHandle.FROST_NOVA.get(), FrostNova.createAttributes().build());
        event.put(EntityHandle.MALE.get(), UrsusVillager.createAttributes().build());
        event.put(EntityHandle.FEMALE.get(), UrsusVillager.createAttributes().build());
        event.put(EntityHandle.PATRIOT.get(), Patriot.createAttributes().build());
        event.put(EntityHandle.GUN_KNIGHT_PATRIOT.get(), GunKnightPatriot.createAttributes().build());
        event.put(EntityHandle.SAINT_GUARD.get(), GunKnightPatriot.createAttributes().build());
        event.put(EntityHandle.PURSUER.get(), Pursuer.createAttributes().build());
        event.put(EntityHandle.SHIELD_GUARD.get(), ShieldGuard.createAttributes().build());
        event.put(EntityHandle.PATHFINDER_BALL.get(), PathfinderBallistarius.createAttributes().build());
        event.put(EntityHandle.MANGLER.get(), Mangler.createAttributes().build());
        event.put(EntityHandle.PREPARATION_OP.get(), Mangler.createAttributes().build());
        event.put(EntityHandle.PATROL_CAPTAIN.get(), Mangler.createAttributes().build());
        event.put(EntityHandle.BURDENBEAST.get(), Burdenbeast.createAttributes().build());
        event.put(EntityHandle.TURRET.get(), Turret.createAttributes().build());
        event.put(EntityHandle.ROSMONTIS.get(), Rosmontis.createAttributes().build());
        event.put(EntityHandle.YETI_ICE_LEAVER.get(), YetiIcecleaver.createAttributes().build());
        event.put(EntityHandle.ROSMONTIS_LIVING_INSTALLATION.get(), RosmontisLivingInstallation.createAttributes().build());
        event.put(EntityHandle.CRAZELYSEON.get(), GunKnightPatriot.createAttributes().build());
    }
}
