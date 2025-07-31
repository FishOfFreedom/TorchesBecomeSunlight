package com.freefish.torchesbecomesunlight.server.block.blockentity;

import com.freefish.torchesbecomesunlight.server.block.ElevatorBlock;
import com.freefish.torchesbecomesunlight.server.block.blockentity.sync.SynchedBlockEntityData;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.init.BlockEntityHandle;
import com.freefish.torchesbecomesunlight.server.init.BlockHandle;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ElevatorBlockEntity extends AutoSynTagBlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> TEST = SynchedBlockEntityData.defineId(ElevatorBlockEntity.class, EntityDataSerializers.INT);

    public List<LivingEntity> players= new ArrayList<>();

    public int tickcount;
    public int upMaxTime;
    public int upDelay;
    public int upDelay1;
    public int upTime;
    public int upHeight;
    public int upHeightO;
    public float upCurrentHeight;
    public float upCurrentHeightO;
    public boolean isUp;

    public ElevatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityHandle.ELEVATOR.get(), pPos, pBlockState);
    }

    public void inter(){
        ElevatorBlock.Elevator value = getBlockState().getValue(ElevatorBlock.ELEVATOR_ENUM_PROPERTY);
        if(value== ElevatorBlock.Elevator.BOTTOM){
            this.startUp(36, 300);
        }else {
            this.startUp(-36, 300);
        }

        if(!level.isClientSide){
            interDoor(getBlockPos(),false);
        }
    }

    public void interDoor(BlockPos pos,boolean open){
        BlockEntity blockEntity = level.getBlockEntity(pos.offset(0, 0, 4));
        if(blockEntity instanceof ElevatorDoorBlockEntity entity){
            entity.inter(level,entity.getBlockPos(),entity.getBlockState(),open);
        }
        BlockEntity blockEntity1 = level.getBlockEntity(pos.offset(3, 0, 4));
        if(blockEntity1 instanceof ElevatorDoorBlockEntity entity){
            entity.inter(level,entity.getBlockPos(),entity.getBlockState(),open);
        }
    }

    public void startUp(int upHeight,int upTime){
        this.upMaxTime = upTime;
        this.upTime = 0;
        this.upHeightO = this.upHeight;
        this.upHeight = upHeight;
        this.isUp = true;
        this.upDelay = 15;
        setTest(2);
    }

    public float getUpCurrentHeight(float par){
        return Mth.lerp(par,upCurrentHeightO,upCurrentHeight);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElevatorBlockEntity cookingPot) {
        cookingPot.tickcount++;

        cookingPot.upCurrentHeightO = cookingPot.upCurrentHeight;
        if(cookingPot.isUp){
            if(cookingPot.upDelay<=0){
                if (cookingPot.upTime <= cookingPot.upMaxTime) {
                    if(cookingPot.upTime == cookingPot.upMaxTime){
                        cookingPot.upDelay1 = 15;
                        cookingPot.setTest(1);
                        if(!level.isClientSide){
                            cookingPot.interDoor(pos.above(cookingPot.upHeight),true);
                        }
                    }
                    cookingPot.upCurrentHeight = Mth.lerp(((float) cookingPot.upTime / cookingPot.upMaxTime), cookingPot.upHeightO, cookingPot.upHeight);
                    cookingPot.upTime++;
                } else {
                    if(cookingPot.upDelay1<=0){
                        cookingPot.isUp = false;
                        BlockPos above = pos.above(cookingPot.upHeight);
                        if (!level.isClientSide) {
                            ElevatorBlock.Elevator value = cookingPot.getBlockState().getValue(ElevatorBlock.ELEVATOR_ENUM_PROPERTY);
                            if(value== ElevatorBlock.Elevator.BOTTOM){
                                value = ElevatorBlock.Elevator.TOP;
                            }else {
                                value = ElevatorBlock.Elevator.BOTTOM;
                            }
                            level.setBlock(above, BlockHandle.ELEVATOR.get().defaultBlockState().setValue(ElevatorBlock.ELEVATOR_ENUM_PROPERTY,value), 3);
                            level.destroyBlock(pos, false);
                        }
                    }
                    cookingPot.upDelay1--;
                }
            }
            cookingPot.upDelay--;
        }

        List<LivingEntity> players1 = cookingPot.players;
        for(LivingEntity living : players1){
            living.setPos(living.getX(),cookingPot.getBlockPos().getY()+cookingPot.upCurrentHeight,living.getZ());
            Vec3 deltaMovement = living.getDeltaMovement();
            living.setDeltaMovement(deltaMovement.x,(cookingPot.getBlockPos().getY()+cookingPot.upCurrentHeight-living.getY()),deltaMovement.z);
        }

        if(!cookingPot.isUp()&&cookingPot.tickcount%2==0){
            AABB aabb = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 4, pos.getY() + 4, pos.getZ() + 4);

            Iterator<LivingEntity> iterator = cookingPot.players.iterator();
            while (iterator.hasNext()){
                LivingEntity player = iterator.next();
                if(!player.getBoundingBox().intersects(aabb)||!player.isAlive()){
                    iterator.remove();
                    if(player instanceof Player player1){
                        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                        if (capability != null) {
                            capability.setHookPlayer(null);
                        }
                    }
                }
            }

            List<LivingEntity> entitiesOfClass = level.getEntitiesOfClass(LivingEntity.class, aabb);
            for(LivingEntity player:entitiesOfClass){
                if(!cookingPot.players.contains(player)){
                    cookingPot.players.add(player);
                    if(player instanceof Player){
                        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                        if (capability != null) {
                            capability.setHookPlayer(new HookPlayerClick(cookingPot));
                        }
                    }
                }
            }

            if(!level.isClientSide){
                for(LivingEntity player:cookingPot.players){
                    if(player instanceof Player player1){
                        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player1, CapabilityHandle.PLAYER_CAPABILITY);
                        if(capability!=null){
                            HookPlayerClick hookPlayer = capability.getHookPlayer();
                            if(hookPlayer!=null){
                                Vec3 center = cookingPot.getBlockPos().getCenter();
                                Vec3 headRotVec = FFEntityUtils.getHeadRotVec(player, new Vec3(0, 0, 1)).subtract(player.getX(), player.getY(), player.getZ());
                                Vec3 subtract = center.subtract(player.getEyePosition());
                                double dot = subtract.dot(headRotVec);
                                if (dot > 0) {
                                    Vec3 normalize = subtract.normalize();
                                    normalize = normalize.scale(dot / subtract.length());
                                    Vec3 subtract1 = normalize.subtract(headRotVec);

                                    if (subtract1.length() < 0.1) {
                                        hookPlayer.isLocalPlayerLooked = true;
                                    } else {
                                        hookPlayer.isLocalPlayerLooked = false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(level instanceof ServerLevel serverLevel){
            onServerTick(serverLevel,pos,cookingPot);
        }
    }

    public boolean isUp(){
        return isUp&&upDelay<=0;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void markUpdated() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("update",0);
        return compoundTag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }

    @Override
    protected void defineSynchedData() {
        this.blockEntityData.define(TEST,0);
    }

    public void setTest(int test){
        this.blockEntityData.set(TEST,test);
    }

    public int getTest(){
        return this.blockEntityData.get(TEST);
    }

    private final AnimationController<ElevatorBlockEntity> animationController = new AnimationController<ElevatorBlockEntity>(this, "Controller", 5, this::predicate);

    private PlayState predicate(AnimationState<ElevatorBlockEntity> elevatorBlockEntityAnimationState) {
        int test = getTest();
        if(test==1){
            elevatorBlockEntityAnimationState.setAnimation(RawAnimation.begin().thenPlayAndHold("open"));
        }
        else if(test==2){
            elevatorBlockEntityAnimationState.setAnimation(RawAnimation.begin().thenPlayAndHold("close"));
        }

        return PlayState.CONTINUE;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(animationController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().move(new Vec3(0,upCurrentHeight,0));
    }

}
