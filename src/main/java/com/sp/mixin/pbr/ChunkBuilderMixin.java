package com.sp.mixin.pbr;

import com.llamalad7.mixinextras.sugar.Local;
// Removed VertexFormats and RenderLayers imports
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkBuilder.BuiltChunk.RebuildTask.class)
public class ChunkBuilderMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/chunk/ChunkBuilder$BuiltChunk;beginBufferBuilding(Lnet/minecraft/client/render/BufferBuilder;)V"))
    private void beginPBR(ChunkBuilder.BuiltChunk instance, BufferBuilder buffer, @Local RenderLayer renderLayer){
        // Removed check for RenderLayers.getPbrLayer()
        buffer.begin(VertexFormat.DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
    }

}