package com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis;

import com.freefish.rosmontislib.client.RLClientUseUtils;
import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.RendererSetting;
import com.freefish.rosmontislib.client.particle.advance.data.UVAnimationSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.GradientHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.Line;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Circle;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.rosmontislib.client.utils.Range;
import com.freefish.torchesbecomesunlight.server.entity.effect.AnimationBlock;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityMultiBlock;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class RosmontisBlock extends EntityMultiBlock {
    private int upTime = 20;
    private UUID owner_UUID;
    private LivingEntity owner;
    private int timeToShoot = 40;
    private int floatOff = 0;

    public void setTimeToShoot(int timeToShoot){
        setDuration(200+timeToShoot);
        this.timeToShoot = timeToShoot;
    }

    public RosmontisBlock(EntityType<RosmontisBlock> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        noPhysics = true;
        floatOff = worldIn.random.nextInt(100);
    }

    public RosmontisBlock(LivingEntity owner, Level worldIn) {
        this(EntityHandle.ROS_MULTI_BLOCK.get(), worldIn);
        setOwner(owner);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if(compound.contains("owner_uuid")){
            owner_UUID = compound.getUUID("owner_uuid");
        }
        timeToShoot = compound.getInt("timeToShoot");
        isfirstHitBlock = compound.getBoolean("first");
        tickCount = compound.getInt("tickCount");
        ownerIsAlive = compound.getBoolean("owner");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if(owner_UUID!=null){
            compound.putUUID("owner_uuid", owner_UUID);
        }
        compound.putInt("tickCount",tickCount);
        compound.putInt("timeToShoot",timeToShoot);
        compound.putBoolean("first",isfirstHitBlock);
        compound.putBoolean("owner",ownerIsAlive);
    }

    boolean ownerIsAlive;

    @Override
    public void tick() {
        move(MoverType.SELF,getDeltaMovement());

        super.tick();

        if(!level().isClientSide){
            if (tickCount < timeToShoot) {
                if (upTime <= 0) {
                    setDeltaMovement(new Vec3(0, -Math.sin(((tickCount - 20 + floatOff) / 150f * Math.PI)) * 0.04f, 0));
                } else {
                    setDeltaMovement(new Vec3(0, MathUtils.easeInExpo(upTime / 20f) * getOffset() - 0.05f, 0));
                    upTime -= 1;
                }
            } else {
                if (tickCount == timeToShoot) {
                    float speed = 3 - (getOffset() / 2f);
                    LivingEntity livingOwner = getOwner();
                    if (livingOwner instanceof Player mob) {
                        BlockHitResult hitResult = (BlockHitResult) mob.pick(40.0D, 0.0F, false);
                        Vec3 targetPos = hitResult.getLocation();
                        Vec3 normalize = targetPos.subtract(position()).normalize().scale(speed);
                        setDeltaMovement(normalize);
                    } else if (livingOwner instanceof Mob mob && mob.isAlive()) {

                        LivingEntity target = mob.getTarget();
                        if (target != null) {
                            Vec3 normalize = target.position().subtract(position()).normalize().scale(speed * 0.6f);
                            setDeltaMovement(normalize);
                        } else {
                            setDeltaMovement(new Vec3(0, -speed, 0));
                        }
                    } else {
                        setDeltaMovement(new Vec3(0, -speed, 0));
                    }
                }
            }
        }

        if(!level().isClientSide){
            Vec3 deltaMovement = getDeltaMovement();
            if(tickCount%5==0&&deltaMovement.length()>0.5f){
                LivingEntity livingOwner = getOwner();
                float damage = 10;
                if(livingOwner!=null&&livingOwner.isAlive()){
                    damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox());
                    for(LivingEntity living:entitiesOfClass){
                        if(living == livingOwner) continue;
                        living.hurt(damageSources().mobAttack(owner),damage*getOffset());
                    }
                }else {
                    if(!ownerIsAlive){
                        if(tickCount<=timeToShoot-2){
                            tickCount=timeToShoot-2;
                        }
                        ownerIsAlive = true;
                    }
                }
            }
        }else {
            if(!isfirstHitBlock){
                setYRot((getYRot() + 0.9f) % 360);
            }
        }

        Vec3 vec3 = this.getDeltaMovement();
        Vec3 vec32 = this.position();
        Vec3 vec33 = vec32.add(vec3);
        HitResult hitresult = this.level().clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitresult.getType() != HitResult.Type.MISS) {
            vec33 = hitresult.getLocation();
        }

        while(!this.isRemoved()) {
            EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
            if (entityhitresult != null) {
                hitresult = entityhitresult;
            }

            if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)hitresult).getEntity();
                Entity entity1 = this.getOwner();
                if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                    hitresult = null;
                    entityhitresult = null;
                }
            }

            if (hitresult != null && hitresult.getType() != HitResult.Type.MISS) {
                this.onHit(hitresult);
                this.hasImpulse = true;
                break;
            }

            if (entityhitresult == null) {
                break;
            }

            hitresult = null;
        }

        HitResult hitblockresult = this.level().clip(new ClipContext(this.position(), this.position().add(vec3), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if(hitblockresult.getType()!= HitResult.Type.MISS){
            onHit(hitblockresult);
        }
    }

    protected void onHit(HitResult pResult) {
        HitResult.Type hitresult$type = pResult.getType();
        if (hitresult$type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult)pResult);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, (BlockState)null));
        } else if (hitresult$type == HitResult.Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult)pResult;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, this.level().getBlockState(blockpos)));
        }

    }

    protected void onHitEntity(EntityHitResult pResult) {
        if(tickCount>timeToShoot){
        }
    }

    boolean isfirstHitBlock;

    protected void onHitBlock(BlockHitResult pResult) {
        if(tickCount>timeToShoot){
            Vec3 vec3 = pResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
            this.setDeltaMovement(vec3);
            if(!isfirstHitBlock){
                if(!level().isClientSide){
                    float damage = 10f;
                    if(getOwner() !=null){
                        damage = (float) getOwner().getAttributeValue(Attributes.ATTACK_DAMAGE);
                    }
                    damage *= getOffset()/2f;

                    int i = getOffset() ;
                    List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(i* 2));
                    for (LivingEntity living:entitiesOfClass){
                        if(living == getOwner()) continue;
                        float v = living.distanceTo(this);
                        if(v > i*2+1) continue;//3 5 7

                        if(v<getOffset()){//
                            living.hurt(getOwner()!=null?damageSources().mobAttack(getOwner()):damageSources().fall(),damage);
                        }else if(living.onGround()){
                            living.hurt(getOwner()!=null?damageSources().mobAttack(getOwner()):damageSources().fall(),damage);
                        }
                    }

                    if(getOffset()==3){
                        AnimationBlock.spawnRing(pResult.getBlockPos().getCenter().add(0,-0.6,0), 5, level());
                        RLClientUseUtils.StartCameraShake(level(),position(),10,0.05f,10,10);
                    }
                } else {
                    Gradient toAlpha = new Gradient(new GradientColor(0XFFFFFFFF, 0XFFFFFFFF, 0X00FFFFFF));

                    RLParticle rlParticle = new RLParticle(level());
                    rlParticle.config.setDuration(20);
                    rlParticle.config.setStartLifetime(NumberFunction.constant(10));
                    rlParticle.config.setStartSize(new NumberFunction3(6));
                    rlParticle.config.setStartColor(new Gradient(new GradientColor(0XDFFFFFFF)));
                    rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst = new EmissionSetting.Burst(); burst.setCount(NumberFunction.constant(1));
                    rlParticle.config.getEmission().addBursts(burst);
                    rlParticle.config.getShape().setShape(new Dot());
                    rlParticle.config.getMaterial().setMaterial(MaterialHandle.RING.create());
                    rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
                    rlParticle.config.getLights().open();
                    rlParticle.config.getColorOverLifetime().open();
                    rlParticle.config.getColorOverLifetime().setColor(toAlpha);
                    rlParticle.config.getSizeOverLifetime().open();
                    rlParticle.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,0.33f,1},new float[]{0,0.7f,1})));
                    RLParticle rlParticle3 = new RLParticle(level());
                    rlParticle3.config.setDuration(20);
                    rlParticle3.config.setStartLifetime(NumberFunction.constant(10));
                    rlParticle3.config.setStartSpeed(NumberFunction.constant(7));
                    rlParticle3.config.setStartColor(new Gradient(new GradientColor(0XDFFFFFFF)));
                    rlParticle3.config.setStartSize(new NumberFunction3(2));
                    rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst3 = new EmissionSetting.Burst(); burst3.setCount(NumberFunction.constant(30));burst3.time=3;
                    rlParticle3.config.getEmission().addBursts(burst3);
                    Circle circle3 = new Circle();circle3.setRadius(0.5f);circle3.setRadiusThickness(1f);
                    rlParticle3.config.getShape().setShape(circle3);
                    rlParticle3.config.getMaterial().setMaterial(MaterialHandle.SMOKE.create());
                    rlParticle3.config.getColorOverLifetime().open();
                    rlParticle3.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
                    rlParticle3.config.getPhysics().open();
                    rlParticle3.config.getPhysics().setHasCollision(false);
                    rlParticle3.config.getPhysics().setFriction(NumberFunction.constant(0.9));
                    rlParticle3.config.getUvAnimation().open();
                    rlParticle3.config.getUvAnimation().setTiles(new Range(2,2));
                    rlParticle3.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);
                    Vector3f scale = new Vector3f(getOffset());
                    rlParticle3.updateScale(scale);
                    rlParticle.updateScale(scale);

                    BlockEffect blockEffect = new BlockEffect(level(), pResult.getBlockPos().getCenter().add(0,0.7,0));
                    rlParticle.emmit(blockEffect);
                    rlParticle3.emmit(blockEffect);
                }
            }
            isfirstHitBlock = true;
        }
    }


    @Override
    public void onRemovedFromWorld() {
        if(level().isClientSide){
            BlockState[] blockStates = getBlockStates();
            if(blockStates.length>0){
                BlockState blockState = blockStates[0];
                for (int i = 0; i < 15; i++) {
                    level().addParticle(
                            new BlockParticleOption(ParticleTypes.BLOCK,blockState
                            ),
                            getX() + ((random.nextDouble() - 0.5D) * getBbWidth()),
                            getY() + ((random.nextDouble()) * getBbHeight()),
                            getZ() + ((random.nextDouble() - 0.5D) * getBbWidth()),
                            0, 0, 0);
                }
            }
        }else {
            if(!isfirstHitBlock){
                playSound(SoundHandle.ROS_BREAK_STONE.get(),1.5f,1.0F / (random.nextFloat() * 0.4F + 0.8F));
            }
        }
        super.onRemovedFromWorld();
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D),(entity -> true));
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }



    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            this.markHurt();
            Entity entity = pSource.getEntity();
            if(pAmount>=9){
                discard();
                return true;
            }

            return false;
        }
    }

    @Override
    public boolean isAttackable() {
        return super.isAttackable();
    }

    @Override
    public boolean skipAttackInteraction(Entity pEntity) {
        return super.skipAttackInteraction(pEntity);
    }

    //@Override
    //public boolean mayInteract(Level pLevel, BlockPos pPos) {
    //    System.out.println(2);
    //    return super.mayInteract(pLevel, pPos);
    //}
    //@Override
    //public InteractionResult interactAt(Player pPlayer, Vec3 pVec, InteractionHand pHand) {
    //    System.out.println(123);
    //    return InteractionResult.SUCCESS;
    //}
    //@Override
    //public boolean skipAttackInteraction(Entity pEntity) {
    //    return super.skipAttackInteraction(pEntity);
    //}
    //@Override
    //public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
    //    //if (pPlayer.isSecondaryUseActive()) {
    //    //    return InteractionResult.PASS;
    //    //} else if (this.outOfControlTicks < 60.0F) {
    //    //    if (!this.level().isClientSide) {
    //    //        return pPlayer.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
    //    //    } else {
    //    System.out.println(1);
    //    return InteractionResult.SUCCESS;
    //    //    }
    //    //} else {
    //    //    return InteractionResult.PASS;
    //    //}
    //}

    @Override
    public boolean isInvulnerable() {
        return super.isInvulnerable();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return super.isInvulnerableTo(pSource);
    }

    public LivingEntity getOwner(){
        if(owner==null&&!level().isClientSide){
            owner = (LivingEntity) ((ServerLevel)level()).getEntity(owner_UUID);
            return owner;
        }else {
            return owner;
        }
    }

    public void setOwner(LivingEntity living){
        owner = living;
        owner_UUID = living.getUUID();
    }
}
