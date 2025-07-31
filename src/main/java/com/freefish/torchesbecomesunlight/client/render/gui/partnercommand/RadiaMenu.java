package com.freefish.torchesbecomesunlight.client.render.gui.partnercommand;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.RendererSetting;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.utils.Position;
import com.freefish.rosmontislib.client.utils.Rect;
import com.freefish.rosmontislib.client.utils.Size;
import com.freefish.rosmontislib.gui.widget.Widget;
import com.freefish.torchesbecomesunlight.client.render.gui.TBSDrawerHelper;
import com.freefish.torchesbecomesunlight.compat.rosmontis.EntityEyesLineEffect;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.command.PartnerCommandBasic;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RadiaMenu extends Widget {
    private final PartnerCommandMenu partnerCommandMenu;
    private final int commandCounter;
    private final PartnerCommandMenuFactory instance;

    public RadiaMenu(PartnerCommandMenu partnerCommandMenu,int x,int y,int width,int height) {
        super(new Position(x,y), new Size(width,height));
        this.partnerCommandMenu = partnerCommandMenu;
        Partner<?> currentPartner = partnerCommandMenu.capability.getPartnerManager().getCurrentPartner();
        this.commandCounter = currentPartner.getSkillManager().getPartnerCommandBasics().size();
        instance = PartnerCommandMenuFactory.INSTANCE;
        instance.open(commandCounter);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double mouseRadiaX = mouseX - (this.getPositionX()+this.getSizeWidth()/2 );
        double mouseRadiaY = mouseY - (this.getPositionY()+this.getSizeHeight()/2);
        float mouseRadia = (float) Math.atan2(-mouseRadiaY,mouseRadiaX);
        Partner<?> currentPartner = partnerCommandMenu.capability.getPartnerManager().getCurrentPartner();
        if(currentPartner==null) return false;
        for(int i=0;i<commandCounter;i++){
            double lerp = Mth.lerp((float) i / commandCounter, -3.14, 3.14);
            if((lerp <mouseRadia)&&(Mth.lerp((float) (i+1)/commandCounter,-3.14,3.14)>mouseRadia)){
                PartnerCommandBasic partnerCommandBasic = currentPartner.getSkillManager().getPartnerCommandBasics().get(i);
                currentPartner.getSkillManager().setCurrentCommand(partnerCommandBasic);
                getGui().getModularUIGui().onClose();

                startAttackParticle();

                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void startAttackParticle(){
        Player player = partnerCommandMenu.player;

        RLParticle rlParticle = new RLParticle(player.level());
        rlParticle.config.setStartLifetime(NumberFunction.constant(500));
        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst = new EmissionSetting.Burst();
        burst.setCount(NumberFunction.constant(1));
        rlParticle.config.getEmission().addBursts(burst);
        rlParticle.config.getMaterial().setMaterial(TBSMaterialHandle.FACE.create());
        rlParticle.config.setStartSize(new NumberFunction3(2));
        rlParticle.config.getMaterial().setDepthTest(false);
        rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.VerticalBillboard);
        rlParticle.config.setStartSpeed(NumberFunction.constant(0));
        EntityEyesLineEffect entityEyesLineEffect = new EntityEyesLineEffect(player.level(), player);
        rlParticle.emmit(entityEyesLineEffect);
    }

    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        if(partnerCommandMenu==null)return;

        Player player = partnerCommandMenu.player;
        //PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        //if(capability!=null){
        //}
        int x = getPositionX();
        int y = getPositionY();
        int width = getSizeWidth();
        int height = getSizeHeight();

        int mouseRadiaX = mouseX - (this.getPositionX()+this.getSizeWidth()/2 );
        int mouseRadiaY = mouseY - (this.getPositionY()+this.getSizeHeight()/2);
        float mouseRadia = (float) Math.atan2(-mouseRadiaY,mouseRadiaX);

        int superWidth = partnerCommandMenu.getSizeWidth();
        int superHeight = partnerCommandMenu.getSizeHeight();
        Partner<?> currentPartner = partnerCommandMenu.capability.getPartnerManager().getCurrentPartner();
        if(currentPartner==null) return;
        List<PartnerCommandBasic> partnerCommandBasics = currentPartner.getSkillManager().getPartnerCommandBasics();
        float v = (float) (2*Math.PI / commandCounter);
        for(int i=0;i<commandCounter;i++){
            double lerp = Mth.lerp((float) i / commandCounter, -3.14, 3.14);
            if((lerp <mouseRadia)&&(Mth.lerp((float) (i+1)/commandCounter,-3.14,3.14)>mouseRadia)){
                instance.parts[i].setMouseOver(true);
            }else {
                instance.parts[i].setMouseOver(false);
            }
            float commandRadiu = instance.parts[i].getCommandRadiu();

            TBSDrawerHelper.drawFlabelate(graphics, Rect.ofRelative((int) x, width, (int) y, height),
                0X8F000000, v, (float) lerp, commandRadiu);

            double v1 = lerp + v / 2;
            graphics.pose().pushPose();
            Vec3 vec3 = (new Vec3(0,0,20+30*commandRadiu)).yRot(-(float)v1);
            graphics.pose().last().pose().translate((float)vec3.z,(float)vec3.x,0);
            graphics.blit(partnerCommandBasics.get(i).getIcon(),superWidth/2-16,superHeight/2-16,0,0,32,32,32,32);
            graphics.pose().popPose();
        }
    }
}
