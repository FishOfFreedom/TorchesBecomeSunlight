package com.freefish.torchesbecomesunlight.client.particle.advance.data.shape;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import org.joml.Vector3f;

/**
 * @author KilaBash
 * @date 2023/5/26
 * @implNote Dot
 */
public class Dot implements IShape {

    @Override
    public void nextPosVel(AdvancedRLParticleBase particle, AdvancedRLParticleBase emitter, Vector3f position, Vector3f rotation, Vector3f scale) {
        particle.setPos(position.add(particle.getVecPos()), true);
        particle.setSpeed(new Vector3f(0, 0, 0));
    }
}
