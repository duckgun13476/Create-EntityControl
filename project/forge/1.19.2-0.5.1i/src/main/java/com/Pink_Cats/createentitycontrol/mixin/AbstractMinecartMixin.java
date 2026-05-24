package com.Pink_Cats.createentitycontrol.mixin;

import com.Pink_Cats.createentitycontrol.Config;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {

    @Unique
    private DamageSource createentitycontrol$currentDamageSource;

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void createentitycontrol$blockFireDamageNamed(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        createentitycontrol$blockFireDamage("named:hurt", source, amount, cir);
    }

    @Inject(method = "m_6469_", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void createentitycontrol$blockFireDamageSrg(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        createentitycontrol$blockFireDamage("srg:m_6469_", source, amount, cir);
    }

    @Unique
    private void createentitycontrol$blockFireDamage(String hook, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        AbstractMinecart minecart = (AbstractMinecart) (Object) this;
        createentitycontrol$currentDamageSource = source;
        if (Config.minecart_protection && source.isFire()) {
            minecart.clearFire();
            cir.setReturnValue(false);
        }
    }

    @Redirect(
        method = "hurt",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;setDamage(F)V",
            remap = false
        ),
        remap = false,
        require = 0
    )
    private void createentitycontrol$quarterIncomingDamageNamed(AbstractMinecart minecart, float damage) {
        createentitycontrol$quarterIncomingDamage("named:hurt/setDamage", minecart, damage);
    }

    @Redirect(
        method = "m_6469_",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;m_38109_(F)V",
            remap = false
        ),
        remap = false,
        require = 0
    )
    private void createentitycontrol$quarterIncomingDamageSrg(AbstractMinecart minecart, float damage) {
        createentitycontrol$quarterIncomingDamage("srg:m_6469_/m_38109_", minecart, damage);
    }

    @Unique
    private void createentitycontrol$quarterIncomingDamage(String hook, AbstractMinecart minecart, float damage) {
        if (!Config.minecart_protection) {
            minecart.setDamage(damage);
            return;
        }

        float currentDamage = minecart.getDamage();
        boolean playerAttack = createentitycontrol$currentDamageSource != null
            && createentitycontrol$currentDamageSource.getEntity() instanceof Player;
        float adjustedDamage = playerAttack ? damage : currentDamage + (damage - currentDamage) * 0.25F;
        minecart.setDamage(adjustedDamage);
    }
}
