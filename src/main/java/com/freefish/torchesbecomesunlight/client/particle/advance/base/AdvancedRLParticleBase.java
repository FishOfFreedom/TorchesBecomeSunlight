package com.freefish.torchesbecomesunlight.client.particle.advance.base;

import com.freefish.torchesbecomesunlight.client.particle.advance.data.ParticleRLComponent;
import com.freefish.torchesbecomesunlight.client.render.model.tools.MathUtils;
import com.freefish.torchesbecomesunlight.client.util.particle.ParticleRibbon;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleRotation;
import com.freefish.torchesbecomesunlight.client.util.render.MMRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public abstract class AdvancedRLParticleBase extends Particle {
    public boolean doRender;

    public float airDrag;
    public float red, green, blue, alpha;
    public float prevRed, prevGreen, prevBlue, prevAlpha;
    public float scale, prevScale, particleScale;
    public ParticleRotation rotation;
    public boolean emissive;
    public double prevMotionX, prevMotionY, prevMotionZ;

    public ParticleRLComponent[] components;

    public ParticleRibbon ribbon;

    protected AdvancedRLParticleBase(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double motionX, double motionY, double motionZ, ParticleRotation rotation, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, boolean canCollide, ParticleRLComponent[] components) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.red = (float) (r);
        this.green = (float) (g);
        this.blue = (float) (b);
        this.alpha = (float) (a);
        this.scale = (float)scale;
        this.lifetime = (int)duration;
        airDrag = (float)drag;
        this.rotation = rotation;
        this.components = components;
        this.emissive = emissive;
        this.ribbon = null;

        doRender = true;

        for (ParticleRLComponent component : components) {
            component.init(this);
        }

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.prevRed = this.red;
        this.prevGreen = this.green;
        this.prevBlue = this.blue;
        this.prevAlpha = this.alpha;
        this.rotation.setPrevValues();
        this.prevScale = this.scale;
        this.hasPhysics = canCollide;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return MMRenderType.PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH;
    }

    public int getLightColor(float partialTick)
    {
        int i = super.getLightColor(partialTick);
        if (emissive) {
            int k = i >> 16 & 255;
            return 240 | k << 16;
        }
        else return i;
    }

    @Override
    public void tick() {
        prevRed = red;
        prevGreen = green;
        prevBlue = blue;
        prevAlpha = alpha;
        prevScale = scale;
        rotation.setPrevValues();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        prevMotionX = xd;
        prevMotionY = yd;
        prevMotionZ = zd;

        for (ParticleRLComponent component : components) {
            component.preUpdate(this);
        }

        if (this.age++ >= this.lifetime)
        {
            this.remove();
        }

        updatePosition();

        for (ParticleRLComponent component : components) {
            component.postUpdate(this);
        }

        if (ribbon != null) {
            ribbon.setPos(x, y, z);
            ribbon.positions[0] = new Vec3(x, y, z);
            ribbon.prevPositions[0] = getPrevPos();
        }
    }

    protected void updatePosition() {
        //this.motionY -= 0.04D * (double)this.particleGravity;
        this.move(this.xd, this.yd, this.zd);

        if (this.onGround && hasPhysics)
        {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
        }

        this.xd *= airDrag;
        this.yd *= airDrag;
        this.zd *= airDrag;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        alpha = prevAlpha + (alpha - prevAlpha) * partialTicks;
        if (alpha < 0.01) alpha = 0.01f;
        rCol = prevRed + (red - prevRed) * partialTicks;
        gCol = prevGreen + (green - prevGreen) * partialTicks;
        bCol = prevBlue + (blue - prevBlue) * partialTicks;
        particleScale = prevScale + (scale - prevScale) * partialTicks;

        for (ParticleRLComponent component : components) {
            component.preRender(this, partialTicks);
        }

        if (!doRender) return;

        Vec3 Vector3d = renderInfo.getPosition();
        float f = (float)(Mth.lerp(partialTicks, this.xo, this.x) - Vector3d.x());
        float f1 = (float)(Mth.lerp(partialTicks, this.yo, this.y) - Vector3d.y());
        float f2 = (float)(Mth.lerp(partialTicks, this.zo, this.z) - Vector3d.z());

        Quaternionf quaternion = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
        if (rotation instanceof ParticleRotation.FaceCamera) {
            ParticleRotation.FaceCamera faceCameraRot = (ParticleRotation.FaceCamera) rotation;
            if (faceCameraRot.faceCameraAngle == 0.0F && faceCameraRot.prevFaceCameraAngle == 0.0F) {
                quaternion = renderInfo.rotation();
            } else {
                quaternion = new Quaternionf(renderInfo.rotation());
                float f3 = Mth.lerp(partialTicks, faceCameraRot.prevFaceCameraAngle, faceCameraRot.faceCameraAngle);
                quaternion.mul(Axis.ZP.rotation(f3));
            }
        }
        else if (rotation instanceof ParticleRotation.EulerAngles) {
            ParticleRotation.EulerAngles eulerRot = (ParticleRotation.EulerAngles) rotation;
            float rotY = eulerRot.prevYaw + (eulerRot.yaw - eulerRot.prevYaw) * partialTicks;
            float rotX = eulerRot.prevPitch + (eulerRot.pitch - eulerRot.prevPitch) * partialTicks;


            float rotZ = eulerRot.prevRoll + (eulerRot.roll - eulerRot.prevRoll) * partialTicks;
            Quaternionf quatX = MathUtils.quatFromRotationXYZ(rotX, 0, 0, false);
            Quaternionf quatY = MathUtils.quatFromRotationXYZ(0, rotY, 0, false);
            Quaternionf quatZ = MathUtils.quatFromRotationXYZ(0, 0, rotZ, false);
            quaternion.mul(quatZ);
            quaternion.mul(quatY);
            quaternion.mul(quatX);
        }
        if (rotation instanceof ParticleRotation.OrientVector) {
            ParticleRotation.OrientVector orientRot = (ParticleRotation.OrientVector) rotation;
            double x = orientRot.prevOrientation.x + (orientRot.orientation.x - orientRot.prevOrientation.x) * partialTicks;
            double y = orientRot.prevOrientation.y + (orientRot.orientation.y - orientRot.prevOrientation.y) * partialTicks;
            double z = orientRot.prevOrientation.z + (orientRot.orientation.z - orientRot.prevOrientation.z) * partialTicks;
            float pitch = (float) Math.asin(-y);
            float yaw = (float) (Mth.atan2(x, z));
            Quaternionf quatX = MathUtils.quatFromRotationXYZ(pitch, 0, 0, false);
            Quaternionf quatY = MathUtils.quatFromRotationXYZ(0, yaw, 0, false);
            quaternion.mul(quatY);
            quaternion.mul(quatX);
        }

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = particleScale * 0.1f;

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            quaternion.transform(vector3f);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        float f7 = 1;
        float f8 = 0;
        float f5 = 1;
        float f6 = 0;
        int j = this.getLightColor(partialTicks);
        buffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();

        for (ParticleRLComponent component : components) {
            component.postRender(this, buffer, renderInfo, partialTicks, j);
        }
    }

    @Override
    public boolean shouldCull() {
        return super.shouldCull();
    }

    public float getAge() {
        return age;
    }

    public double getPosX() {
        return x;
    }

    public void setPosX(double posX) {
        setPos(posX, this.y, this.z);
    }

    public double getPosY() {
        return y;
    }

    public void setPosY(double posY) {
        setPos(this.x, posY, this.z);
    }

    public double getPosZ() {
        return z;
    }

    public void setPosZ(double posZ) {
        setPos(this.x, this.y, posZ);
    }

    public double getMotionX() {
        return xd;
    }

    public void setMotionX(double motionX) {
        this.xd = motionX;
    }

    public double getMotionY() {
        return yd;
    }

    public void setMotionY(double motionY) {
        this.yd = motionY;
    }

    public double getMotionZ() {
        return zd;
    }

    public void setMotionZ(double motionZ) {
        this.zd = motionZ;
    }

    public Vec3 getPrevPos() {
        return new Vec3(xo, yo, zo);
    }

    public double getPrevPosX() {
        return xo;
    }

    public double getPrevPosY() {
        return yo;
    }

    public double getPrevPosZ() {
        return zo;
    }

    public Level getWorld() {
        return level;
    }
}
