package com.freefish.torchesbecomesunlight.client.particle.advance.data.number;
import net.minecraft.util.RandomSource;

import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2023/5/26
 * @implNote Constant
 */
public class Constant implements NumberFunction {
    private Number number;

    public Constant() {
        number = 0;
    }

    public Constant(Number number) {
        this.number = number;
    }

    @Override
    public Number get(RandomSource randomSource, float t) {
        return number;
    }

    @Override
    public Number get(float t, Supplier<Float> lerp) {
        return number;
    }

    public Number getNumber() {
        return number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }
}
