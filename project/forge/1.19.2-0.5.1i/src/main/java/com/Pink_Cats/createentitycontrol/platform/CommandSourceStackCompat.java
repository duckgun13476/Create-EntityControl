package com.Pink_Cats.createentitycontrol.platform;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public final class CommandSourceStackCompat {

    private CommandSourceStackCompat() {}

    public static void sendSuccess(CommandSourceStack source, Supplier<Component> message, boolean broadcast) {
        source.sendSuccess(message.get(), broadcast);
    }
}
