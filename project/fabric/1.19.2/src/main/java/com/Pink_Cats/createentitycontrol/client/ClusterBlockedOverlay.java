package com.Pink_Cats.createentitycontrol.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClusterBlockedOverlay {

    private ClusterBlockedOverlay() {
    }

    public static void remember(List<Integer> entityIds, Component reason, Component location, int durationTicks) {
        ClusterBlockedOverlayState.remember(entityIds, reason, location, durationTicks);
    }

    public static void render(PoseStack poseStack) {
        ClusterBlockedOverlayState.OverlayLines lines = ClusterBlockedOverlayState.getCurrentLines();
        if (lines == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int centerX = mc.getWindow().getGuiScaledWidth() / 2;
        int baseY = mc.getWindow().getGuiScaledHeight() / 2 + 18;

        font.drawShadow(poseStack, lines.line1(), centerX - font.width(lines.line1()) / 2.0F, baseY, ClusterBlockedOverlayState.HINT_GOLD);
        font.drawShadow(poseStack, lines.line2(), centerX - font.width(lines.line2()) / 2.0F, baseY + 11, ClusterBlockedOverlayState.HINT_GOLD);
    }
}
