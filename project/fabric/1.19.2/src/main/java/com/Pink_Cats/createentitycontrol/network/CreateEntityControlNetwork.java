package com.Pink_Cats.createentitycontrol.network;

import com.Pink_Cats.createentitycontrol.CreateEntityControl;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public final class CreateEntityControlNetwork {
    public static final ResourceLocation CLUSTER_BLOCKED_SYNC =
            new ResourceLocation(CreateEntityControl.MODID, "cluster_blocked_sync");

    private CreateEntityControlNetwork() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(CLUSTER_BLOCKED_SYNC, (client, handler, buf, responseSender) -> {
            ClusterBlockedSyncPacket packet = ClusterBlockedSyncPacket.decode(buf);
            client.execute(() -> ClusterBlockedSyncPacket.handle(packet));
        });
    }
}
