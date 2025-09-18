package com.Pink_Cats.createentitycontroller.addition;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StructureBlockStorage {

    // 生成一个随机的 UUID
    public static UUID generateRandomUUID() {
        return UUID.randomUUID();
    }

    // 存储块信息和对应的UUID
    private static final List<BlockInfoEntry> blockEntries = new ArrayList<>(); // 使用可变的 ArrayList

    // 内部类，用于存储UUID、数据和时间戳
    public static class BlockInfoEntry {
        private final UUID uuid;
        private final Map<BlockPos, StructureTemplate.StructureBlockInfo> data; // 假设 data 是 Object 类型，您可以根据实际情况修改
        private final long timestamp; // 存储时间戳

        public BlockInfoEntry(UUID uuid, Map<BlockPos, StructureTemplate.StructureBlockInfo> data, long timestamp) {
            this.uuid = uuid;
            this.data = data;
            this.timestamp = timestamp;
        }

        public UUID getUuid() {
            return uuid;
        }

        public Map<BlockPos, StructureTemplate.StructureBlockInfo> getData() {
            return data;
        }

        public long getTimestamp() {
            return timestamp; // 返回时间戳
        }
    }

    //remove
    public static void removeOldEntries(long threshold) {
        long currentTimestamp = System.currentTimeMillis();
        blockEntries.removeIf(entry -> (currentTimestamp - entry.getTimestamp()) > threshold);
    }

    // 存储数据
    public static void storeData(UUID id, Map<BlockPos, StructureTemplate.StructureBlockInfo> data) {
        long currentTimestamp = System.currentTimeMillis(); // 获取当前时间戳
        blockEntries.add(new BlockInfoEntry(id, data, currentTimestamp));
    }

    // 根据UUID获取数据
    public static Map<BlockPos, StructureTemplate.StructureBlockInfo> getDataById(UUID id) {
        for (BlockInfoEntry entry : blockEntries) {
            if (entry.getUuid().equals(id)) {
                return entry.getData();
            }
        }
        return null; // 如果没有找到，返回 null
    }

    // 获取整个列表
    public static List<BlockInfoEntry> getAllBlockEntries() {
        return blockEntries;
    }

    // 获取存储的时间戳
    public static Long getTimestampById(UUID id) {
        for (BlockInfoEntry entry : blockEntries) {
            if (entry.getUuid().equals(id)) {
                return entry.getTimestamp(); // 返回时间戳
            }
        }
        return null; // 如果没有找到，返回 null
    }
}
