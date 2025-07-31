package com.freefish.torchesbecomesunlight.server.block.blockentity;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.RendererSetting;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.block.GlowBlock;
import com.freefish.torchesbecomesunlight.server.block.blockentity.sync.SynchedBlockEntityData;
import com.freefish.torchesbecomesunlight.server.init.BlockEntityHandle;
import com.freefish.torchesbecomesunlight.server.init.BlockHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class StargateControlerBlockEntity extends AutoSynTagBlockEntity {
    private static final EntityDataAccessor<CompoundTag> TEST = SynchedBlockEntityData.defineId(StargateControlerBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Boolean> IS_GLOWING = SynchedBlockEntityData.defineId(StargateControlerBlockEntity.class, EntityDataSerializers.BOOLEAN);


    public StargateControlerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityHandle.STARGATE_CONTROLER.get(), pPos, pBlockState);
    }

    private boolean isOpen;
    private int tickCount;

    private static final int len = 108;
    private static final int time = (int) (108  * Math.PI);

    public void star(){
        isOpen = true;
        tickCount = 0;
        if(!level.isClientSide){
            this.blockEntityData.set(IS_GLOWING,true,true);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, StargateControlerBlockEntity cookingPot) {
        if(cookingPot.isOpen){
            if(cookingPot.tickCount<time){
                if(!level.isClientSide){
                    int i = cookingPot.tickCount*2;
                    float v = (float) i / time * 3.1415f;
                    Vec3 lenV = new Vec3(0, -len, 0).xRot(v).add(pos.getCenter());
                    //Vec3 lenV1 = new Vec3(0, -len + 1, 0).xRot(v).add(pos.getCenter());
                    BlockPos blockPos = new BlockPos((int) lenV.x, (int) lenV.y, (int) lenV.z);
                    //BlockPos blockPos1 = new BlockPos((int) lenV1.x, (int) lenV1.y, (int) lenV1.z);
                    level.setBlock(blockPos, BlockHandle.STARGATE_FLAME.get().defaultBlockState().setValue(GlowBlock.LIT, true), 3);
                    //level.setBlock(blockPos1, BlockHandle.STARGATE_FLAME.get().defaultBlockState().setValue(GlowBlock.LIT, true), 3);

                    i = cookingPot.tickCount*2 + 1;
                    v = (float) i / time * 3.1415f;
                    lenV = new Vec3(0, -len, 0).xRot(v).add(pos.getCenter());
                    //lenV1 = new Vec3(0, -len + 1, 0).xRot(v).add(pos.getCenter());
                    blockPos = new BlockPos((int) lenV.x, (int) lenV.y, (int) lenV.z);
                    //blockPos1 = new BlockPos((int) lenV1.x, (int) lenV1.y, (int) lenV1.z);
                    level.setBlock(blockPos, BlockHandle.STARGATE_FLAME.get().defaultBlockState().setValue(GlowBlock.LIT, true), 3);
                    //level.setBlock(blockPos1, BlockHandle.STARGATE_FLAME.get().defaultBlockState().setValue(GlowBlock.LIT, true), 3);
                }
                //level.setBlock( blockPos.offset(1,0,0), BlockHandle.STARGATE_FLAME.get().defaultBlockState().setValue(GlowBlock.LIT, true), 3);
                //level.setBlock(blockPos1.offset(1,0,0), BlockHandle.STARGATE_FLAME.get().defaultBlockState().setValue(GlowBlock.LIT, true), 3);
            }else {
                if(level.isClientSide){
                    RLParticle rlParticle = new RLParticle(level);
                    rlParticle.config.setStartLifetime(NumberFunction.constant(1000));
                    rlParticle.config.setStartSize(new NumberFunction3(110));
                    rlParticle.config.setStartRotation(new NumberFunction3(0,90,0));

                    rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst = new EmissionSetting.Burst();burst.setCount(NumberFunction.constant(1));
                    rlParticle.config.getEmission().addBursts(burst);

                    rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Vertical);

                    rlParticle.config.getShape().setShape(new Dot());
                    rlParticle.config.getMaterial().setCull(false);
                    rlParticle.config.getMaterial().setMaterial(TBSMaterialHandle.STARGATE_RING.create());

                    rlParticle.config.getColorOverLifetime().open();
                    rlParticle.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(new float[]{0f,20f/1000,1},new int[]{0X00FFFFFF,0XFFFFFFFF,0XFFFFFFFF})));

                    rlParticle.config.getLights().open();

                    RLParticle rlParticle1 = new RLParticle(level);
                    rlParticle1.config.setStartLifetime(NumberFunction.constant(1000));
                    rlParticle1.config.setStartSize(new NumberFunction3(110));
                    rlParticle1.config.setStartRotation(new NumberFunction3(0,90,0));

                    rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(1));
                    rlParticle1.config.getEmission().addBursts(burst1);

                    rlParticle1.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Vertical);

                    rlParticle1.config.getShape().setShape(new Dot());
                    rlParticle1.config.getMaterial().setCull(false);
                    rlParticle1.config.getMaterial().setMaterial(TBSMaterialHandle.STARGATE.create());

                    rlParticle1.config.getLights().open();

                    BlockEffect blockEffect = new BlockEffect(level, pos.getCenter());
                    BlockEffect blockEffect1 = new BlockEffect(level, pos.getCenter().add(2,0,0));
                    rlParticle.emmit(blockEffect1);
                    rlParticle1.emmit(blockEffect);

                    RLParticle rlParticle2 = new RLParticle(level);
                    rlParticle2.config.setStartLifetime(NumberFunction.constant(1000));
                    rlParticle2.config.setStartSize(new NumberFunction3(110));
                    rlParticle2.config.setStartRotation(new NumberFunction3(0,90,0));

                    rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst2 = new EmissionSetting.Burst();burst2.setCount(NumberFunction.constant(1));
                    rlParticle2.config.getEmission().addBursts(burst2);

                    rlParticle2.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Vertical);

                    rlParticle2.config.getShape().setShape(new Dot());
                    rlParticle2.config.getMaterial().setCull(false);
                    rlParticle2.config.getMaterial().setMaterial(TBSMaterialHandle.STARGATE_RING.create());

                    rlParticle2.config.getColorOverLifetime().open();
                    rlParticle2.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(new float[]{0f,20f/1000,1},new int[]{0X00FFFFFF,0XFFFFFFFF,0XFFFFFFFF})));

                    rlParticle2.config.getLights().open();
                    BlockEffect blockEffect2 = new BlockEffect(level, pos.getCenter().add(-2,0,0));
                    rlParticle2.emmit(blockEffect2);
                }
                cookingPot.isOpen = false;
            }
            cookingPot.tickCount++;
        }

        if(level instanceof ServerLevel serverLevel){
            onServerTick(serverLevel,pos,cookingPot);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
    }


    @Override
    public <T> void onSyncedDataUpdated(EntityDataAccessor<T> pKey) {
        super.onSyncedDataUpdated(pKey);
        if(pKey == IS_GLOWING&&level.isClientSide){
            star();
        }
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
        this.blockEntityData.define(TEST,new CompoundTag());
        this.blockEntityData.define(IS_GLOWING,false);
    }

    public void setTest(CompoundTag test){
        this.blockEntityData.set(TEST,test);
    }

    public CompoundTag getTest(){
        return this.blockEntityData.get(TEST);
    }
}
