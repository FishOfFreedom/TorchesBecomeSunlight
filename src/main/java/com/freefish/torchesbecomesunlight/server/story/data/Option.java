package com.freefish.torchesbecomesunlight.server.story.data;

import com.freefish.rosmontislib.sync.IPersistedSerializable;
import com.freefish.rosmontislib.sync.annotation.Persisted;
import com.freefish.torchesbecomesunlight.server.story.data.canoption.CanOption;
import com.freefish.torchesbecomesunlight.server.story.data.canoption.CanOptionType;
import com.freefish.torchesbecomesunlight.server.story.data.choose.Choose;
import com.freefish.torchesbecomesunlight.server.story.data.choose.ChooseType;
import com.freefish.torchesbecomesunlight.server.story.data.trigger.Trigger;
import com.freefish.torchesbecomesunlight.server.story.data.trigger.TriggerType;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

@Getter
@Setter
public class Option implements IPersistedSerializable {
    @Persisted
    private String nextid;
    @Persisted
    private String text;

    private Trigger[] triggers;

    private CanOption canOptions;

    private Choose choose;

    public String getChooseNextId(DialogueEntity dialogueEntity){
        if(choose!=null){
            return choose.changeDialogue(dialogueEntity);
        }
        return "";
    }

    public boolean canOptions(DialogueEntity dialogueEntity){
        return canOptions == null || canOptions.canOption(dialogueEntity);
    }

    public boolean HasTrigger(){
        return triggers!=null&&triggers.length != 0;
    }

    public void trigger(DialogueEntity dialogueEntity){
        for(Trigger trigger11:triggers){
            trigger11.trigger(dialogueEntity);
        }
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
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
        if(tag.contains("canOptionstTag")){
            CompoundTag canOptionstTag = tag.getCompound("canOptionstTag");
            String type1 = canOptionstTag.getString("type");
            CanOptionType<?> type = CanOptionType.CANOPTION_TYPES.get(type1);
            CanOption trigger = type.create();
            trigger.deserializeNBT(canOptionstTag);
            canOptions = trigger;
        }
        if(tag.contains("chooseTag")){
            CompoundTag chooseTag = tag.getCompound("chooseTag");
            ChooseType<?> type = ChooseType.CHOOSE_TYPES.get(chooseTag.getString("type"));
            Choose trigger = type.create();
            trigger.deserializeNBT(chooseTag);
            choose = trigger;
        }
        IPersistedSerializable.super.deserializeNBT(tag);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = IPersistedSerializable.super.serializeNBT();
        if(triggers!=null){
            ListTag triggersTag = new ListTag();
            for(Trigger trigger: this.triggers){
                CompoundTag compoundTag = trigger.serializeNBT();
                compoundTag.putString("type",trigger.getRegisterID());
                triggersTag.add(compoundTag);
            }
            tag.put("triggersTag",triggersTag);
        }
        if(canOptions!=null){
            CompoundTag compoundTag = canOptions.serializeNBT();
            compoundTag.putString("type",canOptions.getRegisterID());
            tag.put("canOptionstTag",compoundTag);
        }
        if(choose!=null){
            CompoundTag compoundTag = choose.serializeNBT();
            compoundTag.putString("type",choose.getRegisterID());
            tag.put("chooseTag",compoundTag);
        }
        return tag;
    }

    public Option() {}
}