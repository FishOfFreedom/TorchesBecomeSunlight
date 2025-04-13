package com.freefish.torchesbecomesunlight.client.particle.advance.data;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.torchesbecomesunlight.client.particle.advance.data.number.NumberFunction;

/**
 * @author KilaBash
 * @date 2023/5/31
 * @implNote PhysicsSetting
 */
public class PhysicsSetting extends ToggleGroup{
    protected boolean hasCollision = true;
    protected boolean removedWhenCollided = false;
    protected NumberFunction friction = NumberFunction.constant(0.98);
    protected NumberFunction gravity = NumberFunction.constant(0);
    protected NumberFunction bounceChance = NumberFunction.constant(1);
    protected NumberFunction bounceRate =NumberFunction.constant(1);
    protected NumberFunction bounceSpreadRate = NumberFunction.constant(0);

    public NumberFunction getBounceSpreadRate() {
        return bounceSpreadRate;
    }

    public void setBounceSpreadRate(NumberFunction bounceSpreadRate) {
        this.bounceSpreadRate = bounceSpreadRate;
    }

    public NumberFunction getBounceRate() {
        return bounceRate;
    }

    public void setBounceRate(NumberFunction bounceRate) {
        this.bounceRate = bounceRate;
    }

    public NumberFunction getBounceChance() {
        return bounceChance;
    }

    public void setBounceChance(NumberFunction bounceChance) {
        this.bounceChance = bounceChance;
    }

    public NumberFunction getGravity() {
        return gravity;
    }

    public void setGravity(NumberFunction gravity) {
        this.gravity = gravity;
    }

    public NumberFunction getFriction() {
        return friction;
    }

    public void setFriction(NumberFunction friction) {
        this.friction = friction;
    }

    public boolean isRemovedWhenCollided() {
        return removedWhenCollided;
    }

    public void setRemovedWhenCollided(boolean removedWhenCollided) {
        this.removedWhenCollided = removedWhenCollided;
    }

    public boolean isHasCollision() {
        return hasCollision;
    }

    public void setHasCollision(boolean hasCollision) {
        this.hasCollision = hasCollision;
    }

    public PhysicsSetting() {
    }

    public void setupParticlePhysics(AdvancedRLParticleBase particle) {
        particle.setFriction(friction.get(particle.getT(), () -> particle.getMemRandom("friction")).floatValue());
        particle.setGravity(gravity.get(particle.getT(), () -> particle.getMemRandom("gravity")).floatValue());
        particle.setBounceChance(bounceChance.get(particle.getT(), () -> particle.getMemRandom("bounce_chance")).floatValue());
        particle.setBounceRate(bounceRate.get(particle.getT(), () -> particle.getMemRandom("bounce_rate")).floatValue());
        particle.setBounceSpreadRate(bounceSpreadRate.get(particle.getT(), () -> particle.getMemRandom("bounce_spread_rate")).floatValue());
    }

}
