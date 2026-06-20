package com.Pink_Cats.createentitycontrol.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class CreateEntityControlNetwork {

    private CreateEntityControlNetwork() {
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ClusterBlockedSyncPacket.TYPE, ClusterBlockedSyncPacket.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ClusterBlockedSyncPacket.TYPE,
                (packet, context) -> context.client().execute(() -> ClusterBlockedSyncPacket.handle(packet)));
    }
}
