package com.freefish.torchesbecomesunlight.server.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;


/**
 * @author EEEAB
 * @since 1.20.1
 */
public class FFBodyRotationControl extends BodyRotationControl {
    private static final float MAX_ROTATE = 75;
    private final Mob mob;
    private int headStableTime;
    private float lastStableYHeadRot;

    public FFBodyRotationControl(Mob mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public void clientTick() {
        if (this.isMoving()) {
            this.mob.yBodyRot = this.mob.getYRot();
            this.rotateHeadIfNecessary();
            this.lastStableYHeadRot = this.mob.yHeadRot;
            this.headStableTime = 0;
        } else {
            if (this.notCarryingMobPassengers()) {
                float limit = MAX_ROTATE;
                if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > 15) {
                    this.headStableTime = 0;
                    this.lastStableYHeadRot = this.mob.yHeadRot;
                    this.rotateBodyIfNecessary();
                } else {
                    headStableTime++;
                    final int speed = 10;
                    if (headStableTime > speed) {
                        limit = Math.max(1 - (headStableTime - speed) / (float) speed, 0) * MAX_ROTATE;
                    }
                    mob.yBodyRot = approach(mob.yHeadRot, mob.yBodyRot, limit);
                }
            }
        }
    }

    public static float approach(float target, float current, float limit) {
        float delta = Mth.wrapDegrees(current - target);
        if (delta < -limit) {
            delta = -limit;
        } else if (delta >= limit) {
            delta = limit;
        }
        return target + delta * 0.55F;
    }

    private boolean notCarryingMobPassengers() {
        return mob.getPassengers().isEmpty() || !(mob.getPassengers().get(0) instanceof Mob);
    }

    private boolean isMoving() {
        double d0 = this.mob.getX() - this.mob.xo;
        double d1 = this.mob.getZ() - this.mob.zo;
        return d0 * d0 + d1 * d1 > 2.5E-7;
    }

    private void rotateBodyIfNecessary() {
        this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, (float) this.mob.getMaxHeadYRot());
    }

    private void rotateHeadIfNecessary() {
        this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, (float) this.mob.getMaxHeadYRot());
    }
}
