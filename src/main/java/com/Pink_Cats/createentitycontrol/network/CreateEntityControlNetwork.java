package com.Pink_Cats.createentitycontrol.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class CreateEntityControlNetwork {

    private static final String PROTOCOL_VERSION = "1";

    private CreateEntityControlNetwork() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(
                ClusterBlockedSyncPacket.TYPE,
                ClusterBlockedSyncPacket.STREAM_CODEC,
                ClusterBlockedSyncPacket::handle
        );
    }
}
