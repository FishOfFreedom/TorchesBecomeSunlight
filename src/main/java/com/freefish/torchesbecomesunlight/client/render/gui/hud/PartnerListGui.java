package com.freefish.torchesbecomesunlight.client.render.gui.hud;

import com.freefish.rosmontislib.gui.texture.ResourceBorderTexture;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerPlayerManager;
import com.freefish.torchesbecomesunlight.server.partner.command.PartnerCommandBasic;
import com.freefish.torchesbecomesunlight.server.partner.command.PartnerCommandManager;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PartnerListGui extends HudWidget {
    public static final String ID = "partner_list";
    private static final int WIDTH = 30;
    private static final int HEIGHT = 100;
    public static final PartnerListGui INSTANCE = new PartnerListGui(ID);
    private AnimationGui mainAnimationGui;

    public void reset(){
        mainAnimationGui = null;
    }

    public PartnerListGui(String id) {
        super(id);
    }

    @Override
    public void tick() {
        if(mainAnimationGui==null){
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                if (capability != null) {
                    PartnerPlayerManager partnerManager = capability.getPartnerManager();
                    Partner<?> currentPartner = partnerManager.getCurrentPartner();
                    if (currentPartner != null) {
                        mainAnimationGui = new AnimationGui(currentPartner);
                        mainAnimationGui.setAnimation(0, WIDTH, 2);
                    }
                }
            }
        }else {
            Partner partner = mainAnimationGui.partner;
            Mob partnerMob = partner.getPartnerMob();

            if(partnerMob ==null||!partnerMob.isAlive()||partner.isRemoved()){
                if(!mainAnimationGui.isdeath){
                    mainAnimationGui.setAnimation(WIDTH, 0, 2, () -> {
                        mainAnimationGui = null;
                    });
                }
            }
        }
    }

    @Override
    public boolean canRenderer() {
        return true;
    }

    @Override
    public void open() {
        isOpen = true;
    }

    @Override
    public void close() {
        isOpen = false;
    }

    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();

        if(mainAnimationGui!=null){
            graphics.pose().pushPose();
            float value = mainAnimationGui.getValue();
            graphics.pose().last().pose().translate(value,0,0);
            if(mainAnimationGui!=null){
                renderPartner(graphics, mainAnimationGui.partner, mouseX, mouseY, 0, 0, width, height);
            }
            graphics.pose().popPose();
        }
    }

    private void renderPartner(GuiGraphics graphics,Partner partner, int mouseX, int mouseY,float x,float y,int width,int height){
        ResourceBorderTexture.BORDERED_BACKGROUND.draw(graphics,mouseX,mouseY,width - WIDTH+4, height/2 - HEIGHT,WIDTH,HEIGHT*2);

        PartnerCommandManager skillManager = partner.getSkillManager();
        List<PartnerCommandBasic> partnerCommandBasics = skillManager.getPartnerCommandBasics();

        Mob partnerMob = partner.getPartnerMob();
        float healthScale = partnerMob.getHealth()/partnerMob.getMaxHealth();
        int i1 = (int) ((HEIGHT * 2 - 20) * healthScale);
        graphics.fill(width-4,height/2-HEIGHT+11,width-4+2,height/2-HEIGHT+11+(HEIGHT * 2 - 20), 0XFFFFFFFF);
        graphics.fill(width-5,height/2-HEIGHT+10,width-5+2,height/2-HEIGHT+10+i1,0XFF549159);

        for(int i = 0;i<partnerCommandBasics.size();i++){
            PartnerCommandBasic partnerCommandBasic = partnerCommandBasics.get(i);
            if(partnerCommandBasic == skillManager.getCurrentCommand()){
                renderIcon(graphics,partnerCommandBasic.getIcon(),width - WIDTH + 6,height / 2 - HEIGHT + 20 + 40 * i);
            }
            else {
                renderIcon(graphics,partnerCommandBasic.getIcon(),width - WIDTH + 8,height / 2 - HEIGHT + 20 + 40 * i);
            }
        }
    }

    private void renderIcon(GuiGraphics graphics, ResourceLocation resourceLocation,int x,int y){
        graphics.pose().pushPose();
        graphics.pose().last().pose().translate(x,y,0);
        graphics.pose().last().pose().scale(0.125f);
        graphics.blit(resourceLocation,0, 0,0,0,128,128,128,128);
        graphics.pose().popPose();
    }

    private class AnimationGui{
        private final Partner partner;
        private float value,valueO,maxTime;
        private long time;
        private boolean isAnimation;
        private Runnable runnable;

        private boolean isdeath;
        public AnimationGui(Partner partner) {
            this.partner = partner;
        }

        public void setAnimation(float value,float valueO,float maxTime,Runnable runnable) {
            time = System.currentTimeMillis();
            isAnimation = true;
            this.value = value;
            this.valueO = valueO;
            this.maxTime = maxTime;
            this.runnable = runnable;
            isdeath = true;
        }

        public void setAnimation(float value,float valueO,float maxTime) {
            time = System.currentTimeMillis();
            isAnimation = true;
            this.value = value;
            this.valueO = valueO;
            this.maxTime = maxTime;
        }

        public float getValue(){
            if(isAnimation) {
                float v = ((System.currentTimeMillis() - time) / 1000f)/maxTime;
                if(v>=1){
                    if(runnable!=null){
                        runnable.run();
                    }
                    isAnimation = false;
                    v = 1;
                }
                return Mth.lerp(MathUtils.easeOutQuart(v),valueO,value);
            }else {
                return value;
            }
        }
    }
}
