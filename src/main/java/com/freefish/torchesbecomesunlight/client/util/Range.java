package com.freefish.torchesbecomesunlight.client.util;

/**
 * @author KilaBash
 * @date 2023/5/30
 * @implNote Range
 */
public class Range {
    protected Number a, b;

    public Number getA() {
        return a;
    }

    public void setA(Number a) {
        this.a = a;
    }

    public Number getB() {
        return b;
    }

    public void setB(Number b) {
        this.b = b;
    }


    public Range(Number a, Number b) {
        this.a = a;
        this.b = b;
    }

    public Number getMin() {
        return Math.min(a.doubleValue(), b.doubleValue());
    }

    public Number getMax() {
        return Math.min(a.doubleValue(), b.doubleValue());
    }
}
