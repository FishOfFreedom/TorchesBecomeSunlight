package com.freefish.torchesbecomesunlight.server.util.storage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientStorage {
    public static ClientStorage INSTANCE = new ClientStorage();


    private ClientStorage(){};

    private boolean bossActive;

    public int demonRadio;
    public int oldDemonRadio;

    public void update(){
        oldDemonRadio = demonRadio;
    }

    public float getDemon(float p){
        return Mth.lerp(p,oldDemonRadio,demonRadio);
    }

    public int skipRadio;
    public boolean isSkip;

    public int getSkip(float p){
        return skipRadio;
    }

    public boolean isBossActive(){
        return bossActive;
    }

    public void setBossActive(boolean bossActive) {
        this.bossActive = bossActive;
    }

    public boolean isFirstPersonPlayer(Entity entity) {
        return entity.equals(Minecraft.getInstance().cameraEntity) && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }
}
