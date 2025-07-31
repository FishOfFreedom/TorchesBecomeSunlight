package com.freefish.torchesbecomesunlight.server.entity.effect;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.util.particle.ParticleCloud;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SetDemonCentreMessage;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class PursuerEffectEntity extends Entity implements IEntityAdditionalSpawnData {
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity owner;
    private float distantToC;
    private int teleWilling;
    public int damageAmount = 10;

    public void setTtype(int type) {
        this.type = type;
    }

    public int type;

    private static final EntityDataAccessor<Boolean> LOCATE = SynchedEntityData.defineId(PursuerEffectEntity.class, EntityDataSerializers.BOOLEAN);

    public PursuerEffectEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public PursuerEffectEntity(Level pLevel,int age,Pursuer pursuer) {
        this(EntityHandle.PEE.get(), pLevel);
        setOwner(pursuer);
        setLocate(true);
    }

    public Vec3 type1Pursuer;

    @Override
    public void tick() {
        super.tick();

        if(type==0){
            if (getLocate()) {
                List<LivingEntity> nearbyEntities = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(24, 24, 24), e -> !(e instanceof Pursuer) && distanceTo(e) <= 24 + e.getBbWidth() / 2f && e.getY() <= getY() + 10);
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof Player player && player.isCreative()) continue;
                    float len = (float) entity.position().subtract(position()).length();
                    if (entity.isPickable() && !entity.noPhysics && len > 20) {
                        Vec3 motion = new Vec3(entity.getX() - entity.xo, 0, entity.getY() - entity.yo);
                        if (motion.dot(position().subtract(entity.position())) <= 0)
                            entity.setPos(entity.xo, entity.getY(), entity.zo);
                    }
                }
            } else {
                kill();
            }

            if (!level().isClientSide) {
                Entity entity = getOwner();
                if (entity instanceof Pursuer pursuer) {
                    if (tickCount % 100 == 0) {
                        RandomSource random = pursuer.getRandom();
                        LivingEntity target = pursuer.getTarget();
                        if (target != null) {
                            Vec3 vec3 = new Vec3(0, 0, 7 + random.nextFloat() * 3).xRot(1 + 2 * random.nextFloat()).yRot(random.nextFloat() * 6).add(target.position());
                            pursuer.shootBlackSpear(target, vec3, 1);
                        }
                    }

                    if (tickCount == 1) {
                        TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> pursuer), new SetDemonCentreMessage(pursuer.getId(), position()));
                    }

                    float health = pursuer.getHealth();
                    float maxHealth = pursuer.getMaxHealth();
                    float damageAmount1 = (health - maxHealth / 2) / (maxHealth / 4) * 10;
                    if (damageAmount1 < damageAmount) {
                        damageAmount -= 2;
                        List<BlackTuft> nearbyEntities = level().getEntitiesOfClass(BlackTuft.class, getBoundingBox().inflate(24, 24, 24));
                        for (BlackTuft blackTuft : nearbyEntities) {
                        }
                    }
                    if (health <= maxHealth / 2 && getLocate()) setLocate(false);
                    LivingEntity target = pursuer.getTarget();
                    if (target != null) {
                        float v = (float) target.position().subtract(position()).horizontalDistance();
                        float temp = v - distantToC;//0.06 0.21 .027
                        if (temp > 0.1 && v <= 28 && v >= 14) {
                            teleWilling += v / 3;
                        } else if (temp < 0) {
                            teleWilling--;
                        }
                        distantToC = v;
                        float distantToP = (float) target.position().subtract(pursuer.position()).horizontalDistance();
                        if (teleWilling >= 50 && v > 20 && v < 28 && distantToP > 5) {
                            teleWilling = -120;
                            pursuer.canSinceRemote3 = true;
                            AnimationActHandler.INSTANCE.sendAnimationMessage(pursuer, Pursuer.TELE);
                        }
                    }
                } else {
                    kill();
                }
            }
        }else if(type==1){
            if(level().isClientSide){
                if (tickCount == 1) {
                    Vec3 location = position().add(0,6,0);
                    RLParticle rlParticle = new RLParticle(level());
                    rlParticle.config.setStartLifetime(NumberFunction.constant(200));
                    rlParticle.config.setStartSize(new NumberFunction3(12));
                    rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst = new EmissionSetting.Burst();
                    burst.setCount(NumberFunction.constant(1));
                    rlParticle.config.getEmission().addBursts(burst);
                    rlParticle.config.getMaterial().setDepthTest(false);
                    rlParticle.config.getShape().setShape(new Dot());
                    rlParticle.config.getMaterial().setCull(false);
                    rlParticle.config.getMaterial().setMaterial(TBSMaterialHandle.DEMON_EYE.create());
                    rlParticle.config.getColorOverLifetime().open();
                    rlParticle.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(new float[]{0f, 10f / 200,(200 -10f) / 400, 1}, new int[]{0X00FFFFFF, 0XFFFFFFFF, 0XFFFFFFFF,0X00FFFFFF})));
                    rlParticle.config.getLights().open();
                    BlockEffect blockEffect = new BlockEffect(level(), location);
                    rlParticle.emmit(blockEffect);
                }else if(tickCount ==150||tickCount==155){
                    if(type1Pursuer!=null){
                        Vec3 vec3 = new Vec3(0, 0, -0.5).yRot((float) (-getYRot() / 180 * org.joml.Math.PI) + random.nextFloat()).add(type1Pursuer);
                        for (int i = 0; i <= 6; i++) {
                            for (int j = 0; j <= 5; j++) {
                                if (random.nextBoolean())
                                    level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0, 0, 0, (float) (10d + random.nextDouble() * 10d), 50, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), vec3.x + 1 - 2 * random.nextFloat(), vec3.y + i / 2f, vec3.z + 1 - 2 * random.nextFloat(), 0, 0, 0);
                            }
                        }
                    }
                }
            }else {
                if(tickCount==160&type1Pursuer!=null){
                    Pursuer pursuer = new Pursuer(EntityHandle.PURSUER.get(), level());
                    pursuer.setPos(type1Pursuer);
                    level().addFreshEntity(pursuer);
                    AnimationActHandler.INSTANCE.sendAnimationMessage(pursuer,Pursuer.REMOTE_1);
                }
            }

            if(tickCount>=200) discard();
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
        pCompound.putInt("tt", type);
        pCompound.putInt("tick", tickCount);

    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
            this.owner = null;
        }
        setLocate(pCompound.getBoolean("is"));
        type = pCompound.getInt("tt");
        tickCount = pCompound.getInt("tick");
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

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeVarInt(type);

        CompoundTag compoundTag = new CompoundTag();
        if(type1Pursuer!=null){
            compoundTag.putFloat("x",(float) type1Pursuer.x);
            compoundTag.putFloat("y",(float) type1Pursuer.y);
            compoundTag.putFloat("z",(float) type1Pursuer.z);
        }
        buffer.writeNbt(compoundTag);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        type = additionalData.readVarInt();

        CompoundTag compoundTag = additionalData.readNbt();
        if(compoundTag.contains("x")){
            type1Pursuer = new Vec3(compoundTag.getFloat("x"),compoundTag.getFloat("y"),compoundTag.getFloat("z"));
        }
    }
}
