package com.freefish.torchesbecomesunlight.client.render.gui.partnercommand;

import com.freefish.rosmontislib.gui.modular.ModularUI;
import com.freefish.rosmontislib.gui.modular.ModularUIGuiContainer;

public class PartnerGuiContainer extends ModularUIGuiContainer {
    public PartnerGuiContainer(ModularUI modularUI, int windowId) {
        super(modularUI, windowId);
    }

    @Override
    public void onClose() {
        PartnerCommandMenuFactory.INSTANCE.close();
        super.onClose();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        PartnerCommandMenuFactory.INSTANCE.tick();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return super.shouldCloseOnEsc();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode==86){
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
