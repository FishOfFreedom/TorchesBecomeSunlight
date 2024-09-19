package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SnowNovaModel extends GeoModel<SnowNova> {
    @Override
    public ResourceLocation getModelResource(SnowNova object) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/snownova.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SnowNova object) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/snownova.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SnowNova animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/snownova.animation.json");
    }

    @Override
    public void setCustomAnimations(SnowNova animatable, long instanceId, AnimationState<SnowNova> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("Head");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(extraData.headPitch() * 0.017453292F);
        head.setRotY(extraData.netHeadYaw() * 0.017453292F);
    }

    @Override
    public void applyMolangQueries(SnowNova animatable, double animTime) {
        super.applyMolangQueries(animatable, animTime);
        MolangParser parser = MolangParser.INSTANCE;
        LivingEntity livingEntity = animatable;
        Vec3 velocity = livingEntity.getDeltaMovement();
        float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
        parser.setValue("move_speed", () -> groundSpeed*45);
    }
}
