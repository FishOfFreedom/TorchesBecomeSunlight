package com.freefish.torchesbecomesunlight.client.particle.advance.data;

import com.freefish.torchesbecomesunlight.client.particle.advance.data.number.NumberFunction;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class EmissionSetting {

    public enum Mode {
        Exacting,
        Random
    }

    protected NumberFunction emissionRate = NumberFunction.constant(0.5f);

    protected Mode emissionMode = Mode.Exacting;

    protected List<Burst> bursts = new ArrayList<>();

    public int getEmissionCount(int emitterAge, float t, RandomSource randomSource) {
        var result = emissionRate.get(randomSource, t);
        var number = result.intValue();
        var decimals = result.floatValue() - result.intValue();
        if (emissionMode == Mode.Exacting) {
            if (decimals > 0 && emitterAge % ((int) (1 / decimals)) == 0) {
                number += 1;
            }
        } else {
            if (randomSource.nextFloat() < decimals) {
                number += 1;
            }
        }
        for (var bust : bursts) {
            var realAge = emitterAge - bust.time;
            if (realAge >= 0) {
                var count = bust.count.get(randomSource, t).intValue();
                if (realAge % bust.interval == 0) {
                    if (bust.cycles == 0) {
                        if (randomSource.nextFloat() < bust.probability) {
                            number += count;
                        }
                    } else if (realAge / bust.interval < bust.cycles) {
                        if (randomSource.nextFloat() < bust.probability) {
                            number += count;
                        }
                    }
                }
            }
        }
        return number;
    }

    public List<Burst> getBursts() {
        return bursts;
    }

    public void setBursts(List<Burst> bursts) {
        this.bursts = bursts;
    }

    public static class Burst {
        public int time = 0;

        protected NumberFunction count = NumberFunction.constant(50);

        public int cycles = 1;

        public int interval = 1;

        public float probability = 1;

        public NumberFunction getCount() {
            return count;
        }

        public void setCount(NumberFunction count) {
            this.count = count;
        }
    }
}