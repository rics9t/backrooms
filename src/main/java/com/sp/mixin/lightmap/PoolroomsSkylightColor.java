package com.sp.mixin.lightmap;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.init.BackroomsLevels;
import com.sp.render.PoolroomsDayCycle;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector3f;
// Removed Vector3fc import
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightmapTextureManager.class)
public class PoolroomsSkylightColor {

    // Replaced @Redirect with @ModifyVariable to fix "Unable to locate method mapping" error.
    // This modifies the color vector immediately after it is calculated/stored in the update method.
    @ModifyVariable(method = "update", at = @At(value = "STORE"), ordinal = 0)
    private Vector3f fixWeirdBlueDarknessAndChangeSunlightColor(Vector3f original, @Local ClientWorld clientWorld) {
        if(clientWorld.getRegistryKey() == BackroomsLevels.POOLROOMS_WORLD_KEY) {
            float f = clientWorld.getSkyBrightness(1.0F);
            // Create the custom color for poolrooms
            Vector3f baseColor = new Vector3f(f);
            return baseColor.mul(PoolroomsDayCycle.getLightColor());
        }
        return original;
    }

}