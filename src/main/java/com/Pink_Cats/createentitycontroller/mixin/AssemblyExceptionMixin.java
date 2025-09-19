package com.Pink_Cats.createentitycontroller.mixin;

import com.Pink_Cats.createentitycontroller.Config;
import com.Pink_Cats.createentitycontroller.addition.EntityEnrollment;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;




@Mixin(value = AssemblyException.class,remap = false)
public class AssemblyExceptionMixin {

    @Unique
    private static final Logger createentitycontroller$LOGGER = LogUtils.getLogger();

    /**
     * @author Pink_Cats
     * @reason Inject custom behavior for unmovableBlock
     */
    @Inject(method = "unmovableBlock", at = @At("HEAD") )
    private static void injectUnmovableBlock(BlockPos pos, BlockState state, CallbackInfoReturnable<AssemblyException> cir) {
        //LOGGER.error("HELLO FROM MIXIN");
    }

    /**
     * @author Pink_Cats
     * @reason Inject
     */
    @Overwrite
    public static AssemblyException unmovableBlock(BlockPos pos, BlockState state) throws IllegalAccessException, NoSuchFieldException {
        if (EntityEnrollment.getControlStatus() == 8) {
            AssemblyException e = new AssemblyException("unmovableBlock_entity_control_unmovable_block", pos.getX(), pos.getY(), pos.getZ(),
                    state.getBlock().getName());
            if (Config.debug_block_entity_problem) {
                createentitycontroller$LOGGER.info("create.entity has unmovable block locate [{},{},{}]", pos.getX(), pos.getY(), pos.getZ());
            }
            Field positionField = AssemblyException.class.getDeclaredField("position");
            positionField.setAccessible(true); // 允许访问 private 字段
            positionField.set(e, pos); // 设置位置
            return e;
        } else if (EntityEnrollment.getControlStatus() == 16) {
            AssemblyException e = new AssemblyException("unmovableBlock_entity_control_limit_special_block", pos.getX(), pos.getY(), pos.getZ(),
                    state.getBlock().getName());
            if (Config.debug_block_entity_problem) {
                createentitycontroller$LOGGER.info("create.entity has reach special block limit locate [{},{},{}]", pos.getX(), pos.getY(), pos.getZ());
            }
            Field positionField = AssemblyException.class.getDeclaredField("position");
            positionField.setAccessible(true); // 允许访问 private 字段
            positionField.set(e, pos); // 设置位置
            return e;
        } else if (EntityEnrollment.getControlStatus() == 32) {
            AssemblyException e = new AssemblyException("unmovableBlock_entity_control_structure_too_long", pos.getX(), pos.getY(), pos.getZ(),
                    state.getBlock().getName());
            if (Config.debug_block_entity_problem) {
                createentitycontroller$LOGGER.info("create.entity was too long locate [{},{},{}]", pos.getX(), pos.getY(), pos.getZ());
            }
            Field positionField = AssemblyException.class.getDeclaredField("position");
            positionField.setAccessible(true); // 允许访问 private 字段
            positionField.set(e, pos); // 设置位置
            return e;
        } else {
            AssemblyException e = new AssemblyException("unmovableBlock_entity_control_unmovable_block", pos.getX(), pos.getY(), pos.getZ(),
                    state.getBlock().getName());
            if (Config.debug_block_entity_problem) {
                createentitycontroller$LOGGER.info("create.entity is unmovable locate [{},{},{}]", pos.getX(), pos.getY(), pos.getZ());
            }
            Field positionField = AssemblyException.class.getDeclaredField("position");
            positionField.setAccessible(true); // 允许访问 private 字段
            positionField.set(e, pos); // 设置位置
            return e;
        }


    }
    /**
     * @author Pink_Cats
     * @reason Inject
     */
    @Overwrite
    public static AssemblyException structureTooLarge() {
        if (Config.enableBlockEntityExperimentPara)
        {
            String globalValue = System.getProperty("globalValue");
            String globalCount = System.getProperty("globalCount");
            return new AssemblyException("structureTooLargeOrUnstable",
                    AllConfigs.server().kinetics.maxBlocksMoved.get(),
                    globalCount,
                    Config.block_entity_max_stabilize_count,
                    globalValue);
        }
        else {
            return new AssemblyException("structureTooLarge", AllConfigs.server().kinetics.maxBlocksMoved.get());
        }
    }


}