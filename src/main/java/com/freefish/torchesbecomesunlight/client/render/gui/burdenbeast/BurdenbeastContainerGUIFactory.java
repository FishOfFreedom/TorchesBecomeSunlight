package com.freefish.torchesbecomesunlight.client.render.gui.burdenbeast;

import com.freefish.rosmontislib.RosmontisLib;
import com.freefish.rosmontislib.gui.factory.UIFactory;
import com.freefish.rosmontislib.gui.modular.IUIHolder;
import com.freefish.rosmontislib.gui.modular.ModularUI;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class BurdenbeastContainerGUIFactory extends UIFactory<BurdenbeastContainerGUIFactory> implements IUIHolder {
    public static final BurdenbeastContainerGUIFactory INSTANCE = new BurdenbeastContainerGUIFactory();

    private BurdenbeastContainerGUIFactory(){
        super(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"beast_container"));
    }

    @Override
    protected ModularUI createUITemplate(BurdenbeastContainerGUIFactory holder, Player entityPlayer) {
        return createUI(entityPlayer);
    }

    @Override
    protected BurdenbeastContainerGUIFactory readHolderFromSyncData(FriendlyByteBuf syncData) {
        return this;
    }

    @Override
    protected void writeHolderToSyncData(FriendlyByteBuf syncData, BurdenbeastContainerGUIFactory holder) {

    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(this, entityPlayer)
                .widget(new BurdenbeastContainerGUI());
    }

    @Override
    public boolean isInvalid() {
        return false;
    }

    @Override
    public boolean isRemote() {
        return RosmontisLib.isRemote();
    }

    @Override
    public void markAsDirty() {

    }
}
