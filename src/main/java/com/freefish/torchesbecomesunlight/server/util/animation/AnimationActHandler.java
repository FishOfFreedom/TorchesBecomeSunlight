package com.freefish.torchesbecomesunlight.server.util.animation;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.AnimationActMessage;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.ArrayUtils;

public enum AnimationActHandler {
    INSTANCE;

    public <T extends Entity &  IAnimatedEntity> void sendAnimationMessage(T entity,  AnimationAct animation) {
        if (entity.level().isClientSide) {
            return;
        }
        entity.setAnimation(animation);
        TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new AnimationActMessage(entity.getId(), ArrayUtils.indexOf(entity.getAnimations(), animation)));
    }
}
