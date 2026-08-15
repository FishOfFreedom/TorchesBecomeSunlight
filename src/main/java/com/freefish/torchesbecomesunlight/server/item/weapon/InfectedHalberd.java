package com.freefish.torchesbecomesunlight.server.item.weapon;

import com.freefish.torchesbecomesunlight.server.util.TBSItemData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.resources.ResourceLocation;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.Item.InfectedHalberdRenderer;
import com.freefish.torchesbecomesunlight.server.ability.AbilityHandler;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.NeoForgeMod;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class InfectedHalberd extends SwordItem implements GeoItem {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private ItemAttributeModifiers defaultModifiers;
    private static final UUID ENTITY_REACH_UUID = UUID.fromString("5393EB63-DAD9-4B73-A551-C3EFA1F10347");
    public InfectedHalberd(Properties pProperties) {
        super(Tiers.NETHERITE, pProperties.attributes(SwordItem.createAttributes(Tiers.NETHERITE, (int)(-5 + ConfigHandler.COMMON.TOOLs.HALBERD.attackDamageValue), 4.0F)));
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack p_77616_1_) {
        return true;
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public int getDamage(ItemStack stack) {
        return 0;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 0;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer = new InfectedHalberdRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.halberd"));
        tooltip.add(Component.translatable("text.torchesbecomesunlight.halberd_tool"));
        tooltip.add(Component.translatable("text.torchesbecomesunlight.halberd_tool1"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        if (pLivingEntity instanceof Player player) {
            int i2 = ConfigHandler.COMMON.TOOLs.HALBERD.skillAmount2.get();
            int i = this.getUseDuration(pStack) - pTimeCharged;
            CompoundTag orCreateTag = TBSItemData.getTag(pStack);
            orCreateTag.putBoolean("tbsisActive", false);
            TBSItemData.setTag(pStack, orCreateTag);
            if (i >= 20) {
                orCreateTag.putBoolean("tbsisActive", true);
                TBSItemData.setTag(pStack, orCreateTag);
                if (!pLevel.isClientSide) {
                    PlayerCapability.IPlayerCapability playerCapability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                    if (playerCapability != null&&playerCapability.getSkillAmount()>i2) {
                        playerCapability.setHalberdUseTime(i);
                        playerCapability.setSkillAmount(playerCapability.getSkillAmount()-i2,player);
                        AbilityHandler.INSTANCE.sendAbilityMessage(player, AbilityHandler.USE_HALBERD_ABILITY);
                    }
                }
            }
        }
    }

    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.SPEAR;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        int i1 = ConfigHandler.COMMON.TOOLs.HALBERD.skillAmount1.get();

        if (pHand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.fail(itemstack);
        }else if (pPlayer.isShiftKeyDown()) {
            if(!pLevel.isClientSide()){
                PlayerCapability.IPlayerCapability playerCapability = CapabilityHandle.getCapability(pPlayer, CapabilityHandle.PLAYER_CAPABILITY);
                if (playerCapability != null&&playerCapability.getSkillAmount()>i1) {
                    playerCapability.setSkillAmount(playerCapability.getSkillAmount()-i1,pPlayer);
                    AbilityHandler.INSTANCE.sendAbilityMessage(pPlayer, AbilityHandler.USE_HALBERD_CHI_ABILITY);
                }
            }
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, itemstack);
        } else if (EnchantmentHelper.getRiptide(itemstack) > 0 && !pPlayer.isInWaterOrRain()) {
            return InteractionResultHolder.fail(itemstack);
        }else  {
            int i = this.getUseDuration(itemstack) - pPlayer.getUseItemRemainingTicks();
            CompoundTag orCreateTag = TBSItemData.getTag(itemstack);
            orCreateTag.putBoolean("tbsisActive", true);
            TBSItemData.setTag(itemstack, orCreateTag);
            pPlayer.startUsingItem(pHand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return this.defaultModifiers;
    }

    public ItemAttributeModifiers creatAttributesFromConfig() {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "weapon_modifier"), ConfigHandler.COMMON.TOOLs.HALBERD.attackDamageValue - 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "weapon_modifier"), -3.3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        builder.add(NeoForgeMod.ENTITY_REACH.get(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "weapon_modifier"), 2.5D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        return builder.build();
    }

    public void refreshAttributesFromConfig() {
        this.defaultModifiers = this.creatAttributesFromConfig();
    }
}
