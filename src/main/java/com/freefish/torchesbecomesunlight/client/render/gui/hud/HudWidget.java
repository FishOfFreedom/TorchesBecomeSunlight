package com.freefish.torchesbecomesunlight.client.render.gui.hud;

import com.freefish.rosmontislib.gui.widget.WidgetGroup;

import java.util.HashMap;
import java.util.Map;

public class HudWidget extends WidgetGroup {
    public static final Map<String, HudWidget> STRING_WIDGET = new HashMap<>();
    public boolean isOpen = false;

    public HudWidget(String id) {
    }

    public void tick(){

    }

    public boolean canRenderer(){
        return isOpen;
    }

    public void open(){
    }

    public void close(){
    }

    public static void initHud(){
        STRING_WIDGET.put("skill",SkillLineGui.INSTANCE);
        STRING_WIDGET.put(PartnerListGui.ID,PartnerListGui.INSTANCE);
    }
}
