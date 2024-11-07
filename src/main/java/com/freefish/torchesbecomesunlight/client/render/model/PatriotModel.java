package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PatriotModel extends GeoModel<Patriot> {
    @Override
    public void setCustomAnimations(Patriot animatable, long instanceId, AnimationState<Patriot> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone halberdLock = getAnimationProcessor().getBone("halberd_lock");
        if(animatable.time != animatable.tickCount){
            animatable.time = animatable.tickCount;
            Vec3 worldPosFromModel = MathUtils.getWorldPosFromModel(animatable, animatable.yBodyRot, (GeoBone)halberdLock);
            animatable.setClientVectors(0,worldPosFromModel);
        }
        
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        if(animatable.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            head.setRotX(extraData.headPitch() * 0.017453292F);
            head.setRotY(extraData.netHeadYaw() * 0.017453292F);
        }

        CoreGeoBone main = this.getAnimationProcessor().getBone("main");
        if(animatable.getAnimation()== Patriot.CYCLE&&animatable.getAnimationTick()==42) {
            //todo
        }
        //addShieldFX(((float)animatable.tickCount/5));
    }

    @Override
    public ResourceLocation getModelResource(Patriot animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/patriot.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Patriot animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/patriot/patriot.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Patriot animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/patriot.animation.json");
    }

    public void addShieldFX(float animTime){
        animTime /= 2;
        CoreGeoBone shieldFX1 = getAnimationProcessor().getBone("shieldFX1");
        CoreGeoBone shieldFX3 = getAnimationProcessor().getBone("shieldFX3");
        CoreGeoBone shieldFX2 = getAnimationProcessor().getBone("shieldFX2");
        CoreGeoBone shieldFX4 = getAnimationProcessor().getBone("shieldFX4");
        CoreGeoBone shieldFX5 = getAnimationProcessor().getBone("shieldFX5");
        CoreGeoBone shieldFX6 = getAnimationProcessor().getBone("shieldFX6");
        CoreGeoBone shieldFX7 = getAnimationProcessor().getBone("shieldFX7");
        CoreGeoBone shieldFX8 = getAnimationProcessor().getBone("shieldFX8");
        CoreGeoBone shieldFX9 = getAnimationProcessor().getBone("shieldFX9");

        setScale(shieldFX1, 1 - animTime%1);
        setScale(shieldFX3,1 - (animTime-0.2f)%1);
        setScale(shieldFX2,hex(animTime/3,0));
        setScale(shieldFX4,hex(animTime/3,0.3f));
        setScale(shieldFX5,hex(animTime/3,0));
        setScale(shieldFX6,hex(animTime/3,0.6f));
        setScale(shieldFX7,hex(animTime/3,0.3f));
        setScale(shieldFX8,hex(animTime/3,0));
        setScale(shieldFX9,hex(animTime/3,0.6f));
    }
    
    public float hex(float animTime,float offset){
        float f1 = Mth.clamp((Math.abs((animTime+offset)%2-1)-0.375f),0,0.25f);
        return f1<=0?f1:f1+(float) Math.cos(animTime/offset)*0.05f;
    }

    private void setScale(CoreGeoBone bone,float scale){
        bone.setScaleX(scale);
        bone.setScaleY(scale);
        bone.setScaleZ(scale);
    }
}
