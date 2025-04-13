package com.freefish.torchesbecomesunlight.client.particle.advance.base;

import com.freefish.torchesbecomesunlight.client.particle.advance.data.Transform;

public interface IGameObject {

    // Get the layer of this object
    default int getLayer() {
        return 0;
    }

    // Get the transform of this object
    Transform getTransform();

}
