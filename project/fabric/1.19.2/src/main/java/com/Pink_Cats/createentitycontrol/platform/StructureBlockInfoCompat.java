package com.Pink_Cats.createentitycontrol.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class StructureBlockInfoCompat {

    private StructureBlockInfoCompat() {}

    public static BlockPos pos(StructureTemplate.StructureBlockInfo blockInfo) {
        return blockInfo.pos;
    }

    public static BlockState state(StructureTemplate.StructureBlockInfo blockInfo) {
        return blockInfo.state;
    }
}
