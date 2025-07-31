package com.freefish.torchesbecomesunlight.server.story.data;


import com.freefish.rosmontislib.sync.IPersistedSerializable;
import com.freefish.rosmontislib.sync.annotation.Persisted;
import com.freefish.torchesbecomesunlight.server.story.data.generatext.Generatext;
import com.freefish.torchesbecomesunlight.server.story.data.generatext.GeneratextType;
import com.freefish.torchesbecomesunlight.server.story.data.trigger.Trigger;
import com.freefish.torchesbecomesunlight.server.story.data.trigger.TriggerType;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class DialogueEntry implements IPersistedSerializable{
    @Persisted
    private String id;
    @Persisted
    private String nextid;
    @Persisted
    private String speaker;
    @Persisted
    private String text;
    @Persisted
    private int dialoguetime;

    private Option[] options;

    private Trigger[] triggers;
    @Expose
    private Runnable runnable;

    private Generatext generatext;

    public boolean hasGeneratext(){
        return generatext != null;
    }

    public boolean hasOptions(){
        return options != null && options.length != 0;
    }

    public boolean HasTrigger(){
        return triggers!=null&&triggers.length != 0;
    }

    public Option[] getUsefulOptions(DialogueEntity dialogueEntity){
        if(options == null) return new Option[0];

        List<Option> collect = Arrays.stream(options).filter((option -> option.canOptions(dialogueEntity))).toList();
        Option[] temp = new Option[collect.size()];
        for(int i =0;i<collect.size();i++){
            temp[i] = collect.get(i);
        }
        return temp;
    }

    public void trigger(DialogueEntity dialogueEntity){
        if(triggers!=null){
            for (Trigger trigger11 : triggers) {
                trigger11.trigger(dialogueEntity);
            }
        }
        if(runnable!=null) runnable.run();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if(tag.contains("options")){
            ListTag list = tag.getList("options",10);
            options = new Option[list.size()];
            for(int i = 0 ;i<list.size();i++){
                CompoundTag entry = (CompoundTag) list.get(i);
                Option option = new Option();
                option.deserializeNBT(entry);
                options[i] = option;
            }
        }
        if(tag.contains("triggersTag")){
            ListTag list = tag.getList("triggersTag",10);
            triggers = new Trigger[list.size()];
            for(int i = 0 ;i<list.size();i++){
                CompoundTag entry = (CompoundTag) list.get(i);
                TriggerType<?> type = TriggerType.TRIGGER_TYPES.get(entry.getString("type"));
                Trigger trigger = type.create();
                trigger.deserializeNBT(entry);
                triggers[i] = trigger;
            }
        }
        if(tag.contains("generatextTag")){
            CompoundTag generatextTag = tag.getCompound("generatextTag");
            GeneratextType<?> type = GeneratextType.GENERATEXT_TYPES.get(generatextTag.getString("type"));
            Generatext trigger = type.create();
            trigger.deserializeNBT(generatextTag);
            generatext = trigger;
        }
        IPersistedSerializable.super.deserializeNBT(tag);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = IPersistedSerializable.super.serializeNBT();
        if(options!=null){
            ListTag options = new ListTag();
            for(Option option: this.options){
                CompoundTag compoundTag = option.serializeNBT();
                options.add(compoundTag);
            }
            tag.put("options",options);
        }
        if(triggers!=null){
            ListTag triggersTag = new ListTag();
            for(Trigger trigger: this.triggers){
                CompoundTag compoundTag = trigger.serializeNBT();
                compoundTag.putString("type",trigger.getRegisterID());
                triggersTag.add(compoundTag);
            }
            tag.put("triggersTag",triggersTag);
        }
        if(generatext!=null){
            CompoundTag compoundTag = generatext.serializeNBT();
            compoundTag.putString("type",generatext.getRegisterID());
            tag.put("generatextTag",compoundTag);
        }
        return tag;
    }

    public DialogueEntry() {}
}