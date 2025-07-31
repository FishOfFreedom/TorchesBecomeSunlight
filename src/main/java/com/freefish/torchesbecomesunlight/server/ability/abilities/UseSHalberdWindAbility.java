package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.UVAnimationSetting;
import com.freefish.rosmontislib.client.particle.advance.data.VelocityOverLifetimeSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.RandomConstant;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.GradientHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.Line;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Circle;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.rosmontislib.client.utils.Range;
import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

public class UseSHalberdWindAbility extends PlayerAbility {

    public UseSHalberdWindAbility(AbilityType<Player, UseSHalberdWindAbility> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 18),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 17)
        });
    }

    private static final RawAnimation USE_HALBERD = RawAnimation.begin().thenPlay("use_sacred_halberd");

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
        if (getTicksInUse() == 20) {
            Level level = getLevel();
            Player player = getUser();
            if(level.isClientSide){
                jianshuwind(false);
            }else {
                player.playSound(SoundHandle.CycleWind.get(), 2.5F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 0.8F));

                List<LivingEntity> list = player.level().getEntitiesOfClass(LivingEntity.class,player.getBoundingBox().inflate(8+5), livingEntity ->
                        livingEntity.distanceTo(player)<8+livingEntity.getBbWidth()/2);
                for(LivingEntity entityHit:list) {
                    if(entityHit == player) continue;
                    entityHit.hurt(player.damageSources().playerAttack(player), ConfigHandler.COMMON.TOOLs.SACRED_HALBERD.attackDamageValue*2);
                }
            }
        }
    }

    private void jianshuwind(boolean isfan){
        Level level = getLevel();

        RLParticle rlParticle1 = new RLParticle( level);
        rlParticle1.config.setDuration(20);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(30));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle1.config.setStartSize(new NumberFunction3(2.8));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0X67E6FFFF)));

        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(12));
        rlParticle1.config.getEmission().addBursts(burst1);

        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.SMOKE.create());

        Circle circle1 = new Circle();circle1.setRadius(0.5f);circle1.setRadiusThickness(0);
        rlParticle1.config.getShape().setShape(circle1);

        rlParticle1.config.getVelocityOverLifetime().open();
        rlParticle1.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.FixedVelocity);

        float fan = isfan?-1:1;

        rlParticle1.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,0.08f,0.16f,1},new float[]{60*fan,60*fan,30*fan,30*fan}),NumberFunction.constant(0)));

        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF)));

        rlParticle1.config.getUvAnimation().open();
        rlParticle1.config.getUvAnimation().setTiles(new Range(2,2));
        rlParticle1.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);

        rlParticle1.config.trails.open();
        rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.SMOKE.create());

        RLParticle rlParticle3 = new RLParticle( level);
        rlParticle3.config.setDuration(20);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(12));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle3.config.setStartSize(new NumberFunction3(0.1));
        rlParticle3.config.setStartRotation(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(0),new RandomConstant(-40 ,40 , true)));

        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(3));
        EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.setCount(NumberFunction.constant(12));
        rlParticle3.config.getEmission().addBursts(burst3);

        rlParticle3.config.getMaterial().setMaterial(MaterialHandle.GLOW.create());

        Circle circle3 = new Circle();circle3.setRadius(1);circle3.setRadiusThickness(0);
        rlParticle3.config.getShape().setShape(circle3);

        rlParticle3.config.getVelocityOverLifetime().open();
        rlParticle3.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.FixedVelocity);
        rlParticle3.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,0.5f,1},new float[]{16*fan,16*fan,7*fan}),NumberFunction.constant(0)));
        rlParticle3.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(1,0.5,true),NumberFunction.constant(0)));

        rlParticle3.config.getColorOverLifetime().open();
        rlParticle3.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

        BlockEffect blockEffect = new BlockEffect(level, FFEntityUtils.getBodyRotVec(getUser(),new Vec3(0,0.6,0.5)));
        rlParticle1.emmit(blockEffect);
        rlParticle3.emmit(blockEffect);
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
