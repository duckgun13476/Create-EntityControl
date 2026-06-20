package com.Pink_Cats.createentitycontrol.network;

import com.Pink_Cats.createentitycontrol.CreateEntityControl;
import com.Pink_Cats.createentitycontrol.client.ClusterBlockedOverlay;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record ClusterBlockedSyncPacket(
        List<Integer> entityIds,
        Component reason,
        Component location,
        int durationTicks
) implements CustomPacketPayload {

    public static final Type<ClusterBlockedSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateEntityControl.MODID, "cluster_blocked_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClusterBlockedSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.VAR_INT),
                    ClusterBlockedSyncPacket::entityIds,
                    ComponentSerialization.TRUSTED_STREAM_CODEC,
                    ClusterBlockedSyncPacket::reason,
                    ComponentSerialization.TRUSTED_STREAM_CODEC,
                    ClusterBlockedSyncPacket::location,
                    ByteBufCodecs.VAR_INT,
                    ClusterBlockedSyncPacket::durationTicks,
                    ClusterBlockedSyncPacket::new
            );

    @Override
    public Type<ClusterBlockedSyncPacket> type() {
        return TYPE;
    }

    public static void handle(ClusterBlockedSyncPacket packet) {
        ClusterBlockedOverlay.remember(packet.entityIds(), packet.reason(), packet.location(), packet.durationTicks());
    }
}
