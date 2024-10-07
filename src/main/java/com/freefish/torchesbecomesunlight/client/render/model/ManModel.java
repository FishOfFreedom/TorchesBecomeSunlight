package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.villager.Man;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ManModel extends GeoModel<Man> {
    @Override
    public ResourceLocation getModelResource(Man animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/man.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Man animatable) {
        return switch (animatable.getNumber()) {
            case 1 -> new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/man2.png");
            case 2 -> new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/man3.png");
            default -> new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/man.png");
        };
    }

    @Override
    public ResourceLocation getAnimationResource(Man animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/man.animation.json");
    }

    @Override
    public void setCustomAnimations(Man animatable, long instanceId, AnimationState<Man> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(extraData.headPitch() * 0.017453292F);
        head.setRotY(extraData.netHeadYaw() * 0.017453292F);
    }

    @Override
    public void applyMolangQueries(Man animatable, double animTime) {
        super.applyMolangQueries(animatable, animTime);
        //MolangParser parser = MolangParser.INSTANCE;
        //LivingEntity livingEntity = (LivingEntity) animatable;
        //Vec3 velocity = livingEntity.getDeltaMovement();
        //float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
        //parser.setValue("move_speed", () -> groundSpeed * 20);
    }
}
