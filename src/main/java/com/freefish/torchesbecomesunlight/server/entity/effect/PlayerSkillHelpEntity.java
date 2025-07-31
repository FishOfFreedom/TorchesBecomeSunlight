package com.freefish.torchesbecomesunlight.server.entity.effect;


import com.freefish.rosmontislib.client.particle.advance.base.beam.RLBeamParticle;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.Line;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.torchesbecomesunlight.client.particle.DemonHoleParticle;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.init.DamageSourceHandle;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

import java.util.List;

public class PlayerSkillHelpEntity extends EffectEntity implements IEntityAdditionalSpawnData {
    private static final int MAX_ACTIVE = 200;
    private Vec3 reVec3 = Vec3.ZERO;

    public PlayerSkillHelpEntity(EntityType<? extends PlayerSkillHelpEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public PlayerSkillHelpEntity(Level level, LivingEntity caster, int type,Vec3 reVec3) {
        this(EntityHandle.PLAYER_SKILL_ENTITY.get(), level);
        this.caster = caster;
        setType1(type);
        this.reVec3 = reVec3;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        setType1(compoundTag.getInt("type"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("type",getType1());
    }

    @Override
    public void tick() {
        super.tick();
        if(getType1()==1){
            if (level().isClientSide && tickCount == 1) {
                Vec3 vec31 = position().subtract(reVec3);
                this.setDeltaMovement(vec31);
                double d0 = vec31.horizontalDistance();
                this.setYRot((float) (Mth.atan2(vec31.x, vec31.z) * (double) (180F / (float) Math.PI)));
                this.setXRot((float) (Mth.atan2(vec31.y, d0) * (double) (180F / (float) Math.PI)));
                this.yRotO = this.getYRot();
                this.xRotO = this.getXRot();

                Vec3 vec3 = position();
                float f1 = (float) Math.toRadians(getXRot());
                float f2 = (float) Math.toRadians(getYRot() - 180);

                RLBeamParticle rlBeamParticle = new RLBeamParticle(level());
                rlBeamParticle.getConfig().setEnd(new Vector3f((float) (reVec3.x - getX()), (float) (reVec3.y - getY()), (float) (reVec3.z - getZ())));
                rlBeamParticle.getConfig().setDuration(54);
                rlBeamParticle.getConfig().setColor(new Gradient(new GradientColor(0XFF000000,0X00000000)));
                rlBeamParticle.getConfig().setWidth(new Line(new float[]{0, 0.1f, 0.3f,0.4f, 1}, new float[]{0f, 0f, 0.05f, 1f, 1f}));
                rlBeamParticle.getConfig().material.setMaterial(TBSMaterialHandle.BEAM.create());
                rlBeamParticle.getConfig().setLooping(false);
                rlBeamParticle.emmit(new BlockEffect(level(), position()));

                level().addParticle(new DemonHoleParticle.DemonHoleData(54, 3, (float) f2, f1), vec3.x, vec3.y, vec3.z, 0, 0, 0);
                level().addParticle(new DemonHoleParticle.DemonHoleData(54, 3, (float) f2, f1), reVec3.x, reVec3.y, reVec3.z, 0, 0, 0);
            }

            if (this.tickCount > 20 && reVec3 != null) {
                doDemonAttack();
            }

            if(this.tickCount > 54){
                this.discard();
            }
        }

        BlockState blockState = getBlockStateOn();
        if (!blockState.isAir()&&!level().isClientSide) {
            setDeltaMovement(0, 0, 0);
        }
    }

    private void doDemonAttack(){
        Vec3 line = reVec3.subtract(position()).normalize();
        List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(22),entity -> !(entity instanceof Pursuer));
        for (LivingEntity livingEntity : list) {
            if (livingEntity instanceof Player player && player.isCreative()) continue;
            if(livingEntity == caster) continue;

            Vec3 subtract = livingEntity.position().subtract(position());
            double dot = line.dot(subtract);
            if (dot > 0) {
                Vec3 line1 = line.scale(dot);
                float len = (float) line1.subtract(subtract).length();
                if (len < 2) {
                    livingEntity.setDeltaMovement(0, 0, 0);
                    livingEntity.setPos(livingEntity.xo, livingEntity.yo, livingEntity.zo);
                    if(tickCount%3==0) {
                        if (caster instanceof Player player) {
                            livingEntity.actuallyHurt(DamageSourceHandle.demonAttack(caster), 5.5f);
                            livingEntity.invulnerableTime=0;
                            livingEntity.hurt(caster.damageSources().playerAttack(player), 1f);
                        }
                    }
                }
            }
        }
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }
    @Getter
    @Setter
    int type1;

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(type1);
        buffer.writeDouble(reVec3.x);
        buffer.writeDouble(reVec3.y);
        buffer.writeDouble(reVec3.z);

        CompoundTag compoundTag = new CompoundTag();
        if(caster!=null){
            compoundTag.putFloat("player",caster.getId());
        }
        buffer.writeNbt(compoundTag);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        type1 = additionalData.readInt();
        reVec3 = new Vec3(additionalData.readDouble(),additionalData.readDouble(),additionalData.readDouble());

        CompoundTag compoundTag = additionalData.readNbt();
        if(compoundTag.contains("player")){
            int player = compoundTag.getInt("player");
            caster = (LivingEntity) level().getEntity(player);
        }
    }
}
