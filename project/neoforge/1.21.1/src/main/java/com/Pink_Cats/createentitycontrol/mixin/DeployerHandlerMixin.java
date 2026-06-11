package com.Pink_Cats.createentitycontrol.mixin;

import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.kinetics.deployer.DeployerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DeployerHandler.class, remap = false)
public class DeployerHandlerMixin {
    @Inject(
            method = "activateInner",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/kinetics/deployer/DeployerFakePlayer;attack(Lnet/minecraft/world/entity/Entity;)V"
            )
    )
    private static void createentitycontrol$swingBeforeEntityAttack(
            DeployerFakePlayer player,
            Vec3 vec,
            BlockPos clickedPos,
            Vec3 extensionVector,
            Object mode,
            CallbackInfo ci
    ) {
        if ("PUNCH".equals(String.valueOf(mode))) {
            player.swing(InteractionHand.MAIN_HAND, true);
        }
    }
}
