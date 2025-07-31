package com.freefish.torchesbecomesunlight.server.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class TeamMemberStorage <T extends Mob&ITeamMemberStorage> implements INBTSerializable<CompoundTag> {
    public final Map<UUID,T> teamMember = new HashMap<>();
    public final Mob living;

    public TeamMemberStorage(Mob living) {
        this.living = living;
    }

    public void addMember(T member){
        teamMember.put(member.getUUID(),member);
    }

    public void removeMember(T member){
        teamMember.remove(member.getUUID());
    }

    public void tick(){
        int tickCount = living.tickCount;

        if(tickCount==20){
            init();
        }
    }

    public Collection<T> getMembers(){
        return teamMember.values();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        int i = 1;
        for(UUID uuid:teamMember.keySet()){
            compoundTag.putUUID(String.valueOf(i),uuid);
            i++;
        }

        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        Set<String> allKeys = nbt.getAllKeys();
        for(String s:allKeys){
            teamMember.put(nbt.getUUID(s),null);
        }
    }

    private void init(){
        Iterator<Map.Entry<UUID, T>> iterator = teamMember.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<UUID, T> next = iterator.next();
            UUID key = next.getKey();
            Entity entity = ((ServerLevel) living.level()).getEntity(key);
            if(entity!=null){
                T entity1 = (T) entity;
                entity1.setLeader(living);
                next.setValue(entity1);
            } else {
                iterator.remove();
            }
        }
    }
}
