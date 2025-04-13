package com.freefish.torchesbecomesunlight.client.particle.advance.data;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class RendererSetting {
    public enum Mode {
        Billboard(p -> null),
        Horizontal(0, 90),
        Vertical(0, 0);

        final Function<AdvancedRLParticleBase, Quaternionf> quaternion;

        Mode(Function<AdvancedRLParticleBase, Quaternionf> quaternion) {
            this.quaternion = quaternion;
        }

        Mode(float yRot, float xRot) {
            final var q = new Quaternionf();
            q.rotateY((float) Math.toRadians(-yRot));
            q.rotateX((float) Math.toRadians(xRot));
            this.quaternion = p -> q;
        }


        public static Quaternionf CalQuaternion(Vector3f dir) {
            Quaternionf cal = new Quaternionf();
            //欧拉角Y: cosY = z/sqrt(x^2+z^2)
            var CosY = dir.z / Math.sqrt(dir.x * dir.x + dir.z * dir.z);
            var CosYDiv2 = Math.sqrt((CosY + 1) / 2);
            if (dir.x < 0) CosYDiv2 = -CosYDiv2;

            var SinYDiv2 = Math.sqrt((1-CosY) / 2);

            //欧拉角X: cosX = sqrt((x^2+z^2)/(x^2+y^2+z^2)
            var CosX = Math.sqrt((dir.x * dir.x + dir.z * dir.z) / (dir.x * dir.x + dir.y * dir.y + dir.z * dir.z));
            if (dir.z < 0) CosX = -CosX;
            var CosXDiv2 = Math.sqrt((CosX + 1) / 2);
            if (dir.y > 0) CosXDiv2 = -CosXDiv2;
            var SinXDiv2 = Math.sqrt((1 - CosX) / 2);

            //四元数w = cos(x/2)cos(y/2)
            cal.set((float) (SinXDiv2 * CosYDiv2),
                    (float) (CosXDiv2 * SinYDiv2),
                    (float) (-SinXDiv2 * SinYDiv2),
                    (float) (CosXDiv2 * CosYDiv2));
            return cal;
        }
    }

    public static class Cull{
        protected Vector3f from = new Vector3f(-0.5f, -0.5f, -0.5f);

        protected Vector3f to = new Vector3f(0.5f, 0.5f, 0.5f);

        public Vector3f getFrom() {
            return from;
        }

        public void setFrom(Vector3f from) {
            this.from = from;
        }

        public Vector3f getTo() {
            return to;
        }

        public void setTo(Vector3f to) {
            this.to = to;
        }

        //public AABB getCullAABB(AdvancedRLParticleBase particle, float partialTicks) {
        //    var pos = particle.getPos(partialTicks);
        //    return new AABB(from.x, from.y, from.z, to.x, to.y, to.z).move(pos.x, pos.y, pos.z);
        //}
    }

    public enum Layer {
        Opaque,
        Translucent
    }

    protected Mode renderMode = Mode.Billboard;

    protected Layer layer = Layer.Translucent;

    public Cull getCull() {
        return cull;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public boolean isBloomEffect() {
        return bloomEffect;
    }

    public void setBloomEffect(boolean bloomEffect) {
        this.bloomEffect = bloomEffect;
    }

    public Mode getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(Mode renderMode) {
        this.renderMode = renderMode;
    }

    protected boolean bloomEffect = false;

    protected final Cull cull = new Cull();

    //public void setupQuaternion(AdvancedRLParticleBase emitter, AdvancedRLParticleBase particle) {
    //    particle.setQuaternionSupplier(() -> renderMode.quaternion.apply(emitter));
    //}
}