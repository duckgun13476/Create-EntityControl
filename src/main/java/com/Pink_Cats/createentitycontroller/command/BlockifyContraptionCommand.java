package com.Pink_Cats.createentitycontroller.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Optional;

public final class BlockifyContraptionCommand {

    private BlockifyContraptionCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("createentitycontrol")
                .then(Commands.literal("blockify")
                        .executes(context -> blockifyLookedContraption(context.getSource())));

        LiteralArgumentBuilder<CommandSourceStack> shortCommand = Commands.literal("cec")
                .then(Commands.literal("blockify")
                        .executes(context -> blockifyLookedContraption(context.getSource())));

        dispatcher.register(command);
        dispatcher.register(shortCommand);
    }

    private static int blockifyLookedContraption(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception exception) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }

        Optional<AbstractContraptionEntity> target = findLookedContraption(player, 64.0D);
        if (target.isEmpty()) {
            source.sendFailure(Component.literal("No Create contraption entity in your crosshair."));
            return 0;
        }

        AbstractContraptionEntity contraptionEntity = target.get();
        contraptionEntity.disassemble();
        source.sendSuccess(Component.literal("Blockified contraption entity #" + contraptionEntity.getId() + "."), true);
        return 1;
    }

    private static Optional<AbstractContraptionEntity> findLookedContraption(ServerPlayer player, double range) {
        Vec3 start = player.getEyePosition();
        Vec3 look = player.getLookAngle().scale(range);
        Vec3 end = start.add(look);
        AABB searchBox = player.getBoundingBox().expandTowards(look).inflate(1.0D);

        return player.getCommandSenderWorld()
                .getEntitiesOfClass(AbstractContraptionEntity.class, searchBox, Entity::isAlive)
                .stream()
                .filter(entity -> entity.getBoundingBox().inflate(0.5D).clip(start, end).isPresent())
                .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(start.x, start.y, start.z)));
    }
}
