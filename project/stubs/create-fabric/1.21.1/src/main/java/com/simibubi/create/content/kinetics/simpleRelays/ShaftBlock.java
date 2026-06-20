package com.simibubi.create.content.kinetics.simpleRelays;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ShaftBlock extends Block {
    public ShaftBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static BlockState pickCorrectShaftType(BlockState state, LevelAccessor level, BlockPos pos) {
        return state;
    }
}
