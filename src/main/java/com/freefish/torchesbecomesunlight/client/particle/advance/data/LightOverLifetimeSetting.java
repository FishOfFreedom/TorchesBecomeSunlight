package com.freefish.torchesbecomesunlight.client.particle.advance.data;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.torchesbecomesunlight.client.particle.advance.data.number.NumberFunction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2023/6/1
 * @implNote LightSetting
 */
@OnlyIn(Dist.CLIENT)
public class LightOverLifetimeSetting {
    protected NumberFunction skyLight = NumberFunction.constant(15);
    protected NumberFunction blockLight = NumberFunction.constant(15);

    public int getLight(AdvancedRLParticleBase particle, float partialTicks) {
        int sky = skyLight.get(particle.getT(partialTicks), () -> particle.getMemRandom("sky-light")).intValue();
        int block = blockLight.get(particle.getT(partialTicks), () -> particle.getMemRandom("block-light")).intValue();
        return sky << 20 | block << 4;
    }
}
