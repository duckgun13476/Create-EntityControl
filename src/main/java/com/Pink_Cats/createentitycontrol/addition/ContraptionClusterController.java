package com.Pink_Cats.createentitycontrol.addition;

import com.Pink_Cats.createentitycontrol.Config;
import com.Pink_Cats.createentitycontrol.network.ClusterBlockedSyncPacket;
import com.Pink_Cats.createentitycontrol.network.CreateEntityControlNetwork;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ContraptionClusterController {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int HINT_GOLD = 0xE7CD73;
    private static final long SNAPSHOT_REFRESH_INTERVAL = 20L;
    private static final long VALIDATION_INTERVAL = 10L;
    private static final long BLOCKED_CLUSTER_DURATION = 10L;
    private static final long LOCAL_NOTIFY_COOLDOWN = 100L;
    private static final long GLOBAL_NOTIFY_COOLDOWN = 60L * 20L;
    private static final int GLOBAL_NOTIFY_PLAYER_READY_TICKS = 40;
    private static final int OVERLAY_SYNC_DURATION = 60;

    private static final Map<UUID, ContraptionSnapshot> SNAPSHOTS = new HashMap<>();
    private static final Map<UUID, Long> BLOCKED_UNTIL = new HashMap<>();
    private static final Map<String, Long> LOCAL_NOTIFY_UNTIL = new HashMap<>();
    private static final Map<String, Long> GLOBAL_NOTIFY_UNTIL = new HashMap<>();

    private ContraptionClusterController() {}

    public static void tick(AbstractContraptionEntity entity) {
        if (entity == null || entity.level.isClientSide || !entity.isAlive() || isExcluded(entity)) {
            return;
        }

        long gameTime = entity.level.getGameTime();
        cleanup(gameTime);

        Long blockedUntil = BLOCKED_UNTIL.get(entity.getUUID());
        if (blockedUntil != null && blockedUntil >= gameTime) {
            freeze(entity);
            return;
        }

        if (gameTime % VALIDATION_INTERVAL != Math.floorMod(entity.getId(), VALIDATION_INTERVAL)) {
            return;
        }

        Set<AbstractContraptionEntity> cluster = collectCluster(entity);
        if (cluster.size() <= 1) {
            return;
        }

        ClusterStats stats = buildClusterStats(cluster, gameTime);
        ClusterLimitViolation violation = findLimitViolation(cluster, gameTime, stats);
        if (violation == null) {
            return;
        }

        long nextAllowedTick = gameTime + BLOCKED_CLUSTER_DURATION;
        for (AbstractContraptionEntity member : cluster) {
            BLOCKED_UNTIL.put(member.getUUID(), nextAllowedTick);
            freeze(member);
        }

        if (Config.debug) {
            LOGGER.warn(
                    "contraption cluster blocked: clusterKey={}, size={}, blocks={}, stabilize={}, spanX={}, spanY={}, spanZ={}",
                    createClusterKey(cluster),
                    cluster.size(),
                    stats.totalBlocks,
                    stats.totalStability,
                    stats.spanX(),
                    stats.spanY(),
                    stats.spanZ()
            );
        }

        notifyPlayers(cluster, stats, violation, gameTime);
    }

    public static void forget(AbstractContraptionEntity entity) {
        if (entity == null) {
            return;
        }
        SNAPSHOTS.remove(entity.getUUID());
        BLOCKED_UNTIL.remove(entity.getUUID());
    }

    private static boolean isExcluded(AbstractContraptionEntity entity) {
        return entity.getContraption() == null;
    }

    private static void cleanup(long gameTime) {
        BLOCKED_UNTIL.entrySet().removeIf(entry -> entry.getValue() < gameTime);
        SNAPSHOTS.entrySet().removeIf(entry -> entry.getValue().expiresAtTick < gameTime);
        LOCAL_NOTIFY_UNTIL.entrySet().removeIf(entry -> entry.getValue() < gameTime);
        GLOBAL_NOTIFY_UNTIL.entrySet().removeIf(entry -> entry.getValue() < gameTime);
    }

    private static Set<AbstractContraptionEntity> collectCluster(AbstractContraptionEntity root) {
        Set<AbstractContraptionEntity> cluster = new HashSet<>();
        Deque<AbstractContraptionEntity> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            AbstractContraptionEntity current = queue.poll();
            if (current == null || !current.isAlive() || isExcluded(current) || !cluster.add(current)) {
                continue;
            }

            AABB searchBox = current.getBoundingBox().inflate(Config.contraption_cluster_scan_radius);
            List<AbstractContraptionEntity> nearby = current.level.getEntitiesOfClass(
                    AbstractContraptionEntity.class,
                    searchBox,
                    other -> other != current && other.isAlive() && !isExcluded(other)
            );

            for (AbstractContraptionEntity nearbyEntity : nearby) {
                queue.add(nearbyEntity);
            }
        }

        return cluster;
    }

    private static ClusterStats buildClusterStats(Set<AbstractContraptionEntity> cluster, long gameTime) {
        Map<String, Integer> blockCounts = new HashMap<>();
        int totalBlocks = 0;
        int totalStability = 0;
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (AbstractContraptionEntity entity : cluster) {
            ContraptionSnapshot snapshot = getSnapshot(entity, gameTime);
            totalBlocks += snapshot.totalBlocks;
            totalStability += snapshot.totalStability;

            mergeCounts(blockCounts, snapshot.blockCounts);

            AABB box = entity.getBoundingBox();
            minX = Math.min(minX, box.minX);
            minY = Math.min(minY, box.minY);
            minZ = Math.min(minZ, box.minZ);
            maxX = Math.max(maxX, box.maxX);
            maxY = Math.max(maxY, box.maxY);
            maxZ = Math.max(maxZ, box.maxZ);
        }

        return new ClusterStats(blockCounts, totalBlocks, totalStability, minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static ContraptionSnapshot getSnapshot(AbstractContraptionEntity entity, long gameTime) {
        ContraptionSnapshot snapshot = SNAPSHOTS.get(entity.getUUID());
        if (snapshot != null && snapshot.expiresAtTick >= gameTime) {
            return snapshot;
        }

        Contraption contraption = entity.getContraption();
        Map<String, Integer> blockCounts = new HashMap<>();
        int totalBlocks = 0;

        Collection<StructureTemplate.StructureBlockInfo> blocks = contraption.getBlocks().values();
        for (StructureTemplate.StructureBlockInfo blockInfo : blocks) {
            String blockName = extractBlockName(blockInfo);
            blockCounts.put(blockName, blockCounts.getOrDefault(blockName, 0) + 1);
            totalBlocks++;
        }

        int totalStability = calculateStability(blockCounts);
        snapshot = new ContraptionSnapshot(blockCounts, totalBlocks, totalStability, gameTime + SNAPSHOT_REFRESH_INTERVAL);
        SNAPSHOTS.put(entity.getUUID(), snapshot);
        return snapshot;
    }

    private static ClusterLimitViolation findLimitViolation(Set<AbstractContraptionEntity> cluster, long gameTime, ClusterStats stats) {
        for (List<Object> limitEntry : Config.blocksLimitValues) {
            String blockName = (String) limitEntry.get(0);
            int allowedCount = (Integer) limitEntry.get(1);
            int actualCount = stats.blockCounts.getOrDefault(blockName, 0);
            if (actualCount > allowedCount) {
                return new ClusterLimitViolation(
                        "message.createentitycontrol.cluster_blocked.reason.block_limit",
                        translateBlockName(blockName),
                        Integer.toString(actualCount),
                        Integer.toString(allowedCount)
                );
            }
        }

        for (AbstractContraptionEntity entity : cluster) {
            ContraptionSnapshot snapshot = getSnapshot(entity, gameTime);
            if (snapshot.totalBlocks > AllConfigs.server().kinetics.maxBlocksMoved.get()) {
                return new ClusterLimitViolation(
                        "message.createentitycontrol.cluster_blocked.reason.total_blocks",
                        Integer.toString(snapshot.totalBlocks),
                        Integer.toString(AllConfigs.server().kinetics.maxBlocksMoved.get())
                );
            }

            if (Config.enableBlockEntityExperimentPara && snapshot.totalStability > Config.block_entity_max_stabilize_count) {
                return new ClusterLimitViolation(
                        "message.createentitycontrol.cluster_blocked.reason.stability",
                        Integer.toString(snapshot.totalStability),
                        Integer.toString(Config.block_entity_max_stabilize_count)
                );
            }

            AABB box = entity.getBoundingBox();
            int spanX = Math.max(1, (int) Math.ceil(box.getXsize()));
            int spanY = Math.max(1, (int) Math.ceil(box.getYsize()));
            int spanZ = Math.max(1, (int) Math.ceil(box.getZsize()));

            if (spanX > Config.blockEntityXZMaxLength) {
                return new ClusterLimitViolation(
                        "message.createentitycontrol.cluster_blocked.reason.span_x",
                        Integer.toString(spanX),
                        Integer.toString(Config.blockEntityXZMaxLength)
                );
            }

            if (spanY > Config.blockEntityYMaxLength) {
                return new ClusterLimitViolation(
                        "message.createentitycontrol.cluster_blocked.reason.span_y",
                        Integer.toString(spanY),
                        Integer.toString(Config.blockEntityYMaxLength)
                );
            }

            if (spanZ > Config.blockEntityXZMaxLength) {
                return new ClusterLimitViolation(
                        "message.createentitycontrol.cluster_blocked.reason.span_z",
                        Integer.toString(spanZ),
                        Integer.toString(Config.blockEntityXZMaxLength)
                );
            }
        }

        return null;
    }

    private static void freeze(AbstractContraptionEntity entity) {
        entity.setContraptionMotion(Vec3.ZERO);
        entity.setDeltaMovement(Vec3.ZERO);

        if (entity instanceof CarriageContraptionEntity carriageEntity) {
            freezeTrain(carriageEntity);
        }

        Entity vehicle = entity.getVehicle();
        if (vehicle instanceof AbstractMinecart minecart) {
            minecart.setDeltaMovement(Vec3.ZERO);
        }
    }

    private static void freezeTrain(CarriageContraptionEntity carriageEntity) {
        Carriage carriage = carriageEntity.getCarriage();
        if (carriage == null || carriage.train == null) {
            return;
        }

        Train train = carriage.train;
        train.speed = 0.0D;
        train.targetSpeed = 0.0D;
        train.throttle = 0.0D;
        train.speedBeforeStall = 0.0D;
        train.manualTick = false;
        train.manualSteer = TravellingPoint.SteerDirection.NONE;
        train.backwardsDriver = null;

        for (Carriage trainCarriage : train.carriages) {
            trainCarriage.blocked = true;
            trainCarriage.stalled = true;
        }

        if (Config.debug) {
            LOGGER.info(
                    "cluster-train-freeze trainId={} carriageIndex={} speed={} targetSpeed={} throttle={}",
                    train.id,
                    carriageEntity.carriageIndex,
                    train.speed,
                    train.targetSpeed,
                    train.throttle
            );
        }
    }

    private static void notifyPlayers(Set<AbstractContraptionEntity> cluster, ClusterStats stats, ClusterLimitViolation violation, long gameTime) {
        if (cluster.isEmpty()) {
            return;
        }

        AbstractContraptionEntity sample = cluster.iterator().next();
        Component location = buildLocationComponent(stats, sample);
        String clusterKey = createClusterKey(cluster);
        long localNotifyUntil = LOCAL_NOTIFY_UNTIL.getOrDefault(clusterKey, Long.MIN_VALUE);
        if (localNotifyUntil < gameTime) {
            LOCAL_NOTIFY_UNTIL.put(clusterKey, gameTime + LOCAL_NOTIFY_COOLDOWN);
            Vec3 center = stats.center();
            Component nearbyMessage = gold(Component.translatable(
                    "message.createentitycontrol.cluster_blocked.nearby.actionbar",
                    Component.translatable(violation.translationKey, violation.arguments)
            ));

            AABB notifyBox = new AABB(center, center).inflate(Config.contraption_cluster_local_notify_radius);
            for (ServerPlayer player : sample.level.getEntitiesOfClass(ServerPlayer.class, notifyBox, ServerPlayer::isAlive)) {
                if (isLookingAtCluster(player, cluster)) {
                    continue;
                }
                player.displayClientMessage(nearbyMessage, true);
            }
        }

        syncOverlay(cluster, violation, location, sample);

        MinecraftServer server = sample.level.getServer();
        if (server == null) {
            return;
        }

        List<ServerPlayer> readyPlayers = getReadyPlayers(server);
        if (readyPlayers.isEmpty()) {
            if (Config.debug) {
                LOGGER.info(
                        "cluster-global-notify deferred clusterKey={} reason=no_ready_players minTicks={}",
                        clusterKey,
                        GLOBAL_NOTIFY_PLAYER_READY_TICKS
                );
            }
            return;
        }

        long globalNotifyUntil = GLOBAL_NOTIFY_UNTIL.getOrDefault(clusterKey, Long.MIN_VALUE);
        if (globalNotifyUntil < gameTime) {
            GLOBAL_NOTIFY_UNTIL.put(clusterKey, gameTime + GLOBAL_NOTIFY_COOLDOWN);
            Component nearbyPlayers = buildNearbyPlayersComponent(stats, sample);
            List<Component> globalLines = buildGlobalNotificationLines(violation, location, nearbyPlayers);
            for (ServerPlayer player : readyPlayers) {
                if (Config.debug) {
                    LOGGER.info(
                            "cluster-global-notify deliver clusterKey={} player={} lines={}",
                            clusterKey,
                            player.getGameProfile().getName(),
                            globalLines.size()
                    );
                }
                for (Component line : globalLines) {
                    player.sendSystemMessage(line);
                }
            }
            if (Config.debug) {
                LOGGER.info("cluster-global-notify sent clusterKey={} nextAllowedTick={}", clusterKey, gameTime + GLOBAL_NOTIFY_COOLDOWN);
            }
        }
    }

    private static List<Component> buildGlobalNotificationLines(ClusterLimitViolation violation, Component location, Component nearbyPlayers) {
        MutableComponent title = gold(Component.translatable("message.createentitycontrol.cluster_blocked.global.title"));
        MutableComponent reason = gold(Component.translatable(
                "message.createentitycontrol.cluster_blocked.global.reason",
                Component.translatable(violation.translationKey, violation.arguments)
        ));
        MutableComponent locationLine = gold(Component.translatable("message.createentitycontrol.cluster_blocked.global.location", location));
        MutableComponent playersLine = gold(Component.translatable("message.createentitycontrol.cluster_blocked.global.players", nearbyPlayers));
        return List.of(title, reason, locationLine, playersLine);
    }

    private static void syncOverlay(Set<AbstractContraptionEntity> cluster, ClusterLimitViolation violation, Component location, AbstractContraptionEntity sample) {
        Vec3 center = sample.position();
        AABB notifyBox = new AABB(center, center).inflate(Config.contraption_cluster_local_notify_radius);
        List<Integer> entityIds = new ArrayList<>(cluster.size());
        for (AbstractContraptionEntity entity : cluster) {
            entityIds.add(entity.getId());
        }

        ClusterBlockedSyncPacket packet = new ClusterBlockedSyncPacket(
                entityIds,
                Component.translatable(violation.translationKey, violation.arguments),
                location,
                OVERLAY_SYNC_DURATION
        );

        for (ServerPlayer player : sample.level.getEntitiesOfClass(ServerPlayer.class, notifyBox, ServerPlayer::isAlive)) {
            CreateEntityControlNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    private static MutableComponent gold(MutableComponent component) {
        return component.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(HINT_GOLD)));
    }

    private static String createClusterKey(Set<AbstractContraptionEntity> cluster) {
        List<String> ids = new ArrayList<>();
        for (AbstractContraptionEntity entity : cluster) {
            ids.add(entity.getUUID().toString());
        }
        ids.sort(String::compareTo);
        return String.join("|", ids);
    }

    private static void mergeCounts(Map<String, Integer> merged, Map<String, Integer> source) {
        for (Map.Entry<String, Integer> entry : source.entrySet()) {
            merged.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    private static int calculateStability(Map<String, Integer> blockCounts) {
        int totalStability = 0;
        for (Map.Entry<String, Integer> entry : blockCounts.entrySet()) {
            totalStability += entry.getValue() * resolveStability(entry.getKey());
        }
        return totalStability;
    }

    private static int resolveStability(String blockName) {
        for (List<Object> limitEntry : Config.blocksLimitValues) {
            if (limitEntry.size() > 2 && limitEntry.get(0).equals(blockName)) {
                return (Integer) limitEntry.get(2);
            }
        }
        return 100;
    }

    private static String extractBlockName(StructureTemplate.StructureBlockInfo blockInfo) {
        return blockInfo.state.getBlock().toString().replaceAll("Block\\{(.*?)\\}", "$1");
    }

    private static Component translateBlockName(String blockName) {
        ResourceLocation id = ResourceLocation.tryParse(blockName);
        if (id == null) {
            return Component.literal(blockName);
        }

        Block block = ForgeRegistries.BLOCKS.getValue(id);
        if (block == null) {
            return Component.literal(blockName);
        }

        return block.getName();
    }

    private static Component buildLocationComponent(ClusterStats stats, AbstractContraptionEntity sample) {
        Vec3 center = stats.center();
        BlockPos pos = new BlockPos(
                (int) Math.floor(center.x),
                (int) Math.floor(center.y),
                (int) Math.floor(center.z)
        );
        String dimension = sample.level.dimension().location().toString();
        return Component.translatable(
                "message.createentitycontrol.cluster_blocked.location",
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                dimension
        );
    }

    private static boolean isLookingAtCluster(ServerPlayer player, Set<AbstractContraptionEntity> cluster) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(64.0D));
        AABB searchBox = player.getBoundingBox().expandTowards(player.getLookAngle().scale(64.0D)).inflate(4.0D);

        for (AbstractContraptionEntity entity : cluster) {
            AABB box = entity.getBoundingBox().inflate(1.0D);
            if (!searchBox.intersects(box)) {
                continue;
            }
            if (box.clip(start, end).isPresent()) {
                return true;
            }
        }
        return false;
    }

    private static List<ServerPlayer> getReadyPlayers(MinecraftServer server) {
        List<ServerPlayer> readyPlayers = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.isAlive() && player.tickCount >= GLOBAL_NOTIFY_PLAYER_READY_TICKS) {
                readyPlayers.add(player);
            }
        }
        return readyPlayers;
    }

    private static Component buildNearbyPlayersComponent(ClusterStats stats, AbstractContraptionEntity sample) {
        Vec3 center = stats.center();
        AABB searchBox = new AABB(center, center).inflate(Config.contraption_cluster_global_nearby_players_radius);
        List<ServerPlayer> players = sample.level.getEntitiesOfClass(ServerPlayer.class, searchBox, ServerPlayer::isAlive);
        if (players.isEmpty()) {
            return Component.translatable("message.createentitycontrol.cluster_blocked.global.players.none");
        }

        players.sort((a, b) -> Double.compare(a.distanceToSqr(center), b.distanceToSqr(center)));
        List<String> names = new ArrayList<>();
        for (int i = 0; i < players.size() && i < 3; i++) {
            names.add(players.get(i).getGameProfile().getName());
        }
        return Component.literal(String.join(", ", names));
    }

    private static final class ContraptionSnapshot {
        private final Map<String, Integer> blockCounts;
        private final int totalBlocks;
        private final int totalStability;
        private final long expiresAtTick;

        private ContraptionSnapshot(Map<String, Integer> blockCounts, int totalBlocks, int totalStability, long expiresAtTick) {
            this.blockCounts = blockCounts;
            this.totalBlocks = totalBlocks;
            this.totalStability = totalStability;
            this.expiresAtTick = expiresAtTick;
        }
    }

    private static final class ClusterStats {
        private final Map<String, Integer> blockCounts;
        private final int totalBlocks;
        private final int totalStability;
        private final double minX;
        private final double minY;
        private final double minZ;
        private final double maxX;
        private final double maxY;
        private final double maxZ;

        private ClusterStats(Map<String, Integer> blockCounts, int totalBlocks, int totalStability,
                             double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            this.blockCounts = blockCounts;
            this.totalBlocks = totalBlocks;
            this.totalStability = totalStability;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        private int spanX() {
            return (int) Math.ceil(maxX - minX);
        }

        private int spanY() {
            return (int) Math.ceil(maxY - minY);
        }

        private int spanZ() {
            return (int) Math.ceil(maxZ - minZ);
        }

        private Vec3 center() {
            return new Vec3((minX + maxX) / 2.0D, (minY + maxY) / 2.0D, (minZ + maxZ) / 2.0D);
        }
    }

    private static final class ClusterLimitViolation {
        private final String translationKey;
        private final Object[] arguments;

        private ClusterLimitViolation(String translationKey, Object... arguments) {
            this.translationKey = translationKey;
            this.arguments = arguments;
        }
    }
}
