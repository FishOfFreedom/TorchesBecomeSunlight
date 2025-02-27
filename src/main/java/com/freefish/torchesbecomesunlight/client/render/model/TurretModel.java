package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.dlc.Turret;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.core.molang.MolangQueries;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.util.RenderUtils;

public class TurretModel extends GeoModel<Turret> {
    private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/turret.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/turret.animation.json");

    @Override
    public ResourceLocation getModelResource(Turret object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Turret object) {
        return GunKnightPatriotModel.TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Turret animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Turret animatable, long instanceId, AnimationState<Turret> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);

        CoreGeoBone sword = this.getAnimationProcessor().getBone("gun");
        CoreGeoBone gun_1 = this.getAnimationProcessor().getBone("shoot");

        //if(animatable.getPredicate()!=0){
        //    sword2.setHidden(false);
        //    sword.setHidden(true);
        //}
        //else {
        //    sword2.setHidden(true);
        //    sword.setHidden(false);
        //}
        gun_1.setRotX(animatable.getXRot()* 0.017453292F);
        sword.setRotY((180-animatable.getYRot())* 0.017453292F);
    }
}
