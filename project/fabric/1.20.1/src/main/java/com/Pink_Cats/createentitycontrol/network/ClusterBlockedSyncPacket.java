package com.Pink_Cats.createentitycontrol.network;

import com.Pink_Cats.createentitycontrol.client.ClusterBlockedOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ClusterBlockedSyncPacket {

    private final List<Integer> entityIds;
    private final Component reason;
    private final Component location;
    private final int durationTicks;

    public ClusterBlockedSyncPacket(List<Integer> entityIds, Component reason, Component location, int durationTicks) {
        this.entityIds = entityIds;
        this.reason = reason;
        this.location = location;
        this.durationTicks = durationTicks;
    }

    public static void encode(ClusterBlockedSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityIds.size());
        for (Integer entityId : packet.entityIds) {
            buf.writeVarInt(entityId);
        }
        buf.writeComponent(packet.reason);
        buf.writeComponent(packet.location);
        buf.writeVarInt(packet.durationTicks);
    }

    public static ClusterBlockedSyncPacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Integer> entityIds = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            entityIds.add(buf.readVarInt());
        }
        Component reason = buf.readComponent();
        Component location = buf.readComponent();
        int durationTicks = buf.readVarInt();
        return new ClusterBlockedSyncPacket(entityIds, reason, location, durationTicks);
    }

    public static void handle(ClusterBlockedSyncPacket packet) {
        ClusterBlockedOverlay.remember(packet.entityIds, packet.reason, packet.location, packet.durationTicks);
    }
}
