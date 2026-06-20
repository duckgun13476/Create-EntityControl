package com.simibubi.create.content.contraptions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class ControlledContraptionEntity extends AbstractContraptionEntity {
    protected BlockPos controllerPos;

    protected ControlledContraptionEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
}
