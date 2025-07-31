package com.freefish.torchesbecomesunlight.client.render.gui.partnercommand;

import com.freefish.rosmontislib.client.utils.Position;
import com.freefish.rosmontislib.client.utils.Size;
import com.freefish.rosmontislib.gui.widget.WidgetGroup;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PartnerCommandMenu extends WidgetGroup {
    private static final int SCALE = 160;
    public Player player;
    public PlayerCapability.IPlayerCapability capability;

    public PartnerCommandMenu() {
        if(Minecraft.getInstance().level != null){
            player = Minecraft.getInstance().player;
            this.capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onScreenSizeUpdate(int screenWidth, int screenHeight) {
        setSize(new Size(screenWidth, screenHeight));
        super.onScreenSizeUpdate(screenWidth, screenHeight);
        this.clearAllWidgets();
        initEditorViews();
    }

    public void initEditorViews() {
        Position position = getPosition();
        Size size = getSize();

        //ImageWidget imageWidget = new ImageWidget(position.x/2-SCALE/2,position.y/2-SCALE/2,SCALE,SCALE, ColorPattern.YELLOW.rectTexture());
        //addWidget(imageWidget);
        addWidget(new RadiaMenu(this,size.width/2-SCALE/2,size.height/2-SCALE/2,SCALE,SCALE));
    }
}
