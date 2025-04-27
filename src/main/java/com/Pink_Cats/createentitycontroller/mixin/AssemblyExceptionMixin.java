package com.Pink_Cats.createentitycontroller.mixin;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.Pink_Cats.createentitycontroller.addition.ExtendAssemblyException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mojang.text2speech.Narrator.LOGGER;


@Mixin(value = AssemblyException.class,remap = false)
public class AssemblyExceptionMixin {



    /**
     * @author Pink_Cats
     * @reason Inject custom behavior for unmovableBlock
     */
    @Inject(method = "unmovableBlock", at = @At("HEAD") )
    private static void injectUnmovableBlock(BlockPos pos, BlockState state, CallbackInfoReturnable<AssemblyException> cir) throws ExtendAssemblyException {
        // 在调用 unmovableBlock 方法之前打印坐标
        LOGGER.error("HELLO FROM MIXIN");
    }


}