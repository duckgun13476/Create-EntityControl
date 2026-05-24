package com.Pink_Cats.createentitycontrol.mixin;

import com.Pink_Cats.createentitycontrol.Config;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MechanicalBearingBlockEntity.class, remap = false)
public abstract class MechanicalBearingBlockEntityMixin {

    @Shadow
    protected abstract boolean isWindmill();

    @Inject(method = "getAngularSpeed", at = @At("RETURN"), cancellable = true)
    private void createentitycontrol$capMechanicalBearingAngularSpeed(CallbackInfoReturnable<Float> cir) {
        if (isWindmill()) {
            return;
        }

        int configuredCap = Config.mechanical_bearing_gear_max_speed;
        if (configuredCap <= 0) {
            return;
        }

        float originalSpeed = Math.abs(((KineticBlockEntity) (Object) this).getSpeed());
        if (originalSpeed <= configuredCap || originalSpeed <= 0.0F) {
            return;
        }

        float scaledAngularSpeed = cir.getReturnValueF() * (configuredCap / originalSpeed);
        cir.setReturnValue(scaledAngularSpeed);
    }
}
