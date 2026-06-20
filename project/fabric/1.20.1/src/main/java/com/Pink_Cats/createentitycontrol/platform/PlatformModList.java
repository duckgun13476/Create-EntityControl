package com.Pink_Cats.createentitycontrol.platform;

import net.fabricmc.loader.api.FabricLoader;

public final class PlatformModList {
    private PlatformModList() {
    }

    public static boolean isLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
