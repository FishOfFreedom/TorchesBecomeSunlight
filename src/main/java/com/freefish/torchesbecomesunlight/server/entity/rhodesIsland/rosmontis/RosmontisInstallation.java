package com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis;

import com.freefish.rosmontislib.client.RLClientUseUtils;
import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.RendererSetting;
import com.freefish.rosmontislib.client.particle.advance.data.UVAnimationSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.GradientHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.Line;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Circle;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Sphere;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.rosmontislib.client.utils.Range;
import com.freefish.torchesbecomesunlight.client.render.model.tools.geckolib.MowzieAnimationController;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.entity.effect.AnimationBlock;
import com.freefish.torchesbecomesunlight.server.init.DamageSourceHandle;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class RosmontisInstallation extends Entity implements GeoEntity , IEntityAdditionalSpawnData {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> INSTALL_INDEX = SynchedEntityData.defineId(RosmontisInstallation.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<CompoundTag> ANIMATION = SynchedEntityData.defineId(RosmontisInstallation.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Vector3f> MOTION = SynchedEntityData.defineId(RosmontisInstallation.class, EntityDataSerializers.VECTOR3);
    private UUID owner_UUID;
    private LivingEntity owner;
    public Vector3f motion;
    public Vector3f motionO;

    public int animationType;
    private int animationTime;

    private int deathTime;

    private boolean isAnimation;

    public RosmontisInstallation(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        noPhysics = true;
        animationTime =20;
        animationType = 1;
    }

    @Override
    public void tick() {
        motionO = motion;
        motion = this.entityData.get(MOTION);
        int installIndex = getInstallIndex();
        if(!isAnimation||(animationType==12||animationType==13)){
            move(MoverType.SELF, new Vec3(motion));
        }

        super.tick();

        if(animationType==2){
            if(animationTime==50-10) {
                tempPos = tempTempPos;
                isAnimation=true;
            }
            else if (animationTime==50-35) {
                tempPos = null;
                isAnimation=false;
            }

            if(!level().isClientSide){
                if (animationTime == 50-17) {
                    remoteActDamage1(3, 1f,false);
                    playSound(SoundHandle.ROS_END.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
                } else if (animationTime == 50-19) {
                    remoteActDamage1(4.5, 1f,true);
                }
            }else {
                if(animationTime == 38){
                    ros_instationTrailFx(this,FFEntityUtils.getBodyRotVec(this,new Vec3(0,10,5)),position());
                }

                if(animationTime == 36){
                    ros_state_1to2_boom(position().add(0,0.2,0));
                }else if (animationTime == 32) {
                    ros_state_1to2_boom(position().add(0,0.2,0));
                }
            }
        }else if(animationType==3){
            if(animationTime==55-10) {
                if(getOwner() instanceof Rosmontis rosmontis&&rosmontis.getTarget()!=null){
                    LivingEntity target = rosmontis.getTarget();
                    tempPos = target.position().add(rosmontis.getTargetMoveVec(target));
                    if(installIndex==2){
                        tempPos = tempPos.add(0,0,0.4);
                    }else {
                        tempPos = tempPos.add(0,0,-0.4);
                    }

                    Partner<?> partner = PartnerUtil.getPartner(rosmontis);
                    if(partner!=null){
                        Vec3 instancePos = partner.getInstancePos();
                        if(instancePos!=null){
                            tempPos = instancePos;
                            if(installIndex==2){
                                tempPos = tempPos.add(0,0,0.4);
                            }else {
                                tempPos = tempPos.add(0,0,-0.4);
                            }
                        }
                    }
                }
                isAnimation=true;
            }
            else if (animationTime==55-40) {
                tempPos = null;
                isAnimation=false;
            }
            if(!level().isClientSide) {
                if(tempTempPos!=null){
                    //((ServerLevel)level()).sendParticles(ParticleTypes.FLAME,tempTempPos.x,tempTempPos.y,tempTempPos.z,1,0,0,0,0);
                }
                if (animationTime == 55-17 && installIndex == 2) {
                    remoteActDamage1(4, 1f,false);
                    playSound(SoundHandle.ROS_SKILL_2.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
                } else if (animationTime == 55-20 && installIndex != 2) {
                    remoteActDamage1(8, 1f,true);
                } else if (animationTime == 55-23 && installIndex == 2) {
                    remoteActDamage1(8, 1f,true);
                } else if (animationTime == 55-26 && installIndex != 2) {
                    remoteActDamage1(8, 1f,true);
                }
            }else {
                if(animationTime == 41){
                    ros_instationTrailFx(this,FFEntityUtils.getBodyRotVec(this,new Vec3(0,10,0)),position());
                }
                if(animationTime == 41&& installIndex == 2){
                    ros_state_1to2_boom(position().add(0,0.2,0));
                }else if (animationTime == 38 && installIndex != 2) {
                    ros_state_1to2_boom(position().add(0,0.2,0));
                } else if (animationTime == 35 && installIndex == 2) {
                    ros_state_1to2_boom(position().add(0,0.2,0));
                } else if (animationTime == 32 && installIndex != 2) {
                    ros_state_1to2_boom(position().add(0,0.2,0));
                }
            }
        }else if(animationType==5){
            if(!level().isClientSide) {
                if(animationTime==25-10){
                    LivingEntity livingOwner = getOwner();
                    float damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    if(livingOwner instanceof Rosmontis rosmontis) {
                        boolean b = rosmontis.doRangeAttack(4, 60, damage, false);
                        rosmontis.doRangeKnockBack(4, 60, 1);
                        if(b)
                            playSound(SoundHandle.ROS_SKILL_1.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
                        FFEntityUtils.doRangeAttackFX(rosmontis,4,60,0);
                    }
                }
            }
        }else if(animationType==6||animationType==11){
            if(!level().isClientSide) {
                if(animationTime==35-17){
                    float rot = animationType==6?22:-22;

                    LivingEntity livingOwner = getOwner();
                    float damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    if(livingOwner instanceof Rosmontis rosmontis) {
                        boolean b = rosmontis.doRangeAttackAngle(6, 60, damage, rot, false);
                        rosmontis.doRangeKnockBack(6, 60, 1);
                        if(b)
                            playSound(SoundHandle.ROS_SKILL_1.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
                        FFEntityUtils.doRangeAttackFX(rosmontis,6,60,rot);
                    }
                }
            }
        }else if(animationType==7){
            if(!level().isClientSide) {
                if(animationTime==35-16){
                    LivingEntity livingOwner = getOwner();
                    float damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    if(livingOwner instanceof Rosmontis rosmontis) {
                        boolean b = rosmontis.doRangeAttack(10, 14, damage,true);
                        if(b)
                            playSound(SoundHandle.ROS_SKILL_1.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
                        rosmontis.doRangeKnockBack(10, 14, 2);
                        FFEntityUtils.doRangeAttackFX(rosmontis,10,14,0);
                    }
                }
            }
        }else if(animationType==9){
            if(!level().isClientSide) {
                if(animationTime==30-16){
                    LivingEntity livingOwner = getOwner();
                    float damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    if(livingOwner instanceof Rosmontis rosmontis) {

                        List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(8), hit -> {
                            return hit.distanceTo(owner) < 8;
                        });
                        boolean b = false;
                        for(LivingEntity living : entitiesOfClass){
                            if(living == owner) continue;
                            living.invulnerableTime = 0;
                            if(living.hurt(living.damageSources().mobAttack(owner),damage*1.25f)){
                                b = true;
                            }
                        }
                        if(b)
                            playSound(SoundHandle.ROS_SKILL_1.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));

                        FFEntityUtils.doRangeAttackFX(rosmontis,8,360,0);
                    }
                }
            }else {
                if(animationTime==30-12){
                    Gradient toAlpha = new Gradient(new GradientColor(0XFFFFFFFF, 0XFFFFFFFF, 0X00FFFFFF));

                    RLParticle rlParticle = new RLParticle(level());
                    rlParticle.config.setDuration(20);
                    rlParticle.config.setStartLifetime(NumberFunction.constant(10));
                    rlParticle.config.setStartSize(new NumberFunction3(6));
                    rlParticle.config.setStartColor(new Gradient(new GradientColor(0XDFFFFFFF)));
                    rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst = new EmissionSetting.Burst();
                    burst.setCount(NumberFunction.constant(1));
                    rlParticle.config.getEmission().addBursts(burst);
                    rlParticle.config.getShape().setShape(new Dot());
                    rlParticle.config.getMaterial().setMaterial(MaterialHandle.RING.create());
                    rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
                    rlParticle.config.getLights().open();
                    rlParticle.config.getColorOverLifetime().open();
                    rlParticle.config.getColorOverLifetime().setColor(toAlpha);
                    rlParticle.config.getSizeOverLifetime().open();
                    rlParticle.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0, 0.33f, 1}, new float[]{0, 0.7f, 1})));
                    RLParticle rlParticle3 = new RLParticle(level());
                    rlParticle3.config.setDuration(20);
                    rlParticle3.config.setStartLifetime(NumberFunction.constant(10));
                    rlParticle3.config.setStartSpeed(NumberFunction.constant(7));
                    rlParticle3.config.setStartColor(new Gradient(new GradientColor(0XDFFFFFFF)));
                    rlParticle3.config.setStartSize(new NumberFunction3(0.8));
                    rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst3 = new EmissionSetting.Burst();
                    burst3.setCount(NumberFunction.constant(30));
                    burst3.time = 3;
                    rlParticle3.config.getEmission().addBursts(burst3);
                    Circle circle3 = new Circle();
                    circle3.setRadius(0.5f);
                    circle3.setRadiusThickness(1f);
                    rlParticle3.config.getShape().setShape(circle3);
                    rlParticle3.config.getMaterial().setMaterial(MaterialHandle.SMOKE.create());
                    rlParticle3.config.getColorOverLifetime().open();
                    rlParticle3.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
                    rlParticle3.config.getPhysics().open();
                    rlParticle3.config.getPhysics().setHasCollision(false);
                    rlParticle3.config.getPhysics().setFriction(NumberFunction.constant(0.9));
                    rlParticle3.config.getUvAnimation().open();
                    rlParticle3.config.getUvAnimation().setTiles(new Range(2, 2));
                    rlParticle3.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);
                    Vector3f scale = new Vector3f(2);
                    rlParticle3.updateScale(scale);
                    rlParticle.updateScale(scale);

                    BlockEffect blockEffect = new BlockEffect(level(), position().add(0, 0.7, 0));
                    rlParticle.emmit(blockEffect);
                    rlParticle3.emmit(blockEffect);
                }
            }
        }else if(animationType==10){
            int lengh10 = 170;
            if(animationTime==lengh10-12) {
                tempPos = tempTempPos;
                isAnimation=true;
            }
            else if (animationTime==14) {
                tempPos = null;
                isAnimation=false;
            }

            if(!level().isClientSide) {
                if(animationTime>14&&animationTime<lengh10-12){
                    if (installIndex == 2) {
                        if (((lengh10 - animationTime) - 14) % 15 == 0) {
                            LivingEntity livingOwner = getOwner();
                            float damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                            if (livingOwner instanceof Rosmontis rosmontis) {
                                playSound(SoundHandle.ROS_END.get(),1,1.0F / (random.nextFloat() * 0.4F + 0.8F));

                                List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4), hit -> {
                                    return hit.distanceTo(this) <   2+hit.getBbWidth()/2;
                                });
                                for (LivingEntity living : entitiesOfClass) {
                                    if (living == owner) continue;
                                    living.invulnerableTime = 0;
                                    living.hurt(living.damageSources().mobAttack(owner), damage);
                                }
                            }
                        }
                        if (((lengh10 - animationTime) - 22) % 15 == 0) {
                            LivingEntity livingOwner = getOwner();
                            if (livingOwner instanceof Rosmontis mob && mob.getTarget() != null) {
                                LivingEntity target = mob.getTarget();
                                Vec3 targetMoveVec = mob.getTargetMoveVec(target);
                                tempPos = target.position().add(targetMoveVec.scale(-0.2));
                            }
                        }
                    } else {
                        if (((lengh10 - animationTime) - 22) % 15 == 0) {
                            LivingEntity livingOwner = getOwner();
                            float damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                            if (livingOwner instanceof Rosmontis rosmontis) {
                                playSound(SoundHandle.ROS_END.get(),1,1.0F / (random.nextFloat() * 0.4F + 0.8F));

                                List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4), hit -> {
                                    return hit.distanceTo(this) < 2+hit.getBbWidth()/2;
                                });
                                for (LivingEntity living : entitiesOfClass) {
                                    if (living == owner) continue;
                                    living.invulnerableTime = 0;
                                    living.hurt(living.damageSources().mobAttack(owner), damage);
                                }
                            }
                        }
                        if (((lengh10 - animationTime) - 32) % 15 == 0) {
                            LivingEntity livingOwner = getOwner();
                            if (livingOwner instanceof Rosmontis mob && mob.getTarget() != null) {
                                LivingEntity target = mob.getTarget();
                                Vec3 targetMoveVec = mob.getTargetMoveVec(target);
                                tempPos = target.position().add(targetMoveVec.scale(-0.2));
                            }
                        }
                    }
                }
            }else {
                if(animationTime>10&&animationTime<155){
                    if (installIndex == 2) {
                        if (((lengh10 - animationTime) - 14) % 15 == 0) {
                            ros_state_1to2_boom(position().add(0,0.2,0));
                        }
                    } else {
                        if (((lengh10 - animationTime) - 22) % 15 == 0) {
                            ros_state_1to2_boom(position().add(0,0.2,0));
                        }
                    }
                }
            }
        }else if(animationType==12){
            int time = 75 - animationTime;

            if(time==12) {
                tempPos = tempTempPos;
                isAnimation=true;
            }
            else if (time==63) {
                tempPos = null;
                isAnimation=false;
            }
            if(time>12&&time<30){
                LivingEntity owner1 = getOwner();
                if(owner1 instanceof Rosmontis rosmontis){
                    LivingEntity target = rosmontis.getTarget();
                    if(target!=null){
                        Vec3 off = rosmontis.position().subtract(target.position()).normalize();
                        tempPos = target.position().add(off);
                    }

                    Partner<?> partner = PartnerUtil.getPartner(rosmontis);
                    if(partner!=null){
                        LivingEntity instancePos = partner.getInstanceTarget();
                        if(instancePos!=null){
                            tempPos = instancePos.position();
                        }
                    }
                }
            }

            if(!level().isClientSide){
                if(time==37){
                    LivingEntity livingOwner = getOwner();
                    float damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    if (livingOwner instanceof Rosmontis rosmontis) {
                        boolean b = doRangeAttackAngle(rosmontis,4, 115, damage,0, false);
                        if (b)
                            playSound(SoundHandle.ROS_SKILL_1.get(), 2, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                        FFEntityUtils.doRangeAttackFX(this, 4, 115, 0);
                    }else if (livingOwner instanceof Player player){
                        boolean b = doRangeAttackAngle(player,4, 115, 16,0, false);
                        if (b)
                            playSound(SoundHandle.ROS_SKILL_1.get(), 2, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                        FFEntityUtils.doRangeAttackFX(this, 4, 115, 0);
                    }
                }
            }
        }else if(animationType==13){
            int time = 75 - animationTime;

            if(time==12) {
                tempPos = tempTempPos;
                isAnimation=true;
            }
            else if (time==63) {
                tempPos = null;
                isAnimation=false;
            }
            if(time>12&&time<30){
                LivingEntity owner1 = getOwner();
                if(owner1 instanceof Rosmontis rosmontis){
                    LivingEntity target = rosmontis.getTarget();
                    if(target!=null){
                        Vec3 off = rosmontis.position().subtract(target.position()).normalize();
                        tempPos = target.position().add(off);
                    }

                    Partner<?> partner = PartnerUtil.getPartner(rosmontis);
                    if(partner!=null){
                        LivingEntity instancePos = partner.getInstanceTarget();
                        if(instancePos!=null){
                            tempPos = instancePos.position();
                        }
                    }
                }
            }

            if(!level().isClientSide){
                if(time==37){
                    LivingEntity livingOwner = getOwner();
                    float damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    if (livingOwner instanceof Rosmontis rosmontis) {
                        boolean b = doRangeAttackAngle(rosmontis,4, 115, damage,0, false);
                        if (b)
                            playSound(SoundHandle.ROS_SKILL_1.get(), 2, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                        FFEntityUtils.doRangeAttackFX(this, 4, 115, 0);
                    }else if (livingOwner instanceof Player player){
                        boolean b = doRangeAttackAngle(player,4, 115, 16,0, false);
                        if (b)
                            playSound(SoundHandle.ROS_SKILL_1.get(), 2, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                        FFEntityUtils.doRangeAttackFX(this, 4, 115, 0);
                    }
                }
            }
        }else if(animationType==14){
            int time = 55 - animationTime;

            if(time==12) {
                tempPos = tempTempPos;
                isAnimation=true;
            }
            else if (time==43) {
                tempPos = null;
                isAnimation=false;
            }

            if(!level().isClientSide){
                if(time==25){
                    LivingEntity livingOwner = getOwner();
                    if(livingOwner instanceof  Rosmontis rosmontis){
                        float damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                        boolean b = false;
                        List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(6), (e) -> e != livingOwner && e.distanceTo(this) < 6);
                        for (LivingEntity living : entitiesOfClass) {
                            if (living.onGround()) {
                                living.hurt(damageSources().mobAttack(rosmontis),damage*1.5f);
                                b  = true;
                            }
                        }
                        if (b)
                            playSound(SoundHandle.ROS_SKILL_1.get(), 2, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                    }
                }
            }
        }else if(animationType==15){
            int time = 57 - animationTime;

            if(time==12) {
                tempPos = tempTempPos;
                isAnimation=true;
            }
            else if (time==45) {
                tempPos = null;
                isAnimation=false;
            }

            if(!level().isClientSide){
                if(time==20){
                    LivingEntity livingOwner = getOwner();
                    if(livingOwner instanceof  Rosmontis rosmontis){
                        float damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                        List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(12), (e) -> e != livingOwner && e.distanceTo(this) < 12);
                        for (LivingEntity living : entitiesOfClass) {
                            float dist = distanceTo(living);
                            if (dist>4&&living.onGround()) {
                                living.hurt(damageSources().mobAttack(rosmontis),damage*1.5f);
                            }
                            if(dist<=4){
                                living.hurt(damageSources().mobAttack(rosmontis),damage*1.5f);
                            }
                        }
                        AnimationBlock.spawnRing(position().add(0,-1.4,0), 5, level());
                        RLClientUseUtils.StartCameraShake(level(),position().add(0,-1,0),10,0.05f,10,10);
                        playSound(SoundHandle.ROS_SKILL_1.get(), 2, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                    }
                }
            }else {
                if(time==18){
                    ros_instationTrailFx(this,FFEntityUtils.getBodyRotVec(this,new Vec3(0,12,-2)),position());
                }
                if(time==20){
                    Gradient toAlpha = new Gradient(new GradientColor(0XFFFFFFFF, 0XFFFFFFFF, 0X00FFFFFF));

                    RLParticle rlParticle = new RLParticle(level());
                    rlParticle.config.setDuration(20);
                    rlParticle.config.setStartLifetime(NumberFunction.constant(10));
                    rlParticle.config.setStartSize(new NumberFunction3(6));
                    rlParticle.config.setStartColor(new Gradient(new GradientColor(0XDFFFFFFF)));
                    rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst = new EmissionSetting.Burst(); burst.setCount(NumberFunction.constant(1));
                    rlParticle.config.getEmission().addBursts(burst);
                    rlParticle.config.getShape().setShape(new Dot());
                    rlParticle.config.getMaterial().setMaterial(MaterialHandle.RING.create());
                    rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
                    rlParticle.config.getLights().open();
                    rlParticle.config.getColorOverLifetime().open();
                    rlParticle.config.getColorOverLifetime().setColor(toAlpha);
                    rlParticle.config.getSizeOverLifetime().open();
                    rlParticle.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,0.33f,1},new float[]{0,0.7f,1})));
                    RLParticle rlParticle3 = new RLParticle(level());
                    rlParticle3.config.setDuration(20);
                    rlParticle3.config.setStartLifetime(NumberFunction.constant(10));
                    rlParticle3.config.setStartSpeed(NumberFunction.constant(7));
                    rlParticle3.config.setStartColor(new Gradient(new GradientColor(0XDFFFFFFF)));
                    rlParticle3.config.setStartSize(new NumberFunction3(0.8));
                    rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst3 = new EmissionSetting.Burst(); burst3.setCount(NumberFunction.constant(30));burst3.time=3;
                    rlParticle3.config.getEmission().addBursts(burst3);
                    Circle circle3 = new Circle();circle3.setRadius(0.5f);circle3.setRadiusThickness(1f);
                    rlParticle3.config.getShape().setShape(circle3);
                    rlParticle3.config.getMaterial().setMaterial(MaterialHandle.SMOKE.create());
                    rlParticle3.config.getColorOverLifetime().open();
                    rlParticle3.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
                    rlParticle3.config.getPhysics().open();
                    rlParticle3.config.getPhysics().setHasCollision(false);
                    rlParticle3.config.getPhysics().setFriction(NumberFunction.constant(0.9));
                    rlParticle3.config.getUvAnimation().open();
                    rlParticle3.config.getUvAnimation().setTiles(new Range(2,2));
                    rlParticle3.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);
                    Vector3f scale = new Vector3f(3);
                    rlParticle3.updateScale(scale);
                    rlParticle.updateScale(scale);

                    BlockEffect blockEffect = new BlockEffect(level(), position().add(0,0.5,0));
                    rlParticle.emmit(blockEffect);
                    rlParticle3.emmit(blockEffect);
                }
            }
        }else if(animationType==-1){
            if(!level().isClientSide&&animationTime==1) discard();
        }

        if(!level().isClientSide){
            LivingEntity livingOwner = getOwner();
            if(livingOwner!=null&&livingOwner.isAlive()){
                if(tempPos==null){
                    Vec3 offsetPos;
                    float len = 1.2f;

                    if (installIndex == 0) {
                        offsetPos = FFEntityUtils.getBodyRotVec(livingOwner, new Vec3(len, 1, len));
                    } else if (installIndex == 1) {
                        offsetPos = FFEntityUtils.getBodyRotVec(livingOwner, new Vec3(-len, 1, len));
                    } else if (installIndex == 2) {
                        offsetPos = FFEntityUtils.getBodyRotVec(livingOwner, new Vec3(-len, 1, -len));
                    } else {
                        offsetPos = FFEntityUtils.getBodyRotVec(livingOwner, new Vec3(len, 1, -len));
                    }

                    Vec3 offSet = offsetPos.subtract(position());
                    this.entityData.set(MOTION, new Vector3f((float) offSet.x, (float) offSet.y, (float) offSet.z));

                    setYRot(livingOwner.getYRot());
                    setYBodyRot(livingOwner.yBodyRot);
                    setYHeadRot(livingOwner.getYHeadRot());
                }else {
                    if(animationType==12||animationType==13){
                        Vec3 offSet = tempPos.subtract(position());
                        this.entityData.set(MOTION, new Vector3f((float) offSet.x, (float) offSet.y, (float) offSet.z));
                    } else {
                        setPos(tempPos);
                    }
                }
            }else {
                deathTime++;
                if(deathTime==10){
                    setAnimation(-1,10);
                }
            }
        }

        if(animationType!=0&&animationType!=4&&animationTime>0){
            animationTime--;
            if(animationTime==0){
                animationType = 0;
                tempPos = null;
            }
        }
    }

    private void tpOwner(){
        //if(!level().isClientSide){
        //    LivingEntity livingOwner = getOwner();
        //    if (livingOwner != null) {
        //        Vec3 offsetPos;
        //        int installIndex = getInstallIndex();
        //        float len = 1.2f;
//
        //        if (installIndex == 0) {
        //            offsetPos = FFEntityUtils.getBodyRotVec(livingOwner, new Vec3(len, 1, len));
        //        } else if (installIndex == 1) {
        //            offsetPos = FFEntityUtils.getBodyRotVec(livingOwner, new Vec3(-len, 1, len));
        //        } else if (installIndex == 2) {
        //            offsetPos = FFEntityUtils.getBodyRotVec(livingOwner, new Vec3(-len, 1, -len));
        //        } else {
        //            offsetPos = FFEntityUtils.getBodyRotVec(livingOwner, new Vec3(len, 1, -len));
        //        }
//
        //        setPos(offsetPos);
        //    }
        //}
    }

    @Override
    public boolean isPickable() {
        return super.isPickable();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack handItem = player.getItemInHand(hand);

        // 右键交互
        if (hand == InteractionHand.MAIN_HAND) {
            return InteractionResult.SUCCESS;

        }
        return InteractionResult.PASS;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(INSTALL_INDEX,0);
        this.entityData.define(ANIMATION,new CompoundTag());
        this.entityData.define(MOTION,new Vector3f());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        setInstallIndex(pCompound.getInt("install_index"));
        //int animationType = pCompound.getInt("animationtype");
        //int animationTime = pCompound.getInt("animationtime");
        //setAnimation(animationType,animationTime);

        if(pCompound.contains("uuid")){
            owner_UUID = pCompound.getUUID("uuid");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        //animationType = pCompound.getInt("animationtype");
        //animationTime = pCompound.getInt("animationtime");
        pCompound.putInt("install_index",getInstallIndex());
        if(owner_UUID!=null&&!(owner instanceof Player)){
            pCompound.putUUID("uuid", owner_UUID);
        }
    }

    public void setAnimation(int type,int time ){
        if(animationType==-1) return;

        animationType = type;animationTime = time;
        CompoundTag a = new CompoundTag();
        a.putInt("type",type);
        a.putInt("time",time);
        this.entityData.set(ANIMATION,a,true);
    }

    @Override
    public void lerpMotion(double pX, double pY, double pZ) {
        //super.lerpMotion(pX, pY, pZ);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if(pKey.equals(ANIMATION)){
            CompoundTag compoundTag = this.entityData.get(ANIMATION);
            int type = compoundTag.getInt("type");
            int time = compoundTag.getInt("time");
            animationType = type;animationTime = time;
        }
        super.onSyncedDataUpdated(pKey);
    }

    private final AnimationController<RosmontisInstallation> animationController = new MowzieAnimationController<>(this, "Controller", 0, this::predicate,0);
    private final AnimationController<RosmontisInstallation> TypeAnimation = new MowzieAnimationController<RosmontisInstallation>(this, "TypeController", 5, this::predicateType,0);

    private PlayState predicateType(AnimationState<RosmontisInstallation> state) {
        int installIndex = getInstallIndex();
        if(animationType==1||animationType==0||animationType==-1){
            switch (installIndex) {
                case 0:
                    state.setAnimation(RawAnimation.begin().thenLoop("type_1"));
                    break;
                case 1:
                    state.setAnimation(RawAnimation.begin().thenLoop("type_2"));
                    break;
                case 2:
                    state.setAnimation(RawAnimation.begin().thenLoop("type_3"));
                    break;
                default:
                    state.setAnimation(RawAnimation.begin().thenLoop("type_4"));
                    break;
            }
        }else {
            state.setAnimation(RawAnimation.begin().thenLoop("type_0"));
        }
        return PlayState.CONTINUE;
    }

    private PlayState predicate(AnimationState<RosmontisInstallation> state) {
        switch (animationType) {
            case 0 -> state.setAnimation(RawAnimation.begin().thenLoop("idle"));
            case 1 -> state.setAnimation(RawAnimation.begin().thenLoop("ru"));
            case 2 -> state.setAnimation(RawAnimation.begin().thenLoop("act"));
            case 3 -> state.setAnimation(RawAnimation.begin().thenLoop("act2"));
            case 4 -> state.setAnimation(RawAnimation.begin().thenPlayAndHold("disappear"));
            case 5 -> state.setAnimation(RawAnimation.begin().thenPlayAndHold("attack_1"));
            case 6 -> state.setAnimation(RawAnimation.begin().thenPlayAndHold("attack_2"));
            case 7 -> state.setAnimation(RawAnimation.begin().thenPlayAndHold("attack_3"));
            case 8 -> state.setAnimation(RawAnimation.begin().thenPlayAndHold("attack_31"));
            case 9 -> state.setAnimation(RawAnimation.begin().thenPlayAndHold("attack_4"));
            case 10 -> {
                if(getInstallIndex()==2)
                    state.setAnimation(RawAnimation.begin().thenLoop("skill_1"));
                else
                    state.setAnimation(RawAnimation.begin().thenLoop("skill_12"));
            }
            case 11 -> state.setAnimation(RawAnimation.begin().thenPlayAndHold("attack_21"));
            case 12 -> state.setAnimation(RawAnimation.begin().thenPlayAndHold("lian_1"));
            case 13 -> state.setAnimation(RawAnimation.begin().thenPlayAndHold("lian_2"));
            case 14 -> state.setAnimation(RawAnimation.begin().thenPlayAndHold("lian_3"));
            case 15 -> state.setAnimation(RawAnimation.begin().thenPlayAndHold("lian_4"));
            case -1 -> state.setAnimation(RawAnimation.begin().thenLoop("chu"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(animationController);
        controllers.add(TypeAnimation);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public LivingEntity getOwner(){
        if(owner==null&&!level().isClientSide()){
            owner = (LivingEntity) ((ServerLevel)level()).getEntity(owner_UUID);
            if(owner instanceof Rosmontis rosmontis) rosmontis.installations[getInstallIndex()] = this;
            return owner;
        }else {
            return owner;
        }
    }

    @Override
    public void remove(RemovalReason pReason) {
        if(owner instanceof Rosmontis rosmontis){
            if(rosmontis.installations[getInstallIndex()]==this){
                rosmontis.installations[getInstallIndex()]=null;
            }
        }
        super.remove(pReason);
    }

    public void setOwner(LivingEntity living){
        owner = living;
        owner_UUID = living.getUUID();
    }

    public int getInstallIndex(){
        return this.entityData.get(INSTALL_INDEX);
    }

    public void setInstallIndex(int installIndex){
        this.entityData.set(INSTALL_INDEX,installIndex);
    }

    public Vector3f getMotion(){
        return this.entityData.get(MOTION);
    }

    Vec3 tempPos;
    Vec3 tempTempPos;

    public void remoteAct1(Vec3 pos){
        if(animationType==0||animationType==1){
            setAnimation(2, 50);
            tempTempPos = pos;
            playSound(SoundHandle.ROS_START.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
        }
    }

    public void remoteAct2(Vec3 pos){
        if(animationType==0||animationType==1) {
            setAnimation(3, 55);
            tempTempPos = pos;
            playSound(SoundHandle.ROS_START.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
        }
    }

    public void remoteAct3(Vec3 pos){
        if(animationType==0||animationType==1) {
            setAnimation(4, 10);
            playSound(SoundHandle.ROS_START.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
        }
    }

    public void remoteAct3Appear(Vec3 pos){
        setAnimation(1, 20);
        playSound(SoundHandle.ROS_START.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
    }

    public void attack1(){
        setAnimation(5, 25);
        playSound(SoundHandle.ROS_START.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
    }

    public void attack2(){
        if(random.nextBoolean()) {
            setAnimation(6, 35);
        }else {
            setAnimation(11, 35);
        }
        playSound(SoundHandle.ROS_START.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
    }

    public void attack3(){
        setAnimation(7, 30);
        playSound(SoundHandle.ROS_START.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
    }

    public void attack31(){
        setAnimation(8, 35);
        playSound(SoundHandle.ROS_START.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
    }

    public void attack4(){
        setAnimation(9, 35);
        playSound(SoundHandle.ROS_START.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
    }

    public void skill2(Vec3 pos){
        setAnimation(10, 170);
        tempTempPos = pos;
        playSound(SoundHandle.ROS_START.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
    }

    public void skill4(int type,Vec3 pos){
        if(animationType==0||animationType==1) {
            if (type == 0) {
                setAnimation(12, 75);
            } else if (type == 1) {
                setAnimation(13, 75);
            } else if (type == 2) {
                setAnimation(14, 55);
            } else if (type == 3) {
                setAnimation(15, 57);
            }
            tempTempPos = pos;
            playSound(SoundHandle.ROS_START.get(), 2, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
        }
    }

    public void remoteActDamage1(double radio,float damageMul,boolean isground){
        LivingEntity livingOwner = getOwner();
        if(livingOwner!=null){
            float damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
            List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4), entity->{
                return entity.distanceTo(this)<radio;
            });
            for(LivingEntity living:entitiesOfClass){
                if(living == livingOwner) continue;
                if(living instanceof RosmontisLivingInstallation) continue;
                if(isground&&!living.onGround()) continue;
                living.invulnerableTime=0;
                //doHurtEntity(living,);
                living.hurt(damageSources().mobAttack(livingOwner),damage*damageMul);
            }
        }
    }

    public static RosmontisInstallation SpawnInstallation(Level level,LivingEntity owner,int type){
        RosmontisInstallation rosmontisInstallation = new RosmontisInstallation(EntityHandle.ROSMONTIS_INSTALLATION.get(), level);
        rosmontisInstallation.setInstallIndex(type);
        rosmontisInstallation.setOwner(owner);

        Vec3 offsetPos;
        float len = 1.2f;

        if(type==0){
            offsetPos = FFEntityUtils.getBodyRotVec(owner,new Vec3(len,1,len));
        } else if (type==1) {
            offsetPos = FFEntityUtils.getBodyRotVec(owner,new Vec3(-len,1,len));
        } else if (type==2) {
            offsetPos = FFEntityUtils.getBodyRotVec(owner,new Vec3(-len,1,-len));
        } else{
            offsetPos = FFEntityUtils.getBodyRotVec(owner,new Vec3(len,1,-len));
        }

        rosmontisInstallation.setPos(offsetPos);
        rosmontisInstallation.setAnimation(1,20);
        level.addFreshEntity(rosmontisInstallation);
        return rosmontisInstallation;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeVarInt(animationType);
        buffer.writeVarInt(animationTime);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        animationType = additionalData.readVarInt();
        animationTime = additionalData.readVarInt();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void ros_state_1to2_boom(Vec3 pos){
        Gradient toAlpha = new Gradient(new GradientColor(0XFFFFFFFF, 0XFFFFFFFF, 0X00FFFFFF));

        RLParticle rlParticle = new RLParticle(level());
        rlParticle.config.setDuration(20);
        rlParticle.config.setStartLifetime(NumberFunction.constant(10));
        rlParticle.config.setStartSize(new NumberFunction3(6));
        rlParticle.config.setStartColor(new Gradient(new GradientColor(0X7FB2F5FF)));
        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst = new EmissionSetting.Burst(); burst.setCount(NumberFunction.constant(1));
        rlParticle.config.getEmission().addBursts(burst);
        rlParticle.config.getShape().setShape(new Dot());
        rlParticle.config.getMaterial().setMaterial(MaterialHandle.RING.create());
        rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
        rlParticle.config.getLights().open();
        rlParticle.config.getColorOverLifetime().open();
        rlParticle.config.getColorOverLifetime().setColor(toAlpha);
        rlParticle.config.getSizeOverLifetime().open();
        rlParticle.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,0.33f,1},new float[]{0,0.7f,1})));
        RLParticle rlParticle3 = new RLParticle(level());
        rlParticle3.config.setDuration(20);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(10));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(7));
        rlParticle3.config.setStartColor(new Gradient(new GradientColor(0X8FB2F5FF)));
        rlParticle3.config.setStartSize(new NumberFunction3(1));
        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst3 = new EmissionSetting.Burst(); burst3.setCount(NumberFunction.constant(30));burst3.time=3;
        rlParticle3.config.getEmission().addBursts(burst3);
        Circle circle3 = new Circle();circle3.setRadius(0.5f);circle3.setRadiusThickness(1f);
        rlParticle3.config.getShape().setShape(circle3);
        rlParticle3.config.getMaterial().setMaterial(MaterialHandle.SMOKE.create());
        rlParticle3.config.getColorOverLifetime().open();
        rlParticle3.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle3.config.getPhysics().open();
        rlParticle3.config.getPhysics().setHasCollision(false);
        rlParticle3.config.getPhysics().setFriction(NumberFunction.constant(0.9));
        rlParticle3.config.getUvAnimation().open();
        rlParticle3.config.getUvAnimation().setTiles(new Range(2,2));
        rlParticle3.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);

        BlockEffect blockEffect = new BlockEffect(level(), pos);
        rlParticle.emmit(blockEffect);
        rlParticle3.emmit(blockEffect);
    }

    public static void ros_instationTrailFx(Entity entity,Vec3 pos, Vec3 targetPos){
        Vec3 vec3 = targetPos.subtract(pos);
        double d0 = vec3.horizontalDistance();
        float yRot1 = (float) (Mth.atan2(vec3.x, vec3.z) );
        float xRot1 = (float)(Mth.atan2(vec3.y, d0));

        RLParticle rlParticle = new RLParticle(entity.level());
        rlParticle.config.setDuration(40);
        rlParticle.config.setStartLifetime(NumberFunction.constant(16));
        rlParticle.config.setStartSize(new NumberFunction3(2));
        rlParticle.config.setStartColor(new Gradient(new GradientColor(0X6FB2F5FF)));
        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst = new EmissionSetting.Burst();burst.setCount(NumberFunction.constant(1));
        rlParticle.config.getEmission().addBursts(burst);
        rlParticle.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle.config.getShape().setShape(new Dot());
        Vec3 yrotVec3 = new Vec3(0, 0, -20).xRot(xRot1);
        rlParticle.config.getShape().setPosition(new NumberFunction3(0,yrotVec3.y,-yrotVec3.z));
        rlParticle.config.getMaterial().setMaterial(MaterialHandle.VOID.create());
        rlParticle.config.getLights().open();
        rlParticle.config.getVelocityOverLifetime().open();
        Vec3 yrotVec31 = new Vec3(0, 0, 160).xRot(xRot1);
        rlParticle.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,0.5f,1},new float[]{(float) yrotVec31.y,0,0}),new Line(new float[]{0,0.5f,1},new float[]{(float) -yrotVec31.z,0,0})));
        rlParticle.config.trails.open();
        rlParticle.config.trails.config.material.setMaterial(TBSMaterialHandle.BIG_SMOKE.create());
        rlParticle.config.trails.setColorOverLifetime(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle.config.trails.config.lights.open();

        RLParticle rlParticle1 = new RLParticle(entity.level());
        rlParticle1.config.setDuration(8);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle1.config.setStartSize(new NumberFunction3(0.6,0.1,0.1));
        rlParticle1.config.setStartRotation(new NumberFunction3(0, (yRot1 * 180 / Math.PI)-90,xRot1/3.14*180));//负数是顺时针
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XFFB2F5FF)));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(1.6));
        burst1.cycles = 7;
        rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        rlParticle1.config.getEmission().addBursts(burst1);
        Sphere circle1 = new Sphere();circle1.setRadius(1);
        rlParticle1.config.getShape().setShape(circle1);
        Vec3 yrotVec32 = new Vec3(0, 0, 16).xRot(xRot1);
        rlParticle1.config.getShape().setPosition(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,1},new float[]{0,(float) yrotVec32.y}),new Line(new float[]{0,1},new float[]{0,(float) -yrotVec32.z})));
        rlParticle1.config.getMaterial().setMaterial(TBSMaterialHandle.NO_GLOW_PIXEL.create());
        rlParticle1.config.getMaterial().setCull(false);
        rlParticle1.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Vertical);
        rlParticle1.config.getLights().open();
        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

        BlockEffect blockEffect = new BlockEffect(entity.level(),pos);
        rlParticle.updateRotation(new Vector3f(0,(float) (-entity.getYRot() / 180 * Math.PI),0));
        rlParticle1.updateRotation(new Vector3f(0,(float) (-entity.getYRot() / 180 * Math.PI),0));

        rlParticle.emmit(blockEffect);
        rlParticle1.emmit(blockEffect);
    }

    public boolean doRangeAttackAngle(LivingEntity rosmontis,double range, double arc,float damage,float yRot,boolean isBreakingShield){
        boolean flag = false;
        List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range+5, 3, range+5), e -> e != rosmontis && distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * (180 / Math.PI) - 90)+ yRot % 360) ;
            float entityAttackingAngle = getYRot() % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - getZ()) * (entityHit.getZ() - getZ()) + (entityHit.getX() - getX()) * (entityHit.getX() - getX())) - entityHit.getBbWidth() / 2f;
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                if(entityHit.hurt(rosmontis instanceof Player?DamageSourceHandle.noTriggerAttack(rosmontis):damageSources().mobAttack(rosmontis),damage)) {
                    flag = true;
                }
                if(isBreakingShield&&entityHit instanceof Player player){
                    ItemStack pPlayerItemStack = player.getUseItem();
                    if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                        player.getCooldowns().addCooldown(Items.SHIELD, 100);
                        player.stopUsingItem();
                        this.level().broadcastEntityEvent(player, (byte)30);
                    }
                }
            }
        }
        return  flag;
    }
}
