package com.simibubi.create.content.contraptions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class StructureTransform {
    public Direction.Axis rotationAxis;
    public Rotation rotation = Rotation.NONE;

    public BlockPos apply(BlockPos pos) {
        return pos;
    }

    public BlockState apply(BlockState state) {
        return state;
    }

    public Vec3 apply(Vec3 vec) {
        return vec;
    }

    public void apply(BlockEntity blockEntity) {
    }
}
