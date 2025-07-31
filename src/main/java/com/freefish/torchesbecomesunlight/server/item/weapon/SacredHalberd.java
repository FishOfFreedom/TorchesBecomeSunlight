package com.freefish.torchesbecomesunlight.server.item.weapon;

import com.freefish.torchesbecomesunlight.client.render.Item.SacredHalberdRenderer;
import com.freefish.torchesbecomesunlight.server.ability.AbilityHandler;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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

public class SacredHalberd extends SwordItem implements GeoItem {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private Multimap<Attribute, AttributeModifier> defaultModifiers;
    private static final UUID ENTITY_REACH_UUID = UUID.fromString("5393EB63-DAD9-4B73-A551-C3EFA1F10347");
    public SacredHalberd(Properties pProperties) {
        super(Tiers.NETHERITE, (int)(-5 + ConfigHandler.COMMON.TOOLs.SACRED_HALBERD.attackDamageValue), 4, pProperties);
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
            private BlockEntityWithoutLevelRenderer renderer = new SacredHalberdRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.sacred_halberd"));
        tooltip.add(Component.translatable("text.torchesbecomesunlight.sacred_halberd_tool"));
        tooltip.add(Component.translatable("text.torchesbecomesunlight.sacred_halberd_tool1"));
        tooltip.add(Component.translatable("text.torchesbecomesunlight.sacred_halberd_tool2"));
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
        super.releaseUsing(pStack, pLevel, pLivingEntity, pTimeCharged);
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
        int sk1 = ConfigHandler.COMMON.TOOLs.SACRED_HALBERD.skillAmount1.get();
        int sk2 = ConfigHandler.COMMON.TOOLs.SACRED_HALBERD.skillAmount2.get();

        if(pHand == InteractionHand.MAIN_HAND){
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(pPlayer, CapabilityHandle.PLAYER_CAPABILITY);
            if(!pLevel.isClientSide&&capability!=null){
                if(pPlayer.isShiftKeyDown()){
                    if(capability.getSkillAmount()>sk2) {
                        int sacredHalberd = capability.getSacredHalberd();
                        if (sacredHalberd>5) {
                            AbilityHandler.INSTANCE.sendAbilityMessage(pPlayer, AbilityHandler.USE_SHALBERD_LIGHTWIND_ABILITY);
                        } else {
                            AbilityHandler.INSTANCE.sendAbilityMessage(pPlayer, AbilityHandler.USE_SHALBERD_WIND_ABILITY);
                        }
                        capability.setSkillAmount(capability.getSkillAmount()-sk2,pPlayer);
                    }
                }else {
                    if(capability.getSkillAmount()>sk1){
                        AbilityHandler.INSTANCE.sendAbilityMessage(pPlayer, AbilityHandler.USE_SHALBERD_LIGHT_ABILITY);
                        capability.setSkillAmount(capability.getSkillAmount()-sk1,pPlayer);
                    }
                }
            }
            return InteractionResultHolder.consume(itemstack);
        }
        return super.use(pLevel, pPlayer, pHand);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    public Multimap<Attribute, AttributeModifier> creatAttributesFromConfig() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", ConfigHandler.COMMON.TOOLs.SACRED_HALBERD.attackDamageValue - 1, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ENTITY_REACH_UUID, "Weapon modifier", 2.5D, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    public void refreshAttributesFromConfig() {
        this.defaultModifiers = this.creatAttributesFromConfig();
    }
}
