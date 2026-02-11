package com.sp.util;

import net.minecraft.util.math.MathHelper;

public class Timer {
    private boolean started;
    private boolean reversed;
    private long startTime;
    private long duration;
    private float currentTime = 0.0f;
    private boolean done;


    public Timer(long duration){
        this.duration = duration;
    }

    public void startTimer(){
        if(!this.started) {
            this.currentTime = 0.0f;
            this.started = true;
            this.done = false;
            this.startTime = System.currentTimeMillis();
        }
    }

    public float getCurrentTime(){
        if(started){
            currentTime = MathHelper.clamp((float) (System.currentTimeMillis() - this.startTime) / duration, 0.0f, 1.0f);

            if(currentTime >= 1.0){
                this.done = true;
            }
        }

        // Returns linear progress (0.0 to 1.0). If easing is needed, apply MathHelper functions on the result.
        if(reversed){
            return 1.0f - currentTime;
        }
        return currentTime;
    }

    public void forward(){
        if(reversed) {
            this.started = false;
            this.startTimer();
            this.reversed = false;
        }
    }

    public void reverse(){
        if(!reversed) {
            this.started = false;
            this.startTimer();
            this.reversed = true;
        }
    }

    public boolean hasStarted(){
        return this.started;
    }

    public boolean isDone(){
        return this.done;
    }
}
