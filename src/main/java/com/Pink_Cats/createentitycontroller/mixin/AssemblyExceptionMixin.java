package com.Pink_Cats.createentitycontroller.mixin;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.Pink_Cats.createentitycontroller.addition.ExtendAssemblyException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;


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
        AssemblyException e = new AssemblyException("unmovableBlock", pos.getX(), pos.getY(), pos.getZ(),
                state.getBlock().getName());
        System.out.println("unmovableBlockoverwrite");

        Field positionField = AssemblyException.class.getDeclaredField("position");
        positionField.setAccessible(true); // 允许访问 private 字段
        positionField.set(e, pos); // 设置位置
        return e;
    }

}