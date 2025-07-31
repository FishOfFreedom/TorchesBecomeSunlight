package com.freefish.torchesbecomesunlight.server.effect.forceeffect;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.RandomConstant;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Sphere;
import com.freefish.rosmontislib.client.particle.advance.effect.EntityEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class LightingForceEffect extends ForceEffect{
    @Override
    public void addEffect(LivingEntity living, int level) {

    }

    @Override
    public void removeEffect(LivingEntity living) {

    }

    @Override
    public void tick(LivingEntity entity, int level) {
        if (!(entity instanceof Player player && player.isCreative())) {
            if(entity.level().isClientSide&&entity.tickCount%20==0){
                RLParticle rlParticle2 = new RLParticle(entity.level());
                rlParticle2.config.setDuration(20);
                rlParticle2.config.setStartLifetime(NumberFunction.constant(8));
                rlParticle2.config.setStartSpeed(NumberFunction.constant(2));
                rlParticle2.config.setStartColor(new Gradient(new GradientColor(0XFFDFEF86)));
                rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0.2));
                rlParticle2.config.getMaterial().setMaterial(MaterialHandle.VOID.create());
                Sphere circle2 = new Sphere();circle2.setRadius(0.5f);
                rlParticle2.config.getShape().setShape(circle2);
                rlParticle2.config.getShape().setPosition(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(2,0,true),NumberFunction.constant(0)));
                rlParticle2.config.getNoise().open();
                rlParticle2.config.getNoise().setPosition(new NumberFunction3(1.5));

                rlParticle2.config.getVelocityOverLifetime().open();
                rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(0,6,0));

                rlParticle2.config.trails.open();
                rlParticle2.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
                rlParticle2.config.trails.config.getRenderer().setBloomEffect(true);

                EntityEffect effect = new EntityEffect(entity.level(),entity);
                float scale = entity.getBbHeight()/4;
                rlParticle2.updateScale(new Vector3f(scale));
                rlParticle2.emmit(effect);
            }
        }
    }

    @Override
    public ForceEffectType<?> getType() {
        return ForceEffectHandle.LIGHTING_FORCE_EFFECT;
    }
}
