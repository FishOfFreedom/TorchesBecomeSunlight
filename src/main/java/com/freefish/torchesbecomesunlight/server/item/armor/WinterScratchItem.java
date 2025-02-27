package com.freefish.torchesbecomesunlight.server.item.armor;

import com.freefish.torchesbecomesunlight.client.render.Item.WinterScratchRenderer;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public final class WinterScratchItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WinterScratchItem(ArmorMaterial armorMaterial, Type type, Properties properties) {
        super(armorMaterial, type, properties);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if(player.tickCount%100==0){
            List<Mob> list = level.getEntitiesOfClass(Mob.class,player.getBoundingBox().inflate(6), entity ->
                    entity.distanceTo(player)<6&&entity.getTarget()==player);
            int size = list.size();
            if(size!=0) {
                LivingEntity target = list.get(player.getRandom().nextInt(size));
                if (target != null && !level.isClientSide) {
                    IceCrystal abstractarrow = new IceCrystal(level, player);
                    Vec3 position = player.position().add(new Vec3(0, 1.5, 0));
                    abstractarrow.setPos(position);
                    double d0 = target.getX() - player.getX();
                    double d1 = target.getY(0.4D) - player.getY();
                    double d2 = target.getZ() - player.getZ();

                    abstractarrow.shoot(d0, d1, d2, 2F, 0);
                    level.addFreshEntity(abstractarrow);
                }
            }
        }
        super.onArmorTick(stack, level, player);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null)
                    this.renderer = new WinterScratchRenderer();

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.winter_scratch"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}