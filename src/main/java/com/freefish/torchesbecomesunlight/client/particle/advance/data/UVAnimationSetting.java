package com.freefish.torchesbecomesunlight.client.particle.advance.data;
import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.torchesbecomesunlight.client.particle.advance.data.number.NumberFunction;
import com.freefish.torchesbecomesunlight.client.util.Range;
import org.joml.Vector4f;

/**
 * @author KilaBash
 * @date 2023/5/31
 * @implNote UVAnimation
 */
public class UVAnimationSetting{
    public enum Animation {
        WholeSheet,
        SingleRow,
    }

    public float getCycle() {
        return cycle;
    }

    public void setCycle(float cycle) {
        this.cycle = cycle;
    }

    public NumberFunction getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(NumberFunction startFrame) {
        this.startFrame = startFrame;
    }

    public NumberFunction getFrameOverTime() {
        return frameOverTime;
    }

    public void setFrameOverTime(NumberFunction frameOverTime) {
        this.frameOverTime = frameOverTime;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public Range getTiles() {
        return tiles;
    }

    public void setTiles(Range tiles) {
        this.tiles = tiles;
    }

    protected Range tiles = new Range(1, 1);

    protected Animation animation = Animation.WholeSheet;

    protected NumberFunction frameOverTime = NumberFunction.constant(0);

    protected NumberFunction startFrame = NumberFunction.constant(0);

    protected float cycle = 1;

    public Vector4f getUVs(AdvancedRLParticleBase particle, float partialTicks) {
        var t = particle.getT(partialTicks);
        var cellU = 1f / tiles.getA().intValue();
        var cellV = 1f / tiles.getB().intValue();
        var currentFrame = this.startFrame.get(t, () -> particle.getMemRandom("startFrame")).floatValue();
        currentFrame += cycle * frameOverTime.get(t, () -> particle.getMemRandom("frameOverTime")).floatValue();
        float u0, v0, u1, v1;
        int cellSize;
        if (animation == Animation.WholeSheet) {
            cellSize = tiles.getA().intValue() * tiles.getB().intValue();
            int X = (int) (currentFrame % cellSize);
            int Y = (int) (currentFrame / cellSize);
            u0 = X * cellU;
            v0 = Y * cellV;
        } else {
            cellSize = tiles.getA().intValue();
            int X = (int) (currentFrame % cellSize);
            int Y = (int) (particle.getMemRandom("randomRow") * tiles.getB().intValue());
            u0 = X * cellU;
            v0 = Y * cellV;
        }
        u1 = u0 + cellU;
        v1 = v0 + cellV;
        return new Vector4f(u0, v0, u1, v1);
    }
}
