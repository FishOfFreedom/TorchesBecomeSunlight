package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
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

public class PursuerModel extends GeoModel<Pursuer> {
    private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/pursuer.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/pursuer.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/pursuer.json");

    @Override
    public void setCustomAnimations(Pursuer animatable, long instanceId, AnimationState<Pursuer> animationState) {
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        CoreGeoBone m_guanzi = this.getAnimationProcessor().getBone("m_guanzi");
        CoreGeoBone r_guanzi = this.getAnimationProcessor().getBone("r_guanzi");
        CoreGeoBone l_guanzi = this.getAnimationProcessor().getBone("l_guanzi");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        if(animatable.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            float rotX = Mth.clamp(extraData.headPitch(),-10,10) * 0.017453292F;
            float rotY = Mth.clamp(extraData.netHeadYaw(),-40,40) * 0.017453292F;

            head.setRotX(rotX);
            head.setRotY(rotY);
            m_guanzi.setRotY(-rotY*0.6f);
            r_guanzi.setRotY(-rotY);
            l_guanzi.setRotY(-rotY);
        }

        CoreGeoBone sword = this.getAnimationProcessor().getBone("sword");
        CoreGeoBone sword2 = this.getAnimationProcessor().getBone("sword2");

        if(animatable.getPredicate()!=0){
            sword2.setHidden(false);
            sword.setHidden(true);
        }
        else {
            sword2.setHidden(true);
            sword.setHidden(false);
        }


        CoreGeoBone swordLocate1 = this.getAnimationProcessor().getBone("swordlocate_1");
        CoreGeoBone swordLocate2 = this.getAnimationProcessor().getBone("swordlocate_2");
        Vec3 vec31 = MathUtils.getWorldPosFromModel(animatable, animatable.yBodyRot, (GeoBone) swordLocate1);
        Vec3 vec32 = MathUtils.getWorldPosFromModel(animatable, animatable.yBodyRot, (GeoBone) swordLocate2);
        animatable.updateTrail(vec31, vec32);
    }

    @Override
    public ResourceLocation getModelResource(Pursuer animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Pursuer animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Pursuer animatable) {
        return ANIMATION;
    }
}
