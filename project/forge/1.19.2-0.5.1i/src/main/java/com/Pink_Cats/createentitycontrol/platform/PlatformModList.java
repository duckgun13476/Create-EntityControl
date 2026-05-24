package com.Pink_Cats.createentitycontrol.platform;

import net.minecraftforge.fml.ModList;

public final class PlatformModList {
    private PlatformModList() {
    }

    public static boolean isLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
