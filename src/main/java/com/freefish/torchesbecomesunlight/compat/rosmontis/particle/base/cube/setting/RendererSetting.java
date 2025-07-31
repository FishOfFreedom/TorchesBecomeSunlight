package com.freefish.torchesbecomesunlight.compat.rosmontis.particle.base.cube.setting;

import com.freefish.rosmontislib.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.rosmontislib.client.particle.advance.data.ToggleGroup;
import com.freefish.torchesbecomesunlight.compat.rosmontis.particle.base.cube.CubeParticle;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.function.TriFunction;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class RendererSetting {
    protected Layer layer;
    protected boolean bloomEffect;
    protected final Cull cull;

    public RendererSetting() {
        this.layer = RendererSetting.Layer.Translucent;
        this.bloomEffect = false;
        this.cull = new Cull();
    }

    public Cull getCull() {
        return this.cull;
    }

    public boolean isBloomEffect() {
        return this.bloomEffect;
    }

    public void setBloomEffect(boolean bloomEffect) {
        this.bloomEffect = bloomEffect;
    }

    public Layer getLayer() {
        return this.layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public static enum Layer {
        Opaque,
        Translucent;

        private Layer() {
        }
    }

    public static class Cull extends ToggleGroup {
        protected Vector3f from = new Vector3f(-0.5F, -0.5F, -0.5F);
        protected Vector3f to = new Vector3f(0.5F, 0.5F, 0.5F);

        public Cull() {
        }

        public Vector3f getTo() {
            return this.to;
        }

        public void setTo(Vector3f to) {
            this.to = to;
        }

        public Vector3f getFrom() {
            return this.from;
        }

        public void setFrom(Vector3f from) {
            this.from = from;
        }

        public AABB getCullAABB(AdvancedRLParticleBase particle, float partialTicks) {
            Vector3f pos = particle.transform().position();
            return (new AABB((double)this.from.x, (double)this.from.y, (double)this.from.z, (double)this.to.x, (double)this.to.y, (double)this.to.z)).move((double)pos.x, (double)pos.y, (double)pos.z);
        }
    }

    public static class Particle extends RendererSetting {
        protected Mode renderMode;
        protected boolean shade;
        protected boolean useBlockUV;

        public Particle() {
            this.renderMode = RendererSetting.Particle.Mode.Billboard;
            this.shade = true;
            this.useBlockUV = true;
        }

        public Mode getRenderMode() {
            return this.renderMode;
        }

        public void setRenderMode(Mode renderMode) {
            this.renderMode = renderMode;
        }

        public boolean isShade() {
            return this.shade;
        }

        public void setShade(boolean shade) {
            this.shade = shade;
        }

        public boolean isUseBlockUV() {
            return this.useBlockUV;
        }

        public void setUseBlockUV(boolean useBlockUV) {
            this.useBlockUV = useBlockUV;
        }

        public static enum Mode {
            Billboard((p, c, t) -> {
                return c.rotation();
            }),
            Horizontal(0.0F, 90.0F),
            Vertical(0.0F, 0.0F),
            VerticalBillboard((p, c, t) -> {
                Quaternionf quaternion = new Quaternionf();
                quaternion.rotateY((float)Math.toRadians((double)(-c.getYRot())));
                return quaternion;
            }),
            Model((p, c, t) -> {
                return new Quaternionf();
            });

            public final TriFunction<CubeParticle, Camera, Float, Quaternionf> quaternion;

            private Mode(TriFunction<CubeParticle, Camera, Float, Quaternionf> quaternion) {
                this.quaternion = quaternion;
            }

            private Mode(Quaternionf quaternion) {
                this.quaternion = (p, c, t) -> {
                    return quaternion;
                };
            }

            private Mode(float yRot, float xRot) {
                Quaternionf quaternion = new Quaternionf();
                quaternion.rotateY((float)Math.toRadians((double)(-yRot)));
                quaternion.rotateX((float)Math.toRadians((double)xRot));
                this.quaternion = (p, c, t) -> {
                    return quaternion;
                };
            }
        }
    }
}