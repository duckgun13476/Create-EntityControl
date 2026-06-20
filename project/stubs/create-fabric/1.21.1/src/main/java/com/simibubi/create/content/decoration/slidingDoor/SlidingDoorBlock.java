package com.simibubi.create.content.decoration.slidingDoor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SlidingDoorBlock extends Block {
    public static final BooleanProperty VISIBLE = BooleanProperty.create("visible");
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public SlidingDoorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
