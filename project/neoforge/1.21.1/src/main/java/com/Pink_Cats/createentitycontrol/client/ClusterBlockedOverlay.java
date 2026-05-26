package com.Pink_Cats.createentitycontrol.client;

import com.Pink_Cats.createentitycontrol.CreateEntityControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import java.util.List;

@EventBusSubscriber(modid = CreateEntityControl.MODID, value = Dist.CLIENT)
public class ClusterBlockedOverlay {

    private ClusterBlockedOverlay() {}

    public static void remember(List<Integer> entityIds, Component reason, Component location, int durationTicks) {
        ClusterBlockedOverlayState.remember(entityIds, reason, location, durationTicks);
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiLayerEvent.Post event) {
        if (!"hotbar".equals(event.getName().getPath())) {
            return;
        }

        ClusterBlockedOverlayState.OverlayLines lines = ClusterBlockedOverlayState.getCurrentLines();
        if (lines == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int centerX = mc.getWindow().getGuiScaledWidth() / 2;
        int baseY = mc.getWindow().getGuiScaledHeight() / 2 + 18;

        event.getGuiGraphics().drawString(font, lines.line1(), centerX - font.width(lines.line1()) / 2, baseY, ClusterBlockedOverlayState.HINT_GOLD, true);
        event.getGuiGraphics().drawString(font, lines.line2(), centerX - font.width(lines.line2()) / 2, baseY + 11, ClusterBlockedOverlayState.HINT_GOLD, true);
    }
}
