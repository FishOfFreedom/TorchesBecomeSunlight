package com.freefish.torchesbecomesunlight.client.particle.advance.data;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.torchesbecomesunlight.client.particle.advance.data.number.NumberFunction;
import com.freefish.torchesbecomesunlight.client.particle.advance.data.number.color.Gradient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2023/5/30
 * @implNote ColorOverLifetimeSetting
 */
@OnlyIn(Dist.CLIENT)
public class ColorOverLifetimeSetting extends ToggleGroup {

    protected NumberFunction color = new Gradient();

    public int getColor(AdvancedRLParticleBase particle, float partialTicks) {
        return color.get(particle.getT(partialTicks), () -> particle.getMemRandom(this)).intValue();
    }

}
