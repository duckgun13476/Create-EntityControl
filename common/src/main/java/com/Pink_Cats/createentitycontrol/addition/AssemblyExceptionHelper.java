package com.Pink_Cats.createentitycontrol.addition;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AssemblyException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.lang.reflect.Field;

public final class AssemblyExceptionHelper {
    private static final Logger LOGGER = LogUtils.getLogger();

    private AssemblyExceptionHelper() {
    }

    public static AssemblyException limitSpecialBlock(BlockPos pos, BlockState state, Component blockDetails) {
        AssemblyException e = new AssemblyException("unmovableBlock_entity_control_limit_special_block", pos.getX(), pos.getY(), pos.getZ(),
                blockDetails);
        try {
            Field positionField = AssemblyException.class.getDeclaredField("position");
            positionField.setAccessible(true);
            positionField.set(e, pos);
        } catch (ReflectiveOperationException ex) {
            LOGGER.warn("Failed to set assembly exception position", ex);
            return new AssemblyException("unmovableBlock_entity_control_limit_special_block", pos.getX(), pos.getY(), pos.getZ(),
                    state.getBlock().getName());
        }
        return e;
    }
}
