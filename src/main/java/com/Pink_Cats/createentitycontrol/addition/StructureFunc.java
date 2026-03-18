package com.Pink_Cats.createentitycontrol.addition;

import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.*;

import static net.minecraft.world.level.block.Rotation.CLOCKWISE_90;

public class StructureFunc {


    public static Map<Block, List<int[]>> change_Template_to_Axis(Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks) {
        Map<Block, List<int[]>> axis_result = new HashMap<>();

        for (StructureTemplate.StructureBlockInfo block : blocks.values()) {
            Block blockType = block.state.getBlock();
            BlockPos pos = block.pos;
            int[] coordinates = new int[]{pos.getX(), pos.getY(), pos.getZ()};

            // 检查该 Block 是否已经存在于 map 中
            //System.out.println("change_Template_to_Axis"+pos.getX() + " " + pos.getY() + " " + pos.getZ());
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

                    //System.out.println("start"+coord[0] + " " + coord[1] + " " + coord[2]);
                    int[] newCoord = new int[coord.length];
                    newCoord[2] = coord[0];
                    newCoord[0] = coord[2];  // Reverse pos Zero and First
                    newCoord[1] = coord[1];


                    //待用
                    if (newCoord[0] > 0 && newCoord[2] > 0) {
                        newCoord[0] = -newCoord[0];
                    } else if (newCoord[0] < 0 && newCoord[2] > 0) {
                        newCoord[0] = -newCoord[0];
                    } else if (newCoord[0] < 0 && newCoord[2] < 0) {
                        newCoord[0] = -newCoord[0];
                    } else if (newCoord[0] > 0 && newCoord[2] < 0) {
                        newCoord[0] = -newCoord[0];
                    } else if (newCoord[0] > 0 && (newCoord[2] == 0)) {
                        newCoord[0] = -newCoord[0]; // 如果 x 为正，y 为 0，反转 x
                    } else if (newCoord[0] < 0 && (newCoord[2] == 0)) {
                        newCoord[0] = -newCoord[0]; // 如果 x 为负，y 为 0，反转 x
                    }


                   // System.out.println("result"+newCoord[0] + " " + newCoord[1] + " " + newCoord[2]);
                    reversedCoords.add(newCoord);

            }

            reversed.put(block, reversedCoords);
        }

        return reversed;
    }




    public static boolean StructureMatch(Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks, StructureTransform transform) {
        List<StructureBlockStorage.BlockInfoEntry> entries = StructureBlockStorage.getAllBlockEntries();
        //for (StructureBlockStorage.BlockInfoEntry entry : entries) {
            //System.out.println("UUID: " + entry.getUuid() + ", Data: " + entry.getData() + ",Time" + entry.getTimestamp());



                //System.out.println("Data: " + entry.getData() );
        //}


        for (StructureBlockStorage.BlockInfoEntry entry : entries) {


            if (blocks.size() == entry.getData().size()) {
                //System.out.println("matchID"+ entry.getUuid());


                //System.out.println("exist"+ entry.getData());


                // 创建两个 Map 来存储方块及其数量
                Map<Block, Integer> entryBlockCount = new HashMap<>();
                Map<Block, Integer> blockCount = new HashMap<>();

                // 统计 entryData 中的方块数量
                for (StructureTemplate.StructureBlockInfo block : entry.getData().values()) {
                    Block blockType = block.state.getBlock();
                    entryBlockCount.put(blockType, entryBlockCount.getOrDefault(blockType, 0) + 1);
                }

                // 统计 blocks 中的方块数量
                for (StructureTemplate.StructureBlockInfo block : blocks.values()) {
                    Block blockType = block.state.getBlock();
                    blockCount.put(blockType, blockCount.getOrDefault(blockType, 0) + 1);
                    BlockPos pos = block.pos;
                    //System.out.println(pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
                    pos.rotate(CLOCKWISE_90);
                    //System.out.println(pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
                }

                // 输出方块数量
                //System.out.println("Entry Block Count: " + entryBlockCount);
                //System.out.println("Blocks Count: " + blockCount);

                // 比较两个方块数量的 Map
                boolean areBlocksEqual = entryBlockCount.equals(blockCount);
                //System.out.println("Are both block contents equal? " + areBlocksEqual);
                if(areBlocksEqual){

                    Map<Block, List<int[]>> exist_structure = change_Template_to_Axis(entry.getData());
                    Map<Block, List<int[]>> newer_structure = change_Template_to_Axis(blocks);
                    //System.out.println("exist" + exist_structureToString(exist_structure));
                    //System.out.println("new 0 " + exist_structureToString(newer_structure));
                    // 比较原始结构
                    if (compareBlockCoordinates(exist_structure, newer_structure)) {
                        return true;
                    }
                    // 反转坐标并比较
                    for (int i = 0; i < 3; i++) { // 旋转90度三次
                        newer_structure = reverseCoordinates(newer_structure);
                        //System.out.println("new 0 " + exist_structureToString(newer_structure));
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
            sb.setLength(sb.length() - 2); // 删除最后的 ", "
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
            sb.setLength(sb.length() - 2); // 删除最后的 ", "
        }
        sb.append("]");
        return sb.toString();
    }

    public static boolean compareBlockCoordinates(Map<Block, List<int[]>> map1, Map<Block, List<int[]>> map2) {


        for (Map.Entry<Block, List<int[]>> entry : map1.entrySet()) {
            Block block = entry.getKey();
            List<int[]> coords1 = entry.getValue();

            // 在第二个 map 中查找相同的 block
            if (map2.containsKey(block)) {
                List<int[]> coords2 = map2.get(block);

                // 将 coords2 转换为 Set 以便于查找
                Set<String> coordSet2 = new HashSet<>();
                for (int[] coord : coords2) {
                    coordSet2.add(Arrays.toString(coord));
                }

                // 比对 coords1 中的每个坐标，找出不在 coords2 中的坐标
                for (int[] coord : coords1) {
                    if (!coordSet2.contains(Arrays.toString(coord))) {

                        //System.out.println(block + " has a coordinate not present in the second map: " +
                                //Arrays.toString(coord));
                        return false;
                    }
                }
            }
        }
        return true;
    }




}
