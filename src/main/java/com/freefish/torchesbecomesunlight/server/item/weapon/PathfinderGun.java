package com.freefish.torchesbecomesunlight.server.item.weapon;

import com.freefish.torchesbecomesunlight.client.render.Item.PathfinderGunRenderer;
import com.freefish.torchesbecomesunlight.server.entity.projectile.Bullet;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
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
import net.minecraft.world.phys.Vec3;
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

public class PathfinderGun extends Item implements GeoItem {
    private static final RawAnimation POPUP_ANIM = RawAnimation.begin().thenPlayAndHold("fire");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public PathfinderGun(Properties pProperties) {
        super(pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }


    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer = new PathfinderGunRenderer();
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        if (pLivingEntity instanceof Player player) {
            int i = this.getUseDuration(pStack) - pTimeCharged;
            if(i>30&&!pLevel.isClientSide){
                triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(player.getUsedItemHand()),(ServerLevel) pLevel), "gun_controller", "gun");
                shootArtilleryBullet(pLivingEntity, FFEntityUtils.getHeadRotVec(pLivingEntity,new Vec3(0,0,5)).add(0,1,0),pLivingEntity.position().add(0,1,0));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.pathfinder_gun"));
        tooltip.add(Component.translatable("text.torchesbecomesunlight.pathfinder_gun_tool"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack itemstack = player.getItemInHand(pUsedHand);

        if(pUsedHand==InteractionHand.MAIN_HAND){
            if (!level.isClientSide) {
                triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(player.getUsedItemHand()), (ServerLevel) level), "gun_controller", "idle");
            }
            player.startUsingItem(InteractionHand.MAIN_HAND);
            return InteractionResultHolder.success(itemstack);
        }
        return super.use(level, player, pUsedHand);
    }

    public void shootArtilleryBullet(LivingEntity shoot,Vec3 target, Vec3 vec3) {
        Bullet abstractarrow = new Bullet(shoot.level(),shoot,1);
        abstractarrow.setPos(vec3);

        Vec3 move = target.subtract(vec3).normalize().scale(2);
        shoot.level().addFreshEntity(abstractarrow);
        abstractarrow.shoot(move.x, move.y , move.z, 0);
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
