package com.Pink_Cats.createentitycontrol.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClusterBlockedOverlayState {

    public static final int HINT_GOLD = 0xE7CD73;
    private static final Map<Integer, OverlayEntry> ENTRIES = new HashMap<>();

    private ClusterBlockedOverlayState() {}

    public static void remember(List<Integer> entityIds, Component reason, Component location, int durationTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        long expiresAt = mc.level.getGameTime() + durationTicks;
        for (Integer entityId : entityIds) {
            ENTRIES.put(entityId, new OverlayEntry(reason.copy(), location.copy(), expiresAt));
        }
    }

    public static OverlayLines getCurrentLines() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) {
            return null;
        }

        pruneExpired(mc.level.getGameTime());
        OverlayEntry entry = findTargetedEntry(mc);
        if (entry == null) {
            return null;
        }

        return new OverlayLines(
                gold(Component.translatable("message.createentitycontrol.cluster_blocked.nearby.reason", entry.reason)),
                gold(Component.translatable("message.createentitycontrol.cluster_blocked.nearby.location", entry.location))
        );
    }

    private static void pruneExpired(long gameTime) {
        ENTRIES.entrySet().removeIf(entry -> entry.getValue().expiresAt < gameTime);
    }

    private static OverlayEntry findTargetedEntry(Minecraft mc) {
        Vec3 start = mc.player.getEyePosition();
        Vec3 look = mc.player.getLookAngle().scale(64.0D);
        Vec3 end = start.add(look);
        AABB searchBox = mc.player.getBoundingBox().expandTowards(look).inflate(4.0D);

        OverlayEntry bestEntry = null;
        double bestDistance = Double.MAX_VALUE;

        for (Map.Entry<Integer, OverlayEntry> tracked : ENTRIES.entrySet()) {
            Entity entity = mc.level.getEntity(tracked.getKey());
            if (entity == null || !entity.isAlive() || !searchBox.intersects(entity.getBoundingBox().inflate(1.0D))) {
                continue;
            }

            if (entity.getBoundingBox().inflate(0.5D).clip(start, end).isEmpty()) {
                continue;
            }

            double distance = entity.distanceToSqr(mc.player);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestEntry = tracked.getValue();
            }
        }

        return bestEntry;
    }

    private static MutableComponent gold(MutableComponent component) {
        return component.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(HINT_GOLD)));
    }

    private record OverlayEntry(Component reason, Component location, long expiresAt) {}

    public record OverlayLines(MutableComponent line1, MutableComponent line2) {}
}
