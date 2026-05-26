package com.Pink_Cats.createentitycontrol.client;

import com.Pink_Cats.createentitycontrol.CreateEntityControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = CreateEntityControl.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClusterBlockedOverlay {

    private ClusterBlockedOverlay() {}

    public static void remember(List<Integer> entityIds, Component reason, Component location, int durationTicks) {
        ClusterBlockedOverlayState.remember(entityIds, reason, location, durationTicks);
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!"hotbar".equals(event.getOverlay().id().getPath())) {
            return;
        }

        ClusterBlockedOverlayState.OverlayLines lines = ClusterBlockedOverlayState.getCurrentLines();
        if (lines == null) {
            return;
        }

        Font font = Minecraft.getInstance().font;
        int centerX = event.getWindow().getGuiScaledWidth() / 2;
        int baseY = event.getWindow().getGuiScaledHeight() / 2 + 18;

        font.drawShadow(event.getPoseStack(), lines.line1(), centerX - font.width(lines.line1()) / 2.0F, baseY, ClusterBlockedOverlayState.HINT_GOLD);
        font.drawShadow(event.getPoseStack(), lines.line2(), centerX - font.width(lines.line2()) / 2.0F, baseY + 11, ClusterBlockedOverlayState.HINT_GOLD);
    }
}
