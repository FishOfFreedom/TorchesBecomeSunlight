package com.freefish.torchesbecomesunlight.server.util.storage;

import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TBSWorldData extends SavedData {
    private static final String IDENTIFIER = "torchesbecomesunlight_world_data";
    private Set<Integer> trackedSnowNovaIds = new ObjectArraySet();

    private TBSWorldData() {
        super();
    }

    public static TBSWorldData get(Level world) {
        if (world instanceof ServerLevel) {
            ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);
            DimensionDataStorage storage = overworld.getDataStorage();
            TBSWorldData data = storage.computeIfAbsent(TBSWorldData::load, TBSWorldData::new, IDENTIFIER);
            if (data != null) {
                data.setDirty();
            }
            return data;
        }
        return null;
    }

    public static TBSWorldData load(CompoundTag nbt) {
        TBSWorldData data = new TBSWorldData();
        data.trackedSnowNovaIds = Arrays.stream(nbt.getIntArray("TrackedSnowNovaIds")).boxed().collect(Collectors.toSet());
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putIntArray("TrackedSnowNovaIds", trackedSnowNovaIds.stream().mapToInt(Integer::intValue).toArray());
        return compound;
    }

    public boolean isBossActive(Level level){
        for(int i : trackedSnowNovaIds){
            if(level.getEntity(i) instanceof SnowNova snowNova && snowNova.isAlive() && snowNova.isLoadedInWorld()){
                return true;
            }
        }
        return false;
    }

    public float distToBoss(Vec3 pos, Level level){
        List<SnowNova> snowNovas = new ArrayList<>();
        for(int i : trackedSnowNovaIds){
            if(level.getEntity(i) instanceof SnowNova snowNova && snowNova.isAlive() && snowNova.isLoadedInWorld()){
                snowNovas.add(snowNova);
            }
        }
        return (float) MathUtils.getClosestEntity(pos,snowNovas).distanceToSqr(pos);
    }

    public void trackPrimordialBoss(int id, boolean add){
        if(add){
            trackedSnowNovaIds.add(id);
        }else{
            trackedSnowNovaIds.remove(id);
        }
    }
}
