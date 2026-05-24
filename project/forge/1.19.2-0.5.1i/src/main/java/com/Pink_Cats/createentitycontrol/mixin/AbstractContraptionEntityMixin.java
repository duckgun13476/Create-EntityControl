package com.Pink_Cats.createentitycontrol.mixin;

import com.Pink_Cats.createentitycontrol.addition.ContraptionClusterController;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractContraptionEntity.class, remap = false)
public abstract class AbstractContraptionEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void createentitycontrol$validateSpatialCluster(CallbackInfo ci) {
        ContraptionClusterController.tick((AbstractContraptionEntity) (Object) this);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void createentitycontrol$forgetSpatialCluster(Entity.RemovalReason reason, CallbackInfo ci) {
        ContraptionClusterController.forget((AbstractContraptionEntity) (Object) this);
    }
}
