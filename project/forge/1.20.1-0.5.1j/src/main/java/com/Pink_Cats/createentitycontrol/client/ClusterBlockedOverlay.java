package com.Pink_Cats.createentitycontrol.client;

import com.Pink_Cats.createentitycontrol.CreateEntityControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = CreateEntityControl.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClusterBlockedOverlay {

    private static final int HINT_GOLD = 0xE7CD73;
    private static final Map<Integer, OverlayEntry> ENTRIES = new HashMap<>();

    private ClusterBlockedOverlay() {}

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

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!"hotbar".equals(event.getOverlay().id().getPath())) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) {
            return;
        }

        pruneExpired(mc.level.getGameTime());
        TargetedOverlay target = findTargetedOverlay(mc);
        if (target == null) {
            return;
        }

        OverlayEntry entry = target.entry();
        if (entry == null) {
            return;
        }

        Font font = mc.font;
        MutableComponent line1 = gold(Component.translatable("message.createentitycontrol.cluster_blocked.nearby.reason", entry.reason));
        MutableComponent line2 = gold(Component.translatable("message.createentitycontrol.cluster_blocked.nearby.location", entry.location));

        int centerX = event.getWindow().getGuiScaledWidth() / 2;
        int baseY = event.getWindow().getGuiScaledHeight() / 2 + 18;

        event.getGuiGraphics().drawString(font, line1, centerX - font.width(line1) / 2, baseY, HINT_GOLD, true);
        event.getGuiGraphics().drawString(font, line2, centerX - font.width(line2) / 2, baseY + 11, HINT_GOLD, true);
    }

    private static void pruneExpired(long gameTime) {
        ENTRIES.entrySet().removeIf(entry -> entry.getValue().expiresAt < gameTime);
    }

    private static TargetedOverlay findTargetedOverlay(Minecraft mc) {
        Vec3 start = mc.player.getEyePosition();
        Vec3 end = start.add(mc.player.getLookAngle().scale(64.0D));
        AABB searchBox = mc.player.getBoundingBox().expandTowards(mc.player.getLookAngle().scale(64.0D)).inflate(4.0D);

        Entity bestEntity = null;
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
                bestEntity = entity;
                bestEntry = tracked.getValue();
            }
        }

        if (bestEntity == null || bestEntry == null) {
            return null;
        }

        return new TargetedOverlay(bestEntity, bestEntry);
    }

    private static MutableComponent gold(MutableComponent component) {
        return component.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(HINT_GOLD)));
    }

    private record OverlayEntry(Component reason, Component location, long expiresAt) {}

    private record TargetedOverlay(Entity entity, OverlayEntry entry) {}
}
