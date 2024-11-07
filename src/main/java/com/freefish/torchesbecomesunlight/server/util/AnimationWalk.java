package com.freefish.torchesbecomesunlight.server.util;

public class AnimationWalk {
    private int[] time;

    public AnimationWalk(int[] time, float[] speed) {
        this.time = time;
        this.speed = speed;
    }

    private float[] speed;

    public float tickWalk(int tick){
        int len = time.length;
        int record = 0;
        for(int i = 0;i<len;i++){
            if(tick<time[i]) {
                record = i - 1;
                break;
            }
        }
        return  speed[record];
    }
}
