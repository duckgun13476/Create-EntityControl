package com.Pink_Cats.createentitycontroller.mixin;

import com.Pink_Cats.createentitycontroller.Config;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

import static com.mojang.text2speech.Narrator.LOGGER;



@Mixin(value = AssemblyException.class,remap = false)
public class AssemblyExceptionMixin {


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
        AssemblyException e = new AssemblyException("unmovableBlock_test", pos.getX(), pos.getY(), pos.getZ(),
                state.getBlock().getName());
        //System.out.println("unmovableBlockoverwrite");
        LOGGER.info("create.entity is unmovable locate "+"["+ pos.getX()+","+pos.getY()+","+pos.getZ()+"]");
        Field positionField = AssemblyException.class.getDeclaredField("position");
        positionField.setAccessible(true); // 允许访问 private 字段
        positionField.set(e, pos); // 设置位置
        return e;
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