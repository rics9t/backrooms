package com.sp.mixin.renderlayer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
// Removed RenderLayers import
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderLayer.class)
public class RenderLayerMixin {

    @ModifyReturnValue(method = "getBlockLayers", at = @At("RETURN"))
    private static List<RenderLayer> addRenderLayer(List<RenderLayer> original){
        // Simply return original list since custom layers are removed
        return original;
    }

}