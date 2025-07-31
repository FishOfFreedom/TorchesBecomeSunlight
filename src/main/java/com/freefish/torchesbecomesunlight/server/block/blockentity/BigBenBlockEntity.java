package com.freefish.torchesbecomesunlight.server.block.blockentity;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.RendererSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.RandomConstant;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.GradientHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.Line;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.RandomLine;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Circle;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Sphere;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.rosmontislib.client.utils.Range;
import com.freefish.rosmontislib.gui.editor.ColorPattern;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.block.blockentity.sync.SynchedBlockEntityData;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightEntity;
import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.entity.dlc.PathfinderBallistarius;
import com.freefish.torchesbecomesunlight.server.init.BlockEntityHandle;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.TriggerHandler;
import com.freefish.torchesbecomesunlight.server.story.PlayerStoryStoneData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
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

public class BigBenBlockEntity extends AutoSynTagBlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<CompoundTag> TEST = SynchedBlockEntityData.defineId(BigBenBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Boolean> IS_GLOWING = SynchedBlockEntityData.defineId(BigBenBlockEntity.class, EntityDataSerializers.BOOLEAN);
    public float face;
    private int ringTime;
    public int tickcount;
    public int inTime;

    public boolean isUsed = false;

    public Player challengePlayer;
    private GunKnightEntity waitTarget;
    private ChallengeType challengeType = ChallengeType.NONE;
    private int waitTargetTime = -1;

    public List<Player> players= new ArrayList<>();

    public enum ChallengeType{
        NONE,PATH,GUN_NIGHT,FINAL
    }

    public BigBenBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityHandle.BIG_BEN.get(), pPos, pBlockState);
    }

    public boolean isChallengeType(ChallengeType type){
        return type==challengeType;
    }

    public void startChallengePlayer(GunKnightEntity gunKnightEntity,ChallengeType challengeType,Player player){
        challengePlayer = player;
        this.waitTarget = gunKnightEntity;
        this.challengeType = challengeType;
        this.waitTargetTime = 120;
    }

    public void endChallenge(){
        if(waitTarget!=null){
            waitTarget.setTarget(null);
        }
        if(challengePlayer!=null&&challengePlayer.isAlive()){
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(challengePlayer, CapabilityHandle.PLAYER_CAPABILITY);
            if(capability!=null){
                PlayerStoryStoneData playerStory = capability.getPlayerStory();
                if(challengeType==ChallengeType.PATH){
                    playerStory.setWindPathfinder(true);
                } else if(challengeType==ChallengeType.GUN_NIGHT){
                    playerStory.setWinPatriot(true);
                    if(challengePlayer instanceof ServerPlayer player){
                        TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(player, "gunPatriot_2_fight");
                    }
                } else if(challengeType==ChallengeType.FINAL){
                    playerStory.setWinPatriotFinal(true);
                    if(challengePlayer instanceof ServerPlayer player){
                        TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(player, "gunPatriot_3_fight");
                    }
                }
            }
        }

        challengeType=ChallengeType.NONE;
        challengePlayer=null;
        waitTarget=null;
    }

    private void ringFX(int lifeTime,int delay,float scale,Vec3 vec3,Level level){
        RLParticle rlParticle1 = new RLParticle(level);

        rlParticle1.config.setDuration(lifeTime);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(lifeTime));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle1.config.setStartSize(new NumberFunction3(3));

        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(1));
        rlParticle1.config.getEmission().addBursts(burst1);

        rlParticle1.config.getMaterial().setMaterial(TBSMaterialHandle.ROMA_TEXTURE_RING.create());
        rlParticle1.config.getMaterial().setCull(false);
        rlParticle1.config.getLights().open();

        rlParticle1.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
        rlParticle1.config.getShape().setShape(new Dot());
        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(new float[]{0f,6f/lifeTime,1f},new int[]{0X00FFFFFF,0XFFFFFFFF,0XFFFFFFFF})));
        rlParticle1.config.getSizeOverLifetime().open();
        rlParticle1.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0f,6f/lifeTime,(lifeTime-4f)/lifeTime,1f},new float[]{1.4f,1f,1f,0})));
        rlParticle1.config.getRotationOverLifetime().open();
        rlParticle1.config.getRotationOverLifetime().setYaw(new Line(new float[]{0f,1f},new float[]{0,360/(scale*scale)}));

        RLParticle rlParticle2 = new RLParticle(level);

        rlParticle2.config.setDuration(lifeTime);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(lifeTime));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle2.config.setStartSize(new NumberFunction3(0.05));
        rlParticle2.config.setStartRotation(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(0),new RandomConstant(360,0,true)));

        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst2 = new EmissionSetting.Burst();burst2.setCount(NumberFunction.constant(50));
        rlParticle2.config.getEmission().addBursts(burst2);

        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.GLOW.create());
        Circle circle2 = new Circle();circle2.setRadiusThickness(0.1f);circle2.setRadius(2.8f);
        rlParticle2.config.getLights().open();

        rlParticle2.config.getShape().setShape(circle2);
        rlParticle2.config.getColorOverLifetime().open();
        rlParticle2.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(new float[]{0f,6f/lifeTime,(lifeTime-4f)/lifeTime,1f},new int[]{0X00FFFFFF,0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF})));
        rlParticle2.config.getVelocityOverLifetime().open();
        rlParticle2.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(0,-1/(scale*scale),0));
        rlParticle2.config.getRotationOverLifetime().open();
        rlParticle2.config.getRotationOverLifetime().setRoll(new RandomLine(new float[]{0f,1f},new float[]{0,360},new float[]{0,0}));

        rlParticle1.config.setStartDelay(NumberFunction.constant(delay));
        rlParticle2.config.setStartDelay(NumberFunction.constant(delay));

        BlockEffect blockEffect = new BlockEffect(level, vec3);
        RandomSource random = level.random;
        float v = (random.nextFloat()-0.5f) * 100f;
        float x = (random.nextFloat()-0.5f) * 100f;
        rlParticle1.config.getRotationOverLifetime().setRoll(NumberFunction.constant (v));
        rlParticle1.config.getRotationOverLifetime().setPitch(NumberFunction.constant(x));

        rlParticle2.updateRotation(new Vector3f(v/180f*3.14f,0,x/180f*3.14f));

        rlParticle1.updateScale(new Vector3f(scale));
        rlParticle2.updateScale(new Vector3f(scale));

        rlParticle2.emmit(blockEffect);
        rlParticle1.emmit(blockEffect);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BigBenBlockEntity cookingPot) {
        cookingPot.ringTime--;
        cookingPot.tickcount++;

        if(level.isClientSide&&cookingPot.tickcount%10==0&&cookingPot.ringTime<=0){
            Boolean b = cookingPot.blockEntityData.get(IS_GLOWING);
            if(b){
                cookingPot.ringFX(20,0,2-level.random.nextFloat()*0.5f,pos.getCenter(),level);
            }
        }

        if(!level.isClientSide&&cookingPot.tickcount%4==0){
            AABB aabb = new AABB(pos.getX() - 8, pos.getY() - 8, pos.getZ() - 8, pos.getX() + 8, pos.getY() + 8, pos.getZ() + 8);

            Iterator<Player> iterator = cookingPot.players.iterator();
            while (iterator.hasNext()){
                LivingEntity player = iterator.next();
                if(!player.getBoundingBox().intersects(aabb)||!player.isAlive()){
                    iterator.remove();
                }
            }
            if(cookingPot.players.isEmpty()){
                cookingPot.blockEntityData.set(IS_GLOWING,false);
            }

            if(!cookingPot.isUsed){
                List<Player> entitiesOfClass = level.getEntitiesOfClass(Player.class, aabb);
                for (Player player : entitiesOfClass) {
                    if (!cookingPot.players.contains(player)) {
                        cookingPot.players.add(player);
                        Inventory inventory = player.getInventory();
                        boolean flad1 = false;
                        boolean flad2 = false;
                        for (ItemStack itemStack : inventory.items) {
                            if (itemStack.is(ItemHandle.LIGHT.get())) {
                                flad1 = true;
                            }
                            if (itemStack.is(ItemHandle.TIME.get())) {
                                flad2 = true;
                            }
                        }

                        if (flad1 && flad2) {
                            cookingPot.blockEntityData.set(IS_GLOWING, true);
                        }
                    }
                }
            }
        }

        if(cookingPot.waitTargetTime>=0){
            if(cookingPot.waitTargetTime == 0){
                if(cookingPot.waitTarget!=null&&cookingPot.challengePlayer!=null){
                    cookingPot.waitTarget.setTarget(cookingPot.challengePlayer);
                }
            }
            cookingPot.waitTargetTime--;
        }

        if(cookingPot.ringTime>0) {
            int tick = 640 - cookingPot.ringTime;
            if(level.isClientSide){
                if(tick==40){
                    for(int i = 0;i<4;i++){
                        Vec3 vec3 = new Vec3(31,26,48);
                        Vec3 vec31 = new Vec3(-31,26,48);
                        vec3 = vec3.yRot((float) (i / 2.f * Math.PI)).add(pos.getCenter());
                        vec31 = vec31.yRot((float) (i / 2.f * Math.PI)).add(pos.getCenter());
                        cookingPot.ros_gravatationFX(80,vec3,new Vector3f(1.6f));
                        cookingPot.ros_gravatationFX(80,vec31,new Vector3f(1.6f));
                    }
                } else if (tick==120) {
                    for(int i = 0;i<4;i++){
                        Vec3 vec3 = new Vec3(31,26,48);
                        Vec3 vec31 = new Vec3(-31,26,48);
                        vec3 = vec3.yRot((float) (i / 2.f * Math.PI)).add(pos.getCenter());
                        vec31 = vec31.yRot((float) (i / 2.f * Math.PI)).add(pos.getCenter());
                        cookingPot.light2(520,vec3,new Vector3f(3.5f));
                        cookingPot.light2(520,vec31,new Vector3f(3.5f));
                    }
                } else if (tick==200) {
                    for(int i = 0;i<4;i++){
                        Vec3 vec3 = new Vec3(24.5,29,24.5);
                        vec3 = vec3.yRot((float) (i / 2.f * Math.PI)).add(pos.getCenter());
                        cookingPot.light2(520,vec3,new Vector3f(3.5f));
                    }
                }else if (tick==280) {
                    for(int i = 0;i<4;i++){
                        Vec3 vec3 = new Vec3(24.5,29,24.5);
                        vec3 = vec3.yRot((float) (i / 2.f * Math.PI)).add(pos.getCenter());
                        cookingPot.light2(520,vec3,new Vector3f(3.5f));
                    }
                }else if (tick==360) {
                    cookingPot.summonGunFX();
                }else if (tick==599) {
                    cookingPot.summonGunFX1();
                }
            }else {
                if (tick==600) {
                    cookingPot.summonGun();
                }
            }
        }
        if(level instanceof ServerLevel serverLevel){
            onServerTick(serverLevel,pos,cookingPot);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putBoolean("isUsed",isUsed);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        isUsed = pTag.getBoolean("isUsed");
    }

    public void hitBen(float face, int ringTime){
        if(this.ringTime>0||isUsed) return;

        if(level.isClientSide){
            RLParticle rlParticle  = new RLParticle(level);

            rlParticle.config.setStartSize(new NumberFunction3(60.5));
            rlParticle.config.setStartLifetime(NumberFunction.constant(600));

            rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
            rlParticle.config.getShape().setShape(new Dot());
            rlParticle.config.setStartColor(new Gradient(new GradientColor(ColorPattern.T_YELLOW.color)));
            rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);

            rlParticle.config.getMaterial().setMaterial(TBSMaterialHandle.GUN_FADE_RING.create());
            //rlParticle.config.getRenderer().setBloomEffect(true);
            EmissionSetting.Burst burst = new EmissionSetting.Burst();burst.setCount(NumberFunction.constant(1));
            rlParticle.config.getEmission().addBursts(burst);
            rlParticle.config.getLights().open();

            BlockEffect blockEffect = new BlockEffect(level, getBlockPos().getCenter().add(0,-4.4,0));
            rlParticle.emmit(blockEffect);
        }

        this.face = face;
        this.ringTime = ringTime;

        if(!level.isClientSide){
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putFloat("face",face);
            compoundTag.putFloat("ringTime",ringTime);
            this.blockEntityData.set(TEST,compoundTag);
        }
    }

    private void summonGun(){
        isUsed = true;
        this.blockEntityData.set(IS_GLOWING,false);
        for(int i = 0;i<4;i++){
            Vec3 vec3 = new Vec3(32,-3,32);
            vec3 = vec3.yRot((float) (i / 2.f * Math.PI)).add(getBlockPos().getCenter());
            PathfinderBallistarius pathfinderBallistarius = new PathfinderBallistarius(EntityHandle.PATHFINDER_BALL.get(), level);
            pathfinderBallistarius.setPos(vec3);
            pathfinderBallistarius.init(getBlockPos(),new BlockPos((int)vec3.x,(int)vec3.y,(int)vec3.z));
            level.addFreshEntity(pathfinderBallistarius);
        }
        Vec3 vec3 = new Vec3(0,-3,16);
        vec3 = vec3.add(getBlockPos().getCenter());
        GunKnightPatriot pathfinderBallistarius = new GunKnightPatriot(EntityHandle.GUN_KNIGHT_PATRIOT.get(), level);
        pathfinderBallistarius.setPos(vec3);
        pathfinderBallistarius.init(getBlockPos(),new BlockPos((int)vec3.x,(int)vec3.y,(int)vec3.z));
        level.addFreshEntity(pathfinderBallistarius);
    }

    private void summonGunFX(){
        for(int i = 0;i<4;i++){
            Vec3 vec3 = new Vec3(32,-3,32);
            vec3 = vec3.yRot((float) (i / 2.f * Math.PI)).add(getBlockPos().getCenter());

            for(int i1=1;i1<10;i1++){
                ringFX(240-i1*10,i1*10,1+i1/10f, vec3, level);
            }
        }
        Vec3 vec3 = new Vec3(0,-3,16);
        vec3 = vec3.add(getBlockPos().getCenter());

        for(int i=1;i<10;i++){
            ringFX(240-i*10,i*10,1+i/10f, vec3, level);
        }
    }

    private void summonGunFX1(){
        for(int i = 0;i<4;i++){
            Vec3 vec3 = new Vec3(32,-3,32);
            vec3 = vec3.yRot((float) (i / 2.f * Math.PI)).add(getBlockPos().getCenter());

            RLParticle rlParticle = new RLParticle(level);
            rlParticle.config.setStartSpeed(new RandomConstant(6,1,true));
            rlParticle.config.getShape().setShape(new Sphere());
            rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
            rlParticle.config.getEmission().addBursts(new EmissionSetting.Burst());
            rlParticle.config.getPhysics().open();
            rlParticle.config.getPhysics().setHasCollision(false);
            rlParticle.config.getPhysics().setFriction(NumberFunction.constant(0.95));

            BlockEffect blockEffect = new BlockEffect(level, vec3);
            rlParticle.emmit(blockEffect);
        }
        Vec3 vec3 = new Vec3(0,-3,16);
        vec3 = vec3.add(getBlockPos().getCenter());

        RLParticle rlParticle = new RLParticle(level);
        rlParticle.config.setStartSpeed(new RandomConstant(6,1,true));
        rlParticle.config.getShape().setShape(new Sphere());
        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        rlParticle.config.getEmission().addBursts(new EmissionSetting.Burst());
        rlParticle.config.getPhysics().open();
        rlParticle.config.getPhysics().setHasCollision(false);
        rlParticle.config.getPhysics().setFriction(NumberFunction.constant(0.95));

        BlockEffect blockEffect = new BlockEffect(level, vec3);
        rlParticle.emmit(blockEffect);
    }

    public void ros_gravatationFX(int time, Vec3 pos, Vector3f scale){
        Gradient toAlpha = new Gradient(new GradientColor(0XFFFFFFFF, 0XFFFFFFFF, 0X00FFFFFF));

        RLParticle rlParticle = new RLParticle(level);
        rlParticle.config.setDuration(time);
        rlParticle.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle.config.setStartSize(new NumberFunction3(0.5,6,0.5));
        rlParticle.config.setStartColor(new Gradient(new GradientColor(0X7FFCFF9B)));
        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
        rlParticle.config.setStartSpeed(NumberFunction.constant(0));
        EmissionSetting.Burst burst = new EmissionSetting.Burst();burst.time=time-1;burst.setCount(NumberFunction.constant(8));
        EmissionSetting.Burst burstEnd = new EmissionSetting.Burst();burstEnd.setCount(NumberFunction.constant(8));
        rlParticle.config.getEmission().addBursts(burst);rlParticle.config.getEmission().addBursts(burstEnd);
        Circle circle = new Circle();circle.setRadius(2);
        rlParticle.config.getShape().setShape(circle);
        rlParticle.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
        rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.VerticalBillboard);
        rlParticle.config.getLights().open();
        rlParticle.config.getColorOverLifetime().open();
        rlParticle.config.getColorOverLifetime().setColor(toAlpha);
        rlParticle.config.getSizeOverLifetime().open();
        rlParticle.config.getSizeOverLifetime().setSize(new NumberFunction3(NumberFunction.constant(1),new Line(new float[]{0,0.3f,1},new float[]{0,1,1}),NumberFunction.constant(1)));

        RLParticle rlParticle1 = new RLParticle(level);
        rlParticle1.config.setDuration(time);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle1.config.setStartSize(new NumberFunction3(0.1,6,0.1));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0X8FFCFF9B)));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.time=time-1;burst1.setCount(NumberFunction.constant(8));
        EmissionSetting.Burst burstEnd1 = new EmissionSetting.Burst();burstEnd1.setCount(NumberFunction.constant(8));
        rlParticle1.config.getEmission().addBursts(burst1);rlParticle1.config.getEmission().addBursts(burstEnd1);
        Circle circle1 = new Circle();circle1.setRadius(2);
        rlParticle1.config.getShape().setShape(circle1);
        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
        rlParticle1.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.VerticalBillboard);
        rlParticle1.config.getRenderer().setBloomEffect(true);
        rlParticle1.config.getLights().open();
        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(toAlpha);
        rlParticle1.config.getSizeOverLifetime().open();
        rlParticle1.config.getSizeOverLifetime().setSize(new NumberFunction3(NumberFunction.constant(1),new Line(new float[]{0,0.3f,1},new float[]{0,1,1}),NumberFunction.constant(1)));

        RLParticle rlParticle2 = new RLParticle(level);
        rlParticle2.config.setDuration(time);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(40));
        rlParticle2.config.setStartSize(new NumberFunction3(0.1));
        rlParticle2.config.setStartColor(new Gradient(new GradientColor(0XFFFCFF9B)));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
        EmissionSetting.Burst burstEnd2 = new EmissionSetting.Burst();burstEnd2.setCount(NumberFunction.constant(20));
        rlParticle2.config.getEmission().addBursts(burst);rlParticle2.config.getEmission().addBursts(burstEnd2);
        Circle circle2 = new Circle();circle2.setRadius(2);
        rlParticle2.config.getShape().setShape(circle2);
        rlParticle2.config.getMaterial().setMaterial(TBSMaterialHandle.PIXEL.create());
        rlParticle2.config.getLights().open();
        rlParticle2.config.getColorOverLifetime().open();
        rlParticle2.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle2.config.getVelocityOverLifetime().open();
        rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomLine(new float[]{0,1},new float[]{2,0},new float[]{0.15f,0.15f}),NumberFunction.constant(0)));
        rlParticle2.config.getRotationOverLifetime().open();
        rlParticle2.config.getRotationOverLifetime().setYaw(new RandomLine(new float[]{0,1},new float[]{0,360},new float[]{0,60}));
        rlParticle2.config.getNoise().open();
        rlParticle2.config.getNoise().setPosition(new NumberFunction3(0.05));

        RLParticle rlParticle3 = new RLParticle(level);
        rlParticle3.config.setDuration(time);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle3.config.setStartColor(new Gradient(new GradientColor(0X6FFCFF9B)));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
        EmissionSetting.Burst burstEnd3 = new EmissionSetting.Burst();burstEnd3.setCount(NumberFunction.constant(20));
        rlParticle3.config.getEmission().addBursts(burst);rlParticle3.config.getEmission().addBursts(burstEnd3);
        Circle circle3 = new Circle();circle3.setRadius(2);
        rlParticle3.config.getShape().setShape(circle3);
        rlParticle3.config.getMaterial().setMaterial(MaterialHandle.VOID.create());
        rlParticle3.config.getLights().open();
        rlParticle3.config.getColorOverLifetime().open();
        rlParticle3.config.getColorOverLifetime().setColor(toAlpha);
        rlParticle3.config.getVelocityOverLifetime().open();
        rlParticle3.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(5,3,true),NumberFunction.constant(0)));
        rlParticle3.config.getSizeOverLifetime().open();
        rlParticle3.config.getSizeOverLifetime().setSize(new NumberFunction3(NumberFunction.constant(1),new Line(new float[]{0,0.3f,1},new float[]{0,1,1}),NumberFunction.constant(1)));
        rlParticle3.config.getNoise().open();
        rlParticle3.config.getNoise().setPosition(new NumberFunction3(0.05));
        rlParticle3.config.trails.open();
        rlParticle3.config.trails.config.getLights().open();

        BlockEffect blockEffect = new BlockEffect(level, pos);
        rlParticle.updateScale(scale);
        rlParticle1.updateScale(scale);
        rlParticle2.updateScale(scale);
        rlParticle3.updateScale(scale);

        rlParticle.emmit(blockEffect);
        rlParticle1.emmit(blockEffect);
        rlParticle2.emmit(blockEffect);
        rlParticle3.emmit(blockEffect);
    }

    public void light2(int time, Vec3 pos, Vector3f scale){
        Gradient toAlpha = new Gradient(new GradientColor(0XFFFFFFFF, 0XFFFFFFFF, 0X00FFFFFF));

        RLParticle rlParticle = new RLParticle(level);
        rlParticle.config.setDuration(100);
        rlParticle.config.setStartLifetime(NumberFunction.constant(time));
        rlParticle.config.setStartSize(new NumberFunction3(4,0.2,4));
        rlParticle.config.setStartColor(new Gradient(new GradientColor(0XFFFCFF9B)));
        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        rlParticle.config.setStartSpeed(NumberFunction.constant(0));
        EmissionSetting.Burst burst = new EmissionSetting.Burst();burst.setCount(NumberFunction.constant(1));
        rlParticle.config.getEmission().addBursts(burst);
        rlParticle.config.getShape().setShape(new Dot());
        rlParticle.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
        rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.VerticalBillboard);
        rlParticle.config.getRenderer().setBloomEffect(true);
        rlParticle.config.getLights().open();
        rlParticle.config.getColorOverLifetime().open();
        rlParticle.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

        RLParticle rlParticle1 = new RLParticle(level);
        rlParticle1.config.setDuration(100);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(time));
        rlParticle1.config.setStartSize(new NumberFunction3(0.4,7,0.4));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XFFFCFF9B)));
        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(1));
        rlParticle1.config.getEmission().addBursts(burst1);
        rlParticle1.config.getShape().setShape(new Dot());
        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
        rlParticle1.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.VerticalBillboard);
        rlParticle1.config.getRenderer().setBloomEffect(true);
        rlParticle1.config.getLights().open();
        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

        RLParticle rlParticle2 = new RLParticle(level);
        rlParticle2.config.setDuration(100);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(time));
        rlParticle2.config.setStartSize(new NumberFunction3(1.3));
        rlParticle2.config.setStartColor(new Gradient(new GradientColor(0XFFFCFF9B)));
        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(0));
        EmissionSetting.Burst burst2 = new EmissionSetting.Burst();burst2.setCount(NumberFunction.constant(1));
        rlParticle2.config.getEmission().addBursts(burst2);
        rlParticle2.config.getShape().setShape(new Dot());
        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
        rlParticle2.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.VerticalBillboard);
        rlParticle2.config.getRenderer().setBloomEffect(true);
        rlParticle2.config.getLights().open();
        rlParticle2.config.getColorOverLifetime().open();
        rlParticle2.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

        RLParticle rlParticle3 = new RLParticle(level);
        rlParticle3.config.setDuration(time-20);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(30));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle3.config.setStartSize(new NumberFunction3(1.1));
        rlParticle3.config.setStartColor(new Gradient(new GradientColor(0X8FFFFFFF)));
        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0.1));
        rlParticle3.config.getShape().setShape(new Dot());
        rlParticle3.config.getMaterial().setMaterial(TBSMaterialHandle.ROS_RING.create());
        rlParticle3.config.getLights().open();
        rlParticle3.config.getColorOverLifetime().open();
        rlParticle3.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle3.config.getSizeOverLifetime().open();
        rlParticle3.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,1},new float[]{0,1})));

        RLParticle rlParticle4 = new RLParticle(level);
        rlParticle4.config.setDuration(time-20);
        rlParticle4.config.setStartLifetime(NumberFunction.constant(10));
        rlParticle4.config.setStartSpeed(NumberFunction.constant(1));
        rlParticle4.config.setStartSize(new NumberFunction3(0.9));
        rlParticle4.config.setStartColor(new Gradient(new GradientColor(0X7FFCFF9B)));
        rlParticle4.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
        Sphere sphere4 = new Sphere();sphere4.setRadius(0.1f);
        rlParticle4.config.getShape().setShape(sphere4);
        rlParticle4.config.getMaterial().setMaterial(MaterialHandle.SMOKE.create());
        rlParticle4.config.getColorOverLifetime().open();
        rlParticle4.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle4.config.getUvAnimation().open();
        rlParticle4.config.getUvAnimation().setTiles(new Range(2,2));

        RLParticle rlParticle5 = new RLParticle(level);
        rlParticle5.config.setDuration(time-20);
        rlParticle5.config.setStartLifetime(NumberFunction.constant(50));
        rlParticle5.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle5.config.setStartSize(new NumberFunction3(0.3));
        rlParticle5.config.setStartColor(new Gradient(new GradientColor(0XFFFCFF9B)));
        rlParticle5.config.getEmission().setEmissionRate(NumberFunction.constant(0.2));
        Sphere sphere5 = new Sphere();sphere5.setRadius(0.8f);
        rlParticle5.config.getShape().setShape(sphere5);
        rlParticle5.config.getMaterial().setMaterial(MaterialHandle.VOID.create());
        rlParticle5.config.getColorOverLifetime().open();
        rlParticle5.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle5.config.getVelocityOverLifetime().open();
        rlParticle5.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(3,4,true),NumberFunction.constant(0)));
        rlParticle5.config.trails.open();
        rlParticle5.config.trails.setColorOverLifetime(new Gradient(GradientHandle.CENTER_OPAQUE));

        BlockEffect blockEffect = new BlockEffect(level, pos);
         rlParticle.updateScale(scale);
        rlParticle1.updateScale(scale);
        rlParticle2.updateScale(scale);
        rlParticle3.updateScale(scale);
        rlParticle4.updateScale(scale);
        rlParticle5.updateScale(scale);

         rlParticle.emmit(blockEffect);
        rlParticle1.emmit(blockEffect);
        rlParticle2.emmit(blockEffect);
        rlParticle3.emmit(blockEffect);
        rlParticle4.emmit(blockEffect);
        rlParticle5.emmit(blockEffect);
    }


    @Override
    public <T> void onSyncedDataUpdated(EntityDataAccessor<T> pKey) {
        super.onSyncedDataUpdated(pKey);
        if(pKey == TEST){
            CompoundTag compoundTag = this.blockEntityData.get(TEST);
            hitBen(compoundTag.getFloat("face"),compoundTag.getInt("ringTime"));
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

    private final AnimationController<BigBenBlockEntity> animationController = new AnimationController<BigBenBlockEntity>(this, "Controller", 5, this::predicate);

    private PlayState predicate(AnimationState<BigBenBlockEntity> elevatorBlockEntityAnimationState) {
        if(ringTime>0){
            elevatorBlockEntityAnimationState.setAnimation(RawAnimation.begin().thenLoop("ring"));
        }
        else {
            elevatorBlockEntityAnimationState.setAnimation(RawAnimation.begin().thenLoop("idle"));
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
}
