package com.freefish.torchesbecomesunlight.server.util.bossbar;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.FreeFishEntity;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.MessageUpdateBossBar;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;

import java.util.*;

public class CustomBossInfoServer extends ServerBossEvent {
    private final FreeFishEntity entity;
    private final int renderType;

    private final Map<ServerPlayer,Boolean> unFight = new HashMap<>();

    public CustomBossInfoServer(FreeFishEntity entity,int renderType) {
        super(entity.getDisplayName(), entity.bossBarColor(), BossBarOverlay.PROGRESS);
        this.setVisible(entity.hasBossBar());
        this.entity = entity;
        this.renderType = renderType;
    }

    public void update() {
        updateHealth();
        Iterator<ServerPlayer> it = this.unFight.keySet().iterator();
        while (it.hasNext()) {
            ServerPlayer player = it.next();
            if (this.entity.getSensing().hasLineOfSight(player)&& this.entity.getTarget() instanceof Player&&unFight.get(player)) {
                super.addPlayer(player);
                TorchesBecomeSunlight.NETWORK.sendTo(new MessageUpdateBossBar(this.getId(), renderType), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                unFight.replace(player,false);
            }
            else if(!(this.entity.getTarget()instanceof Player)&&!unFight.get(player)){
                super.removePlayer(player);
                TorchesBecomeSunlight.NETWORK.sendTo(new MessageUpdateBossBar(this.getId(), -1), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                unFight.replace(player,true);
            }
        }
    }

    public void updateHealth(){
        this.setProgress(this.entity.getHealth() / this.entity.getMaxHealth());
    }

    @Override
    public void addPlayer(ServerPlayer player) {
        this.unFight.put(player,true);
    }

    @Override
    public void removePlayer(ServerPlayer player) {
        TorchesBecomeSunlight.NETWORK.sendTo(new MessageUpdateBossBar(this.getId(), -1), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        super.removePlayer(player);
        this.unFight.remove(player);
    }
}
