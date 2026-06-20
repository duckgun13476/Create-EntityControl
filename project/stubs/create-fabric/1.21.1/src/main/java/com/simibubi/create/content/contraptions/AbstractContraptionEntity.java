package com.simibubi.create.content.contraptions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractContraptionEntity extends Entity {
    protected Contraption contraption;

    protected AbstractContraptionEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public Contraption getContraption() {
        return contraption;
    }

    public void setContraptionMotion(Vec3 motion) {
    }

    public void disassemble() {
    }

    public BlockPos blockPosition() {
        return super.blockPosition();
    }
}
