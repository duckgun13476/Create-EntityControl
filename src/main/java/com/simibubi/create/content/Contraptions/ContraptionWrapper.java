package com.simibubi.create.content.Contraptions; // 目标类的包

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import info.journeymap.shaded.org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Queue;
import java.util.Set;

import net.minecraft.world.level.Level;

public class ContraptionWrapper extends Contraption {
    public ContraptionWrapper() {
        super();
    }

    @Override
    public boolean assemble(net.minecraft.world.level.Level world, BlockPos pos) throws AssemblyException {
        return false;
    }

    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        return false;
    }

    @Override
    public ContraptionType getType() {
        return null;
    }

    public boolean callMoveBlock(Level world, @Nullable Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited) throws AssemblyException {
        return moveBlock(world, forcedDirection, frontier, visited);
    }

    public boolean callAddToInitialFrontier(Level world, BlockPos pos, Direction forcedDirection, Queue<BlockPos> frontier) throws AssemblyException {
        return addToInitialFrontier(world, pos, forcedDirection, frontier);
    }

}