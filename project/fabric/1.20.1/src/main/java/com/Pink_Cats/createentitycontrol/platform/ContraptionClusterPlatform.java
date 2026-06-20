package com.Pink_Cats.createentitycontrol.platform;

import com.Pink_Cats.createentitycontrol.network.ClusterBlockedSyncPacket;
import com.Pink_Cats.createentitycontrol.network.CreateEntityControlNetwork;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public final class ContraptionClusterPlatform {
    private ContraptionClusterPlatform() {
    }

    public static Level level(AbstractContraptionEntity entity) {
        return entity.level();
    }

    public static Component translateBlockName(String selector) {
        if (selector.startsWith("#")) {
            return Component.literal(selector);
        }
        ResourceLocation id = ResourceLocation.tryParse(selector);
        if (id == null) {
            return Component.literal(selector);
        }
        Block block = BuiltInRegistries.BLOCK.getOptional(id).orElse(null);
        return block == null ? Component.literal(selector) : block.getName();
    }

    public static void sendClusterBlocked(ServerPlayer player, List<Integer> entityIds, Component reason,
                                          Component location, int durationTicks) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ClusterBlockedSyncPacket.encode(new ClusterBlockedSyncPacket(entityIds, reason, location, durationTicks), buf);
        ServerPlayNetworking.send(player, CreateEntityControlNetwork.CLUSTER_BLOCKED_SYNC, buf);
    }
}
