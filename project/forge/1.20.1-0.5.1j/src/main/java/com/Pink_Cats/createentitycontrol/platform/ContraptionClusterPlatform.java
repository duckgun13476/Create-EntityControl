package com.Pink_Cats.createentitycontrol.platform;

import com.Pink_Cats.createentitycontrol.network.ClusterBlockedSyncPacket;
import com.Pink_Cats.createentitycontrol.network.CreateEntityControlNetwork;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

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
        Block block = ForgeRegistries.BLOCKS.getValue(id);
        return block == null ? Component.literal(selector) : block.getName();
    }

    public static void sendClusterBlocked(ServerPlayer player, List<Integer> entityIds, Component reason,
                                          Component location, int durationTicks) {
        ClusterBlockedSyncPacket packet = new ClusterBlockedSyncPacket(entityIds, reason, location, durationTicks);
        CreateEntityControlNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
