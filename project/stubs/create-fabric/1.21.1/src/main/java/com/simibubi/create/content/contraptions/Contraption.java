package com.simibubi.create.content.contraptions;

import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contraption {
    public BlockPos anchor;
    protected Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks = new HashMap<>();
    protected List<net.minecraft.world.phys.AABB> superglue;
    public boolean disassembled;
    protected Multimap<BlockPos, StructureTemplate.StructureBlockInfo> capturedMultiblocks;
    protected MountedStorageManager storage;

    public Map<BlockPos, StructureTemplate.StructureBlockInfo> getBlocks() {
        return blocks;
    }

    protected void addBlock(Level level, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair) {
    }

    protected boolean shouldUpdateAfterMovement(StructureTemplate.StructureBlockInfo info) {
        return false;
    }

    protected boolean customBlockPlacement(LevelAccessor world, BlockPos pos, BlockState state) {
        return false;
    }

    protected void translateMultiblockControllers(StructureTransform transform) {
    }
}
