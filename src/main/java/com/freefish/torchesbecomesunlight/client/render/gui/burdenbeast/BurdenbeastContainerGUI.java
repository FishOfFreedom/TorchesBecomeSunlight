package com.freefish.torchesbecomesunlight.client.render.gui.burdenbeast;

import com.freefish.rosmontislib.client.utils.Position;
import com.freefish.rosmontislib.client.utils.Size;
import com.freefish.rosmontislib.gui.widget.WidgetGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BurdenbeastContainerGUI extends WidgetGroup {
    public BurdenbeastContainerGUI() {
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
    }
}
