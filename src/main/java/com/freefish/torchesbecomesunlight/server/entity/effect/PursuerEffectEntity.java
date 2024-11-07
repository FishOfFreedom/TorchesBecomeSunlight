package com.freefish.torchesbecomesunlight.server.entity.effect;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SetDemonCentreMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.UUID;

public class PursuerEffectEntity extends Entity {
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity owner;
    private float distantToC;
    private int teleWilling;

    public PursuerEffectEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public PursuerEffectEntity(Level pLevel,int age,Pursuer pursuer) {
        this(EntityHandle.PEE.get(), pLevel);
        setOwner(pursuer);
    }

    @Override
    public void tick() {
        super.tick();
        //todo pursuer
        if(!level().isClientSide&&false)
        {
            Entity entity = getOwner();
            if (entity instanceof Pursuer pursuer) {
                if(tickCount==1){
                    TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> pursuer),new SetDemonCentreMessage(pursuer.getId(),position()));
                }
                LivingEntity target = pursuer.getTarget();
                if (target != null) {
                    float v = (float) target.position().subtract(position()).horizontalDistance();
                    float temp = v-distantToC;//0.06 0.21 .027
                    if (temp > 0.1 && v <= 28 && v >= 14) {
                        teleWilling+=v/3;
                    }
                    else if(temp<0){
                        teleWilling--;
                    }
                    distantToC = v;
                    if(teleWilling>=50&&v>20&&v<28) {
                        teleWilling=-120;
                        pursuer.canSinceRemote3 = true;
                        AnimationActHandler.INSTANCE.sendAnimationMessage(pursuer, Pursuer.TELE);
                    }
                }
            } else {
                kill();
            }
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
            this.owner = null;
        }
    }

    public void setOwner(@Nullable Entity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.owner = pOwner;
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.owner != null && !this.owner.isRemoved()) {
            return this.owner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.owner = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
            return this.owner;
        } else {
            return null;
        }
    }
}
