package com.freefish.torchesbecomesunlight.server.util.storage;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientStorage {
    public static ClientStorage INSTANCE = new ClientStorage();



    private ClientStorage(){};

    private boolean bossActive;

    public boolean isBossActive(){
        return bossActive;
    }

    public void setBossActive(boolean bossActive) {
        this.bossActive = bossActive;
    }
}
