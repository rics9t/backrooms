package com.sp.compat.modmenu;

import eu.midnightdust.lib.config.MidnightConfig;

public class ConfigStuff extends MidnightConfig {
    public static final String VIDEO = "video";
    public static final String SHADERS = "shaders";
    public static final String SERVER = "server";

    @Entry(category = VIDEO)
    public static boolean enableRealCamera = true;

    @Entry(category = VIDEO, isSlider = true, min = 0, max = 10)
    public static float lookRollMultiplier = 7.0f;

    @Entry(category = VIDEO, isSlider = true, min = 0, max = 10)
    public static float strafeRollMultiplier = 7.0f;

    @Entry(category = VIDEO)
    public static boolean enableSmoothCamera = true;

    @Entry(category = VIDEO, isSlider = true, min = 0, max = 1)
    public static float cameraSmoothing = 0.7f;

    @Comment(category = VIDEO)
    public static Comment spacer1;

    @Entry(category = VIDEO)
    public static boolean showHands = false;

    @Entry(category = VIDEO)
    public static boolean useDefaultGUI = false;

    @Entry(category = VIDEO)
    public static boolean enableHint = true;

    @Comment(category = VIDEO)
    public static Comment spacer2;

    @Comment(category = VIDEO)
    public static Comment VhsAspectRatioComment;

    @Entry(category = VIDEO)
    public static boolean enableVHSAspectRatio = false;

    @Entry(category = SHADERS)
    public static boolean enableVhsEffect = true;

    @Entry(category = SHADERS)
    public static boolean enableVhsEffectInTheBackrooms = true;

    @Entry(category = SHADERS, isSlider = true, min = 0, max = 1)
    public static float VHSDistortionMultiplier = 1.0f;

    @Entry(category = SHADERS, min = 10)
    public static int lightRenderDistance = 100;

    public static int getLightRenderDistance() {
        return lightRenderDistance;
    }

    @Comment(category = SHADERS)
    public static Comment spacer5;

    // Removed GrassQuality and BirdQuality entries due to missing classes

    @Entry(category = SERVER, min = 200, max = 1000)
    public static int exitSpawnRadius = 300;
}