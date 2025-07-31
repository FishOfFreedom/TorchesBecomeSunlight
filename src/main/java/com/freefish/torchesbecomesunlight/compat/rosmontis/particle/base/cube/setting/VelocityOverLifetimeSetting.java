package com.freefish.torchesbecomesunlight.compat.rosmontis.particle.base.cube.setting;

import com.freefish.rosmontislib.client.particle.advance.base.IParticle;
import com.freefish.rosmontislib.client.particle.advance.data.ToggleGroup;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.utils.Vector3fHelper;
import com.freefish.torchesbecomesunlight.compat.rosmontis.particle.base.cube.CubeParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class VelocityOverLifetimeSetting extends ToggleGroup {
    protected NumberFunction3 linear = new NumberFunction3(0, 0, 0);
    protected OrbitalMode orbitalMode;
    protected NumberFunction3 orbital;
    protected NumberFunction3 offset;
    protected NumberFunction speedModifier;

    public VelocityOverLifetimeSetting() {
        this.orbitalMode = VelocityOverLifetimeSetting.OrbitalMode.AngularVelocity;
        this.orbital = new NumberFunction3(0, 0, 0);
        this.offset = new NumberFunction3(0, 0, 0);
        this.speedModifier = NumberFunction.constant(1);
    }

    public NumberFunction getSpeedModifier() {
        return this.speedModifier;
    }

    public void setSpeedModifier(NumberFunction speedModifier) {
        this.speedModifier = speedModifier;
    }

    public NumberFunction3 getOffset() {
        return this.offset;
    }

    public void setOffset(NumberFunction3 offset) {
        this.offset = offset;
    }

    public NumberFunction3 getOrbital() {
        return this.orbital;
    }

    public void setOrbital(NumberFunction3 orbital) {
        this.orbital = orbital;
    }

    public OrbitalMode getOrbitalMode() {
        return this.orbitalMode;
    }

    public void setOrbitalMode(OrbitalMode orbitalMode) {
        this.orbitalMode = orbitalMode;
    }

    public NumberFunction3 getLinear() {
        return this.linear;
    }

    public void setLinear(NumberFunction3 linear) {
        this.linear = linear;
    }

    public Vector3f getVelocityAddition(CubeParticle particle) {
        float lifetime = particle.getT();
        Vector3f addition = this.linear.get(lifetime, () -> {
            return particle.getMemRandom("vol0");
        }).mul(0.05F);
        Vector3f orbitalVec = this.orbital.get(lifetime, () -> {
            return particle.getMemRandom("vol1");
        });
        Vector3f center = this.offset.get(lifetime, () -> {
            return particle.getMemRandom("vol2");
        });
        if (!Vector3fHelper.isZero(orbitalVec)) {
            Vector3f toCenter;
            Vector3f radiusVec;
            if (this.orbitalMode == VelocityOverLifetimeSetting.OrbitalMode.AngularVelocity) {
                toCenter = (new Vector3f(particle.getLocalPos())).sub(center);
                if (orbitalVec.x != 0.0F) {
                    radiusVec = (new Vector3f(toCenter)).sub(Vector3fHelper.project(new Vector3f(toCenter), new Vector3f(1.0F, 0.0F, 0.0F)));
                    addition.add((new Vector3f(radiusVec)).rotateX(orbitalVec.x * 0.05F).sub(radiusVec));
                }

                if (orbitalVec.y != 0.0F) {
                    radiusVec = (new Vector3f(toCenter)).sub(Vector3fHelper.project(new Vector3f(toCenter), new Vector3f(0.0F, 1.0F, 0.0F)));
                    addition.add((new Vector3f(radiusVec)).rotateY(orbitalVec.y * 0.05F).sub(radiusVec));
                }

                if (orbitalVec.z != 0.0F) {
                    radiusVec = (new Vector3f(toCenter)).sub(Vector3fHelper.project(new Vector3f(toCenter), new Vector3f(0.0F, 0.0F, 1.0F)));
                    addition.add((new Vector3f(radiusVec)).rotateZ(orbitalVec.z * 0.05F).sub(radiusVec));
                }
            } else if (this.orbitalMode == VelocityOverLifetimeSetting.OrbitalMode.LinearVelocity) {
                toCenter = particle.getLocalPos().sub(center);
                float r;
                if (orbitalVec.x != 0.0F) {
                    radiusVec = (new Vector3f(toCenter)).sub(Vector3fHelper.project(new Vector3f(toCenter), new Vector3f(1.0F, 0.0F, 0.0F)));
                    r = radiusVec.length();
                    addition.add((new Vector3f(radiusVec)).rotateX(orbitalVec.x * 0.05F / r).sub(radiusVec));
                }

                if (orbitalVec.y != 0.0F) {
                    radiusVec = (new Vector3f(toCenter)).sub(Vector3fHelper.project(new Vector3f(toCenter), new Vector3f(0.0F, 1.0F, 0.0F)));
                    r = radiusVec.length();
                    addition.add((new Vector3f(radiusVec)).rotateY(orbitalVec.y * 0.05F / r).sub(radiusVec));
                }

                if (orbitalVec.z != 0.0F) {
                    radiusVec = (new Vector3f(toCenter)).sub(Vector3fHelper.project(new Vector3f(toCenter), new Vector3f(0.0F, 0.0F, 1.0F)));
                    r = radiusVec.length();
                    addition.add((new Vector3f(radiusVec)).rotateZ(orbitalVec.z * 0.05F / r).sub(radiusVec));
                }
            } else if (this.orbitalMode == VelocityOverLifetimeSetting.OrbitalMode.FixedVelocity) {
                toCenter = center.sub(particle.getLocalPos());
                if (orbitalVec.x != 0.0F) {
                    addition.add((new Vector3f(toCenter)).cross(new Vector3f(1.0F, 0.0F, 0.0F)).normalize().mul(orbitalVec.x * 0.05F));
                }

                if (orbitalVec.y != 0.0F) {
                    addition.add((new Vector3f(toCenter)).cross(new Vector3f(0.0F, 1.0F, 0.0F)).normalize().mul(orbitalVec.y * 0.05F));
                }

                if (orbitalVec.z != 0.0F) {
                    addition.add((new Vector3f(toCenter)).cross(new Vector3f(0.0F, 0.0F, 1.0F)).normalize().mul(orbitalVec.z * 0.05F));
                }
            }
        }

        return addition;
    }

    public float getVelocityMultiplier(IParticle particle) {
        float lifetime = particle.getT();
        return this.speedModifier.get(lifetime, () -> {
            return particle.getMemRandom(this);
        }).floatValue();
    }

    public static enum OrbitalMode {
        AngularVelocity,
        LinearVelocity,
        FixedVelocity;

        private OrbitalMode() {
        }
    }
}