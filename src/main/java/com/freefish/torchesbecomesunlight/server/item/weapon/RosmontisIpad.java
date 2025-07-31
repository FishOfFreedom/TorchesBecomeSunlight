package com.freefish.torchesbecomesunlight.server.item.weapon;

import com.freefish.torchesbecomesunlight.client.render.Item.RosmontisIpadRenderer;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.Rosmontis;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.TriggerHandler;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerHandler;
import com.freefish.torchesbecomesunlight.server.partner.PartnerType;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import com.freefish.torchesbecomesunlight.server.story.PlayerStoryStoneData;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class RosmontisIpad extends SummonItem<Rosmontis> implements GeoItem{
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public RosmontisIpad(Properties pProperties) {
        super(pProperties,Rosmontis.class);
    }

    @Override
    public Rosmontis create(Level level) {
        return new Rosmontis(EntityHandle.ROSMONTIS.get(), level);
    }

    @Override
    public PartnerType getPartnerType() {
        return PartnerHandler.ROSMONTIS_PARTNER;
    }

    @Override
    public void whenSummonSuccess(Player player,Rosmontis patriot) {
        if(patriot!=null&&patriot.isOnStoryGround){
            patriot.isOnStoryGround = false;
        }
        if(player instanceof ServerPlayer serverPlayer){
            TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(serverPlayer, "partner_rosmontis");
        }
    }

    @Override
    public boolean testPlayerStory(PlayerStoryStoneData storyStoneData) {
        return storyStoneData.isCanSummonRosmontis();
    }

    @Override
    public boolean test(Rosmontis entity) {
        Partner<?> partner = PartnerUtil.getPartner(entity);
        if (partner != null||entity.getDialogueEntity()!=null) {
            return true;
        }
        return false;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer = new RosmontisIpadRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.rosmontis_ipad"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar event) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
