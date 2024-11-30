package com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.event.ServerNetwork;
import com.freefish.torchesbecomesunlight.server.event.packet.toserver.SynNumberEntity;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.StartDialogueMessage;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class DialogueEntity extends Entity {
    private Entity[] chatEntities;

    @OnlyIn(Dist.CLIENT)
    private int oldOptionNumber;
    @OnlyIn(Dist.CLIENT)
    private int optionNumber;

    private Entity locateEntity;
    private Dialogue endDialogue;

    private int number;
    private int oldNumber;
    private int floatNumber;
    private int floatOldNumber;
    private int floatCount;

    private int idleTime;

    private static final EntityDataAccessor<Boolean> IS_FLOAT = SynchedEntityData.defineId(DialogueEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FLOAT_SCALE = SynchedEntityData.defineId(DialogueEntity.class, EntityDataSerializers.INT);

    private Dialogue dialogue;

    private int speakTickCount;

    private int maxSpeakTickCount;

    public DialogueEntity(EntityType<DialogueEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        maxSpeakTickCount = speakTickCount = 0;
    }

    public DialogueEntity(Level level){
        this(EntityHandle.DIALOGUE.get(), level);
    }

    public DialogueEntity(Entity locateEntity,Level level,Dialogue dialogue,Entity ... entities){
        this(EntityHandle.DIALOGUE.get(), level);
        if(!level.isClientSide()){
            this.locateEntity = locateEntity;
            setChatEntities(entities);
            this.startSpeak(dialogue,dialogue.getDialogueTime());
        }
    }

    @Override
    public void tick() {
        super.tick();

        //locate
        if(!level().isClientSide){
            if(locateEntity!=null&&locateEntity.isAlive())
                absMoveTo(locateEntity.getX(), locateEntity.getY() + locateEntity.getEyeHeight(), locateEntity.getZ(), 0, 0);
        }

        //update in Server
        if(!level().isClientSide&&getDialogue()!=null){
            if(tickCount==1) {
                TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new StartDialogueMessage(getDialogue(), this, getChatEntities()));
            }

            if(getSpeakTickCount() == getMaxSpeakTickCount()){
                if(getDialogue().getOptions()!=null&&!getDialogue().getOptions().isEmpty()){
                    if(getDialogue().getNextDialogue() != null) {
                        idleTime++;
                        if (idleTime >= getMaxSpeakTickCount() / 2) {
                            idleTime = 0;
                            startSpeakInServer(getDialogue().getNextDialogue(), getDialogue().getNextDialogue().getDialogueTime());
                        }
                    }
                }
                else if(getDialogue().getNextDialogue() != null) {
                    startSpeakInServer(getDialogue().getNextDialogue(), getDialogue().getNextDialogue().getDialogueTime());
                }
                else {
                    setDialogue(null);
                }
            }
        }else if(!level().isClientSide&&getDialogue()==null){
            List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(6));
            for(LivingEntity livingEntity:list){
                if(livingEntity instanceof IDialogue iDialogue) iDialogue.setDialogueEntity(null);
            }
            kill();
        }
        //updateAll
        if (getSpeakTickCount() < getMaxSpeakTickCount()) speakTickCount++;

        //pos update
        if(getDialogue()!=null&&level().isClientSide){
            if(getFloatScale()<20){
                setFloatScale(getFloatScale()+1);
            }
            floatOldNumber = floatNumber;
            if(floatCount<6){
                int oldlen = oldNumber*10;
                int len = getNumber()*10;
                int i = Mth.lerpInt(floatCount / 5f, oldlen, len);
                floatCount++;
                floatNumber = i;
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        setNumber(pCompound.getInt("number"));
        setIsFloat(pCompound.getBoolean("isFloat"));
        setFloatScale(pCompound.getInt("floatScale"));
        //setOldOptions(pCompound.getInt("oldOption"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("number",getNumber());
        pCompound.putBoolean("isFloat",getIsFloat());
        pCompound.putInt("floatScale",getFloatScale());
        //pCompound.putInt("oldOption",getOldOptions());
    }

    @Override
    protected void defineSynchedData() {
        //this.entityData.define(NUMBER,0);
        //this.entityData.define(NUMBER_TESt,0);
        this.entityData.define(IS_FLOAT,false);
        this.entityData.define(FLOAT_SCALE,0);
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return false;
    }

    public float getDialogueScale(){
        return getSpeakTickCount()/((float)getMaxSpeakTickCount());
    }

    public void setChatEntities(Entity ... entityList){
        this.chatEntities = entityList;
    }

    public Entity[] getChatEntities(){
        return chatEntities;
    }

    public void startSpeakInServer(Dialogue dialogue ,int time){
        Entity chatEntity = chatEntities[dialogue.getSpeakerNumber()];
        if(chatEntity !=null)
            dialogue.trigger(chatEntity);
        setDialogue(dialogue);
        setSpeakTickCount(time);

        TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new StartDialogueMessage(getDialogue(), this));
    }

    public void startSpeak(Dialogue dialogue ,int time){
        setDialogue(dialogue);
        setSpeakTickCount(time);
    }

    public int getNumber(){
        return number;
    }

    public Dialogue getEndDialogue() {
        return endDialogue;
    }

    public void setEndDialogue(Dialogue endDialogue) {
        this.endDialogue = endDialogue;
    }

    public void setNumber(int number){
        if(level().isClientSide)
            ServerNetwork.toServerMessage(new SynNumberEntity(this.getId(),number));
        this.oldNumber = getNumber();
        this.number = number;
    }

    public boolean getIsFloat(){return this.entityData.get(IS_FLOAT);}

    public void setIsFloat(boolean isFloat){this.entityData.set(IS_FLOAT,isFloat);}

    public int getFloatScale(){return this.entityData.get(FLOAT_SCALE);}

    public void setFloatScale(int floatScale){this.entityData.set(FLOAT_SCALE,floatScale);}

    public Dialogue getDialogue() {
        return dialogue;
    }

    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
    }

    public int getSpeakTickCount() {
        return speakTickCount;
    }

    public void setSpeakTickCount(int speakTickCount) {
        this.speakTickCount = 0;
        this.maxSpeakTickCount = speakTickCount;
    }

    public int getMaxSpeakTickCount() {
        return maxSpeakTickCount;
    }

    public int getOldOptions(){
        return this.oldOptionNumber;
    }

    public void setOldOptions(int oldOptions){
        this.oldOptionNumber = oldOptions;
    }

    public int getOptions(){
        return this.optionNumber;
    }

    public void setOptions(int oldOptions){
        this.optionNumber = oldOptions;
    }

    public void resetFloat(){
        this.floatCount = 0;
    }
    public void resetFloatScale(){
        this.floatNumber = floatOldNumber = 0;
        this.oldNumber = getNumber();
    }

    public float getFloatOp(float p){
        return Mth.lerp(p,floatOldNumber,floatNumber)/10f;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


}
