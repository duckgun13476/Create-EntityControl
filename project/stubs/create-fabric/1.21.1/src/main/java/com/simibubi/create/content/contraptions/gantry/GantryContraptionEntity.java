package com.simibubi.create.content.contraptions.gantry;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class GantryContraptionEntity extends AbstractContraptionEntity {
    protected GantryContraptionEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public Vec3 getAnchorVec() {
        return Vec3.ZERO;
    }
}
