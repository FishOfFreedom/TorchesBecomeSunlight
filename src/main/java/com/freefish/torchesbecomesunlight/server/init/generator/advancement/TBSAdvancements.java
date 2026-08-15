package com.freefish.torchesbecomesunlight.server.init.generator.advancement;

import net.minecraft.advancements.AdvancementRequirements;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.ChainedAdvancementTrigger;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.StringAdvancementTrigger;
import com.freefish.torchesbecomesunlight.server.world.structure.StructureHandle;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.AdvancementProvider;

import java.util.function.Consumer;

public class TBSAdvancements implements AdvancementProvider.AdvancementGenerator {
    Consumer<Advancement> advCon;

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> con, ExistingFileHelper existingFileHelper) {
        this.advCon = con;
        Advancement root = builder(TorchesBecomeSunlight.MOD_ID).display(ItemHandle.TITLE.get(), Component.translatable("torchesbecomesunlight.advancement.title.root"),
                Component.translatable("torchesbecomesunlight.advancement.desc.root"),
                ResourceLocation.parse("textures/block/stone.png"),
                AdvancementType.TASK, false, false, false)
                .addCriterion("tick", PlayerTrigger.TriggerInstance.tick()).save(con, "torchesbecomesunlight:root");

        Advancement main = builder("main").parent(root).display(ItemHandle.TITLE.get(), AdvancementType.TASK)
                .addCriterion("pre",new ChainedAdvancementTrigger.Instance(
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"main_1_village"),
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"main_8_1_frostnova"),
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"main_8_2_patriot")
                )).save(con);
        //Advancement main_1_village = builder("main_1_village").display(ItemHandle.TITLE.get(), AdvancementType.TASK)
        //        .addCriterion("ad_id", new StringAdvancementTrigger.Instance("main_1_village"))
        //        .parent(main).save(advCon);
        //Advancement main_2_villager = saveBasicItem("main_2_villager",ItemHandle.TITLE.get(), main_1_village);
        //Advancement main_3_danger = saveBasicItem("main_3_danger",ItemHandle.TITLE.get(), main_2_villager);
        //Advancement main_4_ursus = saveBasicItem("main_4_ursus",ItemHandle.TITLE.get(), main_3_danger);
        //Advancement main_5_guerrillas = saveBasicItem("main_5_guerrillas",ItemHandle.TITLE.get(), main_4_ursus);
        //Advancement main_6_guerrillas_villager = saveBasicItem("main_6_guerrillas_villager",ItemHandle.TITLE.get(), main_5_guerrillas);
        //Advancement main_7_join_guerrillas = saveBasicItem("main_7_join_guerrillas",ItemHandle.TITLE.get(), main_6_guerrillas_villager);
        Advancement main_8_1_frostnova = builder("main_8_1_frostnova").display(ItemHandle.BLESSING_OF_SAMI.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("main_8_1_frostnova"))
                .parent(main).save(advCon);
        Advancement main_8_2_patriot = builder("main_8_2_patriot").display(ItemHandle.THORN_RING.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("main_8_2_patriot"))
                .parent(main).save(advCon);

        Advancement talk = builder("talk").display(ItemHandle.TITLE.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("talk"))
                .parent(root).save(advCon);

        Advancement cook = builder("cook").parent(root).display(ItemHandle.STEW_POT.get(), AdvancementType.TASK)
                .addCriterion("pre",new ChainedAdvancementTrigger.Instance(
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"cook_cutting"),
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"cook_oven"),
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"cook_stew"))).save(con);

        Advancement cook_cutting = saveBasicItem("cook_cutting",ItemHandle.CUTTING_BOARD.get(), cook);
        Advancement cook_stew = saveBasicItem("cook_stew",ItemHandle.STEW_POT.get(), cook);
        Advancement cook_oven = saveBasicItem("cook_oven",ItemHandle.OVEN.get(), cook);
        Advancement cook_meal = saveBasicItem("cook_meal",ItemHandle.POLAR_STEW_MEAT.get(), cook);

        Advancement partner = builder("partner").parent(root).display(ItemHandle.ROSMONTIS_IPAD.get(), AdvancementType.TASK)
                .addCriterion("pre",new ChainedAdvancementTrigger.Instance(
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"partner_frostnova"),
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"partner_patriot"),
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"partner_pursuer")
                )).save(con);
        Advancement partner_frostnova = builder("partner_frostnova").display(ItemHandle.FROSTED_TALISMAN.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("partner_frostnova"))
                .parent(partner).save(advCon);
        Advancement partner_patriot = builder("partner_patriot").display(ItemHandle.TALISMAN.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("partner_patriot"))
                .parent(partner).save(advCon);
        //Advancement partner_pursuer = builder("partner_pursuer").display(ItemHandle.URSUS_MACHETE.get(), AdvancementType.TASK)
        //        .addCriterion("ad_id", new StringAdvancementTrigger.Instance("partner_pursuer"))
        //        .parent(partner).save(advCon);
        Advancement partner_rosmontis = builder("partner_rosmontis").display(ItemHandle.ROSMONTIS_IPAD.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("partner_rosmontis"))
                .parent(partner).save(advCon);
        //Advancement partner_gun_patriot = builder("partner_gun_patriot").display(ItemHandle.GUN.get(), AdvancementType.TASK)
        //        .addCriterion("ad_id", new StringAdvancementTrigger.Instance("partner_gun_patriot"))
        //        .parent(partner).save(advCon);

        Advancement ursus = builder("ursus").parent(root).display(ItemHandle.MACHETE.get(), AdvancementType.TASK)
                .addCriterion("pre",new ChainedAdvancementTrigger.Instance(
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"ursus_1"))).save(con);
        Advancement ursus_1 = builder("ursus_1").display(ItemHandle.DEMON_EYE.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("ursus_1"))
                .parent(ursus).save(advCon);
        //Advancement ursus_2_run = builder("ursus_2_run").display(ItemHandle.URSUS_MACHETE.get(), AdvancementType.TASK)
        //        .addCriterion("ad_id", new StringAdvancementTrigger.Instance("ursus_2_run"))
        //        .parent(ursus_1).save(advCon);
        Advancement ursus_2_fight = builder("ursus_2_fight").display(ItemHandle.URSUS_MACHETE.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("ursus_2_fight"))
                .parent(ursus_1).save(advCon);
        //Advancement ursus_3_demon = builder("ursus_3_demon").display(ItemHandle.URSUS_MACHETE.get(), AdvancementType.TASK)
        //        .addCriterion("ad_id", new StringAdvancementTrigger.Instance("ursus_3_demon"))
        //        .parent(ursus_2_fight).save(advCon);


        Advancement rhodes = builder("rhodes").parent(root).display(ItemHandle.RHODES_KNIFE.get(), AdvancementType.TASK)
                .addCriterion("pre",new ChainedAdvancementTrigger.Instance(
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"rhodes_1"))).save(con);
        Advancement rhodes_1 = builder("rhodes_1").display(ItemHandle.RHODES_SHIELD.get(), AdvancementType.TASK)
                .addCriterion("near_structure", PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(StructureHandle.RHODE_ISLAND_OFFICE_K)))
                .parent(rhodes).save(advCon);
        Advancement rhodes_2_c = builder("rhodes_2_c").display(ItemHandle.RHODES_SHIELD.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("rhodes_2_c"))
                .parent(rhodes_1).save(advCon);
        Advancement rhodes_3_rosmontis = builder("rhodes_3_rosmontis").display(ItemHandle.ROSMONTIS_EMBRACE.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("rhodes_3_rosmontis"))
                .parent(rhodes_2_c).save(advCon);
        Advancement rhodes_4_trust = builder("rhodes_4_trust").display(ItemHandle.ROSMONTIS_IPAD.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("rhodes_4_trust"))
                .parent(rhodes_3_rosmontis).save(advCon);

        Advancement gunPatriot = builder("gunPatriot").parent(root).display(ItemHandle.TIME.get(), AdvancementType.TASK)
                .addCriterion("pre",new ChainedAdvancementTrigger.Instance(
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"gunPatriot_0_time"),
                        ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"gunPatriot_structure"))).save(con);

        Advancement gunPatriot_0_time = builder("gunPatriot_0_time").display(ItemHandle.LIGHT.get(), AdvancementType.TASK)
                .addCriterion("has_time", InventoryChangeTrigger.TriggerInstance.hasItems(ItemHandle.TIME.get()))
                .addCriterion("has_light", InventoryChangeTrigger.TriggerInstance.hasItems(ItemHandle.LIGHT.get()))
                .requirements(AdvancementRequirements.Strategy.OR)
                .parent(gunPatriot).save(advCon);

        Advancement gunPatriot_structure = builder("gunPatriot_structure").display(ItemHandle.BIG_BEN.get(), AdvancementType.TASK)
                .addCriterion("near_structure", PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(StructureHandle.SANKTA_STATUE_K)))
                .parent(gunPatriot).save(advCon);

        Advancement gunPatriot_1_time = builder("gunPatriot_1_time").display(ItemHandle.INFECTED_GUN.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("gunPatriot_1_time"))
                .parent(gunPatriot_structure).save(advCon);

        Advancement gunPatriot_2_fight = builder("gunPatriot_2_fight").display(ItemHandle.GUN.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("gunPatriot_2_fight"))
                .parent(gunPatriot_1_time).save(advCon);

        Advancement gunPatriot_3_fight = builder("gunPatriot_3_fight").display(ItemHandle.SACRED_HALBERD.get(), AdvancementType.TASK)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("gunPatriot_3_fight"))
                .parent(gunPatriot_2_fight).save(advCon);

        Advancement gunPatriot_4_sakta = builder("gunPatriot_4_sakta").display(ItemHandle.SANKTA_RING.get(), AdvancementType.CHALLENGE)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("gunPatriot_4_sakta"))
                .parent(gunPatriot_3_fight).save(advCon);
        Advancement gunPatriot_5_control = builder("gunPatriot_5_control").display(ItemHandle.SANKTA_RING.get(), AdvancementType.GOAL)
                .addCriterion("ad_id", new StringAdvancementTrigger.Instance("gunPatriot_5_control"))
                .parent(gunPatriot_4_sakta).save(advCon);
        //Advancement gunPatriot_hide = builder("gunPatriot_hide").display(ItemHandle.SANKTA_RING.get(), AdvancementType.CHALLENGE,true)
        //        .addCriterion("ad_id", new StringAdvancementTrigger.Instance("gunPatriot_hide"))
        //        .parent(gunPatriot).save(advCon);
    }

    public AdvancementBuilder buildBasicItem(String s,ItemLike item, Advancement parent){
        return builder(s).normalItemRequirement(item).parent(parent);
    }

    public Advancement saveBasicItem(String s,ItemLike item, Advancement parent){
        return buildBasicItem(s,item, parent).save(advCon);
    }

    public AdvancementBuilder builder(String key){
        return AdvancementBuilder.builder(TorchesBecomeSunlight.MOD_ID, key);
    }
}
