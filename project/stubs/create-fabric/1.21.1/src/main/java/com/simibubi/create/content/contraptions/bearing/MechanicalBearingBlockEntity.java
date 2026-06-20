package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalBearingBlockEntity extends KineticBlockEntity {
    public MechanicalBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected boolean isWindmill() {
        return false;
    }

    public float getAngularSpeed() {
        return 0.0F;
    }
}
