package com.freefish.torchesbecomesunlight.client.render.model.tools.geckolib;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public abstract class MowzieGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
    public MowzieGeoModel() {
    }

    public MowzieGeoBone getMowzieBone(String boneName) {
        Optional<GeoBone> bone = this.getBone(boneName);
        return (MowzieGeoBone) bone.orElse(null);
    }

    public boolean isInitialized() {
        return !this.getAnimationProcessor().getRegisteredBones().isEmpty();
    }

    public void resetBoneToSnapshot(GeoBone bone) {
        BoneSnapshot initialSnapshot = bone.getInitialSnapshot();

        bone.setRotX(initialSnapshot.getRotX());
        bone.setRotY(initialSnapshot.getRotY());
        bone.setRotZ(initialSnapshot.getRotZ());

        bone.setPosX(initialSnapshot.getOffsetX());
        bone.setPosY(initialSnapshot.getOffsetY());
        bone.setPosZ(initialSnapshot.getOffsetZ());

        bone.setScaleX(initialSnapshot.getScaleX());
        bone.setScaleY(initialSnapshot.getScaleY());
        bone.setScaleZ(initialSnapshot.getScaleZ());
    }

    @Override
    public void applyMolangQueries(AnimationState<T> animationState, double animTime) {
        getAnimationProcessor().getRegisteredBones().forEach(this::resetBoneToSnapshot);
        super.applyMolangQueries(animationState, animTime);
    }

    public void codeAnimations(T entity, Integer uniqueID, AnimationState<?> customPredicate) {

    }

    public float getControllerValueInverted(String controllerName) {
        if (!isInitialized()) return 1.0f;
        Optional<GeoBone> bone = getBone(controllerName);
        if (bone.isEmpty()) return 1.0f;
        return 1.0f - bone.get().getPosX();
    }

    public float getControllerValue(String controllerName) {
        if (!isInitialized()) return 0.0f;
        Optional<GeoBone> bone = getBone(controllerName);
        if (bone.isEmpty()) return 0.0f;
        return bone.get().getPosX();
    }
}
