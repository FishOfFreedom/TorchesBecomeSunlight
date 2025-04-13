package com.freefish.torchesbecomesunlight.client.particle.advance.data.shape;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.torchesbecomesunlight.client.util.Vector3fHelper;
import org.joml.Vector3f;
import net.minecraft.util.Mth;

/**
 * @author KilaBash
 * @date 2023/5/26
 * @implNote Sphere
 */
public class Sphere implements IShape {
    private float radius = .5f;
    private float radiusThickness = 1;
    private float arc = 360;

    public float getRadiusThickness() {
        return radiusThickness;
    }

    public void setRadiusThickness(float radiusThickness) {
        this.radiusThickness = radiusThickness;
    }

    public float getArc() {
        return arc;
    }

    public void setArc(float arc) {
        this.arc = arc;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }


    @Override
    public void nextPosVel(AdvancedRLParticleBase particle, AdvancedRLParticleBase emitter, Vector3f position, Vector3f rotation, Vector3f scale) {
        var random = particle.getRandomSource();
        var outer = radius;
        var inner = (1 - radiusThickness) * radius;
        var origin = inner * inner * inner;
        var bound = outer * outer * outer;
        var r = outer == inner ? outer : Math.cbrt(origin + random.nextDouble() * (bound - origin));

        var theta = Math.acos(2 * random.nextDouble() - 1);
        var phi = arc * Mth.TWO_PI * random.nextDouble() / 360;

        var pos = new Vector3f((float) (r * Math.sin(theta) * Math.cos(phi)),
                (float) (r * Math.sin(theta) * Math.sin(phi)),
                (float) (r * Math.cos(theta))).mul(scale);

        particle.setPos(Vector3fHelper.rotateYXY(new Vector3f(pos), rotation).add(position).add(particle.getVecPos()), true);
        particle.setSpeed(Vector3fHelper.rotateYXY(new Vector3f(pos).normalize().mul(0.05f), rotation));
    }
}
