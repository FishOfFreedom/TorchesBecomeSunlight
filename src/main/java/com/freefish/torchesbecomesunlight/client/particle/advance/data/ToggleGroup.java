package com.freefish.torchesbecomesunlight.client.particle.advance.data;


/**
 * @author KilaBash
 * @date 2023/5/30
 * @implNote ToggleGroup
 */
public class ToggleGroup{
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    protected boolean enable;

}
