package com.freefish.torchesbecomesunlight.server.entity.effect;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SetDemonCentreMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class PursuerEffectEntity extends Entity {
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity owner;
    private float distantToC;
    private int teleWilling;
    public int damageAmount = 10;

    private static final EntityDataAccessor<Boolean> LOCATE = SynchedEntityData.defineId(PursuerEffectEntity.class, EntityDataSerializers.BOOLEAN);

    public PursuerEffectEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public PursuerEffectEntity(Level pLevel,int age,Pursuer pursuer) {
        this(EntityHandle.PEE.get(), pLevel);
        setOwner(pursuer);
        setLocate(true);
    }

    @Override
    public void tick() {
        super.tick();
        if(getLocate()) {
            List<LivingEntity> nearbyEntities = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(24, 24, 24), e -> !(e instanceof Pursuer) && distanceTo(e) <= 24 + e.getBbWidth() / 2f && e.getY() <= getY() + 10);
            for (Entity entity : nearbyEntities) {
                float len = (float) entity.position().subtract(position()).length();
                if (entity.isPickable() && !entity.noPhysics && len > 20) {
                    Vec3 motion = new Vec3(entity.getX()-entity.xo,0,entity.getY()-entity.yo);
                    if(motion.dot(position().subtract(entity.position()))<=0)
                        entity.setPos(entity.xo, entity.getY(), entity.zo);
                }
            }
        }
        else {
            kill();
        }

        if(!level().isClientSide)
        {
            Entity entity = getOwner();
            if (entity instanceof Pursuer pursuer) {
                if(tickCount==1){
                    TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> pursuer),new SetDemonCentreMessage(pursuer.getId(),position()));
                }

                float health = pursuer.getHealth();
                float maxHealth = pursuer.getMaxHealth();
                float damageAmount1 = (health - maxHealth/ 2)/(maxHealth/ 4)*10;
                if(damageAmount1<damageAmount){
                    damageAmount -=2;
                    List<BlackTuft> nearbyEntities = level().getEntitiesOfClass(BlackTuft.class, getBoundingBox().inflate(24, 24, 24));
                    for (BlackTuft blackTuft : nearbyEntities) {
                        if(random.nextFloat()>damageAmount/10f+0.2)
                            blackTuft.kill();
                    }
                }
                if (health <=  maxHealth/ 2&&getLocate()) setLocate(false);
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
                    float distantToP = (float) target.position().subtract(pursuer.position()).horizontalDistance();
                    if(teleWilling>=50&&v>20&&v<28&&distantToP>5) {
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

    private double getAngleBetweenEntities(Entity first, Entity second) {
        return Math.atan2(second.getZ() - first.getZ(), second.getX() - first.getX()) * (180 / Math.PI) + 90;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LOCATE,false);
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
        pCompound.putBoolean("is", getLocate());

    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
            this.owner = null;
        }
        setLocate(pCompound.getBoolean("is"));
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

    public void setLocate(boolean locate){
        this.entityData.set(LOCATE,locate);
    }

    public boolean getLocate(){
        return this.entityData.get(LOCATE);
    }
}
