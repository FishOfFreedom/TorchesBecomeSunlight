package com.freefish.torchesbecomesunlight.server.item.weapon;

import com.freefish.torchesbecomesunlight.client.render.Item.LightGunRenderer;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectInstance;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class LightGun extends Item implements GeoItem {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public LightGun(Properties pProperties) {
        super(pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer = new LightGunRenderer();
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.light"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        ItemStack itemstack = player.getItemInHand(pUsedHand);

        if(pUsedHand==InteractionHand.MAIN_HAND){

            if(!level.isClientSide){
                Vec3 vec33 = FFEntityUtils.getHeadRotVec(player,new Vec3(0,0,10)).add(0,player.getEyeHeight(),0);
                Vec3 vec32 = player.getEyePosition();

                HitResult hitresult = level.clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                if (hitresult.getType() != HitResult.Type.MISS) {
                    vec33 = hitresult.getLocation();
                }

                EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33,player);
                if (entityhitresult != null) {
                    Entity entity = entityhitresult.getEntity();
                    if(entity instanceof LivingEntity living){
                        living.hurt(player.damageSources().playerAttack(player),10f);
                        ForceEffectHandle.addForceEffect(living,new ForceEffectInstance(ForceEffectHandle.LIGHTING_FORCE_EFFECT,1,20));
                    }
                }
            }

            player.startUsingItem(InteractionHand.MAIN_HAND);
            player.getCooldowns().addCooldown(itemstack.getItem(),80);
            return InteractionResultHolder.success(itemstack);
        }
        return super.use(level, player, pUsedHand);
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec,Entity shoot) {
        return ProjectileUtil.getEntityHitResult(shoot.level(), shoot, pStartVec, pEndVec, shoot.getBoundingBox().expandTowards(pEndVec.subtract(pStartVec)).inflate(1.0D), (e)-> true);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
