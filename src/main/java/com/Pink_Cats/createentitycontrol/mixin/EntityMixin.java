package com.Pink_Cats.createentitycontrol.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Unique
    private static final Logger createentitycontrol$logger = LogUtils.getLogger();
    @Unique
    private static int createentitycontrol$lavaLogCount;

    @Inject(method = "lavaHurt", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void createentitycontrol$blockMinecartLavaDamageNamed(CallbackInfo ci) {
        createentitycontrol$blockMinecartLavaDamage("named:lavaHurt", ci);
    }

    @Inject(method = "m_20093_", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void createentitycontrol$blockMinecartLavaDamageSrg(CallbackInfo ci) {
        createentitycontrol$blockMinecartLavaDamage("srg:m_20093_", ci);
    }

    @Unique
    private void createentitycontrol$blockMinecartLavaDamage(String hook, CallbackInfo ci) {
        if ((Object) this instanceof AbstractMinecart minecart) {
            int count = ++createentitycontrol$lavaLogCount;
            createentitycontrol$logger.info(
                "[CEC MinecartDiag] lava blocked hook={} count={} cart={} damageBefore={} fireTicks={}",
                hook,
                count,
                minecart.getType(),
                minecart.getDamage(),
                minecart.getRemainingFireTicks()
            );
            minecart.clearFire();
            ci.cancel();
        }
    }
}
