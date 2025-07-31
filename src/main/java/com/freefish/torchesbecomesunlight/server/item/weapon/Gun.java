package com.freefish.torchesbecomesunlight.server.item.weapon;

import com.freefish.torchesbecomesunlight.client.render.Item.GunRenderer;
import com.freefish.torchesbecomesunlight.server.ability.AbilityHandler;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class Gun extends Item implements GeoItem {
    private static final RawAnimation POPUP_ANIM = RawAnimation.begin().thenLoop("shot");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("shot");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public Gun(Properties pProperties) {
        super(pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }


    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer = new GunRenderer();
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        if (pLivingEntity instanceof Player player) {
            if(!pLevel.isClientSide){
                triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(player.getUsedItemHand()),(ServerLevel) pLevel), "gun_controller", "idle");
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.gun"));
        tooltip.add(Component.translatable("text.torchesbecomesunlight.gun_tool"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack itemstack = player.getItemInHand(pUsedHand);

        if(pUsedHand==InteractionHand.MAIN_HAND){
            if(!player.isShiftKeyDown()){
                if (!level.isClientSide) {
                    triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(player.getUsedItemHand()), (ServerLevel) level), "gun_controller", "gun");
                }
                player.startUsingItem(InteractionHand.MAIN_HAND);
                return InteractionResultHolder.success(itemstack);
            }else {
                PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                if(!level.isClientSide&&capability!=null) {
                    int i = ConfigHandler.COMMON.TOOLs.SACRED_GUN.skillAmount1.get();
                    if(capability.getSkillAmount()>i){
                        AbilityHandler.INSTANCE.sendAbilityMessage(player, AbilityHandler.USE_GUN_ABILITY);
                        capability.setSkillAmount(capability.getSkillAmount()-i, player);
                        return InteractionResultHolder.fail(itemstack);
                    }
                }
            }
        }
        return super.use(level, player, pUsedHand);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "gun_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("gun", POPUP_ANIM)
                .triggerableAnim("idle", IDLE)
                .setSoundKeyframeHandler(state -> {
                }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
