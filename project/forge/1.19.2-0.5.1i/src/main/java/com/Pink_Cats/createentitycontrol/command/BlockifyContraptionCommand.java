package com.Pink_Cats.createentitycontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.gantry.GantryContraption;
import com.simibubi.create.content.contraptions.gantry.GantryContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.Pink_Cats.createentitycontrol.mixin.ControlledContraptionEntityAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BlockifyContraptionCommand {

    private static final double BLOCKIFY_RANGE = 64.0D;
    private static final long CONFIRM_TIMEOUT_MS = 30000L;
    private static final Map<UUID, PendingBlockify> PENDING_CONFIRMATIONS = new ConcurrentHashMap<>();

    private BlockifyContraptionCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("createentitycontrol")
                .requires(source -> source.hasPermission(4))
                .then(Commands.literal("blockify")
                        .executes(context -> requestBlockifyConfirmation(context.getSource()))
                        .then(Commands.literal("confirm")
                                .executes(context -> confirmBlockify(context.getSource())))
                        .then(Commands.literal("cancel")
                                .executes(context -> cancelBlockify(context.getSource()))));

        LiteralArgumentBuilder<CommandSourceStack> shortCommand = Commands.literal("cec")
                .requires(source -> source.hasPermission(4))
                .then(Commands.literal("blockify")
                        .executes(context -> requestBlockifyConfirmation(context.getSource()))
                        .then(Commands.literal("confirm")
                                .executes(context -> confirmBlockify(context.getSource())))
                        .then(Commands.literal("cancel")
                                .executes(context -> cancelBlockify(context.getSource()))));

        dispatcher.register(command);
        dispatcher.register(shortCommand);
    }

    private static int requestBlockifyConfirmation(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception exception) {
            source.sendFailure(Component.translatable("command.createentitycontrol.player_only"));
            return 0;
        }

        Optional<AbstractContraptionEntity> target = findLookedContraption(player, BLOCKIFY_RANGE);
        if (target.isEmpty()) {
            source.sendFailure(Component.translatable("command.createentitycontrol.blockify.no_target"));
            return 0;
        }

        AbstractContraptionEntity contraptionEntity = target.get();
        if (isUnsupportedForBlockify(contraptionEntity)) {
            source.sendFailure(Component.translatable("command.createentitycontrol.blockify.unsupported_train"));
            return 0;
        }

        long expiresAt = System.currentTimeMillis() + CONFIRM_TIMEOUT_MS;
        PENDING_CONFIRMATIONS.put(player.getUUID(), new PendingBlockify(contraptionEntity.getUUID(), expiresAt));

        source.sendFailure(buildContraptionMessage(
                "command.createentitycontrol.blockify.confirm_prompt",
                contraptionEntity
        ));
        return 1;
    }

    private static int confirmBlockify(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception exception) {
            source.sendFailure(Component.translatable("command.createentitycontrol.player_only"));
            return 0;
        }

        PendingBlockify pending = PENDING_CONFIRMATIONS.get(player.getUUID());
        if (pending == null) {
            source.sendFailure(Component.translatable("command.createentitycontrol.blockify.no_pending"));
            return 0;
        }

        if (pending.expiresAt() < System.currentTimeMillis()) {
            PENDING_CONFIRMATIONS.remove(player.getUUID());
            source.sendFailure(Component.translatable("command.createentitycontrol.blockify.expired"));
            return 0;
        }

        Entity entity = ((ServerLevel) player.getCommandSenderWorld()).getEntity(pending.contraptionUuid());
        if (!(entity instanceof AbstractContraptionEntity contraptionEntity) || !entity.isAlive()) {
            PENDING_CONFIRMATIONS.remove(player.getUUID());
            source.sendFailure(Component.translatable("command.createentitycontrol.blockify.missing"));
            return 0;
        }

        if (isUnsupportedForBlockify(contraptionEntity)) {
            PENDING_CONFIRMATIONS.remove(player.getUUID());
            source.sendFailure(Component.translatable("command.createentitycontrol.blockify.unsupported_train"));
            return 0;
        }

        PENDING_CONFIRMATIONS.remove(player.getUUID());
        contraptionEntity.disassemble();
        source.sendSuccess(buildContraptionMessage("command.createentitycontrol.blockify.success", contraptionEntity), true);
        return 1;
    }

    private static int cancelBlockify(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception exception) {
            source.sendFailure(Component.translatable("command.createentitycontrol.player_only"));
            return 0;
        }

        if (PENDING_CONFIRMATIONS.remove(player.getUUID()) == null) {
            source.sendFailure(Component.translatable("command.createentitycontrol.blockify.cancel_missing"));
            return 0;
        }

        source.sendSuccess(Component.translatable("command.createentitycontrol.blockify.cancelled"), false);
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

    private static boolean isUnsupportedForBlockify(AbstractContraptionEntity contraptionEntity) {
        return contraptionEntity instanceof CarriageContraptionEntity;
    }

    private static Component buildContraptionMessage(String promptKey, AbstractContraptionEntity contraptionEntity) {
        BlockPos entityPos = contraptionEntity.blockPosition();
        BindingTarget bindingTarget = resolveBindingTarget(contraptionEntity);

        return Component.translatable(
                "command.createentitycontrol.blockify.details",
                Component.translatable(promptKey),
                Integer.toString(contraptionEntity.getId()),
                formatBlockPos(entityPos),
                bindingTarget.name(),
                bindingTarget.posText()
        );
    }

    private static BindingTarget resolveBindingTarget(AbstractContraptionEntity contraptionEntity) {
        if (contraptionEntity instanceof ControlledContraptionEntity controlled) {
            BlockPos controllerPos = ((ControlledContraptionEntityAccessor) controlled).createentitycontrol$getControllerPos();
            if (controllerPos != null) {
                BlockState controllerState = contraptionEntity.getCommandSenderWorld().getBlockState(controllerPos);
                return new BindingTarget(extractBlockName(controllerState), formatBlockPos(controllerPos));
            }
        }

        if (contraptionEntity instanceof OrientedContraptionEntity oriented) {
            Entity vehicle = oriented.getVehicle();
            if (vehicle != null) {
                return new BindingTarget(extractEntityTypeName(vehicle), formatBlockPos(vehicle.blockPosition()));
            }
        }

        if (contraptionEntity instanceof GantryContraptionEntity gantryEntity
                && gantryEntity.getContraption() instanceof GantryContraption gantryContraption) {
            Vec3 anchorVec = gantryEntity.getAnchorVec().add(.5, .5, .5);
            BlockPos anchorPos = new BlockPos((int) Math.floor(anchorVec.x), (int) Math.floor(anchorVec.y), (int) Math.floor(anchorVec.z));
            BlockPos shaftPos = anchorPos.relative(gantryContraption.getFacing().getOpposite());
            BlockState shaftState = contraptionEntity.getCommandSenderWorld().getBlockState(shaftPos);
            return new BindingTarget(extractBlockName(shaftState), formatBlockPos(shaftPos));
        }

        BlockPos anchorPos = contraptionEntity.getContraption() != null ? contraptionEntity.getContraption().anchor : null;
        if (anchorPos != null) {
            BlockState anchorState = contraptionEntity.getCommandSenderWorld().getBlockState(anchorPos);
            return new BindingTarget("anchor-ref:" + extractBlockName(anchorState), formatBlockPos(anchorPos));
        }

        return new BindingTarget("unknown", "unknown");
    }

    private static String extractBlockName(BlockState state) {
        return state.getBlock().toString().replaceAll("Block\\{(.*?)\\}", "$1");
    }

    private static String extractEntityTypeName(Entity entity) {
        return entity.getType().toString().replaceAll(".*\\[(.*)]", "$1");
    }

    private static String formatBlockPos(BlockPos pos) {
        return "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
    }

    private record BindingTarget(String name, String posText) {}

    private record PendingBlockify(UUID contraptionUuid, long expiresAt) {}
}
