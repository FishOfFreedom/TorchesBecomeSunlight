package com.freefish.torchesbecomesunlight.compat.rosmontis;

import com.freefish.rosmontislib.client.particle.advance.base.IFXObject;
import com.freefish.rosmontislib.client.particle.advance.effect.FXEffect;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.command.PartnerCommandBasic;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicHandle;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicType;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class EntityEyesLineEffect extends FXEffect {
    private final Player living;

    public EntityEyesLineEffect(Level level, Player living) {
        super(level);
        this.living = living;
    }

    @Override
    public void updateFXObjectFrame(IFXObject fxObject, float partialTicks) {
        if(living!=null&&living.isAlive()){
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(living, CapabilityHandle.PLAYER_CAPABILITY);
            if(capability!=null){
                Partner<?> currentPartner = capability.getPartnerManager().getCurrentPartner();
                if(currentPartner!=null){
                    PartnerCommandBasic command = currentPartner.getSkillManager().getCurrentCommand();
                    if(command!=null){
                        TriggerBasicType triggerType = command.getTriggerType();
                        if(triggerType== TriggerBasicHandle.BLOCK_TRIGGER){
                            BlockHitResult hitResult = (BlockHitResult) living.pick(20.0D, partialTicks, false);
                            Vec3 location = hitResult.getLocation();
                            fxObject.updatePos(new Vector3f((float) location.x, (float) location.y + 0.1f, (float) location.z));
                        }else {
                            Vec3 add = FFEntityUtils.getHeadRotVec(living, new Vec3(0, 0, 20)).add(0, living.getEyeHeight(), 0);
                            EntityHitResult entityHitResult = FFEntityUtils.getEntityHitResult(living.level(), living, living.getEyePosition(), add, living.getBoundingBox().expandTowards(add.subtract(living.position())).inflate(1.0D));
                            if(entityHitResult!=null){
                                Entity entity = entityHitResult.getEntity();
                                fxObject.updatePos(new Vector3f((float) entity.getX(), (float) entity.getY() + 0.1f, (float) entity.getZ()));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateFXObjectTick(IFXObject fxObject) {
        boolean flad = true;
        if(living!=null&&living.isAlive()){
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(living, CapabilityHandle.PLAYER_CAPABILITY);
            if(capability!=null){
                Partner<?> currentPartner = capability.getPartnerManager().getCurrentPartner();
                if(currentPartner!=null){
                    PartnerCommandBasic command = currentPartner.getSkillManager().getCurrentCommand();
                    if(command!=null){
                        flad = false;
                    }
                }
            }
        }

        if(flad){
            fxObject.remove(true);
        }
        super.updateFXObjectTick(fxObject);
    }

    @Override
    public void start() {
    }
}
