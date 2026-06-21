package com.Pink_Cats.createentitycontrol.mixin;

import com.Pink_Cats.createentitycontrol.Config;
import com.Pink_Cats.createentitycontrol.addition.EntityEnrollment;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(value = AssemblyException.class, remap = false)
public class AssemblyExceptionMixin {

    @Unique
    private static final Logger createentitycontrol$LOGGER = LogUtils.getLogger();

    @Inject(method = "unmovableBlock", at = @At("HEAD"), cancellable = true)
    private static void createentitycontrol$unmovableBlock(BlockPos pos, BlockState state,
                                                           CallbackInfoReturnable<AssemblyException> cir)
            throws IllegalAccessException, NoSuchFieldException {
        cir.setReturnValue(createentitycontrol$createUnmovableBlock(pos, state));
    }

    @Inject(method = "structureTooLarge", at = @At("HEAD"), cancellable = true)
    private static void createentitycontrol$structureTooLarge(CallbackInfoReturnable<AssemblyException> cir) {
        if (Config.enableBlockEntityExperimentPara) {
            String globalValue = System.getProperty("globalValue");
            String globalCount = System.getProperty("globalCount");
            cir.setReturnValue(new AssemblyException("structureTooLargeOrUnstable",
                    AllConfigs.server().kinetics.maxBlocksMoved.get(),
                    globalCount,
                    Config.block_entity_max_stabilize_count,
                    globalValue));
        } else {
            cir.setReturnValue(new AssemblyException("structureTooLarge", AllConfigs.server().kinetics.maxBlocksMoved.get()));
        }
    }

    @Unique
    private static AssemblyException createentitycontrol$createUnmovableBlock(BlockPos pos, BlockState state)
            throws NoSuchFieldException, IllegalAccessException {
        int status = EntityEnrollment.getControlStatus();
        String translationKey;
        String logMessage;
        if (status == 8) {
            translationKey = "unmovableBlock_entity_control_unmovable_block";
            logMessage = "create.entity has unmovable block locate [{},{},{}]";
        } else if (status == 16) {
            translationKey = "unmovableBlock_entity_control_limit_special_block";
            logMessage = "create.entity has reach special block limit locate [{},{},{}]";
        } else if (status == 32) {
            translationKey = "unmovableBlock_entity_control_structure_too_long";
            logMessage = "create.entity was too long locate [{},{},{}]";
        } else {
            translationKey = "unmovableBlock_entity_control_unmovable_block";
            logMessage = "create.entity is unmovable locate [{},{},{}]";
        }

        AssemblyException exception = new AssemblyException(translationKey, pos.getX(), pos.getY(), pos.getZ(),
                state.getBlock().getName());
        if (Config.debug_block_entity_problem) {
            createentitycontrol$LOGGER.info(logMessage, pos.getX(), pos.getY(), pos.getZ());
        }
        Field positionField = AssemblyException.class.getDeclaredField("position");
        positionField.setAccessible(true);
        positionField.set(exception, pos);
        return exception;
    }
}