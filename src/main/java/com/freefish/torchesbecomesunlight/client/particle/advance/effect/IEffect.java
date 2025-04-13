package com.freefish.torchesbecomesunlight.client.particle.advance.effect;

import com.freefish.torchesbecomesunlight.client.particle.advance.base.IParticleEmitter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface IEffect {
    /**
     * get all emitters included in this effect.
     */
    List<IParticleEmitter> getEmitters();

    /**
     * update each emitter during their duration,
     * @param emitter emitter
     * @return true - block emitter origin tick logic.
     */
    boolean updateEmitter(IParticleEmitter emitter);

    /**
     * Get emitter by name
     */
    @Nullable
    default IParticleEmitter getEmitterByName(String name) {
        for (var emitter : getEmitters()) {
            if (emitter.getName().equals(name)) return emitter;
        }
        return null;
    };
}