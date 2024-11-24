package com.freefish.torchesbecomesunlight.server.entity.effect;


import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class BlackTuft extends EffectEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity owner;

    public BlackTuft(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<BlackTuft>(this, "Controller", 2, this::predicate));
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide){
            Entity owner1 = getOwner();
            if(owner1==null) kill();
        }
    }

    private PlayState predicate(AnimationState<BlackTuft> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("a"));
        return PlayState.CONTINUE;
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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static void spawnBlackTuft(Level level,Entity pursuerEffectEntity,Vec3 pos,float rotX,float rotY){
        BlackTuft blackTuft = new BlackTuft(EntityHandle.BLACK_TUFT.get(), level);
        blackTuft.setOwner(pursuerEffectEntity);
        blackTuft.setPos(pos);
        blackTuft.setRot(rotX,rotY);
        level.addFreshEntity(blackTuft);
    }
}
