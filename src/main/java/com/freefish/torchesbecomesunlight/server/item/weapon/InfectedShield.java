package com.freefish.torchesbecomesunlight.server.item.weapon;

import com.freefish.torchesbecomesunlight.client.render.Item.InfectedShieldRenderer;
import com.freefish.torchesbecomesunlight.server.entity.effect.StompEntity;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class InfectedShield extends ShieldItem implements GeoItem{
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private boolean isUsed = false;

    public InfectedShield(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer = new InfectedShieldRenderer();

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
            if (i >= 30) {
                if (!pLevel.isClientSide) {
                    StompEntity stompEntity = new StompEntity(pLevel,8,player,3);
                    stompEntity.setPos(player.position().add(new Vec3(0, 0, 4.5).yRot((float) (-player.yBodyRot / 180 * Math.PI))));
                    player.level().addFreshEntity(stompEntity);
                    player.playSound(SoundHandle.GROUND.get(), 1.3F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 0.8F));

                    List<Monster> monsters = pLevel.getEntitiesOfClass(Monster.class,player.getBoundingBox().inflate(10),entity->
                            entity.distanceTo(player) <10 );
                    for(Monster monster:monsters){
                        monster.setTarget(player);
                    }
                }
            }
        }
    }

    @Override
    public boolean useOnRelease(ItemStack pStack) {
        isUsed = false;
        return super.useOnRelease(pStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        isUsed = true;
        return super.use(pLevel, pPlayer, pHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.shield"));
    }

    private final AnimationController<InfectedShield> animationController1 = new AnimationController<InfectedShield>(this, "IceController", 5, this::icePredicate);

    private PlayState icePredicate(AnimationState<InfectedShield> event) {
        if (isUsed)
            event.setAnimation(RawAnimation.begin().thenPlayAndHold("hold"));
        else
            event.setAnimation(RawAnimation.begin().thenPlayAndHold("none"));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar event) {
        event.add(animationController1);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
