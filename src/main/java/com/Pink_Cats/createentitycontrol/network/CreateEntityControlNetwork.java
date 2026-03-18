package com.Pink_Cats.createentitycontrol.network;

import com.Pink_Cats.createentitycontrol.CreateEntityControl;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class CreateEntityControlNetwork {

    private static final String PROTOCOL_VERSION = "1";
    private static int nextPacketId = 0;

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CreateEntityControl.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private CreateEntityControlNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(
                nextPacketId++,
                ClusterBlockedSyncPacket.class,
                ClusterBlockedSyncPacket::encode,
                ClusterBlockedSyncPacket::decode,
                ClusterBlockedSyncPacket::handle
        );
    }
}
