package com.freefish.torchesbecomesunlight.server.partner.vanilla;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.RendererSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.GradientHandle;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.torchesbecomesunlight.compat.rosmontis.EntityPosRotEffect;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerHandler;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SanktaPartner extends Partner<Mob> {
    private int controlTime;

    public SanktaPartner() {
        super(PartnerHandler.WOLF_PARTNER);
    }

    @Override
    public boolean isReplaceGoal() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        controlTime++;

        Mob partnerMob = getPartnerMob();
        Player player = getPlayer();
        Level level = partnerMob.level();

        if(level.isClientSide&&controlTime==1){
            RLParticle rlParticle = new RLParticle(level);
            rlParticle.config.setStartLifetime(NumberFunction.constant(40));
            rlParticle.config.setStartSize(new NumberFunction3(partnerMob.getBbWidth()));
            rlParticle.config.setStartSpeed(NumberFunction.constant(0));
            rlParticle.config.setStartColor(new Gradient(new GradientColor(0XFFfafc75)));
            rlParticle.config.getShape().setShape(new Dot());
            rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
            EmissionSetting.Burst burst = new EmissionSetting.Burst();
            burst.setCount(NumberFunction.constant(1));
            rlParticle.config.getEmission().addBursts(burst);
            rlParticle.config.getRenderer().setBloomEffect(true);
            rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
            rlParticle.config.getMaterial().setCull(false);

            rlParticle.config.getMaterial().setMaterial(MaterialHandle.RING.create());
            rlParticle.config.getColorOverLifetime().open();
            rlParticle.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

            EntityPosRotEffect effect =  new EntityPosRotEffect(level,partnerMob,new Vec3(0,partnerMob.getBbHeight()+0.5,0));
            rlParticle.emmit(effect);
        }


        if(controlTime>=200){
            PartnerUtil.setPartner(partnerMob,null);
        }

        if(!level.isClientSide&&partnerMob.tickCount%2==0){
            LivingEntity lastHurtMob = player.getLastAttacker();
            LivingEntity target = partnerMob.getTarget();
            if((target==null||!target.isAlive())&&lastHurtMob!=partnerMob){
                partnerMob.setTarget(lastHurtMob);
            }
            if(target == player){
                partnerMob.setTarget(null);
            }
        }
    }
}
