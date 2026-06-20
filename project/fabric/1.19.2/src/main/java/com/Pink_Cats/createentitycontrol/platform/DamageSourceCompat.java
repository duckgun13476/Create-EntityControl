package com.Pink_Cats.createentitycontrol.platform;

import net.minecraft.world.damagesource.DamageSource;

public final class DamageSourceCompat {
    private DamageSourceCompat() {
    }

    public static boolean isFire(DamageSource source) {
        return source.isFire();
    }
}
