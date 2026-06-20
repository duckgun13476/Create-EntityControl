package com.simibubi.create.content.contraptions;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class OrientedContraptionEntity extends AbstractContraptionEntity {
    protected OrientedContraptionEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
}
