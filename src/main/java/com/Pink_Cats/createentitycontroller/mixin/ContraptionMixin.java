package com.Pink_Cats.createentitycontroller.mixin;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import info.journeymap.shaded.org.jetbrains.annotations.Nullable;
import net.createmod.catnip.data.UniqueLinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mixin(value = Contraption.class,remap = false)
public class ContraptionMixin {
    @Shadow
    protected boolean moveBlock(Level world, @Nullable Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited) {
        // 这里不需要实现方法体，因为它会被调用
        return false; // 这是一个占位符
    }
    @Shadow
    protected boolean addToInitialFrontier(Level world, BlockPos pos, Direction forcedDirection,
                                           Queue<BlockPos> frontier) {
        return true;
    }
    @Shadow  public AABB bounds;
    @Shadow  public BlockPos anchor;
    @Shadow  private Map<BlockPos, Entity> initialPassengers;

    /**
     * @author Pink_Cats
     * @reason catch_check
     */
    @Overwrite
    public boolean searchMovedStructure(Level world, BlockPos pos, @Nullable Direction forcedDirection) throws AssemblyException {
        initialPassengers.clear();
        LOGGER.error("searchMovedStructure");
        Queue<BlockPos> frontier = new UniqueLinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        anchor = pos;

        if (bounds == null)
            bounds = new AABB(BlockPos.ZERO);

        if (!BlockMovementChecks.isBrittle(world.getBlockState(pos)))
            frontier.add(pos);

        if (!addToInitialFrontier(world, pos, forcedDirection, frontier))
            return false;

        for (int limit = 100000; limit > 0; limit--) {
            if (frontier.isEmpty())
                return true;
            if (!moveBlock(world, forcedDirection, frontier, visited))
                return false;
        }

        // 处理抛出的异常
        // 您可以选择抛出新的异常，或者返回一个特定值
        throw AssemblyException.structureTooLarge(); // 根据需要处理异常
    }




}