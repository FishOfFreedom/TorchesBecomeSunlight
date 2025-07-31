package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.rosmontislib.client.RLClientUseUtils;
import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.RendererSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.RandomConstant;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.Line;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.RandomLine;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Circle;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Cone;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.rosmontislib.client.utils.Range;
import com.freefish.rosmontislib.commom.init.DamageSourceHandle;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

public class UseSHalberdWindLightAbility extends PlayerAbility {

    public UseSHalberdWindLightAbility(AbilityType<Player, UseSHalberdWindLightAbility> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 20),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 30)
        });
    }

    private static final RawAnimation USE_HALBERD = RawAnimation.begin().thenPlay("use_sacred_halberd3");

    @Override
    public void start() {
        super.start();
        heldItemMainHandVisualOverride = getUser().getMainHandItem();
        if (!getUser().level().isClientSide()) {
        }
        else {
            playAnimation(USE_HALBERD);
        }
    }

    @Override
    public void tickUsing() {
        super.tickUsing();
        Player player = getUser();
        Level level = getLevel();
        if (getTicksInUse() == 28) {
            player.setDeltaMovement(player.getDeltaMovement().add(new Vec3(0,1,0)));
        }
        else if (getTicksInUse() == 32) {
            player.setDeltaMovement(player.getDeltaMovement().add(new Vec3(0,-1,0)));
        }
        else if (getTicksInUse() == 37) {
            if(!level.isClientSide){
                RLClientUseUtils.StartCameraShake(level, player.position(), 20, 0.06F, 20, 15);

                player.playSound(SoundHandle.ShotLight.get(), 4.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
                List<LivingEntity> entitiesOfClass = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(18), living ->
                        living.distanceTo(player) < 5);
                for (LivingEntity living : entitiesOfClass) {
                    if (living == player) continue;

                    living.hurt(DamageSourceHandle.realDamage(player), 24 * 3);
                }
                List<LivingEntity> entitiesOfClass1 = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(64), living -> {
                    float v = living.distanceTo(player);
                    return v > 5 && v < 20;
                });
                for (LivingEntity living : entitiesOfClass1) {
                    if (living == player) continue;
                    living.hurt(player.damageSources().playerAttack(player), 24 * 2);
                }
            }else {
                Vec3 pos = FFEntityUtils.getBodyRotVec(player,new Vec3(0,0,2));
                bigSkillHalberd2(new Vector3f(0.8f),pos);
                skillHalberd12(pos);
            }
        }
    }

    //大闪电冲击波
    public void bigSkillHalberd2(Vector3f scale, Vec3 pos){
        RLParticle rlParticle1 = new RLParticle( getLevel());
        rlParticle1.config.setDuration(10);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(5));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(3));
        rlParticle1.config.setStartSize(new NumberFunction3(0.2));

        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(10));
        rlParticle1.config.getEmission().addBursts(burst1);

        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.VOID.create());

        Circle circle1 = new Circle();circle1.setRadius(1);circle1.setRadiusThickness(1);
        rlParticle1.config.getShape().setShape(circle1);

        rlParticle1.config.getVelocityOverLifetime().open();
        rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(3),NumberFunction.constant(0)));

        rlParticle1.config.getNoise().open();
        rlParticle1.config.getNoise().setPosition(new NumberFunction3(1));

        rlParticle1.config.trails.open();
        rlParticle1.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0XFFDFEF86)));
        rlParticle1.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle2 = new RLParticle( getLevel());
        rlParticle2.config.setDuration(8);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(2));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(20));
        rlParticle2.config.setStartSize(new NumberFunction3(0.4));

        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst2 = new EmissionSetting.Burst();burst2.setCount(NumberFunction.constant(7));
        burst2.cycles = 3;burst2.interval = 2;
        rlParticle2.config.getEmission().addBursts(burst2);

        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.VOID.create());

        Circle circle2 = new Circle();circle2.setRadius(0.2f);circle2.setRadiusThickness(0.2f);
        rlParticle2.config.getShape().setScale(new NumberFunction3(NumberFunction.constant(0.4),NumberFunction.constant(1),NumberFunction.constant(0.4)));
        rlParticle2.config.getShape().setShape(circle2);

        rlParticle2.config.getVelocityOverLifetime().open();
        rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(30,0,true),NumberFunction.constant(0)));

        rlParticle2.config.getNoise().open();
        rlParticle2.config.getNoise().setPosition(new NumberFunction3(1));

        rlParticle2.config.trails.open();
        rlParticle2.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFDFEF86)));
        rlParticle2.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle3 = new RLParticle( getLevel());
        rlParticle3.config.setDuration(8);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(3));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(10));
        rlParticle3.config.setStartSize(new NumberFunction3(1.4));

        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.setCount(NumberFunction.constant(5));
        rlParticle3.config.getEmission().addBursts(burst3);

        rlParticle3.config.getMaterial().setMaterial(MaterialHandle.VOID.create());

        rlParticle3.config.getShape().setShape(new Dot());

        rlParticle3.config.getVelocityOverLifetime().open();
        rlParticle3.config.getVelocityOverLifetime().setLinear(new NumberFunction3(0,70,0));

        rlParticle3.config.getNoise().open();
        rlParticle3.config.getNoise().setPosition(new NumberFunction3(0.7));
        //todo
        rlParticle3.config.trails.open();
        rlParticle3.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
        rlParticle3.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFDFEF86)));
        rlParticle3.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle4 = new RLParticle( getLevel());
        rlParticle4.config.setDuration(20);
        rlParticle4.config.setStartLifetime(NumberFunction.constant(9));
        rlParticle4.config.setStartSpeed(NumberFunction.constant(6));

        rlParticle4.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst4 = new EmissionSetting.Burst();burst4.setCount(NumberFunction.constant(20));
        rlParticle4.config.getEmission().addBursts(burst4);burst4.time = 2;

        rlParticle4.config.getMaterial().setMaterial(MaterialHandle.VOID.create());

        Circle circle4 = new Circle();circle4.setRadius(2f);circle4.setRadiusThickness(1f);
        rlParticle4.config.getShape().setShape(circle4);

        rlParticle4.config.getNoise().open();
        rlParticle4.config.getNoise().setPosition(new NumberFunction3(0.5));

        rlParticle4.config.trails.open();
        rlParticle4.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
        rlParticle4.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0XFFDFEF86)));
        rlParticle4.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle5 = new RLParticle( getLevel());
        rlParticle5.config.setDuration(20);
        rlParticle5.config.setStartLifetime(NumberFunction.constant(8));
        rlParticle5.config.setStartSpeed(NumberFunction.constant(1));
        rlParticle5.config.setStartSize(new NumberFunction3(10));

        rlParticle5.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst5 = new EmissionSetting.Burst();burst5.setCount(NumberFunction.constant(2));
        burst5.cycles = 2;burst5.interval = 2;
        rlParticle5.config.getEmission().addBursts(burst5);

        rlParticle5.config.getMaterial().setMaterial(MaterialHandle.RING.create());
        rlParticle5.config.getShape().setShape(new Dot());

        ((RendererSetting.Particle)rlParticle5.config.getRenderer()).setRenderMode(RendererSetting.Particle.Mode.Horizontal);

        rlParticle5.config.getColorOverLifetime().open();
        rlParticle5.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0X00DFEF86)));

        rlParticle5.config.getSizeOverLifetime().open();
        rlParticle5.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,1},new float[]{0,1})));

        RLParticle rlParticle6 = new RLParticle( getLevel());
        rlParticle6.config.setDuration(4);
        rlParticle6.config.setStartLifetime(NumberFunction.constant(5));
        rlParticle6.config.setStartSize(new NumberFunction3(10));

        rlParticle6.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
        rlParticle6.config.setStartColor(new Gradient(new GradientColor(0X48FFFFFF)));
        rlParticle6.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());

        rlParticle6.config.getShape().setShape(new Dot());
        rlParticle6.config.getShape().setPosition(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,1},new float[]{-3,14}),NumberFunction.constant(0)));

        rlParticle6.config.getColorOverLifetime().open();
        rlParticle6.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFDFEF86,0XFFDFEF86,0X00DFEF86)));

        RLParticle rlParticle7 = new RLParticle( getLevel());
        rlParticle7.config.setDuration(10);
        rlParticle7.config.setStartLifetime(NumberFunction.constant(10));
        rlParticle7.config.setStartSpeed(NumberFunction.constant(40));

        rlParticle7.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst7 = new EmissionSetting.Burst();burst7.setCount(NumberFunction.constant(7));
        rlParticle7.config.getEmission().addBursts(burst7);burst7.time = 6;burst7.cycles=0;

        rlParticle7.config.getMaterial().setMaterial(MaterialHandle.VOID.create());

        Cone circle7 = new Cone();circle7.setRadius(0.5f);circle7.setRadiusThickness(1f);circle7.setAngle(60);
        rlParticle7.config.getShape().setShape(circle7);

        rlParticle7.config.getPhysics().open();
        rlParticle7.config.getPhysics().setHasCollision(false);
        rlParticle7.config.getPhysics().setFriction(NumberFunction.constant(0.5));

        rlParticle7.config.trails.open();
        rlParticle7.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
        rlParticle7.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFDFEF86,0X00DFEF86)));
        rlParticle7.config.trails.config.getRenderer().setBloomEffect(true);

        BlockEffect blockEffect = new BlockEffect(getLevel(),pos);
        rlParticle1.updateScale(scale);
        rlParticle2.updateScale(scale);
        rlParticle3.updateScale(scale);
        rlParticle4.updateScale(scale);
        rlParticle5.updateScale(scale);
        rlParticle6.updateScale(scale);
        rlParticle7.updateScale(scale);
        rlParticle1.emmit(blockEffect);
        rlParticle2.emmit(blockEffect);
        rlParticle3.emmit(blockEffect);
        rlParticle4.emmit(blockEffect);
        rlParticle5.emmit(blockEffect);
        rlParticle6.emmit(blockEffect);
        rlParticle7.emmit(blockEffect);
    }

    public void skillHalberd12(Vec3 geoBone){
        RLParticle rlParticle1 = new RLParticle( getLevel());

        rlParticle1.config.setDuration(100);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(100));
        rlParticle1.config.setStartDelay(NumberFunction.constant(3));
        rlParticle1.config.setStartSpeed(new RandomConstant(30 ,5,true));
        rlParticle1.config.setStartSize(new NumberFunction3(1.1));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0X61DFDDAF)));

        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(80));
        rlParticle1.config.getEmission().addBursts(burst1);

        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.SMOKE.create());

        Circle circle1 = new Circle();
        rlParticle1.config.getShape().setShape(circle1);

        rlParticle1.config.getPhysics().open();
        rlParticle1.config.getPhysics().setHasCollision(false);
        rlParticle1.config.getPhysics().setFriction(NumberFunction.constant(0.93));

        rlParticle1.config.getVelocityOverLifetime().open();
        rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomLine(new float[]{0,0.083f,1},new float[]{4,0,0},new float[]{0,0,0}),NumberFunction.constant(0)));

        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF)));

        rlParticle1.config.getUvAnimation().open();
        rlParticle1.config.getUvAnimation().setTiles(new Range(2,2));

        rlParticle1.config.trails.open();
        rlParticle1.config.trails.setColorOverLifetime(new Gradient(new GradientColor(new float[]{1,0.96f,1},new int[]{0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF})));

        RLParticle rlParticle2 = new RLParticle( getLevel());
        rlParticle2.config.setDuration(100);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(9));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(30));
        rlParticle2.config.setStartSize(new NumberFunction3(1));
        rlParticle2.config.setStartColor(new Gradient(new GradientColor(0XFFDFDDAF)));

        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst2 = new EmissionSetting.Burst();burst2.setCount(NumberFunction.constant(20));
        burst2.cycles = 2;
        rlParticle2.config.getEmission().addBursts(burst2);

        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());

        rlParticle2.config.getShape().setShape(new Circle());

        rlParticle2.config.getVelocityOverLifetime().open();
        rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(1),NumberFunction.constant(0)));

        rlParticle2.config.getColorOverLifetime().open();
        rlParticle2.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF)));

        rlParticle2.config.getSizeOverLifetime().open();
        rlParticle2.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,1},new float[]{1,2})));

        RLParticle rlParticle3 = new RLParticle( getLevel());
        rlParticle3.config.setDuration(100);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(80));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(1.5));
        rlParticle3.config.setStartSize(new NumberFunction3(0.2));
        rlParticle3.config.setStartColor(new Gradient(new GradientColor(0XFFDFDDAF)));

        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.setCount(NumberFunction.constant(150));
        rlParticle3.config.getEmission().addBursts(burst3);

        rlParticle3.config.getMaterial().setMaterial(TBSMaterialHandle.PIXEL.create());

        Circle circle3 = new Circle();circle3.setRadius(15);
        rlParticle3.config.getShape().setShape(circle3);

        rlParticle3.config.getPhysics().open();
        rlParticle3.config.getPhysics().setFriction(NumberFunction.constant(0.99));
        rlParticle3.config.getPhysics().setBounceChance(NumberFunction.constant(0));
        rlParticle3.config.getPhysics().setGravity(new Line(new float[]{0,0.33f,1},new float[]{0,1,1}));

        rlParticle3.config.getVelocityOverLifetime().open();
        rlParticle3.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomLine(new float[]{0,0.3f,1},new float[]{15,0,0},new float[]{0,0,0}),NumberFunction.constant(0)));

        rlParticle3.config.getRotationOverLifetime().open();
        rlParticle3.config.getRotationOverLifetime().setRoll(new Line(new float[]{0,1},new float[]{0,360}));

        rlParticle3.config.trails.open();
        rlParticle3.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0X00FFFFFF,0X00FFFFFF,0X00FFFFFF)));
        rlParticle3.config.trails.open();

        BlockEffect geoBoneEffect = new BlockEffect(getLevel(), geoBone);
        rlParticle1.emmit(geoBoneEffect);
        rlParticle2.emmit(geoBoneEffect);
        rlParticle3.emmit(geoBoneEffect);
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public boolean preventsAttacking() {
        return false;
    }
}
