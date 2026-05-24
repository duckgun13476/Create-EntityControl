package com.Pink_Cats.createentitycontrol.mixin;

import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ControlledContraptionEntity.class, remap = false)
public interface ControlledContraptionEntityAccessor {

    @Accessor("controllerPos")
    BlockPos createentitycontrol$getControllerPos();
}
