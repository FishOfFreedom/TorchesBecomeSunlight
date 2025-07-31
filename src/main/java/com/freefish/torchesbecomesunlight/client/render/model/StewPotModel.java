package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.StewPotBlock;
import com.freefish.torchesbecomesunlight.server.block.blockentity.StewPotBlockEntity;
import com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum.StewPotEnum;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class StewPotModel extends GeoModel<StewPotBlockEntity> {
    public static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"geo/stew_pot.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/block/stew_pot.png");
    @Override
    public ResourceLocation getModelResource(StewPotBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(StewPotBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(StewPotBlockEntity animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(StewPotBlockEntity animatable, long instanceId, AnimationState<StewPotBlockEntity> animationState) {

        CoreGeoBone swordLocate1 = this.getAnimationProcessor().getBone("firewood_1");
        CoreGeoBone swordLocate2 = this.getAnimationProcessor().getBone("firewood_2");
        CoreGeoBone swordLocate3 = this.getAnimationProcessor().getBone("firewood_2");
        StewPotEnum value = animatable.getBlockState().getValue(StewPotBlock.STEW_POT_ENUM_PROPERTY);
        if(value==StewPotEnum.Firewood_1){
            swordLocate1.setHidden(true);
            swordLocate2.setHidden(true);
            swordLocate3.setHidden(true);
        }else if(value==StewPotEnum.Firewood_2){
            swordLocate1.setHidden(false);
            swordLocate2.setHidden(true);
            swordLocate3.setHidden(true);
        }else if(value==StewPotEnum.Firewood_3){
            swordLocate1.setHidden(false);
            swordLocate2.setHidden(false);
            swordLocate3.setHidden(true);
        }else if(value==StewPotEnum.Firewood_4){
            swordLocate1.setHidden(false);
            swordLocate2.setHidden(false);
            swordLocate3.setHidden(false);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}
