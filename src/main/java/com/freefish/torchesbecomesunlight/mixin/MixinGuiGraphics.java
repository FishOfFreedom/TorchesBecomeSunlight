package com.freefish.torchesbecomesunlight.mixin;

import com.freefish.torchesbecomesunlight.server.init.EffectHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class MixinGuiGraphics {
    @Shadow
    private  Minecraft minecraft;
    @Shadow
    private  PoseStack pose;

    @Shadow
    public void fill(RenderType pRenderType, int pMinX, int pMinY, int pMaxX, int pMaxY, int pColor){};

    @Inject(method = "Lnet/minecraft/client/gui/GuiGraphics;renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"
            ,at = @At("HEAD"), cancellable = true)
    public void renderItemFDemon(Font pFont, ItemStack pStack, int pX, int pY,String pText,CallbackInfo info){
        if (!pStack.isEmpty()) {
            this.pose.pushPose();
            LocalPlayer localplayer = this.minecraft.player;
            if(localplayer!=null) {
                MobEffectInstance effect = localplayer.getEffect(EffectHandle.COLLAPSAL.get());

                float f = 1;
                if (effect != null && localplayer.getCooldowns().isOnCooldown(pStack.getItem())) {
                    int i1 = pY + Mth.floor(16.0F * (1.0F - f));
                    int j1 = i1 + Mth.ceil(16.0F * f);
                    this.fill(RenderType.guiOverlay(), pX, i1, pX + 16, j1, 0Xff000000);
                    info.cancel();
                }
            }
            this.pose.popPose();
        }
    }

    @Inject(method = "renderItem"
            ,at = @At("HEAD"), cancellable = true)
    public void renderItemDemon(ItemStack pStack, int pX, int pY,CallbackInfo info){
        if (!pStack.isEmpty()) {
            LocalPlayer localplayer = this.minecraft.player;
            if(localplayer!=null) {
                MobEffectInstance effect = localplayer.getEffect(EffectHandle.COLLAPSAL.get());
                if (effect != null && localplayer.getCooldowns().isOnCooldown(pStack.getItem())) {
                    info.cancel();
                }
            }
        }
    }
}
