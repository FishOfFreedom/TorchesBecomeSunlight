package com.freefish.torchesbecomesunlight.client.particle.advance.data.shape;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import org.joml.Vector3f;
import net.minecraft.nbt.CompoundTag;

/**
 * @author KilaBash
 * @date 2023/5/26
 * @implNote IShape
 */
public interface IShape {
    void nextPosVel(AdvancedRLParticleBase particle, AdvancedRLParticleBase emitter, Vector3f position, Vector3f rotation, Vector3f scale);
}
