package com.freefish.torchesbecomesunlight.client.event;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueTrigger;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.List;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, value = Dist.CLIENT)
public class GuiDialogueRenderEvent {
    private static final ResourceLocation DO = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/gui/dialogue_op.png");

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if(player == null || !minecraft.isWindowActive())
            return;

        //todo skip
        if (event.getOverlay().id() == VanillaGuiOverlay.HOTBAR.id()) {
            int skipRadio = ClientStorage.INSTANCE.getSkip(1);

            List<DialogueEntity> entities = player.level().getEntitiesOfClass(DialogueEntity.class, player.getBoundingBox().inflate(9));
            DialogueEntity dialogueEntity = null;
            for (DialogueEntity dialogueEntity1 : entities) {
                if (dialogueEntity1.getChatEntities() != null && dialogueEntity1.getChatEntities().length != 0 && dialogueEntity1.getChatEntities()[0] == player) {
                    dialogueEntity = dialogueEntity1;
                }
            }
            if (dialogueEntity != null) {
                int wight = minecraft.getWindow().getGuiScaledWidth();
                Font font = minecraft.font;
                String s = Component.translatable("torchesbecomesunlight.skip").getString();
                int wightFont = font.width(s);
                int heighFont = font.lineHeight;

                event.getGuiGraphics().setColor(1, 1, 1, 1);
                event.getGuiGraphics().fill(wight - wightFont, 0, wight - wightFont + (int) (MathUtils.easeOutCubic((skipRadio / 41f)) * wightFont), heighFont, 0x08000000);
                event.getGuiGraphics().drawString(font, s, wight - wightFont, 0, 0xFFFFFF);

                Dialogue dialogue = dialogueEntity.getDialogue();
                if (dialogue != null) {
                    renderChatCenter(event, dialogueEntity);
                    if (dialogue.getOptions() != null && !dialogue.getOptions().isEmpty() && dialogueEntity.getFloatScale() == 20)
                        renderOptionsCenter(event, dialogueEntity);
                }
            }
        }
    }

    private static void renderChatCenter(RenderGuiOverlayEvent.Post event, DialogueEntity dialogueEntity){
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        GuiGraphics guiGraphics = event.getGuiGraphics();
        float partialTick = event.getPartialTick();
        //i18
        String message = cuttingString(dialogueEntity.getDialogue().getMessage(), MathUtils.easeOutCubic(Math.min(1,2f*dialogueEntity.getDialogueScale())));
        Entity chatEntity = dialogueEntity.getChatEntities()[dialogueEntity.getDialogue().getSpeakerNumber()];
        String name = (chatEntity.hasCustomName()
                ? ChatFormatting.ITALIC + chatEntity.getCustomName().getString()
                : chatEntity.getDisplayName().getString());

        int fontHeight = font.lineHeight;
        int fontWidth = font.width(message);
        int width = minecraft.getWindow().getGuiScaledWidth() / 4;
        int height = minecraft.getWindow().getGuiScaledHeight() / 5 * 4;
//in 1s
        float len;
        if(dialogueEntity.getFloatScale()==20)
            len = dialogueEntity.getOptions()*fontHeight;
        else
            len = Mth.lerp(MathUtils.easeOutCubic((float) ((dialogueEntity.getFloatScale()+ partialTick)/ 20.0)) ,dialogueEntity.getOldOptions(),dialogueEntity.getOptions())*fontHeight;
        guiGraphics.pose().pushPose();
        Matrix4f pose = guiGraphics.pose().last().pose();
        pose.translate(0,- len,0);
        guiGraphics.fill(width, height , width + fontWidth, height + fontHeight , 0x08000000);
        guiGraphics.drawString(font,  message, width, height , 0xFFFFFF);

        guiGraphics.drawString(font, "["+name+"]", width, height -fontHeight, 0xFFFFFF);
        guiGraphics.pose().popPose();
    }

    private static void renderOptionsCenter(RenderGuiOverlayEvent.Post event,DialogueEntity dialogueEntity){
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int fontHeight = font.lineHeight;
        GuiGraphics guiGraphics = event.getGuiGraphics();

        List<String> messages = dialogueEntity.getDialogue().getOptions().stream().map(DialogueTrigger::getContent).toList();
        int number = dialogueEntity.getNumber();
        float scale1 = Math.min(1,(dialogueEntity.getSpeakTickCount()-20)/20f);
        int len = messages.size();

        int width = minecraft.getWindow().getGuiScaledWidth() / 4;

        float floatOp = dialogueEntity.getFloatOp(event.getPartialTick());
        int height1 = minecraft.getWindow().getGuiScaledHeight() / 5 * 4;

        guiGraphics.pose().pushPose();
        Matrix4f pose = guiGraphics.pose().last().pose();
        pose.translate(0,-floatOp*fontHeight,0);
        RenderSystem.enableBlend();
        guiGraphics.setColor(1,1,1,0.8f);
        guiGraphics.blit(DO,width,height1,0,0,fontHeight*4,fontHeight,fontHeight*4,fontHeight);
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
        guiGraphics.setColor(1,1,1,1);

        for(int i=0;i<len;i++){
            int height = minecraft.getWindow().getGuiScaledHeight() / 5 * 4 - i * fontHeight;
            scale1 = scale1*(len*0.2f+1);
            String s = cuttingString(dialogueEntity.getDialogue().getOptions().get(i).getContent(),MathUtils.easeOutCubic(Math.min(1,2f*scale1)));
            if(number == i)
                guiGraphics.drawString(font, s, width, height, 0xFFFF00);
            else
                guiGraphics.drawString(font, s, width, height, 0xFFFFFF);
        }

    }

    private static String cuttingString(String input , double fraction){
        if(fraction > 1) fraction = 1;
        int cutIndex = Math.round((int)(input.length() * fraction));
        return input.substring(0, cutIndex);
    }
}
