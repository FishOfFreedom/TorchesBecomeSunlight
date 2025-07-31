package com.freefish.torchesbecomesunlight.server.story.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import static com.freefish.torchesbecomesunlight.server.story.task.TaskHandle.KILL_ENTITY_TASK;

public class KillEntityTask extends Task{
    public final List<KillEntityCounter> killEntityCounterList = new ArrayList<>();

    public KillEntityTask addList(String id,int killEntityGoal){
        killEntityCounterList.add(new KillEntityCounter(id,killEntityGoal));
        return this;
    }

    public void killEntity(String s){
        for(KillEntityCounter counter:killEntityCounterList){
            if(counter.entityId.equals(s)){
                counter.killed += 1;
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag listTag = new ListTag();
        for(KillEntityCounter killEntityCounter:killEntityCounterList){
            CompoundTag kill = new CompoundTag();
            kill.putString("entityId",killEntityCounter.entityId);
            kill.putInt("killEntityGoal",killEntityCounter.killEntityGoal);
            kill.putInt("killed",killEntityCounter.killed);
            listTag.add(kill);
        }
        compoundTag.put("killEntityCounterList",listTag);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        ListTag list = compoundTag.getList("killEntityCounterList", 10);
        killEntityCounterList.clear();
        for(int i = 0 ;i<list.size();i++){
            CompoundTag kill =(CompoundTag) list.get(i);
            KillEntityCounter killEntityCounter = new KillEntityCounter(kill.getString("entityId"), kill.getInt("killEntityGoal"));
            killEntityCounter.killed = kill.getInt("killed");
            killEntityCounterList.add(killEntityCounter);
        }
    }

    @Override
    public TaskType<?> getTaskType() {
        return KILL_ENTITY_TASK;
    }

    @Override
    public boolean isCompleted(Player player) {
        for(KillEntityCounter killEntityCounter:killEntityCounterList){
            if(killEntityCounter.killed < killEntityCounter.killEntityGoal) return false;
        }
        return true;
    }

    public class KillEntityCounter{
        public String entityId;
        public int killEntityGoal;
        public int killed;

        public KillEntityCounter(String entityId, int killEntityGoal) {
            this.entityId = entityId;
            this.killEntityGoal = killEntityGoal;
        }
    }
}
