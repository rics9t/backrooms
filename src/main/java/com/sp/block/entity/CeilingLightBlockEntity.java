package com.sp.block.entity;

import com.sp.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CeilingLightBlockEntity extends BlockEntity {
    Random random = Random.create();
    int randomInt;
    boolean on;

    public CeilingLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CEILING_LIGHT_BLOCK_ENTITY, pos, state);
        this.randomInt = random.nextBetween(1, 4);
        this.on = true;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        // Logic removed as it was purely visual light handling
    }
}
