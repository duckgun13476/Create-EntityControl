package com.Pink_Cats.createentitycontrol.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class CreateEntityControlClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((graphics, tickDelta) -> ClusterBlockedOverlay.render(graphics));
    }
}
