package com.sp.block.entity;

import com.sp.clientWrapper.ClientWrapper;
import com.sp.init.ModBlockEntities;
import com.sp.sounds.EmergencyAlarmSoundInstance;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class EmergencyLightBlockEntity extends BlockEntity {
    public final float randomOffset;
    public boolean playingEmergencyAlarm = false;
    public EmergencyAlarmSoundInstance emergencyAlarmSoundInstance;

    public EmergencyLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EMERGENCY_LIGHT_BLOCK_ENTITY, pos, state);
        this.randomOffset = Random.create().nextFloat() * 180;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            return;
        }
        ClientWrapper.tickEmergencyLight(world, pos, state, this);
    }

    public void setEmergencyAlarm(boolean b) {
        this.playingEmergencyAlarm = b;
    }
}
