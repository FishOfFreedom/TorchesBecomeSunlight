package com.freefish.torchesbecomesunlight.client.particle.advance.base;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.torchesbecomesunlight.client.particle.advance.RLParticleQueueRenderType;
import com.freefish.torchesbecomesunlight.client.particle.advance.RLParticleRenderType;
import com.freefish.torchesbecomesunlight.client.particle.advance.effect.IEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;


/**
 * @author KilaBash
 * @date 2023/6/2
 * @implNote IParticleEmitter
 */
@OnlyIn(Dist.CLIENT)
public interface IParticleEmitter extends IGameObject {

    default AdvancedRLParticleBase self() {
        return (AdvancedRLParticleBase) this;
    }

    /**
     * reset runtime data
     */
    default void reset() {
        self().resetParticle();
    }

    /**
     * get amount of existing particle which emitted from it.
     */
    default int getParticleAmount() {
        var sum = 0;
        for (var entry : getParticles().entrySet()) {
            if (entry.getKey() == RLParticleQueueRenderType.INSTANCE) {
                for (var particle : entry.getValue()) {
                    sum += ((IParticleEmitter) particle).getParticleAmount();
                }
            }
            sum += entry.getValue().size();
        }
        return sum;
    }

    /**
     * emit to a given level.
     */
    @Deprecated
    default void emmitToLevel(Level level, double x, double y, double z) {
        emmitToLevel(null, level, x, y, z, 0, 0, 0);
    }

    default void emmitToLevel(@Nullable IEffect effect, Level level, double x, double y, double z, double xR, double yR, double zR) {
        setEffect(effect);
        self().setPos(x, y, z, true);
        self().setRotation(new Vector3f((float) xR, (float) yR, (float) zR));
        self().setLevel(level);
        self().prepareForEmitting(null);

        Minecraft.getInstance().particleEngine.add(self());
    }

    default void updatePos(Vector3f newPos) {
        self().setPos(newPos, true);
    }

    /**
     * emitter type
     */
    default String getEmitterType() {
        return getName();
    }

    /**
     * force - remove without waiting.
     */
    default void remove(boolean force) {
        self().remove();
    }

    /**
     * emitter name unique for one project
     */
    String getName();

    void setName(String name);

    /**
     * is it a sub emitter which not be emitted by default.
     */
    boolean isSubEmitter();

    void setSubEmitter(boolean isSubEmitter);

    /**
     * particles emitted from this emitter.
     * <br>
     * you should not modify it, just read data.
     */
    Map<RLParticleRenderType, Queue<AdvancedRLParticleBase>> getParticles();

    /**
     * emit particle from this emitter
     */
    boolean emitParticle(AdvancedRLParticleBase particle);

    /**
     * should render particle
     */
    boolean isVisible();

    /**
     * set particle visible
     */
    void setVisible(boolean visible);

    /**
     * get the box of cull.
     * <br>
     * return null - culling disabled.
     */
    @Nullable
    AABB getCullBox(float partialTicks);

    /**
     * use bloom effect
     */
    boolean usingBloom();

    void setEffect(IEffect effect);

    IEffect getEffect();
}
