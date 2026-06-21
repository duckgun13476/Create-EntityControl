package com.Pink_Cats.createentitycontrol;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {


    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<List<? extends List<?>>> BLOCKS_LIMIT;

    static {
        BUILDER.push("blocks_limit")
                .comment("--------------------------------------------------------------------------")
                .comment("The count limit of blocks that are used in block entities. The first para is count limit and the second para is stabilize para.");
        BLOCKS_LIMIT = BUILDER
                .defineList("blocks_limit", Arrays.asList
                                (
                                        Arrays.asList("create:deployer", 256, 100),
                                        Arrays.asList("create:mechanical_drill", 256, 100),
                                        Arrays.asList("create:mechanical_roller", 64, 100),
                                        Arrays.asList("minecraft:dirt", 2048, 10000),
                                        Arrays.asList("create:linear_chassis", 2048, -100),
                                        Arrays.asList("minecraft:netherrack", 2048, 5000),
                                        Arrays.asList("minecraft:stone", 2048, 4000),
                                        Arrays.asList("minecraft:deepslate", 2048, 4000),
                                        Arrays.asList("minecraft:end_stone", 2048, 4000),
                                        Arrays.asList("minecraft:white_wool", 2048, 80),
                                        Arrays.asList("create:item_vault", 2048, 360),
                                        Arrays.asList("metalbarrels:obsidian_barrel", 2048, 1800),
                                        Arrays.asList("metalbarrels:diamond_barrel", 2048, 1800),
                                        Arrays.asList("metalbarrels:crystal_barrel", 2048, 1800),
                                        Arrays.asList("metalbarrels:netherite_barrel", 2048, 2000),
                                        Arrays.asList("metalbarrels:gold_barrel", 2048, 1100),
                                        Arrays.asList("metalbarrels:iron_barrel", 2048, 800),
                                        Arrays.asList("metalbarrels:copper_barrel", 2048, 700),
                                        Arrays.asList("minecraft:barrel", 2048, 400),
                                        Arrays.asList("minecraft:chest", 2048, 800),
                                        Arrays.asList("twilightforest:twilight_oak_chest", 2048, 800),
                                        Arrays.asList("twilightforest:canopy_chest", 2048, 800),
                                        Arrays.asList("twilightforest:mangrove_chest", 2048, 800),
                                        Arrays.asList("twilightforest:dark_chest", 2048, 800),
                                        Arrays.asList("twilightforest:time_chest", 2048, 800),
                                        Arrays.asList("twilightforest:transformation_chest", 2048, 800),
                                        Arrays.asList("twilightforest:mining_chest", 2048, 800),
                                        Arrays.asList("twilightforest:sorting_chest", 2048, 800),
                                        Arrays.asList("ae2:smooth_sky_stone_chest", 2048, 800),
                                        Arrays.asList("quark:ancient_chest", 2048, 800),
                                        Arrays.asList("quark:azalea_chest", 2048, 800),
                                        Arrays.asList("quark:blossom_chest", 2048, 800),
                                        Arrays.asList("quark:oak_chest", 2048, 800),
                                        Arrays.asList("quark:spruce_chest", 2048, 800),
                                        Arrays.asList("quark:birch_chest", 2048, 800),
                                        Arrays.asList("quark:jungle_chest", 2048, 800),
                                        Arrays.asList("quark:acacia_chest", 2048, 800),
                                        Arrays.asList("quark:dark_oak_chest", 2048, 800),
                                        Arrays.asList("quark:crimson_chest", 2048, 800),
                                        Arrays.asList("quark:mangrove_chest", 2048, 800),
                                        Arrays.asList("quark:cherry_chest", 2048, 800)
                                ),
                        Config::validateLimitEntry);

    }


    private static final ForgeConfigSpec.BooleanValue KEEP_STRUCTURE_AT_FIRST =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("Default on this config will ignore one entity crush at first time.")
                    .comment("This can keep miner not broken at the first time crush for helping player.")
                    .define("keep_structure_at_first", true);

    private static final ForgeConfigSpec.IntValue KEEP_STRUCTURE_REFRESH_TIME =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("The time record list refresh default 3600s")
                    .comment("If a structure is ignored at first time and if the second time less than 3600s")
                    .comment("The structure will controlled by this mod while more than 3600s will controlled by Create.")
                    .defineInRange("keep structure refresh time(s)", 3600, 0, 36000);

    private static final ForgeConfigSpec.BooleanValue DEBUG_BLOCK_ENTITY_PROBLEM =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("Whether to log the block entity problem if it can't turned into block entities.")
                    .define("Log block entity problem", false);

    private static final ForgeConfigSpec.BooleanValue DEBUG =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("Whether to log debug information for extra runtime control logic such as contraption clusters.")
                    .define("debug", false);

    private static final ForgeConfigSpec.BooleanValue MINECART_PROTECTION =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("Protect vanilla minecarts from lava and fire damage, and reduce non-player incoming damage to 25%.")
                    .comment("Player attacks keep vanilla damage so players can still break minecarts normally.")
                    .define("minecart protection", true);

    private static final ForgeConfigSpec.IntValue SQUEEZE_DESTROY_SPEED =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("10x of destroy speed | If you set 14 and will calculate 1.4 as destroy speed ")
                    .comment("Exp: obsidian is 40 and 400 in this case, dirt is 0.5 and 5 set in this case. ")
                    .comment("default value is suggested because dirt and sand will drop but stone will stay")
                    .defineInRange("10% of destroy speed", 14, 0, Integer.MAX_VALUE);


    private static final ForgeConfigSpec.IntValue MECHANICAL_BEARING_GEAR_MAX_SPEED =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("Dedicated max speed for Mechanical Bearing rotation.")
                    .comment("0 disables this cap. If Create provides 256 and you set 128, the bearing rotates at 128.")
                    .comment("If you set a value higher than the original Create speed, the original speed is kept.")
                    .defineInRange("mechanical bearing gear max speed", 128, 0, Integer.MAX_VALUE);


    private static final ForgeConfigSpec.IntValue BLOCK_ENTITY_MAX_XZ_LENGTH =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("The longest XZ distance of block entity | If set 20: 17*42*20 is allowed but 20*42*21 is forbidden.")
                    .defineInRange("block entity max length XZ", 40, 3, 500);

    private static final ForgeConfigSpec.IntValue BLOCK_ENTITY_MAX_Y_LENGTH =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("The longest Y  distance of block entity | If set 20: 42*14*42 is allowed but 42*24*42 is forbidden.")
                    .defineInRange("block entity max length Y", 60, 3, 500);

    private static final ForgeConfigSpec.DoubleValue CONTRAPTION_CLUSTER_SCAN_RADIUS =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("The scan radius used by contraption cluster detection.")
                    .comment("Only contraptions within this radius will be merged into one cluster for cluster-level limit checks.")
                    .defineInRange("contraption cluster scan radius", 96.0D, 1.0D, 512.0D);

    private static final ForgeConfigSpec.DoubleValue CONTRAPTION_CLUSTER_LOCAL_NOTIFY_RADIUS =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("The local notify radius for nearby actionbar hints and overlay sync.")
                    .defineInRange("contraption cluster local notify radius", 32.0D, 1.0D, 512.0D);

    private static final ForgeConfigSpec.DoubleValue CONTRAPTION_CLUSTER_GLOBAL_NEARBY_PLAYERS_RADIUS =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("The search radius used to list nearby players inside the global cluster-blocked message.")
                    .comment("This does not change who receives the global message.")
                    .defineInRange("contraption cluster global nearby players radius", 32.0D, 1.0D, 512.0D);

    private static final ForgeConfigSpec.DoubleValue CONTRAPTION_CLUSTER_BLOCK_LIMIT_MULTIPLIER =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("Multiplier applied to cluster-level aggregated block limits from blocks_limit.")
                    .comment("Single-contraption limits stay unchanged; only the cluster total uses this multiplier.")
                    .defineInRange("contraption cluster block limit multiplier", 1.5D, 0.0D, 64.0D);

    private static final ForgeConfigSpec.BooleanValue CONTRAPTION_CLUSTER_CHAIN_DETECTION =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("Enable chained cluster detection for nearby multiple contraptions.")
                    .comment("This can detect multi-entity clusters, but it costs some extra performance.")
                    .define("contraption cluster chain detection", true);

    private static final ForgeConfigSpec.IntValue CONTRAPTION_CLUSTER_SCAN_INTERVAL_SECONDS =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("How often contraption cluster validation runs, in seconds.")
                    .comment("This controls the periodic cluster scan cooldown added for performance control.")
                    .defineInRange("contraption cluster scan interval seconds", 1, 1, 10);

    private static final ForgeConfigSpec.BooleanValue CONTRAPTION_CLUSTER_GLOBAL_NOTIFY_ENABLED =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("Enable global cluster-blocked chat notifications. Nearby hints still work when this is false.")
                    .define("contraption cluster global notify enabled", false);

    private static final ForgeConfigSpec.IntValue CONTRAPTION_CLUSTER_GLOBAL_NOTIFY_COOLDOWN_MINUTES =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("Cooldown for global cluster-blocked chat notifications, in minutes.")
                    .defineInRange("contraption cluster global notify cooldown minutes", 5, 1, 60);


    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKS_STRING =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("A list of blocks can't be moved and change into block entities.")
                    .defineListAllowEmpty("blocks_unmoved", List.of(
                            "create:mechanical_crafter", "supplementaries:notice_board",
                            "minecraft:cobblestone", "minecraft:stone",
                            "minecraft:deepslate", "create:belt",
                            "lightmanscurrency:coin_chest", "create:white_toolbox",
                            "create:orange_toolbox", "create:magenta_toolbox",
                            "create:yellow_toolbox", "create:lime_toolbox",
                            "create:brown_toolbox", "create:pink_toolbox",
                            "create:gray_toolbox", "create:light_gray_toolbox",
                            "create:light_blue_toolbox", "create:blue_toolbox",
                            "create:purple_toolbox", "create:cyan_toolbox",
                            "create:green_toolbox", "create:red_toolbox",
                            "create:black_toolbox", "minecraft:spawner",
                            "mynethersdelight:powdery_cannon","butchercraft:meat_hook_item"
                    ), Config::validateItemName);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCKS_UNCRUSHABLE =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("A list of blocks can't be crushed by block entities. ")
                    .comment("If you squeeze a stone with dirt and stone in this list the dirt will drop while stone leave!")
                    .defineListAllowEmpty("blocks_uncrushable", List.of(
                            "minecraft:deepslate","minecraft:stone","minecraft:cobblestone"
                    ), Config::validateItemName);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCKS_UNCRUSHABLE_IGNORE =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("A list of blocks can be crushed by block entities.")
                    .comment("Create's default config.")
                    .comment("If you squeeze a stone with dirt and stone in this list the stone will drop while dirt leave!")
                    .comment("If you add fluid, these fluid will vanish like create vanilla")
                    .comment("Warning:Block burned by lava might be conflict with player,you can add minecraft:lava to avoid block burned by lava!"
                    )
                    .defineListAllowEmpty("blocks_crushable", List.of(
                            "minecraft:water",
                            "minecraft:lava"
                    ), Config::validateItemName);



    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCKS_IGNORE =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("A list of blocks will be ignored by entity control")
                    .comment("Some block entity use the same func but they have unique method and para.")
                    .comment("Add all of them in this list will ignore these entity")
                    .comment("default config compat with Create:big cannon! Just add new block not remove them!  ")
                    .comment("Warning! Make sure these new added block won't with bug!")
                    .defineListAllowEmpty("blocks_ignore", List.of(
                            "create:piston_extension_pole", "createbigcannons:ap_shot", "createbigcannons:powder_charge", "createbigcannons:ram_head",
                            "createbigcannons:worm_head","createbigcannons:solid_shot","createbigcannons:ap_shot","createbigcannons:mortar_stone",
                            "createbigcannons:bag_of_grapeshot","createbigcannons:he_shell","createbigcannons:ap_shell","createbigcannons:shrapnel_shell",
                            "createbigcannons:fluid_shell","createbigcannons:smoke_shell"

                    ), Config::validateItemName);






    private static final ForgeConfigSpec.BooleanValue ENABLE_BLOCK_EXPERIMENT_PARA =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("When enabled, blocks will calculate the experiment para for block entities.This might take very little time.")
                    .define("calculate block stabilize para", true);

    private static final ForgeConfigSpec.IntValue BLOCK_ENTITY_MAX_STABILIZE_COUNT =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("The max limit of stabilize para. One normal block have 100 default stabilize count.")
                    .comment("Set depend on storage box can avoid block entities NBT overflow.")
                    .defineInRange("block entity max stabilize para", 204800, 0, Integer.MAX_VALUE);




    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean debug_block_entity_problem;
    public static boolean debug;
    public static boolean minecart_protection;
    public static int blockEntityXZMaxLength;
    public static int block_entity_max_stabilize_count;
    public static boolean enableBlockEntityExperimentPara;
    public static boolean keep_structure_at_first;
    public static int blockEntityYMaxLength;
    public static int keep_structure_refresh_time;
    public static float squeeze_destroy_speed;
    public static int mechanical_bearing_gear_max_speed;
    public static double contraption_cluster_scan_radius;
    public static double contraption_cluster_local_notify_radius;
    public static double contraption_cluster_global_nearby_players_radius;
    public static double contraption_cluster_block_limit_multiplier;
    public static boolean contraption_cluster_chain_detection;
    public static int contraption_cluster_scan_interval_seconds;
    public static boolean contraption_cluster_global_notify_enabled;
    public static int contraption_cluster_global_notify_cooldown_minutes;
    public static Set<String> blocks_uncrushable; // 定义为 Set<String>
    public static Set<String> blocks_uncrushableIgnore;
    public static Set<String> blocks_unmoved; // 定义为 Set<String>
    public static List<List<Object>> blocksLimitValues;
    public static Set<String> blocks_ignore;


    private static boolean validateItemName(final Object obj) {
        return validateBlockSelector(obj);
    }

    private static boolean validateLimitEntry(final Object obj) {
        if (!(obj instanceof List<?> list) || list.size() < 2) {
            return false;
        }
        return validateBlockSelector(list.get(0)) && list.get(1) instanceof Number
                && (list.size() < 3 || list.get(2) instanceof Number);
    }

    private static boolean validateBlockSelector(final Object obj) {
        if (!(obj instanceof String itemName)) {
            return false;
        }
        if (itemName.startsWith("#")) {
            return ResourceLocation.tryParse(itemName.substring(1)) != null;
        }

        ResourceLocation id = ResourceLocation.tryParse(itemName);
        return id != null && BuiltInRegistries.BLOCK.containsKey(id);
    }

    public static String blockName(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
    }

    public static boolean matchesAnyBlockSelector(Collection<String> selectors, BlockState state) {
        for (String selector : selectors) {
            if (matchesBlockSelector(selector, state)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchesBlockSelector(String selector, String blockName) {
        ResourceLocation id = ResourceLocation.tryParse(blockName);
        if (id == null) {
            return selector.equals(blockName);
        }
        Block block = BuiltInRegistries.BLOCK.getOptional(id).orElse(null);
        return block != null ? matchesBlockSelector(selector, block.defaultBlockState()) : selector.equals(blockName);
    }

    public static boolean matchesBlockSelector(String selector, BlockState state) {
        if (selector == null || state == null) {
            return false;
        }
        if (!selector.startsWith("#")) {
            return selector.equals(blockName(state));
        }

        ResourceLocation tagId = ResourceLocation.tryParse(selector.substring(1));
        if (tagId == null) {
            return false;
        }
        if (state.is(TagKey.create(Registries.BLOCK, tagId))) {
            return true;
        }

        Item item = state.getBlock().asItem();
        return item != Items.AIR && item.builtInRegistryHolder().is(TagKey.create(Registries.ITEM, tagId));
    }

    public static int countMatchingBlocks(Map<String, Integer> blockCounts, String selector) {
        int total = 0;
        for (Map.Entry<String, Integer> entry : blockCounts.entrySet()) {
            if (matchesBlockSelector(selector, entry.getKey())) {
                total += entry.getValue();
            }
        }
        return total;
    }

    public static int resolveStability(String blockName) {
        for (List<Object> limitValue : blocksLimitValues) {
            if (limitValue.size() > 2 && matchesBlockSelector((String) limitValue.get(0), blockName)) {
                return ((Number) limitValue.get(2)).intValue();
            }
        }
        return 100;
    }
    public static void load() {
        debug_block_entity_problem = DEBUG_BLOCK_ENTITY_PROBLEM.get();
        debug = DEBUG.get();
        minecart_protection = MINECART_PROTECTION.get();
        squeeze_destroy_speed = SQUEEZE_DESTROY_SPEED.get().floatValue()/10;
        mechanical_bearing_gear_max_speed = MECHANICAL_BEARING_GEAR_MAX_SPEED.get();
        blockEntityYMaxLength = BLOCK_ENTITY_MAX_Y_LENGTH.get();
        blockEntityXZMaxLength = BLOCK_ENTITY_MAX_XZ_LENGTH.get();
        contraption_cluster_scan_radius = CONTRAPTION_CLUSTER_SCAN_RADIUS.get();
        contraption_cluster_local_notify_radius = CONTRAPTION_CLUSTER_LOCAL_NOTIFY_RADIUS.get();
        contraption_cluster_global_nearby_players_radius = CONTRAPTION_CLUSTER_GLOBAL_NEARBY_PLAYERS_RADIUS.get();
        contraption_cluster_block_limit_multiplier = CONTRAPTION_CLUSTER_BLOCK_LIMIT_MULTIPLIER.get();
        contraption_cluster_chain_detection = CONTRAPTION_CLUSTER_CHAIN_DETECTION.get();
        contraption_cluster_scan_interval_seconds = CONTRAPTION_CLUSTER_SCAN_INTERVAL_SECONDS.get();
        contraption_cluster_global_notify_enabled = CONTRAPTION_CLUSTER_GLOBAL_NOTIFY_ENABLED.get();
        contraption_cluster_global_notify_cooldown_minutes = CONTRAPTION_CLUSTER_GLOBAL_NOTIFY_COOLDOWN_MINUTES.get();
        block_entity_max_stabilize_count = BLOCK_ENTITY_MAX_STABILIZE_COUNT.get();
        enableBlockEntityExperimentPara = ENABLE_BLOCK_EXPERIMENT_PARA.get();
        blocksLimitValues = new ArrayList<>();
        for (List<?> list : BLOCKS_LIMIT.get()) {
            List<Object> castedList = new ArrayList<>(list);
            blocksLimitValues.add(castedList);
        }
        blocks_uncrushable = new HashSet<>(BLOCKS_UNCRUSHABLE.get());
        blocks_uncrushableIgnore = new HashSet<>(BLOCKS_UNCRUSHABLE_IGNORE.get());
        blocks_unmoved = new HashSet<>(BLACKS_STRING.get());
        blocks_ignore = new HashSet<>(BLOCKS_IGNORE.get());
        keep_structure_at_first = KEEP_STRUCTURE_AT_FIRST.get();
        keep_structure_refresh_time = KEEP_STRUCTURE_REFRESH_TIME.get();
    }
}


