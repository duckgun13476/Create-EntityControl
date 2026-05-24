package com.Pink_Cats.createentitycontrol.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.slf4j.Logger;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {

    @Unique
    private static final Logger createentitycontrol$logger = LogUtils.getLogger();
    @Unique
    private static int createentitycontrol$hurtLogCount;
    @Unique
    private static int createentitycontrol$damageLogCount;
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
        createentitycontrol$logHurt(hook, minecart, source, amount);
        if (source.is(DamageTypeTags.IS_FIRE)) {
            minecart.clearFire();
            createentitycontrol$logger.info(
                "[CEC MinecartDiag] fire damage blocked hook={} cart={} source={} amount={} damageBefore={}",
                hook,
                minecart.getType(),
                source.getMsgId(),
                amount,
                minecart.getDamage()
            );
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
        float currentDamage = minecart.getDamage();
        boolean playerAttack = createentitycontrol$currentDamageSource != null
            && createentitycontrol$currentDamageSource.getEntity() instanceof Player;
        float adjustedDamage = playerAttack ? damage : currentDamage + (damage - currentDamage) * 0.25F;
        createentitycontrol$logDamage(hook, minecart, createentitycontrol$currentDamageSource, currentDamage, damage, adjustedDamage, playerAttack);
        minecart.setDamage(adjustedDamage);
    }

    @Unique
    private static void createentitycontrol$logHurt(String hook, AbstractMinecart minecart, DamageSource source, float amount) {
        int count = ++createentitycontrol$hurtLogCount;
        if (count <= 20 || count % 100 == 0) {
            createentitycontrol$logger.info(
                "[CEC MinecartDiag] hurt hook={} count={} cart={} source={} fireTagged={} amount={} damageBefore={}",
                hook,
                count,
                minecart.getType(),
                source.getMsgId(),
                source.is(DamageTypeTags.IS_FIRE),
                amount,
                minecart.getDamage()
            );
        }
    }

    @Unique
    private static void createentitycontrol$logDamage(
        String hook,
        AbstractMinecart minecart,
        DamageSource source,
        float before,
        float vanilla,
        float adjusted,
        boolean playerAttack
    ) {
        int count = ++createentitycontrol$damageLogCount;
        if (count <= 20 || count % 100 == 0) {
            createentitycontrol$logger.info(
                "[CEC MinecartDiag] damage hook={} count={} cart={} source={} playerAttack={} damageBefore={} vanillaSetDamage={} adjustedSetDamage={}",
                hook,
                count,
                minecart.getType(),
                source == null ? "<unknown>" : source.getMsgId(),
                playerAttack,
                before,
                vanilla,
                adjusted
            );
        }
    }
}
