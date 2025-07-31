package com.freefish.torchesbecomesunlight.server.item.weapon;

import com.freefish.torchesbecomesunlight.server.ability.Ability;
import com.freefish.torchesbecomesunlight.server.ability.AbilityHandler;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectInstance;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.List;
import java.util.UUID;

public class WinterPass extends SwordItem{
    private Multimap<Attribute, AttributeModifier> defaultModifiers;
    private static final UUID ENTITY_REACH_UUID = UUID.fromString("5393EB63-DAD9-4B73-A551-C3EFA1F10347");

    public WinterPass(Properties pProperties) {
        super(Tiers.NETHERITE,(int)(-5 + ConfigHandler.COMMON.TOOLs.WINTER_PASS.attackDamageValue), 4,pProperties);
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
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.winter_pass"));
        tooltip.add(Component.translatable("text.torchesbecomesunlight.winter_pass_tool"));
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        boolean b = super.hurtEnemy(pStack, pTarget, pAttacker);
        if(pAttacker instanceof Player player){
            Ability ability = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.USE_PASS_ABILITY);
            if(!pAttacker.level().isClientSide&&ability!=null&&ability.isUsing()){
                ForceEffectInstance forceEffect = ForceEffectHandle.getForceEffect(pTarget, ForceEffectHandle.FROZEN_FORCE_EFFECT);
                if(forceEffect!=null&&forceEffect.getLevel()>1){
                    pTarget.invulnerableTime = 0;
                    forceEffect.discard(pTarget);
                    pTarget.hurt(pAttacker.damageSources().freeze(),ConfigHandler.COMMON.TOOLs.WINTER_PASS.attackDamageValue*0.8f);
                }else {
                    ForceEffectHandle.addForceEffect(pTarget,new ForceEffectInstance(ForceEffectHandle.FROZEN_FORCE_EFFECT,1,40));
                }
            }
        }
        pTarget.invulnerableTime = 5;
        return b;
    }

    public void shootIce(Player player){
        IceCrystal iceCrystal = new IceCrystal(EntityHandle.ICE_CRYSTAL.get(),player.level());
        iceCrystal.setOwner(player);
        iceCrystal.setType(2);

        Vec3 headRotVec1 = FFEntityUtils.getHeadRotVec(player, new Vec3(-0.45, -0.3, 1));
        iceCrystal.setPos(headRotVec1.add(0,1.4,0));

        Vec3 headRotVec = FFEntityUtils.getHeadRotVec(player, new Vec3(0, 0, 1)).subtract(player.position());
        iceCrystal.shoot(headRotVec.x, headRotVec.y, headRotVec.z, 3F, 0.1f);
        player.level().addFreshEntity(iceCrystal);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);

        int i1 = ConfigHandler.COMMON.TOOLs.WINTER_PASS.skillAmount1.get();
        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(pPlayer, CapabilityHandle.PLAYER_CAPABILITY);
        if(pUsedHand == InteractionHand.MAIN_HAND&&capability!=null){
            if(!pLevel.isClientSide&&capability.getSkillAmount()>i1){
                AbilityHandler.INSTANCE.sendAbilityMessage(pPlayer,AbilityHandler.USE_PASS_ABILITY);
                capability.setSkillAmount(capability.getSkillAmount()-i1,pPlayer);
            }
            return InteractionResultHolder.success(itemInHand);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    public Multimap<Attribute, AttributeModifier> creatAttributesFromConfig() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", ConfigHandler.COMMON.TOOLs.WINTER_PASS.attackDamageValue - 1, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -0.5, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(ENTITY_REACH_UUID, "Weapon modifier", -1D, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    public void refreshAttributesFromConfig() {
        this.defaultModifiers = this.creatAttributesFromConfig();
    }
}
