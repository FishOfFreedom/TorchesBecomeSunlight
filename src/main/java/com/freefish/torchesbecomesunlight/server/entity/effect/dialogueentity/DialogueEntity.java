package com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SetDialogueMessage;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.StartDialogueMessage;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class DialogueEntity extends Entity {
    private Entity[] chatEntities;
    private int oldOptions;
    private Entity locateEntity;

    private static final EntityDataAccessor<Integer> NUMBER = SynchedEntityData.defineId(DialogueEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_FLOAT = SynchedEntityData.defineId(DialogueEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_TALKING = SynchedEntityData.defineId(DialogueEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FLOAT_SCALE = SynchedEntityData.defineId(DialogueEntity.class, EntityDataSerializers.INT);

    private Dialogue dialogue;

    private int speakTickCount;

    private int maxSpeakTickCount;

    public DialogueEntity(EntityType<DialogueEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        maxSpeakTickCount = speakTickCount = 0;
    }

    public DialogueEntity(Level level,Dialogue dialogue){
        this(EntityHandle.DIALOGUE.get(), level);
        if(!level.isClientSide()){
            this.startSpeak(dialogue,100);
        }
    }

    public DialogueEntity(Entity locateEntity,Level level,Dialogue dialogue,Entity ... entities){
        this(EntityHandle.DIALOGUE.get(), level);
        if(!level.isClientSide()){
            this.locateEntity = locateEntity;
            setChatEntities(entities);
            this.startSpeak(dialogue,100);
            setIsTalking(true);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide){
            if(locateEntity!=null&&locateEntity.isAlive())
                absMoveTo(locateEntity.getX(), locateEntity.getY() + locateEntity.getEyeHeight(), locateEntity.getZ(), 0, 0);
        }
        if(!level().isClientSide&&tickCount==1&&getDialogue()!=null){
            TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new StartDialogueMessage(getDialogue(),this.getId(),getChatEntities()));
            String temp = getDialogue().trigger(this);
            if(temp!=null)
                TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new SetDialogueMessage(temp,this.getId()));
        }
        if(getDialogue() != null) {
            if(getSpeakTickCount() == getMaxSpeakTickCount()){
                if(getDialogue().getOptions()!=null){
                }
                else if(getDialogue().getNextDialogue() != null) {
                    setOldOptions(0);
                    startSpeak(getDialogue().getNextDialogue(), 100);
                }
                else {
                    setOldOptions(0);
                    setDialogue(null);
                }
            }
            if (getSpeakTickCount() <= getMaxSpeakTickCount()) speakTickCount++;
        }
        else {
            kill();
        }
        if(getDialogue()!=null){
            setIsFloat(getDialogue().getOptions() != null);
            int len;
            if(getDialogue().getOptions()!=null)
                len=getDialogue().getOptions().size();
            else
                len=0;
            if(getIsFloat()){
                if(getFloatScale()<len*10)
                    setFloatScale(getFloatScale()+1);
                else if(getFloatScale()>len*10)
                    setFloatScale(getFloatScale()-1);
            }
            else if(!getIsFloat()&&getFloatScale()>0){
                setFloatScale(getFloatScale()-1);
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        setNumber(pCompound.getInt("number"));
        setIsFloat(pCompound.getBoolean("isFloat"));
        setFloatScale(pCompound.getInt("floatScale"));
        setOldOptions(pCompound.getInt("oldOption"));
        setIsTalking(pCompound.getBoolean("isTaking"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("number",getNumber());
        pCompound.putBoolean("isFloat",getIsFloat());
        pCompound.putInt("floatScale",getFloatScale());
        pCompound.putInt("oldOption",getOldOptions());
        pCompound.putBoolean("isTaking",getIsTalking());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(NUMBER,0);
        this.entityData.define(IS_FLOAT,false);
        this.entityData.define(FLOAT_SCALE,0);
        this.entityData.define(IS_TALKING,false);
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return false;
    }

    public float getDialogueScale(){
        return getSpeakTickCount()/(getMaxSpeakTickCount() / 2.0f);
    }

    public void setChatEntities(Entity ... entityList){
        this.chatEntities = entityList;
    }

    public Entity[] getChatEntities(){
        return chatEntities;
    }

    public void startSpeak(Dialogue dialogue ,int time){
        setDialogue(dialogue);
        setSpeakTickCount(time);
    }

    public int getNumber(){
        return this.entityData.get(NUMBER);
    }

    public void setNumber(int number){
        this.entityData.set(NUMBER,number);
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
        return this.oldOptions;
    }

    public void setOldOptions(int oldOptions){
        this.oldOptions = oldOptions;
    }

    public void setIsTalking(boolean isTalking){
        this.entityData.set(IS_TALKING,isTalking);
    }

    public boolean getIsTalking(){
        return this.entityData.get(IS_TALKING);
    }
}
