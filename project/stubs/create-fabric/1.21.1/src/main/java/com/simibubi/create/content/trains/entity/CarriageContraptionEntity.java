package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class CarriageContraptionEntity extends AbstractContraptionEntity {
    public int carriageIndex;

    protected CarriageContraptionEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public Carriage getCarriage() {
        return null;
    }
}
