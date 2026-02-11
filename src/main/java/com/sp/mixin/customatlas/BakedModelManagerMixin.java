package com.sp.mixin.customatlas;

import com.sp.SPBRevamped;
// Removed RenderLayers import
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {

    @Mutable
    @Shadow
    @Final
    private static Map<Identifier, Identifier> LAYERS_TO_LOADERS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addPBRAtlas(CallbackInfo ci){
        LAYERS_TO_LOADERS = new HashMap<>(LAYERS_TO_LOADERS);
        // Removed custom atlas textures (normal/height) as RenderLayers constants are missing
        LAYERS_TO_LOADERS.put(
                SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
                new Identifier(SPBRevamped.MOD_ID, "blocks")
        );
    }

}