package com.freefish.torchesbecomesunlight.server.entity.effect;

import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class StompEntity extends Entity implements IEntityAdditionalSpawnData {
    int continueTime,frame;
    LivingEntity owner;
    double maxDist;

    public StompEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        maxDist = 5;
        continueTime = 16;
    }

    public StompEntity(Level pLevel,int continueTime,LivingEntity owner,double maxDist) {
        super(EntityHandle.STOMP_ENTITY.get(), pLevel);
        this.continueTime = continueTime;
        this.owner = owner;
        this.maxDist = maxDist;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        continueTime = compoundTag.getInt("continue");
        frame = compoundTag.getInt("frame");
        maxDist = compoundTag.getDouble("dist");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("continue",continueTime);
        compoundTag.putInt("frame",frame);
        compoundTag.putDouble("dist",maxDist);
    }

    @Override
    public void tick() {
        super.tick();
        frame++;
        double facingAngle = owner != null?owner.yBodyRot:0;
        double maxDistance = maxDist;
        Level world = this.level();
        int tick = frame;
        if (tick <= continueTime){
            int hitY = Mth.floor(this.getBoundingBox().minY - 0.5);
            if (tick == 1) {
                this.playSound(SoundEvents.GENERIC_EXPLODE, 2, 1F + this.random.nextFloat() * 0.1F);
                EntityCameraShake.cameraShake(world, this.position(), 25, 0.1f, 0, 20);
            }
            if (tick % 2 == 0) {
                int distance = tick/ 2;
                double spread = Math.PI * 2;
                int arcLen = Mth.ceil(distance * spread * 2);
                double minY = this.getBoundingBox().minY;
                double maxY = this.getBoundingBox().maxY;

                for (int i = 0; i < arcLen; i++) {
                    double theta = (i / (arcLen - 1.0) - 0.5) * spread + facingAngle;
                    double vx = Math.cos(theta);
                    double vz = Math.sin(theta);
                    double px = this.getX() + vx * distance;
                    double pz = this.getZ() + vz * distance;
                    float factor = 1 - distance / (float) maxDistance;
                    AABB selection = new AABB(px - 1.5, minY, pz - 1.5, px + 1.5, maxY, pz + 1.5);
                    List<LivingEntity> hits = world.getEntitiesOfClass(LivingEntity.class, selection);
                    for (LivingEntity hit : hits) {
                        if (hit.onGround()) {
                            if (hit==owner) {
                                continue;
                            }
                            float applyKnockbackResistance = 0;
                            if(owner!=null) {
                                float damage = (float) owner.getAttribute(Attributes.ATTACK_DAMAGE).getValue()*0.4f;
                                hit.hurt(this.damageSources().mobAttack(owner), (factor * 5 + 1)*damage);
                            }
                            applyKnockbackResistance = (float) hit.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
                            double magnitude = this.random.nextDouble() * 0.15 + 0.1;
                            float x = 0, y = 0, z = 0;
                            x += vx * factor * magnitude * (1 - applyKnockbackResistance);
                            y += 0.1 * (1 - applyKnockbackResistance) + factor * 0.15 * (1 - applyKnockbackResistance);
                            z += vz * factor * magnitude * (1 - applyKnockbackResistance);
                            hit.setDeltaMovement(hit.getDeltaMovement().add(x, y, z));
                            if (hit instanceof ServerPlayer player) {
                                player.connection.send(new ClientboundSetEntityMotionPacket(this));
                            }
                        }
                    }
                    if (world.random.nextBoolean()) {
                        int hitX = Mth.floor(px);
                        int hitZ = Mth.floor(pz);
                        BlockPos pos = new BlockPos(hitX, hitY, hitZ);
                        BlockPos abovePos = new BlockPos(pos).above();
                        BlockState block = world.getBlockState(pos);
                        BlockState blockAbove = world.getBlockState(abovePos);
                        if (!block.isAir() && block.isRedstoneConductor(world, pos) && !block.hasBlockEntity() && !blockAbove.blocksMotion()) {
                            EntityFallingBlock fallingBlock = new EntityFallingBlock(EntityHandle.FALLING_BLOCK.get(), world, block, (float) (0.4 + factor * 0.2));
                            fallingBlock.setPos(hitX + 0.5, hitY + 1, hitZ + 0.5);
                            world.addFreshEntity(fallingBlock);
                        }
                    }
                }
            }
        }
        else if(!level().isClientSide){
            kill();
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        CompoundTag compoundTag = new CompoundTag();
        if(owner!=null){
            compoundTag.putInt("id",owner.getId());
        }
        buffer.writeNbt(compoundTag);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        CompoundTag compoundTag = additionalData.readNbt();
        if(compoundTag.contains("id")){
            owner = (LivingEntity) level().getEntity(compoundTag.getInt("id"));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
