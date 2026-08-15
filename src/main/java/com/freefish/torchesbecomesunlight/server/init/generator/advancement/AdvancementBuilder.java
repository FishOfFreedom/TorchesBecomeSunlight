package com.freefish.torchesbecomesunlight.server.init.generator.advancement;

import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.ChainedAdvancementTrigger;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.StringAdvancementTrigger;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.TriggerHandler;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.registries.BuiltInRegistries;
import com.google.common.collect.Maps;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Copied from Advancements.Builder with extentions to reduce copy pasta
 */
public class AdvancementBuilder{
    @Nullable
    private ResourceLocation parentId;
    @Nullable
    private AdvancementHolder parent;
    @Nullable
    private DisplayInfo display;
    private AdvancementRewards rewards = AdvancementRewards.EMPTY;
    private Map<String, Criterion<?>> criteria = Maps.newLinkedHashMap();
    @Nullable
    private AdvancementRequirements requirements;
    private AdvancementRequirements.Strategy requirementsStrategy = AdvancementRequirements.Strategy.AND;
    private String modid;
    private String fileKey;

    private AdvancementBuilder(@Nullable ResourceLocation pParentId, @Nullable DisplayInfo pDisplay, AdvancementRewards pRewards, Map<String, Criterion<?>> pCriteria, AdvancementRequirements pRequirements) {
        this.parentId = pParentId;
        this.display = pDisplay;
        this.rewards = pRewards;
        this.criteria = pCriteria;
        this.requirements = pRequirements;
    }

    private AdvancementBuilder(String modid, String fileKey) {
        this.modid = modid;
        this.fileKey = fileKey;
    }

    public static AdvancementBuilder builder(String modid, String fileKey) {
        return new AdvancementBuilder(modid, fileKey);
    }

    public AdvancementBuilder parent(AdvancementHolder pParent) {
        this.parent = pParent;
        return this;
    }

    public AdvancementBuilder parent(ResourceLocation pParentId) {
        this.parentId = pParentId;
        return this;
    }

    public AdvancementBuilder display(ItemStack pStack, Component pTitle, Component pDescription, @Nullable ResourceLocation pBackground, AdvancementType pFrame, boolean pShowToast, boolean pAnnounceToChat, boolean pHidden) {
        return this.display(new DisplayInfo(pStack, pTitle, pDescription, java.util.Optional.ofNullable(pBackground), pFrame, pShowToast, pAnnounceToChat, pHidden));
    }

    public AdvancementBuilder display(ItemLike pItem, Component pTitle, Component pDescription, @Nullable ResourceLocation pBackground, AdvancementType pFrame, boolean pShowToast, boolean pAnnounceToChat, boolean pHidden) {
        return this.display(new DisplayInfo(new ItemStack(pItem.asItem()), pTitle, pDescription, java.util.Optional.ofNullable(pBackground), pFrame, pShowToast, pAnnounceToChat, pHidden));
    }

    public AdvancementBuilder display(DisplayInfo pDisplay) {
        this.display = pDisplay;
        return this;
    }

    // The following displays cannot be used for roots.
    public AdvancementBuilder display(ItemStack pItem, AdvancementType pFrame) {
        return this.display(new DisplayInfo(pItem, this.getComponent("title"), this.getComponent("desc"), java.util.Optional.empty(), pFrame, true, true, false));
    }

    public AdvancementBuilder display(ItemLike pItem, AdvancementType pFrame) {
        return this.display(new ItemStack(pItem), pFrame);
    }

    // The following displays cannot be used for roots.
    public AdvancementBuilder display(ItemStack pItem, AdvancementType pFrame, boolean hidden) {
        return this.display(new DisplayInfo(pItem, this.getComponent("title"), this.getComponent("desc"), java.util.Optional.empty(), pFrame, true, true, hidden));
    }

    public AdvancementBuilder display(ItemLike pItem, AdvancementType pFrame, boolean hidden) {
        return this.display(new ItemStack(pItem), pFrame, hidden);
    }


    public AdvancementBuilder rewards(AdvancementRewards.Builder pRewardsBuilder) {
        return this.rewards(pRewardsBuilder.build());
    }

    public AdvancementBuilder rewards(AdvancementRewards pRewards) {
        this.rewards = pRewards;
        return this;
    }

    public AdvancementBuilder addCriterion(String pKey, ChainedAdvancementTrigger.Instance pCriterion) {
        return this.addCriterion(pKey, new Criterion<>(TriggerHandler.CHAINED_ADVANCEMENT_TRIGGER.get(), pCriterion));
    }

    public AdvancementBuilder addCriterion(String pKey, StringAdvancementTrigger.Instance pCriterion) {
        return this.addCriterion(pKey, new Criterion<>(TriggerHandler.STRING_ADVANCEMENT_TRIGGER.get(), pCriterion));
    }

    public AdvancementBuilder addCriterion(String pKey, Criterion<?> pCriterion) {
        if (this.criteria.containsKey(pKey)) {
            throw new IllegalArgumentException("Duplicate criterion " + pKey);
        } else {
            this.criteria.put(pKey, pCriterion);
            return this;
        }
    }

    public AdvancementBuilder requirements(AdvancementRequirements.Strategy pStrategy) {
        this.requirementsStrategy = pStrategy;
        return this;
    }

    public AdvancementBuilder requirements(AdvancementRequirements pRequirements) {
        this.requirements = pRequirements;
        return this;
    }

    public AdvancementBuilder normalItemRequirement(ItemLike item){
        return this.display(item, AdvancementType.TASK).requireItem(item);
    }

    public AdvancementBuilder requireItem(ItemLike item){
        return this.addCriterion("has_" + BuiltInRegistries.ITEM.getKey(item.asItem()).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(item));
    }

    public MutableComponent getComponent(String type){
        return Component.translatable(modid + ".adv." + type + "." + fileKey);
    }

    /**
     * Tries to resolve the parent of this advancement, if possible. Returns true on success.
     */
    public boolean canBuild(Function<ResourceLocation, AdvancementHolder> pParentLookup) {
        if (this.parentId == null) {
            return true;
        } else {
            if (this.parent == null) {
                this.parent = pParentLookup.apply(this.parentId);
            }

            return this.parent != null;
        }
    }

    public AdvancementHolder build(ResourceLocation pId) {
        if (!this.canBuild((p_138407_) -> {
            return null;
        })) {
            throw new IllegalStateException("Tried to build incomplete advancement!");
        } else {
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.create(this.criteria.keySet());
            }

            return new AdvancementHolder(pId, new Advancement(
                    java.util.Optional.ofNullable(this.parent).map(AdvancementHolder::id),
                    java.util.Optional.ofNullable(this.display),
                    this.rewards, this.criteria, this.requirements, false));
        }
    }

    public AdvancementHolder save(Consumer<AdvancementHolder> pConsumer, String pId) {
        AdvancementHolder advancement = this.build(ResourceLocation.parse(pId));
        pConsumer.accept(advancement);
        return advancement;
    }

    public AdvancementHolder save(Consumer<AdvancementHolder> pConsumer) {
        return this.save(pConsumer, ResourceLocation.fromNamespaceAndPath(modid, fileKey).toString());
    }

    public String toString() {
        return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + this.requirements + "}";
    }

    public Map<String, Criterion<?>> getCriteria() {
        return this.criteria;
    }
}

