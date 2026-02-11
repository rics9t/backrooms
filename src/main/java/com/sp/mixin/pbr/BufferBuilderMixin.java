package com.sp.mixin.pbr;

import com.sp.mixininterfaces.BlockMaterial;
// Removed VertexFormats, BlockIdMap, PbrRegistry imports
import net.minecraft.block.Block;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements BlockMaterial {
    @Shadow public abstract void nextElement();

    @Shadow private ByteBuffer buffer;
    @Shadow private int elementOffset;

    @Shadow public abstract void putFloat(int index, float value);

    @Unique boolean isRenderingBlock;
    @Unique Block currentBlock;
    @Unique VertexFormat currentFormat;

    @Override
    public void setCurrentBlock(Block block) {
        this.currentBlock = block;
    }

    @ModifyVariable(method = "begin", at = @At("HEAD"), argsOnly = true)
    private VertexFormat setFormat(VertexFormat format) {
        this.isRenderingBlock = false;
        currentFormat = format;
        // PBR logic removed
        return format;
    }


    @Inject(method = "next", at = @At("HEAD"))
    private void putBlockID(CallbackInfo ci){
        // PBR logic removed
    }

    @Unique
    private void putInt(int offset, int value){
        this.buffer.putInt(this.elementOffset + offset, value);
    }

    @Unique
    private VertexFormat setRendering(VertexFormat format){
        this.isRenderingBlock = true;
        this.currentFormat = format;
        return format;
    }

}