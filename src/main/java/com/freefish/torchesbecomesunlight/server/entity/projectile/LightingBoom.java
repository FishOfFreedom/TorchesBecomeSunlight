package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Circle;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Cone;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Sphere;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.particle.advance.effect.EntityEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.FrozenCapability;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

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
    public boolean isHitEntityDiscard(Entity entity) {
        if(getOwner() instanceof Mob living&&living.getTarget() == entity){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean hitEntity(Entity target) {
        if (!level().isClientSide) {
            if(getOwner() instanceof Mob mob&&mob.getTarget() == target){
                this.playSound(SoundHandle.BigLight.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                float damage = 10;
                if (getOwner() instanceof LivingEntity living)
                    damage = (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE);
                List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(2));
                for (LivingEntity boom : entitiesOfClass) {
                    if (boom == getOwner()) continue;
                    boom.invulnerableTime = 0;
                    boom.hurt(boom.damageSources().mobAttack(mob), damage*1.5f);
                    FrozenCapability.IFrozenCapability capability = CapabilityHandle.getCapability(boom, CapabilityHandle.FROZEN_CAPABILITY);
                    if(capability!=null){
                        capability.setLighting(boom,100);
                    }
                }
            }
            if(target instanceof LivingEntity living){
                FrozenCapability.IFrozenCapability capability = CapabilityHandle.getCapability(living, CapabilityHandle.FROZEN_CAPABILITY);
                if(capability!=null){
                    capability.setLighting(living,100);
                }
            }
            return true;
        }else {
            RLParticle rlParticle1 = new RLParticle();
            rlParticle1.config.setDuration(10);
            rlParticle1.config.setStartLifetime(NumberFunction.constant(5));
            rlParticle1.config.setStartSpeed(NumberFunction.constant(3));
            rlParticle1.config.setStartSize(new NumberFunction3(0.2));

            rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
            EmissionSetting.Burst burst1 = new EmissionSetting.Burst();
            burst1.setCount(NumberFunction.constant(20));
            rlParticle1.config.getEmission().addBursts(burst1);

            rlParticle1.config.getMaterial().setMaterial(MaterialHandle.VOID);

            Circle circle1 = new Circle();
            circle1.setRadius(1);
            circle1.setRadiusThickness(1);
            rlParticle1.config.getShape().setShape(circle1);

            rlParticle1.config.getVelocityOverLifetime().open();
            rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0), NumberFunction.constant(3), NumberFunction.constant(0)));

            rlParticle1.config.getNoise().open();
            rlParticle1.config.getNoise().setPosition(new NumberFunction3(1));

            rlParticle1.config.trails.open();
            rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
            rlParticle1.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF, 0XFFDFEF86)));

            rlParticle1.config.trails.config.getRenderer().setBloomEffect(true);

            RLParticle rlParticle2 = new RLParticle();
            rlParticle2.config.setDuration(8);
            rlParticle2.config.setStartLifetime(NumberFunction.constant(2));
            rlParticle2.config.setStartSpeed(NumberFunction.constant(20));
            rlParticle2.config.setStartSize(new NumberFunction3(0.2));

            rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
            EmissionSetting.Burst burst2 = new EmissionSetting.Burst();
            burst2.setCount(NumberFunction.constant(7));
            burst2.cycles = 3;
            burst2.interval = 2;
            rlParticle2.config.getEmission().addBursts(burst2);

            rlParticle2.config.getMaterial().setMaterial(MaterialHandle.VOID);

            Cone circle2 = new Cone();
            circle2.setRadius(0.5f);
            circle2.setAngle(40);
            rlParticle2.config.getShape().setScale(new NumberFunction3(0.4, 1, 0.4));
            rlParticle2.config.getShape().setShape(circle2);
            rlParticle2.config.getShape().setRotation(new NumberFunction3(50, 0, 0));
            rlParticle2.updateScale(new Vector3f(1.3f, 1.3f, 1.3f));

            rlParticle2.config.getNoise().open();
            rlParticle2.config.getNoise().setPosition(new NumberFunction3(1));

            rlParticle2.config.trails.open();
            rlParticle2.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
            rlParticle2.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF, 0XFFDFEF86)));

            rlParticle2.config.trails.config.getRenderer().setBloomEffect(true);

            RLParticle rlParticle3 = new RLParticle();
            rlParticle3.config.setDuration(20);
            rlParticle3.config.setStartLifetime(NumberFunction.constant(6));
            rlParticle3.config.setStartSpeed(NumberFunction.constant(3));

            rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
            EmissionSetting.Burst burst3 = new EmissionSetting.Burst();
            burst3.setCount(NumberFunction.constant(40));
            burst3.cycles = 1;
            burst3.interval = 7;
            burst3.time = 2;
            rlParticle3.config.getEmission().addBursts(burst3);

            rlParticle3.config.getMaterial().setMaterial(MaterialHandle.VOID);

            Circle circle3 = new Circle();
            circle3.setRadius(2);
            rlParticle3.config.getShape().setShape(circle3);

            rlParticle3.config.getNoise().open();
            rlParticle3.config.getNoise().setPosition(new NumberFunction3(0.5));

            rlParticle3.config.trails.open();
            rlParticle3.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
            rlParticle3.config.trails.setLifetime(NumberFunction.constant(0.5));
            rlParticle3.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF, 0XFFDFEF86)));

            rlParticle3.config.trails.config.getRenderer().setBloomEffect(true);

            RLParticle rlParticle4 = new RLParticle();
            rlParticle4.config.setDuration(4);
            rlParticle4.config.setStartLifetime(NumberFunction.constant(6));
            rlParticle4.config.setStartColor(new Gradient(new GradientColor(0X5FDFEF86)));

            rlParticle4.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));

            rlParticle4.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);

            rlParticle4.config.getShape().setShape(new Dot());

            rlParticle4.config.getColorOverLifetime().open();
            rlParticle4.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFDFEF86, 0X00DFEF86)));

            BlockEffect blockEffect = new BlockEffect(level(), position());
            rlParticle1.emmit(blockEffect);
            rlParticle2.updateRotation(new Vector3f(0, (float) (-this.getYRot() / 180 * Math.PI), 0));
            rlParticle2.emmit(blockEffect);
            rlParticle3.emmit(blockEffect);
            rlParticle4.emmit(blockEffect);
        }
        return false;
    }

    public static void shootLightingBoom(Level level, LivingEntity owner, @Nullable LivingEntity target, Vec3 shootPos, boolean isRight){
        LightingBoom lightingBoom = new LightingBoom(EntityHandle.LIGHT_BOOM.get(), level);

        Vec3 targetPos ;
        if(target==null){
            targetPos = FFEntityUtils.getBodyRotVec(owner, new Vec3(0, 0, 10));
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
