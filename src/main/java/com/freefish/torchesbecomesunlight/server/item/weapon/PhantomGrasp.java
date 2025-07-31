package com.freefish.torchesbecomesunlight.server.item.weapon;

import com.freefish.torchesbecomesunlight.client.render.Item.PhantomGraspRenderer;
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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PhantomGrasp extends PickaxeItem implements GeoItem {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private Multimap<Attribute, AttributeModifier> defaultModifiers;
    private static final UUID ENTITY_REACH_UUID = UUID.fromString("5393EB63-DAD9-4B73-A551-C3EFA1F10347");
    private static final UUID BLOCK_REACH_UUID = UUID.fromString("5393EB63-DAD9-4B73-A111-C3EFA1F10347");
    public PhantomGrasp(Properties pProperties) {
        super(Tiers.NETHERITE, (int)(-5 + ConfigHandler.COMMON.TOOLs.PHANTOM_GRASP.attackDamageValue), 4, pProperties);
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
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        if (pLivingEntity instanceof Player player) {
            int i = this.getUseDuration(pStack) - pTimeCharged;
            CompoundTag orCreateTag = pStack.getOrCreateTag();
            int i2 = ConfigHandler.COMMON.TOOLs.PHANTOM_GRASP.skillAmount1.get();
            if (i >= 20) {
                int i1 = Math.min(3,i / 20);
                orCreateTag.putInt("tbsisActive", i1);
                if (!pLevel.isClientSide) {
                    PlayerCapability.IPlayerCapability playerCapability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                    if (playerCapability != null&&playerCapability.getSkillAmount()>i2) {
                        playerCapability.setHalberdUseTime(i);
                        playerCapability.setSkillAmount(playerCapability.getSkillAmount()-i2,pLivingEntity);
                        AbilityHandler.INSTANCE.sendAbilityMessage(player, AbilityHandler.USE_ROSMONTIS_GRASP_ABILITY);
                    }
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (pHand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.fail(itemstack);
        } else if (EnchantmentHelper.getRiptide(itemstack) > 0 && !pPlayer.isInWaterOrRain()) {
            return InteractionResultHolder.fail(itemstack);
        }else  {
            pPlayer.startUsingItem(pHand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer = new PhantomGraspRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.phantom_grasp"));
        tooltip.add(Component.translatable("text.torchesbecomesunlight.phantom_grasp_tool"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    public Multimap<Attribute, AttributeModifier> creatAttributesFromConfig() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", ConfigHandler.COMMON.TOOLs.PHANTOM_GRASP.attackDamageValue - 1, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ENTITY_REACH_UUID, "Weapon modifier", 4D, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(BLOCK_REACH_UUID, "Weapon modifier", 6D, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    public void refreshAttributesFromConfig() {
        this.defaultModifiers = this.creatAttributesFromConfig();
    }
}
