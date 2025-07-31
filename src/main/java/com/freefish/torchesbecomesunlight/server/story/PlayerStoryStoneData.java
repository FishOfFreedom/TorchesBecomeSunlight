package com.freefish.torchesbecomesunlight.server.story;

import com.freefish.rosmontislib.sync.IPersistedSerializable;
import com.freefish.rosmontislib.sync.annotation.Persisted;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerStoryStoneData implements IPersistedSerializable {
    @Persisted
    private boolean isSeenGunPatriot;
    @Persisted
    private boolean isWindPathfinder;
    @Persisted
    private boolean isWinPatriot;
    @Persisted
    private boolean isHasPatriotGun;
    @Persisted
    private boolean isWinPatriotFinal;
    //rhodes island
    @Persisted
    private boolean isSeenRosmontis;
    @Persisted
    private boolean isWinRosmontis;
    @Persisted
    private boolean canSummonRosmontis;
    //g
    @Persisted
    private boolean isSeenOPatriot;
    @Persisted
    private boolean isWinOPatriot;
    @Persisted
    private boolean canSummonOPatriot;
    @Persisted
    private boolean isSeenFrostNova;
    @Persisted
    private boolean isWinFrostNova;
    @Persisted
    private boolean canSummonFrostNova;

    public void clear(){
        isSeenGunPatriot = false;
        isWindPathfinder = false;
        isWinPatriot = false;
        isWinPatriotFinal = false;
        isSeenRosmontis = false;
        isWinRosmontis = false;
        canSummonRosmontis = false;
        isSeenOPatriot = false;
        isWinOPatriot = false;
        isSeenFrostNova = false;
        isWinFrostNova = false;
    }

    @Override
    public String toString() {
        return "isSeenGunPatriot:"+isSeenGunPatriot+
                "isWindPathfinder:"+isWindPathfinder+
        "isWinPatriot:"+isWinPatriot+
        "isWinPatriotFinal:"+isWinPatriotFinal+
        "isWinRosmontis:"+isWinRosmontis+
        "canSummonRosmontis:"+canSummonRosmontis+
        "isSeenRosmontis:"+isSeenRosmontis;
    }
}
