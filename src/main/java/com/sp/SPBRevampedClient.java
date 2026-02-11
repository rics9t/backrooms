package com.sp;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.cca_stuff.WorldEvents;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.entity.client.model.SmilerModel;
import com.sp.entity.client.renderer.SkinWalkerRenderer;
import com.sp.entity.client.renderer.SmilerRenderer;
import com.sp.entity.client.renderer.WalkerRenderer;
import com.sp.init.*;
import com.sp.render.PoolroomsDayCycle;
import com.sp.networking.InitializePackets;
import com.sp.networking.callbacks.ClientConnectionEvents;
import com.sp.render.camera.CameraShake;
import com.sp.render.camera.CutsceneManager;
import com.sp.render.gui.StaminaBar;
import com.sp.render.gui.TitleText;
import com.sp.util.MathStuff;
import com.sp.util.TickTimer;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import com.sp.world.levels.custom.PoolroomsBackroomsLevel;
import de.maxhenkel.voicechat.voice.client.ClientManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.Vector;

public class SPBRevampedClient implements ClientModInitializer {
    private static final CutsceneManager cutsceneManager = new CutsceneManager();
    private static final CameraShake cameraShake = new CameraShake();

    static boolean inBackrooms = false;
    public static Camera camera;
    public static Vector3f cameraBobOffset;

    public static TickTimer tickTimer = new TickTimer();
    public static boolean blackScreen;
    public static boolean youCantEscape;

    private static boolean shouldBeUnmuted = false;

    private static final Random random = Random.create();
    private static final Random random2 = Random.create(34563264);

    public static boolean shouldRenderWarp = false;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new TitleText());
        HudRenderCallback.EVENT.register(new StaminaBar());

        InitializePackets.registerS2CPackets();

        ModKeyBinds.initializeKeyBinds();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POOLROOMS_SKY_BLOCK, RenderLayer.getCutout()); // Reverted to cutout

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BOTTOM_TRIM, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_TEXT_1, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_TEXT_2, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_TEXT_3, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_TEXT_4, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_TEXT_5, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_TEXT_6, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_TEXT_7, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_TEXT_8, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_TEXT_99, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_ARROW_1, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_ARROW_2, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_ARROW_3, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_ARROW_4, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_SMALL_1, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_SMALL_2, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_DRAWING_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_DRAWING_WINDOW, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RUG_1, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RUG_2, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RED_METAL_CASING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WINDOW, RenderLayer.getTranslucent());

        // Removed BlockEntityRenderer factories for lights as they relied on shaders

        EntityRendererRegistry.register(ModEntities.SKIN_WALKER_ENTITY, SkinWalkerRenderer::new);
        EntityRendererRegistry.register(ModEntities.WALKER_ENTITY, WalkerRenderer::new);
        EntityRendererRegistry.register(ModEntities.SMILER_ENTITY, SmilerRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SMILER, SmilerModel::getTexturedModelData);

        ClientPlayConnectionEvents.JOIN.register(((handler,sender, client) -> {
            if(client.world != null){
                HelpfulHintManager.sendMessages(client.player);

                BackroomsLevels.getLevel(client.world).ifPresent((backroomsLevel -> {
                    if (backroomsLevel instanceof PoolroomsBackroomsLevel poolroomsBackroomsLevel) {
                        PoolroomsDayCycle.dayTime = poolroomsBackroomsLevel.getTimeOfDay();
                    }
                }));
            }
        }));

        ClientConnectionEvents.DISCONNECT.register(client -> {
            PlayerEntity player = client.player;
            if (player != null) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                playerComponent.setFlashLightOn(false);
                playerComponent.setDoingCutscene(false);
            }
            cutsceneManager.reset();
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            cutsceneManager.reset();
        });


        ClientTickEvents.END_WORLD_TICK.register((client) ->{
            Vector<TickTimer> tickTimers = TickTimer.getAllInstances();
            if(!tickTimers.isEmpty()){
                for(TickTimer timer : tickTimers){
                    timer.addCurrentTick();
                }
            }

            MinecraftClient client1 = MinecraftClient.getInstance();
            PlayerEntity player = client1.player;
            if(player != null) {
                if (player != client1.getCameraEntity() && client1.getCameraEntity() != null) {
                    Vec3d pos = client1.getCameraEntity().getPos();
                    player.setPosition(pos);
                }
                
                // Logic for seeing the Skinwalker (formerly in Veil render stage)
                World world = client1.world;
                if(world != null) {
                    PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                    WorldEvents events = InitializeComponents.EVENTS.get(world);
                    Entity activeSkinwalker = events.getActiveSkinwalkerTarget();

                    if (activeSkinwalker != null) {
                        if (player.canSee(activeSkinwalker)) {
                            if (!playerComponent.canSeeActiveSkinWalkerTarget()) {
                                playerComponent.setCanSeeActiveSkinWalkerTarget(true);
                                PacketByteBuf buffer = PacketByteBufs.create();
                                buffer.writeBoolean(true);
                                ClientPlayNetworking.send(InitializePackets.SEE_SKINWALKER_SYNC, buffer);
                            }
                        } else {
                            if (playerComponent.canSeeActiveSkinWalkerTarget()) {
                                playerComponent.setCanSeeActiveSkinWalkerTarget(false);
                                PacketByteBuf buffer = PacketByteBufs.create();
                                buffer.writeBoolean(false);
                                ClientPlayNetworking.send(InitializePackets.SEE_SKINWALKER_SYNC, buffer);
                            }
                        }
                    }
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) ->{
            if(cutsceneManager.isPlaying) {
                if(!ClientManager.getPlayerStateManager().isMuted()) {
                    shouldBeUnmuted = true;
                    ClientManager.getPlayerStateManager().setMuted(true);
                }
            } else if(shouldBeUnmuted) {
                ClientManager.getPlayerStateManager().setMuted(false);
                shouldBeUnmuted = false;
            }

            PlayerEntity playerClient = client.player;
            if(playerClient != null){
                setInBackrooms(BackroomsLevels.isInBackrooms(playerClient.getWorld().getRegistryKey()));
                
                if(shouldRenderCameraEffect() && isInBackrooms()) {
                     HelpfulHintManager.disableSuffocateHint();
                }
            }
        });

    }

    public static float getWarpTimer(World world) {
        if (!(BackroomsLevels.getLevel(world).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL) instanceof Level2BackroomsLevel level)) {
            return 0;
        }

        if (level.isWarping() || tickTimer.getCurrentTick() != 0) {
            tickTimer.setOnOrOff(true);
            float x = tickTimer.getCurrentTick();
            float w = 0.03141592f;
            float result = MathStuff.mod((x * w) * 0.002f, w);
            if (result == 0 || (!level.isWarping() && result == 0.03141592f/2)) {
                tickTimer.resetToZero();
            }
            return result;

        } else {
            tickTimer.setOnOrOff(false);
            return 0;
        }
    }

    public static boolean finishedWarp(World world) {
        float warp = getWarpTimer(world);
        return warp == 0 || warp == 0.03141592f/2;
    }

    public static void sendComponentSyncPacket(boolean writeBoolean, String component) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(writeBoolean);
        buffer.writeString(component);
        ClientPlayNetworking.send(InitializePackets.COMPONENT_SYNC, buffer);
    }

    public static boolean isInBackrooms() {
        return inBackrooms;
    }

    public static boolean shouldRenderCameraEffect() {
        return (!isInBackrooms() && ConfigStuff.enableVhsEffect) || (isInBackrooms() && ConfigStuff.enableVhsEffectInTheBackrooms);
    }

    public static void setInBackrooms(boolean inBackrooms) {
        SPBRevampedClient.inBackrooms = inBackrooms;
    }

    public static CutsceneManager getCutsceneManager() {
        return cutsceneManager;
    }

    public static CameraShake getCameraShake() {
        return cameraShake;
    }

    public static Optional<BackroomsLevel> getCurrentBackroomsLevel() {
        return BackroomsLevels.getLevel(MinecraftClient.getInstance().world);
    }

    public static boolean isInLevel(BackroomsLevel level) {
        return BackroomsLevels.isInBackroomsLevel(MinecraftClient.getInstance().world, level);
    }
}
