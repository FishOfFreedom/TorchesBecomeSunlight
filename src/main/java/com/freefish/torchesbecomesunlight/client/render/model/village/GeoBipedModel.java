package com.freefish.torchesbecomesunlight.client.render.model.village;

import com.freefish.torchesbecomesunlight.client.render.model.tools.geckolib.MowzieGeoBone;
import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public abstract class GeoBipedModel<T extends UrsusVillager> extends GeoModel<T> {

    public MowzieGeoBone bipedHead() {
        return getMowzieBone("Head");
    }

    public MowzieGeoBone bipedHeadwear() {
        return getMowzieBone("Hat");
    }

    public MowzieGeoBone bipedBody() {
        return getMowzieBone("Body");
    }

    public MowzieGeoBone bipedRightArm() {
        return getMowzieBone("RightArm");
    }

    public MowzieGeoBone bipedLeftArm() {
        return getMowzieBone("LeftArm");
    }

    public MowzieGeoBone bipedRightLeg() {
        return getMowzieBone("RightLowerLeg");
    }

    public MowzieGeoBone bipedLeftLeg() {
        return getMowzieBone("LeftLowerLeg");
    }

    public MowzieGeoBone getMowzieBone(String boneName) {
        Optional<GeoBone> bone = this.getBone(boneName);
        return (MowzieGeoBone) bone.orElse(null);
    }
}
