package com.sp.sounds.entity;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.init.ModSounds;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

public class SkinWalkerChaseSoundInstance extends MovingSoundInstance {
    private final SkinWalkerEntity entity;
    private final SkinWalkerComponent component;
    private boolean beginFade;
    private int ticksToFade = 80;

    public SkinWalkerChaseSoundInstance(SkinWalkerEntity entity) {
        super(ModSounds.SKINWALKER_CHASE, SoundCategory.HOSTILE, SoundInstance.createRandom());
        this.entity = entity;
        this.component = InitializeComponents.SKIN_WALKER.get(entity);
        this.setPosition(entity);
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 100.0f;
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public void tick() {
        if(this.entity.isRemoved()){
            this.beginFade = true;
        } else if(!this.component.isChasing()){
            this.beginFade = true;
        }

        if(this.component.isChasing() && this.beginFade){
            this.beginFade = false;
            this.ticksToFade = 80;
        }

        if(this.beginFade){
            this.ticksToFade--;
            float t = (float) this.ticksToFade / 80;
            // Simple Sine easing replacement
            this.volume = 10 * (float) Math.sin((t * Math.PI) / 2);

            if(ticksToFade <= 0) {
                this.setDone();
            }
        }

        this.setPosition(this.entity);
    }

    private void setPosition(SkinWalkerEntity entity){
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }
}
