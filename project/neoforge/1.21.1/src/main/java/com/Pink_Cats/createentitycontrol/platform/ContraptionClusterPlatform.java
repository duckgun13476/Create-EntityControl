package com.Pink_Cats.createentitycontrol.platform;

import com.Pink_Cats.createentitycontrol.network.ClusterBlockedSyncPacket;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.PacketDistributor;

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
        PacketDistributor.sendToPlayer(player, new ClusterBlockedSyncPacket(entityIds, reason, location, durationTicks));
    }
}
