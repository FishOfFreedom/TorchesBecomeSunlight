package com.freefish.torchesbecomesunlight.client.particle.advance.data.shape;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.torchesbecomesunlight.client.util.Vector3fHelper;
import org.joml.Vector3f;
import net.minecraft.util.Mth;

/**
 * @author KilaBash
 * @date 2023/5/29
 * @implNote Circle
 */
public class Circle implements IShape {
    private float radius = .5f;
    private float radiusThickness = 1;
    private float arc = 360;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

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

    @Override
    public void nextPosVel(AdvancedRLParticleBase particle, AdvancedRLParticleBase emitter, Vector3f position, Vector3f rotation, Vector3f scale) {
        var random = particle.getRandomSource();
        var outer = radius;
        var inner = (1 - radiusThickness) * radius;
        var origin = inner * inner;
        var bound = outer * outer;
        var r = outer == inner ? outer : Math.sqrt(origin + random.nextDouble() * (bound - origin));

        var theta = arc * Mth.TWO_PI * random.nextDouble() / 360;

        var pos = new Vector3f((float) (r * Math.cos(theta)),
                0f,
                (float) (r * Math.sin(theta))).mul(scale);

        particle.setPos(Vector3fHelper.rotateYXY(new Vector3f(pos), rotation).add(position).add(particle.getVecPos()), true);
        particle.setSpeed(Vector3fHelper.rotateYXY(new Vector3f(pos).normalize().mul(0.05f), rotation));
    }
}
