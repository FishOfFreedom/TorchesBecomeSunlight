package com.freefish.torchesbecomesunlight.client.particle.advance.data.material;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2023/5/29
 * @implNote MaterialsResource
 */
@OnlyIn(Dist.CLIENT)
public interface IMaterial {

    void begin(boolean isInstancing);

    void end(boolean isInstancing);
}
