package com.freefish.torchesbecomesunlight.client.sound;

import com.freefish.torchesbecomesunlight.server.entity.FreeFishEntity;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class BossMusicSound extends AbstractTickableSoundInstance {
    private FreeFishEntity boss;
    private int ticksExisted = 0;
    private int timeUntilFade;
    public int time;
    @Setter@Getter
    private boolean isCanLoop;

    private final SoundEvent soundEvent;
    ControlledAnimation volumeControl;

    public BossMusicSound(SoundEvent sound, FreeFishEntity boss) {
        super(sound, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.boss = boss;
        this.soundEvent = sound;
        this.attenuation = Attenuation.NONE;
        this.looping = true;
        this.delay = 0;
        this.x = boss.getX();
        this.y = boss.getY();
        this.z = boss.getZ();

        volumeControl = new ControlledAnimation(40);
        volumeControl.setTimer(20);
        volume = volumeControl.getAnimationFraction();
        timeUntilFade = 80;
    }

    public void reset(){
        time = 0;
    }

    public boolean canPlaySound() {
        return BossMusicPlayer.bossMusic == this;
    }

    public void tick() {
        //if(boss!=null&&isCanLoop){
        //    System.out.println(time);
        //    if(time>boss.timeToLoop()){
        //        BossMusicPlayer.playBossMusic(boss,boss.getLoopMusic(),false);
        //    }time++;
        //}
        // If the music should stop playing
        if (boss == null || !boss.isAlive() || boss.isSilent()) {
            // If the boss is dead, skip the fade timer and fade out right away
            if (boss != null && !boss.isAlive()) timeUntilFade = 0;
            boss = null;
            if (timeUntilFade > 0) timeUntilFade--;
            else volumeControl.decreaseTimer();
        }
        // If the music should keep playing
        else {
            volumeControl.increaseTimer();
            timeUntilFade = 60;
        }

        if (volumeControl.getAnimationFraction() < 0.025) {
            stop();
            BossMusicPlayer.bossMusic = null;
        }

        volume = volumeControl.getAnimationFraction();

        if (ticksExisted % 100 == 0) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
        }
        ticksExisted++;
    }

    @Override
    public boolean isLooping() {
        return super.isLooping();
    }

    public void setBoss(FreeFishEntity boss) {
        this.boss = boss;
    }

    public FreeFishEntity getBoss() {
        return boss;
    }

    public SoundEvent getSoundEvent() {
        return soundEvent;
    }
}
