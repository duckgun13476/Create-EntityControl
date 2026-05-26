package com.Pink_Cats.createentitycontrol.addition;

import com.Pink_Cats.createentitycontrol.platform.StructureBlockInfoCompat;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.minecraft.world.level.block.Rotation.CLOCKWISE_90;

public class StructureFunc {

    public static Map<Block, List<int[]>> change_Template_to_Axis(Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks) {
        Map<Block, List<int[]>> axis_result = new HashMap<>();

        for (StructureTemplate.StructureBlockInfo block : blocks.values()) {
            Block blockType = StructureBlockInfoCompat.state(block).getBlock();
            BlockPos pos = StructureBlockInfoCompat.pos(block);
            int[] coordinates = new int[]{pos.getX(), pos.getY(), pos.getZ()};

            axis_result.computeIfAbsent(blockType, k -> new ArrayList<>()).add(coordinates);
        }

        return axis_result;
    }

    public static Map<Block, List<int[]>> reverseCoordinates(Map<Block, List<int[]>> original) {
        Map<Block, List<int[]>> reversed = new HashMap<>();

        for (Map.Entry<Block, List<int[]>> entry : original.entrySet()) {
            Block block = entry.getKey();
            List<int[]> originalCoords = entry.getValue();
            List<int[]> reversedCoords = new ArrayList<>();

            for (int[] coord : originalCoords) {
                int[] newCoord = new int[coord.length];
                newCoord[2] = coord[0];
                newCoord[0] = coord[2];
                newCoord[1] = coord[1];

                if (newCoord[0] > 0 && newCoord[2] > 0) {
                    newCoord[0] = -newCoord[0];
                } else if (newCoord[0] < 0 && newCoord[2] > 0) {
                    newCoord[0] = -newCoord[0];
                } else if (newCoord[0] < 0 && newCoord[2] < 0) {
                    newCoord[0] = -newCoord[0];
                } else if (newCoord[0] > 0 && newCoord[2] < 0) {
                    newCoord[0] = -newCoord[0];
                } else if (newCoord[0] > 0 && newCoord[2] == 0) {
                    newCoord[0] = -newCoord[0];
                } else if (newCoord[0] < 0 && newCoord[2] == 0) {
                    newCoord[0] = -newCoord[0];
                }

                reversedCoords.add(newCoord);
            }

            reversed.put(block, reversedCoords);
        }

        return reversed;
    }

    public static boolean StructureMatch(Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks, StructureTransform transform) {
        List<StructureBlockStorage.BlockInfoEntry> entries = StructureBlockStorage.getAllBlockEntries();

        for (StructureBlockStorage.BlockInfoEntry entry : entries) {
            if (blocks.size() == entry.getData().size()) {
                Map<Block, Integer> entryBlockCount = new HashMap<>();
                Map<Block, Integer> blockCount = new HashMap<>();

                for (StructureTemplate.StructureBlockInfo block : entry.getData().values()) {
                    Block blockType = StructureBlockInfoCompat.state(block).getBlock();
                    entryBlockCount.put(blockType, entryBlockCount.getOrDefault(blockType, 0) + 1);
                }

                for (StructureTemplate.StructureBlockInfo block : blocks.values()) {
                    Block blockType = StructureBlockInfoCompat.state(block).getBlock();
                    blockCount.put(blockType, blockCount.getOrDefault(blockType, 0) + 1);
                    BlockPos pos = StructureBlockInfoCompat.pos(block);
                    pos.rotate(CLOCKWISE_90);
                }

                boolean areBlocksEqual = entryBlockCount.equals(blockCount);
                if (areBlocksEqual) {
                    Map<Block, List<int[]>> exist_structure = change_Template_to_Axis(entry.getData());
                    Map<Block, List<int[]>> newer_structure = change_Template_to_Axis(blocks);
                    if (compareBlockCoordinates(exist_structure, newer_structure)) {
                        return true;
                    }
                    for (int i = 0; i < 3; i++) {
                        newer_structure = reverseCoordinates(newer_structure);
                        if (compareBlockCoordinates(exist_structure, newer_structure)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static String exist_structureToString(Map<Block, List<int[]>> structure) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry<Block, List<int[]>> entry : structure.entrySet()) {
            sb.append(entry.getKey()).append("=").append(formatCoordinates(entry.getValue())).append(", ");
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }

    public static String formatCoordinates(List<int[]> coords) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int[] coord : coords) {
            sb.append(Arrays.toString(coord)).append(", ");
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");
        return sb.toString();
    }

    public static boolean compareBlockCoordinates(Map<Block, List<int[]>> map1, Map<Block, List<int[]>> map2) {
        for (Map.Entry<Block, List<int[]>> entry : map1.entrySet()) {
            Block block = entry.getKey();
            List<int[]> coords1 = entry.getValue();

            if (map2.containsKey(block)) {
                List<int[]> coords2 = map2.get(block);

                Set<String> coordSet2 = new HashSet<>();
                for (int[] coord : coords2) {
                    coordSet2.add(Arrays.toString(coord));
                }

                for (int[] coord : coords1) {
                    if (!coordSet2.contains(Arrays.toString(coord))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
