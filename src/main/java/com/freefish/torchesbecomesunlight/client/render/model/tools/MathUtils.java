package com.freefish.torchesbecomesunlight.client.render.model.tools;

import org.joml.Quaternionf;

/**
 * @author Paul Fulham
 */
public final class MathUtils {

    public static Quaternionf quatFromRotationXYZ(float x, float y, float z, boolean degrees) {
        if (degrees) {
            x *= ((float)Math.PI / 180F);
            y *= ((float)Math.PI / 180F);
            z *= ((float)Math.PI / 180F);
        }
        return (new Quaternionf()).rotationXYZ(x, y, z);
    }
}
