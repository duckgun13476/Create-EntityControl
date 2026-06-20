package com.simibubi.create.content.contraptions;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class AssemblyException extends Exception {
    private BlockPos position;

    public AssemblyException(String key, Object... args) {
        super(key);
    }

    public static AssemblyException structureTooLarge() {
        return new AssemblyException("structureTooLarge");
    }

    public static AssemblyException unmovableBlock(BlockPos pos, BlockState state) {
        AssemblyException exception = new AssemblyException("unmovableBlock", pos.getX(), pos.getY(), pos.getZ(),
                state.getBlock().getName());
        exception.position = pos;
        return exception;
    }

    public BlockPos getPosition() {
        return position;
    }

    public Component getComponent() {
        return Component.literal(getMessage());
    }
}
