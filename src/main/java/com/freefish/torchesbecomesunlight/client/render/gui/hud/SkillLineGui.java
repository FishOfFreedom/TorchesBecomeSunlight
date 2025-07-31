package com.freefish.torchesbecomesunlight.client.render.gui.hud;

import com.freefish.torchesbecomesunlight.client.render.gui.TBSDrawerHelper;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SkillLineGui extends HudWidget {
    private static final int WIDTH = 120;
    private static final int HEIGHT = 11;
    private int tou = 0;
    private int touO = 0;
    public static final SkillLineGui INSTANCE = new SkillLineGui("skill");

    public SkillLineGui(String id) {
        super(id);
    }

    @Override
    public void tick() {
        touO = tou;
        if(isOpen){
            if(tou<20) tou++;
        }else {
            if(tou>0) tou--;
        }
    }

    @Override
    public boolean canRenderer() {
        return tou>0;
    }

    @Override
    public void open() {
        isOpen = true;
    }

    @Override
    public void close() {
        isOpen = false;
    }

    public float getTou(float pa){
        return Mth.lerp(pa,touO,tou)/20;
    }

    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        Player player = Minecraft.getInstance().player;
        if(player!=null){
            Minecraft minecraft = Minecraft.getInstance();
            int width = minecraft.getWindow().getGuiScaledWidth();
            int height = minecraft.getWindow().getGuiScaledHeight();

            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
            if(capability!=null){
                int skillAmount = capability.getSkillAmount();
                float radio = skillAmount / 100f;
                width = width - 10;
                height = height - 10;

                float tou1 = getTou(partialTicks);
                //TBSDrawerHelper.drawRLGrayFade(graphics, Rect.ofRelative(width - WIDTH - 1, WIDTH + 2, height - HEIGHT - 1, HEIGHT + 2), FastColor.ARGB32.color((int) (120 * tou1), 0, 0, 0), 1 - tou1);

                //int len = 3;
                //final int sWidth = WIDTH /len;
                //float sScale =  1f / len;
                //for(int i=0;i<Math.ceil(len*radio);i++){
                //    float currentScale = radio - i * sScale;
                //    int currentWidth = width - i * sWidth;
                //    graphics.fill(currentWidth - sWidth, height - HEIGHT, Mth.lerpInt(currentScale/sScale, currentWidth - sWidth, currentWidth), height, FastColor.ARGB32.color((int) (255 * tou1), 255, 255, 255));
                //}

                //int len = 3;
                //final int sWidth = WIDTH /len;
                //float sScale =  1f / len;
                //for(int i=0;i<len;i++){
                //    float currentScale = radio - i * sScale;
                //    int currentWidth = width - i * sWidth;
                //    graphics.fill(currentWidth - sWidth, height - HEIGHT, Mth.lerpInt(currentScale/sScale, currentWidth - sWidth, currentWidth), height, FastColor.ARGB32.color((int) (255 * tou1), 255, 255, 255));
                //}

                //TBSDrawerHelper.drawTetragon(width - WIDTH);
                int i = (int)(tou1*radio*(WIDTH-20));
                TBSDrawerHelper.drawTetragon(width - i, width - i-10, height - HEIGHT+1, height - HEIGHT+1, i, i+10, HEIGHT-2, HEIGHT-2, FastColor.ARGB32.color((int) (120 * tou1), 255, 255, 255));
            }
        }
    }
}
