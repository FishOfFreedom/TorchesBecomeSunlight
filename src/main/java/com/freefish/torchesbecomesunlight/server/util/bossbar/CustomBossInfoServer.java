package com.freefish.torchesbecomesunlight.server.util.bossbar;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.FreeFishEntity;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.MessageUpdateBossBar;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CustomBossInfoServer extends ServerBossEvent {
    private final FreeFishEntity entity;
    private final int renderType;

    private final Set<ServerPlayer> unseen = new HashSet<>();

    public CustomBossInfoServer(FreeFishEntity entity,int renderType) {
        super(entity.getDisplayName(), entity.bossBarColor(), BossBarOverlay.PROGRESS);
        this.setVisible(entity.hasBossBar());
        this.entity = entity;
        this.renderType = renderType;
    }

    public void update() {
        updateHealth();
        Iterator<ServerPlayer> it = this.unseen.iterator();
        while (it.hasNext()) {
            ServerPlayer player = it.next();
            if (this.entity.getSensing().hasLineOfSight(player)) {
                super.addPlayer(player);
                it.remove();
            }
        }
    }

    public void updateHealth(){
        this.setProgress(this.entity.getHealth() / this.entity.getMaxHealth());
    }

    @Override
    public void addPlayer(ServerPlayer player) {
        TorchesBecomeSunlight.NETWORK.sendTo(new MessageUpdateBossBar(this.getId(), renderType), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        if (this.entity.getSensing().hasLineOfSight(player)) {
            super.addPlayer(player);
        } else {
            this.unseen.add(player);
        }
    }

    @Override
    public void removePlayer(ServerPlayer player) {
        TorchesBecomeSunlight.NETWORK.sendTo(new MessageUpdateBossBar(this.getId(), -1), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        super.removePlayer(player);
        this.unseen.remove(player);
    }
}
