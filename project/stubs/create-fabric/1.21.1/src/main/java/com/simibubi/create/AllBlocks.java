package com.simibubi.create;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class AllBlocks {
    public static final BlockEntry SHAFT = new BlockEntry();

    private AllBlocks() {
    }

    public static final class BlockEntry {
        public boolean has(BlockState state) {
            return state != null && state.is(Blocks.AIR);
        }
    }
}
