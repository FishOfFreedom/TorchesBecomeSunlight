package com.freefish.torchesbecomesunlight.client.render.util;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public abstract class AnimationGeoModel<T extends GeoAnimatable> extends GeoModel<T> {

    public GeoBone getFFBone(String name){
        Optional<GeoBone> bone = getBone(name);
        return bone.orElse(null);
    }

    public float animationCycle(float blend, float limbSwing, float limbSwingAmount, float speed,float globalDegree,float start){
        return blend * -(float) (Math.cos(limbSwing * speed * 0.5+ start) * 1.1f * globalDegree ) * limbSwingAmount;
    }

    public void addRotX(GeoBone bone,float blend, float limbSwing, float limbSwingAmount, float speed,float globalDegree,float start){
        bone.setRotX(bone.getRotX()+animationCycle(blend,limbSwing,limbSwingAmount,speed,globalDegree,start));
    }

    public void addRotX(GeoBone bone,float blend){
        bone.setRotX(bone.getRotX()+blend);
    }

    public void addRotY(GeoBone bone,float blend, float limbSwing, float limbSwingAmount, float speed,float globalDegree,float start){
        bone.setRotY(bone.getRotY()+animationCycle(blend,limbSwing,limbSwingAmount,speed,globalDegree,start));
    }

    public void addRotZ(GeoBone bone,float blend, float limbSwing, float limbSwingAmount, float speed,float globalDegree,float start){
        bone.setRotZ(bone.getRotZ()+animationCycle(blend,limbSwing,limbSwingAmount,speed,globalDegree,start));
    }

    public void addPosX(GeoBone bone,float blend, float limbSwing, float limbSwingAmount, float speed,float globalDegree,float start){
        bone.setPosX(bone.getPosX()+animationCycle(blend,limbSwing,limbSwingAmount,speed,globalDegree,start));
    }

    public void addPosY(GeoBone bone,float blend, float limbSwing, float limbSwingAmount, float speed,float globalDegree,float start){
        bone.setPosY(bone.getPosY()+animationCycle(blend,limbSwing,limbSwingAmount,speed,globalDegree,start));
    }

    public void addPosZ(GeoBone bone,float blend, float limbSwing, float limbSwingAmount, float speed,float globalDegree,float start){
        bone.setPosZ(bone.getPosZ()+animationCycle(blend,limbSwing,limbSwingAmount,speed,globalDegree,start));
    }

    public float getValue(String name) {
        Optional<GeoBone> bone = getBone(name);
        if (bone.isEmpty()) return 0.0f;
        return bone.get().getPosX();
    }
}
