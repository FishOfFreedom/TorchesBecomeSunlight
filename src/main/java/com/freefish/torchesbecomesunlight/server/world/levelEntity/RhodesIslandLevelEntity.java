package com.freefish.torchesbecomesunlight.server.world.levelEntity;

import com.freefish.rosmontislib.levelentity.LevelEntity;
import com.freefish.rosmontislib.sync.ITagSerializable;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.Rosmontis;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;

public class RhodesIslandLevelEntity extends LevelEntity {
    private static final int CHECK_INTERVAL = 10;
    private int tickCounter = 0;

    @Getter
    private final List<SpawnBuilding> BUILDINGS = new ArrayList<>();

    public RhodesIslandLevelEntity() {
        super(TBSLevelEntityHandle.RHODES_ISLAND_DATA);
    }

    public void add(BlockPos blockPos,int triger,float chance){
        BUILDINGS.add(new SpawnBuilding(blockPos, triger, chance));
    }

    @Override
    public void tick() {
        super.tick();

        if(!level.isClientSide){
            if(level.getGameTime()>120) {
                if (++tickCounter % CHECK_INTERVAL == 0) {
                    List<? extends Player> players = level.players();
                    for (SpawnBuilding building : BUILDINGS) {
                        for (Player player : players) {
                            BlockPos playerPos = player.blockPosition();
                            int playerChunkX = playerPos.getX() >> 4;
                            int playerChunkZ = playerPos.getZ() >> 4;

                            int chunkDistX = Math.abs((building.center.getX() >> 4) - playerChunkX);
                            int chunkDistZ = Math.abs((building.center.getZ() >> 4) - playerChunkZ);

                            if (chunkDistX > 4 || chunkDistZ > 4) continue;
                            building.onPlayerTick(player);
                        }
                        building.tick();
                    }
                }
            }
        }
    }

    @Override
    public boolean save(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();
        for(SpawnBuilding spawnBuilding:BUILDINGS){
            listTag.add(spawnBuilding.serializeNBT());
        }
        compoundTag.put("spawnlist",listTag);

        return super.save(compoundTag);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        ListTag list = compoundTag.getList("spawnlist", 10);
        for(int i =0;i<list.size();i++){
            CompoundTag compound = list.getCompound(i);
            SpawnBuilding spawnBuilding = new SpawnBuilding();
            spawnBuilding.deserializeNBT(compound);
            BUILDINGS.add(spawnBuilding);
        }
        super.load(compoundTag);
    }

    public class SpawnBuilding implements ITagSerializable<CompoundTag> {
        public BlockPos center;
        public int triggerDistance;
        public float spawnChance;
        public List<Player> players = new ArrayList<>();
        public boolean hasPlayer;
        public boolean isUsed;

        public void addPlayer(Player player){
            if(!players.contains(player)){
                players.add(player);
                if(!hasPlayer){
                    Iterable<Entity> all = ((ServerLevel) player.level()).getEntities().getAll();
                    boolean flad = false;
                    Rosmontis rosmontis = null;

                    for (Entity e:all){
                        if(e instanceof Rosmontis r&&r.getSpawnPos() != null){
                            flad = true;
                            rosmontis = r;
                            break;
                        }
                    }

                    if(flad){
                        TorchesBecomeSunlight.LOGGER.info("has rosmontis rhodes: " + rosmontis.blockPosition());
                    }
                    else {
                        if(player.getRandom().nextFloat()<spawnChance){
                            if(!isUsed){
                                TorchesBecomeSunlight.LOGGER.info("rosmontis rhodes spawn: " + center);
                                spawnEntity(player.level(), center);
                                isUsed = true;
                            }
                        }
                    }
                }
                hasPlayer = true;
            }
        }


        public SpawnBuilding(BlockPos center, int trigger, float chance) {
            this.center = center;
            this.triggerDistance = trigger;
            this.spawnChance = chance;
        }

        public SpawnBuilding() {
        }

        private static final Map<BlockPos, Long> cooldownMap = new HashMap<>();
        private static final int COOLDOWN = 10;

        public void onPlayerTick(Player player) {
            BlockPos playerPos = player.blockPosition();
            long currentTime = player.level().getGameTime();

            double distSq = playerPos.distSqr(center);

            if (distSq <= triggerDistance * triggerDistance) {
                Long lastSpawnTime = cooldownMap.get(center);
                if (lastSpawnTime == null || currentTime - lastSpawnTime > COOLDOWN) {
                    addPlayer(player);
                    //if (player.getRandom().nextFloat() < spawnChance) {
                    //    spawnEntity((ServerLevel) player.level(), playerPos);
                    //    cooldownMap.put(center, currentTime);
                    //}
                }
            }
        }

        public void tick(){
            Iterator<Player> iterator = players.iterator();
            while (iterator.hasNext()){
                Player next = iterator.next();
                if(!next.isAlive()||next.blockPosition().distSqr(center)> triggerDistance * triggerDistance){
                    iterator.remove();
                }
            }

            if(players.isEmpty()){
                hasPlayer = false;
            }
        }

        private void spawnEntity(Level level, BlockPos playerPos) {
            Rosmontis entity = new Rosmontis(EntityHandle.ROSMONTIS.get(), level);
            entity.init(playerPos);
            entity.setPos(playerPos.getX() + 0.5, playerPos.getY(), playerPos.getZ() + 0.5);
            level.addFreshEntity(entity);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("center", NbtUtils.writeBlockPos(center));
            compoundTag.putInt("triggerDistance", triggerDistance);
            compoundTag.putFloat("spawnChance", spawnChance);
            compoundTag.putBoolean("hasPlayer", hasPlayer);
            compoundTag.putBoolean("isUsed", isUsed);
            return compoundTag;
        }

        @Override
        public void deserializeNBT(CompoundTag compoundTag) {
            center = NbtUtils.readBlockPos(compoundTag.getCompound("center"));
            triggerDistance = compoundTag.getInt("triggerDistance");
            spawnChance     = compoundTag.getFloat("spawnChance");
            hasPlayer       = compoundTag.getBoolean("hasPlayer");
            isUsed       = compoundTag.getBoolean("isUsed");
        }
    }
}
