package com.sp.render.camera;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

public class CutsceneManager {
    public boolean started;
    public boolean isPlaying;
    public boolean fall;
    private int prevLightRenderDistance;
    public boolean backroomsBySP;
    private long startTime;
    private final int duration;
    private final int duration2;
    public BlackScreen blackScreen;
    private Entity camera;
    public float cameraRotZ;
    private final MinecraftClient client;

    public CutsceneManager(){
        this.started = false;
        this.isPlaying = false;
        this.fall = false;
        this.backroomsBySP = false;
        this.camera = null;
        this.duration = 7000;
        this.duration2 = 5000;
        this.blackScreen = new BlackScreen();
        this.client = MinecraftClient.getInstance();
        this.cameraRotZ = 0;
        // Paths removed as they required Veil animation API
    }


    public void tick(){
        if(client.player != null && client.world != null) {
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(client.player);
            if(playerComponent.isDoingCutscene() && client.world.getRegistryKey() == BackroomsLevels.LEVEL0_WORLD_KEY){
                this.pause();
                this.Fall();
                this.BackroomsBySP();
            } else {
                playerComponent.setDoingCutscene(false);
            }
            this.blackScreen.tick();
        }
    }

    private void pause(){
        if(!this.backroomsBySP && !this.fall) {
            if (!this.started) {
                this.blackScreen.showBlackScreen(60, true, false);
                this.startTime = System.currentTimeMillis();
                this.started = true;
            }
            float timer = (float) (System.currentTimeMillis() - this.startTime) / 2900;
            client.options.hudHidden = true;
            if (timer >= 1.0) {
                this.fall = true;
            }
        }
    }

    private void Fall() {
        if(!this.backroomsBySP && this.fall) {
            if (!this.isPlaying) {
                this.prevLightRenderDistance = ConfigStuff.lightRenderDistance;
                ConfigStuff.lightRenderDistance = 1000;
                this.startTime = System.currentTimeMillis();
                this.isPlaying = true;
                client.getSoundManager().play(PositionedSoundInstance.master(ModSounds.FALLING, 1.0f));
            }
            float timer = (float) (System.currentTimeMillis() - this.startTime) / this.duration;
            if (this.camera == null) {
                this.initCamera();
            }
            if (timer >= 1.0) {
                this.blackScreen.showBlackScreen(50, true, false);
                this.backroomsBySP = true;
                this.startTime = System.currentTimeMillis() + 2500L;
                this.fall = false;
                camera.refreshPositionAndAngles(3, 21, 1.5, 15, (float) 90);
                ConfigStuff.lightRenderDistance = this.prevLightRenderDistance;
            } else {
                client.options.hudHidden = true;
                // Complex camera movement logic removed to avoid Veil dependency
                client.cameraEntity = camera;
            }
        }
    }

    private void BackroomsBySP(){
        if(this.backroomsBySP) {
            float timer = (float) (System.currentTimeMillis() - this.startTime) / this.duration2;

            if(timer >= 1.0){
                this.blackScreen.showBlackScreen(40, true, false);
                this.reset();
            } else {
                client.options.hudHidden = false;
                camera.refreshPositionAndAngles(3, 21, 1.5, 5, (float) 83);
                this.cameraRotZ = 100;
                client.cameraEntity = camera;
            }
        }
    }

    public void reset(){
        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(client.player);
        this.isPlaying = false;
        this.started = false;
        this.fall = false;
        this.backroomsBySP = false;
        if(this.camera != null) {
            this.camera.remove(Entity.RemovalReason.DISCARDED);
            this.camera = null;
        }
        client.cameraEntity = client.player;
        client.options.hudHidden = false;
        this.startTime = 0L;
        playerComponent.setDoingCutscene(false);

        SPBRevampedClient.sendComponentSyncPacket(playerComponent.isDoingCutscene(), "cutscene");
    }

    private void initCamera(){
        this.camera = new ItemEntity(client.world, 1.5, 300, 1.5, ItemStack.EMPTY);
        this.camera.refreshPositionAndAngles(1.5, 300, 1.5, 0, 90);
    }

    public class BlackScreen{
        public boolean isBlackScreen;
        public boolean noEscape;
        private long duration;
        private long startTime;
        private boolean shouldPauseSounds;

        public BlackScreen(){
            this.startTime = 0L;
            this.isBlackScreen = false;
            this.duration = 0;
        }

        public void showBlackScreen(int time, boolean shouldPauseSounds, boolean noEscape){
            this.duration = time * 50L;
            this.isBlackScreen = true;
            this.noEscape = noEscape;
            this.startTime = System.currentTimeMillis();
            this.shouldPauseSounds = shouldPauseSounds;
            client.options.hudHidden = true;
        }

        public void tick(){
            if(isBlackScreen){
                MinecraftClient client = MinecraftClient.getInstance();
                float timer = (float) (System.currentTimeMillis() - this.startTime) / this.duration;

                if (timer >= 1.0) {
                    SPBRevampedClient.blackScreen = false;
                    SPBRevampedClient.youCantEscape = false;
                    this.isBlackScreen = false;
                    client.options.hudHidden = false;
                    this.startTime = 0;
                    client.getSoundManager().resumeAll();
                } else {
                    if(shouldPauseSounds) {
                        client.getSoundManager().pauseAll();
                    }
                    if(noEscape){
                        SPBRevampedClient.youCantEscape = true;
                    }
                    client.options.hudHidden = true;
                    SPBRevampedClient.blackScreen = true;
                }
            }
        }
    }
}
