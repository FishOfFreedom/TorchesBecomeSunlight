package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Sphere;
import com.freefish.rosmontislib.client.particle.advance.effect.EntityEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class LightingBoom extends NoGravityProjectileEntity{
    float rotSpeed = 0;

    public LightingBoom(EntityType<? extends NoGravityProjectileEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public Vec3 changeDeltaMovement(Vec3 vec3) {
        if(rotSpeed!=0) {
            vec3 = vec3.yRot(rotSpeed);
            setDeltaMovement(vec3);
        }
        return super.changeDeltaMovement(vec3);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeFloat(rotSpeed);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        super.readSpawnData(additionalData);
        rotSpeed = additionalData.readFloat();
        if(level().isClientSide){
            RLParticle rlParticle1 = new RLParticle();
            rlParticle1.config.setDuration(80);
            rlParticle1.config.setStartLifetime(NumberFunction.constant(10));
            rlParticle1.config.setStartSpeed(NumberFunction.constant(1));
            rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XFFDFEF86)));
            rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(2.5));

            rlParticle1.config.getMaterial().setMaterial(MaterialHandle.VOID);
            Sphere circle1 = new Sphere();circle1.setRadius(0.1f);
            rlParticle1.config.getShape().setShape(circle1);
            rlParticle1.config.getNoise().open();
            rlParticle1.config.getNoise().setPosition(new NumberFunction3(2));

            rlParticle1.config.trails.open();
            rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
            rlParticle1.config.trails.config.getRenderer().setBloomEffect(true);

            rlParticle1.emmit(new EntityEffect(level(),this));
        }
    }

    @Override
    public int getTickDespawn() {
        return 100;
    }

    @Override
    public boolean isNoPhysics() {
        return true;
    }

    @Override
    public boolean isHitEntityDiscard() {
        return true;
    }

    public static void shootLightingBoom(Level level, LivingEntity owner, @Nullable LivingEntity target, Vec3 shootPos, boolean isRight){
        LightingBoom lightingBoom = new LightingBoom(EntityHandle.LIGHT_BOOM.get(), level);

        Vec3 targetPos ;
        if(target==null){
            targetPos = FFEntityUtils.getBodyRotVec(owner,new Vec3(0,0,10));
        }
        else {
            targetPos = target.position();
        }

        float posToPosRot = (float) -Math.toRadians(FFEntityUtils.getPosToPosRot(shootPos, targetPos)+ (isRight?30:-30));
        Vec3 bodyRotVec = new Vec3(0,0,1).yRot(posToPosRot);
        lightingBoom.shoot(bodyRotVec.x,bodyRotVec.y,bodyRotVec.z,2.5f,0);
        double tick = shootPos.distanceTo(targetPos) / 2.165;
        lightingBoom.rotSpeed = (float)(1.047/tick);

        if(!isRight) lightingBoom.rotSpeed = -lightingBoom.rotSpeed;

        lightingBoom.setPos(shootPos.x,shootPos.y,shootPos.z);
        lightingBoom.setOwner(owner);
        level.addFreshEntity(lightingBoom);
    }

}
