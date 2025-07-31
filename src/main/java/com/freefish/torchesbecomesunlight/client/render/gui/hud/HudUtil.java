package com.freefish.torchesbecomesunlight.client.render.gui.hud;

public class HudUtil {
    public static boolean skillIsOpen(){
        return SkillLineGui.INSTANCE.isOpen;
    }

    public static void skillClose(){
        SkillLineGui.INSTANCE.close();
    }

    public static void skillOpen(){
        SkillLineGui.INSTANCE.open();
    }
}
