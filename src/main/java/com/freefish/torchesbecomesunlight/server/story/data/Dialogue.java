package com.freefish.torchesbecomesunlight.server.story.data;

import com.freefish.rosmontislib.sync.IPersistedSerializable;
import com.freefish.rosmontislib.sync.annotation.Persisted;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Arrays;
import java.util.Optional;

@Getter
@Setter
public class Dialogue implements IPersistedSerializable{
    @Persisted
    private String id;
    @Persisted
    private String title;
    @Persisted
    private String description;
    @Persisted
    private String[] speakers;
    @Persisted
    private String startid;
    @Persisted
    private String skipid;

    private DialogueEntry[] entries;

    public DialogueEntry getDialogueEntry(String dialogueID){
        Optional<DialogueEntry> first = Arrays.stream(entries).filter((dialogueEntry -> dialogueEntry.getId().equals(dialogueID))).findFirst();
        return first.orElse(null);
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if(tag.contains("entities")){
            ListTag list = tag.getList("entities",10);
            entries = new DialogueEntry[list.size()];
            for(int i = 0 ;i<list.size();i++){
                CompoundTag entry = (CompoundTag) list.get(i);
                DialogueEntry dialogueEntry = new DialogueEntry();
                dialogueEntry.deserializeNBT(entry);
                entries[i] = dialogueEntry;
            }
        }
        IPersistedSerializable.super.deserializeNBT(tag);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = IPersistedSerializable.super.serializeNBT();
        if(entries!=null){
            ListTag entities = new ListTag();
            for(DialogueEntry dialogueEntry:entries){
                CompoundTag compoundTag = dialogueEntry.serializeNBT();
                entities.add(compoundTag);
            }
            tag.put("entities",entities);
        }
        return tag;
    }

    public Dialogue() {}
}