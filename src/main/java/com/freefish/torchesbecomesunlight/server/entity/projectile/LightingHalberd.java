package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.RendererSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.RandomConstant;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.Line;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Circle;
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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

public class LightingHalberd extends NoGravityProjectileEntity{
    int timeToBoom;
    int timeToBoomMaxTime;

    public LightingHalberd(EntityType<? extends NoGravityProjectileEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        if(timeToBoomMaxTime!=0&&timeToBoom==3){
            this.playSound(SoundHandle.BigLight.get(), 2.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
            float damage = 30;
            if (getOwner() instanceof LivingEntity living)
                damage = (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE)*2.5f;
            List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(8.5));
            for (LivingEntity boom : entitiesOfClass) {
                if (boom == getOwner()||!boom.onGround()) continue;
                boom.invulnerableTime = 0;
                boom.hurt(getOwner() instanceof Mob mob?boom.damageSources().mobAttack(mob):boom.damageSources().lightningBolt(), damage);
                FrozenCapability.IFrozenCapability capability = CapabilityHandle.getCapability(boom, CapabilityHandle.FROZEN_CAPABILITY);
                if(capability!=null){
                    capability.setLighting(boom,300);
                }
            }
        }

        if(timeToBoom>0) timeToBoom--;
    }

    @Override
    public boolean hitEntity(Entity target) {
        if(!level().isClientSide&&target instanceof LivingEntity living){
            FrozenCapability.IFrozenCapability capability = CapabilityHandle.getCapability(living, CapabilityHandle.FROZEN_CAPABILITY);
            if(capability!=null){
                capability.setLighting(living,300);
            }
        }
        return false;
    }

    @Override
    public int getTickDespawn() {
        return 110+timeToBoomMaxTime;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        timeToBoom = pCompound.getInt("boom");
        timeToBoomMaxTime = pCompound.getInt("boommax");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("boom",timeToBoom);
        pCompound.putInt("boommax",timeToBoomMaxTime);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        super.readSpawnData(additionalData);
        if(level().isClientSide){
            RLParticle rlParticle1 = new RLParticle();
            rlParticle1.config.setDuration(100);
            rlParticle1.config.setStartLifetime(NumberFunction.constant(10));
            rlParticle1.config.setStartSpeed(NumberFunction.constant(1));
            rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XFFDFEF86)));
            rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(2));
            EmissionSetting.Burst burst = new EmissionSetting.Burst();
            burst.setCount(NumberFunction.constant(20));

            rlParticle1.config.getEmission().addBursts(burst);

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
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        BlockPos blockPos = pResult.getBlockPos();

        if(level().isClientSide){
            RLParticle rlParticle10 = new RLParticle();
            rlParticle10.config.setDuration(10);
            rlParticle10.config.setStartLifetime(NumberFunction.constant(20));
            rlParticle10.config.setStartSpeed(NumberFunction.constant(1));

            rlParticle10.config.getEmission().setEmissionRate(NumberFunction.constant(15));

            rlParticle10.config.getMaterial().setMaterial(MaterialHandle.VOID);

            Circle circle10 = new Circle();circle10.setRadius(4);circle10.setRadiusThickness(1);
            rlParticle10.config.getShape().setShape(circle10);

            rlParticle10.config.getNoise().open();
            rlParticle10.config.getNoise().setPosition(new NumberFunction3(0.6));

            rlParticle10.config.trails.open();
            rlParticle10.config.trails.setLifetime(NumberFunction.constant(0.2));
            rlParticle10.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0X00DFEF86,0XFFDFEF86,0XFFDFEF86,0X00DFEF86)));
            rlParticle10.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
            rlParticle10.config.trails.config.getRenderer().setBloomEffect(true);

            rlParticle10.emmit(new BlockEffect(level(),new Vec3(blockPos.getX()+0.5,blockPos.getY()+1.1,blockPos.getZ()+0.5)));

            RLParticle rlParticle1 = new RLParticle();
            rlParticle1.config.setDuration(10);
            rlParticle1.config.setStartLifetime(NumberFunction.constant(5));
            rlParticle1.config.setStartSpeed(NumberFunction.constant(3));
            rlParticle1.config.setStartSize(new NumberFunction3(0.2));
            rlParticle1.config.setStartDelay(NumberFunction.constant(30));

            rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
            EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(20));
            rlParticle1.config.getEmission().addBursts(burst1);

            rlParticle1.config.getMaterial().setMaterial(MaterialHandle.VOID);

            Circle circle1 = new Circle();circle1.setRadius(1);circle1.setRadiusThickness(1);
            rlParticle1.config.getShape().setShape(circle1);

            rlParticle1.config.getVelocityOverLifetime().open();
            rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(3),NumberFunction.constant(0)));

            rlParticle1.config.getNoise().open();
            rlParticle1.config.getNoise().setPosition(new NumberFunction3(1));

            rlParticle1.config.trails.open();
            rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
            rlParticle1.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0XFFDFEF86)));

            rlParticle1.config.trails.config.getRenderer().setBloomEffect(true);

            RLParticle rlParticle2 = new RLParticle();
            rlParticle2.config.setDuration(8);
            rlParticle2.config.setStartLifetime(NumberFunction.constant(2));
            rlParticle2.config.setStartSpeed(NumberFunction.constant(16));
            rlParticle2.config.setStartSize(new NumberFunction3(0.2));
            rlParticle2.config.setStartDelay(NumberFunction.constant(30));

            rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
            EmissionSetting.Burst burst2 = new EmissionSetting.Burst();burst2.setCount(NumberFunction.constant(7));
            burst2.cycles = 3;burst2.interval = 2;
            rlParticle2.config.getEmission().addBursts(burst2);

            rlParticle2.config.getMaterial().setMaterial(MaterialHandle.VOID);

            Circle circle2 = new Circle();circle2.setRadius(0.2f);circle2.setRadiusThickness(0.2f);
            rlParticle2.config.getShape().setScale(new NumberFunction3(NumberFunction.constant(0.4),NumberFunction.constant(1),NumberFunction.constant(0.4)));
            rlParticle2.config.getShape().setShape(circle2);

            rlParticle2.config.getVelocityOverLifetime().open();
            rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(30,0,true),NumberFunction.constant(0)));

            rlParticle2.config.getNoise().open();
            rlParticle2.config.getNoise().setPosition(new NumberFunction3(1));

            rlParticle2.config.trails.open();
            rlParticle2.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
            rlParticle2.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFDFEF86)));
            rlParticle2.config.trails.config.getRenderer().setBloomEffect(true);

            RLParticle rlParticle3 = new RLParticle();
            rlParticle3.config.setDuration(8);
            rlParticle3.config.setStartLifetime(NumberFunction.constant(4));
            rlParticle3.config.setStartSpeed(NumberFunction.constant(20));
            rlParticle3.config.setStartSize(new NumberFunction3(0.2));
            rlParticle3.config.setStartDelay(NumberFunction.constant(30));

            rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
            EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.setCount(NumberFunction.constant(3));
            rlParticle3.config.getEmission().addBursts(burst3);

            rlParticle3.config.getMaterial().setMaterial(MaterialHandle.VOID);

            rlParticle3.config.getShape().setShape(new Dot());

            rlParticle3.config.getVelocityOverLifetime().open();
            rlParticle3.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(40),NumberFunction.constant(0)));

            rlParticle3.config.getNoise().open();
            rlParticle3.config.getNoise().setPosition(new NumberFunction3(0.7));

            rlParticle3.config.trails.open();
            rlParticle3.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
            rlParticle3.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFDFEF86)));
            rlParticle3.config.trails.config.getRenderer().setBloomEffect(true);

            RLParticle rlParticle4 = new RLParticle();
            rlParticle4.config.setDuration(20);
            rlParticle4.config.setStartLifetime(NumberFunction.constant(6));
            rlParticle4.config.setStartSpeed(NumberFunction.constant(6));
            rlParticle4.config.setStartDelay(NumberFunction.constant(30));

            rlParticle4.config.getEmission().setEmissionRate(NumberFunction.constant(0));
            EmissionSetting.Burst burst4 = new EmissionSetting.Burst();burst4.setCount(NumberFunction.constant(40));
            rlParticle4.config.getEmission().addBursts(burst4);burst4.time = 2;

            rlParticle4.config.getMaterial().setMaterial(MaterialHandle.VOID);

            Circle circle4 = new Circle();circle4.setRadius(2f);circle4.setRadiusThickness(1f);
            rlParticle4.config.getShape().setShape(circle4);

            rlParticle4.config.getNoise().open();
            rlParticle4.config.getNoise().setPosition(new NumberFunction3(0.5));

            rlParticle4.config.trails.open();
            rlParticle4.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
            rlParticle4.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0XFFDFEF86)));
            rlParticle4.config.trails.config.getRenderer().setBloomEffect(true);

            RLParticle rlParticle5 = new RLParticle();
            rlParticle5.config.setDuration(20);
            rlParticle5.config.setStartLifetime(NumberFunction.constant(8));
            rlParticle5.config.setStartSpeed(NumberFunction.constant(1));
            rlParticle5.config.setStartSize(new NumberFunction3(5));
            rlParticle5.config.setStartDelay(NumberFunction.constant(30));

            rlParticle5.config.getEmission().setEmissionRate(NumberFunction.constant(0));
            EmissionSetting.Burst burst5 = new EmissionSetting.Burst();burst5.setCount(NumberFunction.constant(2));
            burst5.cycles = 2;burst5.interval = 2;
            rlParticle5.config.getEmission().addBursts(burst5);

            rlParticle5.config.getMaterial().setMaterial(MaterialHandle.RING);
            rlParticle5.config.getShape().setShape(new Dot());

            ((RendererSetting.Particle)rlParticle5.config.getRenderer()).setRenderMode(RendererSetting.Particle.Mode.Horizontal);

            rlParticle5.config.getColorOverLifetime().open();
            rlParticle5.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0X00DFEF86)));

            rlParticle5.config.getSizeOverLifetime().open();
            rlParticle5.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,1},new float[]{0,1})));

            RLParticle rlParticle6 = new RLParticle();
            rlParticle6.config.setDuration(4);
            rlParticle6.config.setStartLifetime(NumberFunction.constant(6));
            rlParticle6.config.setStartSpeed(NumberFunction.constant(1));
            rlParticle6.config.setStartSize(new NumberFunction3(4));
            rlParticle6.config.setStartDelay(NumberFunction.constant(30));

            rlParticle6.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
            rlParticle6.config.setStartColor(new Gradient(new GradientColor(0X48FFFFFF)));
            rlParticle6.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);

            rlParticle6.config.getColorOverLifetime().open();
            rlParticle6.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFDFEF86,0XFFDFEF86,0X00DFEF86)));

            BlockEffect blockEffect = new BlockEffect(level(), new Vec3(blockPos.getX()+0.5,blockPos.getY()+1.1,blockPos.getZ()+0.5));

            rlParticle1.updateScale(new Vector3f(2,2,2));
            rlParticle2.updateScale(new Vector3f(2,2,2));
            rlParticle3.updateScale(new Vector3f(2,2,2));
            rlParticle4.updateScale(new Vector3f(2,2,2));
            rlParticle5.updateScale(new Vector3f(2,2,2));
            rlParticle6.updateScale(new Vector3f(2,2,2));

            rlParticle1.emmit(blockEffect);
            rlParticle2.emmit(blockEffect);
            rlParticle3.emmit(blockEffect);
            rlParticle4.emmit(blockEffect);
            rlParticle5.emmit(blockEffect);
            rlParticle6.emmit(blockEffect);
        }else {
            float damage = 10;
            if (getOwner() instanceof LivingEntity living)
                damage = (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE);
            List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(5).move(blockPos.getX()+0.5f-getX(),blockPos.getY()+0.5f-getY(),blockPos.getZ()+0.5f-getZ()));
            for (LivingEntity boom : entitiesOfClass) {
                if (boom == getOwner()||!boom.onGround()) continue;
                boom.invulnerableTime = 0;
                boom.hurt((getOwner() instanceof Mob mob?boom.damageSources().mobAttack(mob):boom.damageSources().lightningBolt()), damage*2);
                FrozenCapability.IFrozenCapability capability = CapabilityHandle.getCapability(boom, CapabilityHandle.FROZEN_CAPABILITY);
                if(capability!=null){
                    capability.setLighting(boom,300);
                }
            }
        }

        timeToBoomMaxTime = 33;
        timeToBoom = 33;
    }

    @Override
    public boolean isHitEntityDiscard(Entity entity) {
        return false;
    }

    public static void shootLightingBoom(Level level, LivingEntity owner, @Nullable LivingEntity target, Vec3 shootPos){
        LightingHalberd lightingBoom = new LightingHalberd(EntityHandle.LIGHT_HALBERD.get(), level);

        Vec3 targetPos ;
        if(target==null){
            targetPos = FFEntityUtils.getBodyRotVec(owner,new Vec3(0,0,10));
        }
        else {
            targetPos = target.position();
        }
        Vec3 bodyRotVec = targetPos.subtract(shootPos);
        lightingBoom.shoot(bodyRotVec.x,bodyRotVec.y,bodyRotVec.z,12f,0);

        lightingBoom.setPos(shootPos.x,shootPos.y,shootPos.z);
        lightingBoom.setOwner(owner);
        level.addFreshEntity(lightingBoom);
    }
}
