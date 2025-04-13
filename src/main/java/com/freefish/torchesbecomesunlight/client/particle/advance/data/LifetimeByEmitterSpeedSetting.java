package com.freefish.torchesbecomesunlight.client.particle.advance.data;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.torchesbecomesunlight.client.particle.advance.data.number.NumberFunction;
import com.freefish.torchesbecomesunlight.client.util.Range;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2023/5/30
 * @implNote LifetimeByEmitterSpeed
 */
@OnlyIn(Dist.CLIENT)
public class LifetimeByEmitterSpeedSetting extends ToggleGroup{
    protected NumberFunction multiplier = NumberFunction.constant(1);

    protected Range speedRange = new Range(0f, 1f);

    public int getLifetime(AdvancedRLParticleBase particle, AdvancedRLParticleBase emitter, int initialLifetime) {
        var value = emitter.getVelocity().length() * 20;
        return (int) (multiplier.get((value - speedRange.getA().floatValue()) / (speedRange.getB().floatValue() - speedRange.getA().floatValue()), () -> particle.getMemRandom(this)).floatValue() * initialLifetime);
    }

}
