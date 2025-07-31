package com.freefish.torchesbecomesunlight.server.story.dialogueentity;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.event.ServerNetwork;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.StartDialogueMessage;
import com.freefish.torchesbecomesunlight.server.event.packet.toserver.SynNumberEntity;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.TriggerHandler;
import com.freefish.torchesbecomesunlight.server.story.data.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.data.DialogueEntry;
import com.freefish.torchesbecomesunlight.server.story.data.Option;
import com.freefish.torchesbecomesunlight.server.story.data.generatext.Generatext;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DialogueEntity extends Entity implements IEntityAdditionalSpawnData {
    private final Map<String, LivingEntity> stringLivingEntityMap = new HashMap<>();;

    private int oldOptionNumber;

    private int optionNumber;

    private Entity locateEntity;

    public String currentText = "";

    @Getter
    private int number;
    private int oldNumber;
    private int floatNumber;
    private int floatOldNumber;
    private int floatCount;

    private int idleTime;

    private static final EntityDataAccessor<Boolean> IS_FLOAT = SynchedEntityData.defineId(DialogueEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FLOAT_SCALE = SynchedEntityData.defineId(DialogueEntity.class, EntityDataSerializers.INT);

    @Getter
    @Setter
    private DialogueEntry dialogue;

    @Getter
    @Setter
    private Option[] currentOptions;

    @Getter
    private Dialogue allDialogue;

    @Getter
    private int speakTickCount;

    @Getter
    private int maxSpeakTickCount;

    @Getter
    private Player player;

    public DialogueEntity(EntityType<DialogueEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DialogueEntity(Entity locateEntity,Level level,Dialogue dialogue,LivingEntity ... entities){
        this(EntityHandle.DIALOGUE.get(), level);

        initDialogue(locateEntity,dialogue,entities);
        DialogueEntry dialogueEntry = getDialogueEntry(allDialogue.getStartid());
        this.startSpeak(dialogueEntry,dialogueEntry.getDialoguetime());

        boolean flad1 = true;
        for(LivingEntity living:entities){
            if(living instanceof Player player){
                this.player = player;
                if(player instanceof ServerPlayer serverPlayer){
                    TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(serverPlayer, "talk");
                }
                flad1 = false;
                break;
            }
        }
        if(flad1){
            TorchesBecomeSunlight.LOGGER.info("DialogueEntity none Player");
        }
    }

    public void initDialogue(Entity locateEntity,Dialogue dialogue,LivingEntity ... entities){
        this.locateEntity = locateEntity;
        setPos(locateEntity.position());
        String[] speakers = dialogue.getSpeakers();

        for(int i = 0;i<speakers.length;i++){
            stringLivingEntityMap.put(speakers[i],entities[i]);
        }

        allDialogue = dialogue;
    }

    public DialogueEntry getDialogueEntry(String dialogueID){
        return allDialogue.getDialogueEntry(dialogueID);
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
            if(getSpeakTickCount() == getMaxSpeakTickCount()){
                DialogueEntry dialogueEntry = getDialogueEntry(getDialogue().getNextid());

                if(hasOptions()){
                    if(dialogueEntry != null) {
                        idleTime++;
                        if (idleTime >= getMaxSpeakTickCount() / 2) {
                            idleTime = 0;
                            startSpeak(dialogueEntry, dialogueEntry.getDialoguetime());
                            dialogueEntry.trigger(this);
                        }
                    }
                }
                else if(dialogueEntry != null) {
                    startSpeak(dialogueEntry, dialogueEntry.getDialoguetime());
                    dialogueEntry.trigger(this);
                }
                else {
                    setDialogue(null);
                }
            }
        }else if(!level().isClientSide&&getDialogue()==null){
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

    public boolean hasOptions(){
        return currentOptions != null && currentOptions.length != 0;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        setNumber(pCompound.getInt("number"));
        setIsFloat(pCompound.getBoolean("isFloat"));
        setFloatScale(pCompound.getInt("floatScale"));

        Dialogue dialogue111111 = new Dialogue();
        dialogue111111.deserializeNBT(pCompound.getCompound("alldialogue"));

        UUID locateEntity1 = pCompound.getUUID("locateEntity");
        ServerLevel serverLevel = (ServerLevel) level();

        CompoundTag compound = pCompound.getCompound("chatEntities");
        LivingEntity[] livingEntities = new LivingEntity[compound.size()];
        int i = 0;
        for(String key:compound.getAllKeys()){
            livingEntities[i] = (LivingEntity) serverLevel.getEntity(compound.getUUID(key));
        }

        initDialogue(serverLevel.getEntity(locateEntity1),dialogue111111,livingEntities);
        DialogueEntry dialogueEntry = getDialogueEntry(allDialogue.getStartid());
        this.startSpeak(dialogueEntry,dialogueEntry.getDialoguetime());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("number",getNumber());
        pCompound.putBoolean("isFloat",getIsFloat());
        pCompound.putInt("floatScale",getFloatScale());

        pCompound.put("alldialogue",allDialogue.serializeNBT());
        pCompound.putUUID("locateEntity",locateEntity.getUUID());

        CompoundTag chatEntities = new CompoundTag();
        int i = 0;
        for(LivingEntity living:stringLivingEntityMap.values()){
            chatEntities.putUUID(String.valueOf(i),living.getUUID());
            i++;
        }
        pCompound.put("chatEntities",chatEntities);
    }

    @Override
    protected void defineSynchedData() {
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

    public LivingEntity getChatEntities(String id){
        return stringLivingEntityMap.get(id);
    }

    public boolean hasChatEntities(){
        return !stringLivingEntityMap.isEmpty();
    }

    public void startSpeak(DialogueEntry dialogue , int time){
        setDialogue(dialogue);
        setSpeakTickCount(time);

        if(!level().isClientSide&&dialogue != null){
            currentOptions = dialogue.getUsefulOptions(this);
            TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this),new StartDialogueMessage(this));
        }
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

    public boolean startDialogueFirst = false;

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
    }

    public void setSpeakTickCount(int speakTickCount) {
        this.speakTickCount = 0;
        this.maxSpeakTickCount = speakTickCount;
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


    @Override
    public void writeSpawnData(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(allDialogue.serializeNBT());

        CompoundTag entities = new CompoundTag();
        for(String key:stringLivingEntityMap.keySet()){
            entities.putInt(key,stringLivingEntityMap.get(key).getId());
        }
        friendlyByteBuf.writeNbt(entities);

        friendlyByteBuf.writeUtf(dialogue.getId());
        CompoundTag options = new CompoundTag();
        for(int i = 0;i<currentOptions.length;i++){
            options.putString(String.valueOf(i),currentOptions[i].getText());
        }
        friendlyByteBuf.writeNbt(options);

        String currentText111;
        if(dialogue.hasGeneratext()){
            Generatext generatext = dialogue.getGeneratext();
            currentText111 = Component.translatable(dialogue.getText(),generatext.generaText(this)).getString();
        }
        else
            currentText111 = Component.translatable(dialogue.getText()).getString();
        friendlyByteBuf.writeUtf(currentText111);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf friendlyByteBuf) {
        CompoundTag compoundTag = friendlyByteBuf.readNbt();
        Dialogue dialogue1 = new Dialogue();
        dialogue1.deserializeNBT(compoundTag);
        allDialogue = dialogue1;

        CompoundTag entities = friendlyByteBuf.readNbt();
        for(String key:entities.getAllKeys()){
            Entity entity = level().getEntity(entities.getInt(key));
            stringLivingEntityMap.put(key,(LivingEntity) entity);
        }

        String dialogueid = friendlyByteBuf.readUtf();

        CompoundTag options = friendlyByteBuf.readNbt();

        String currentText111 = friendlyByteBuf.readUtf();
        Option[] cOption = new Option[options.size()];
        int i = 0;
        for(String key:options.getAllKeys()){
            Option option = new Option();
            option.setText(options.getString(key));
            cOption[i] = option;
            i++;
        }
        DialogueEntry dialogueEntry = getDialogueEntry(dialogueid);
        setFloatScale(0);
        startSpeak(dialogueEntry,dialogueEntry.getDialoguetime());

        currentText = currentText111;

        setOldOptions(getOptions());
        resetFloatScale();

        setCurrentOptions(cOption);
        setOptions(hasOptions()?getCurrentOptions().length:0);

        if(!startDialogueFirst){
            setOldOptions(getOptions());
            startDialogueFirst = true;
        }
    }
}
