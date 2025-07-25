package com.Pink_Cats.createentitycontroller;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = createentitycontroller.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
                                        Arrays.asList("minecraft:dirt", 2048, 10000),
                                        Arrays.asList("create:linear_chassis", 2048, -100),
                                        Arrays.asList("minecraft:netherrack", 2048, 5000)
                                ),
                        it -> it instanceof List && ((List<?>) it).get(0) instanceof String && ((List<?>) it).get(1) instanceof Number);

    }



    private static final ForgeConfigSpec.BooleanValue DEBUG_BLOCK_ENTITY_PROBLEM =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("Whether to log the block entity problem if it can't turned into block entities.")
                    .define("Log block entity problem", false);


    private static final ForgeConfigSpec.IntValue SQUEEZE_DESTROY_SPEED =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("10x of destroy speed | If you set 14 and will calculate 1.4 as destroy speed ")
                    .comment("Exp: obsidian is 40 and 400 in this case, dirt is 0.5 and 5 set in this case. ")
                    .comment("default value is suggested because dirt and sand will drop but stone will stay")
                    .defineInRange("10% of destroy speed", 14, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue BLOCK_ENTITY_MAX_XZ_LENGTH =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("The longest XZ distance of block entity | If set 20: 17*42*20 is allowed but 20*42*21 is forbidden.")
                    .defineInRange("block entity max length XZ", 40, 3, 500);

    private static final ForgeConfigSpec.IntValue BLOCK_ENTITY_MAX_Y_LENGTH =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("The longest Y  distance of block entity | If set 20: 42*14*42 is allowed but 42*24*42 is forbidden.")
                    .defineInRange("block entity max length Y", 60, 3, 500);


    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKS_STRING =
            BUILDER.comment("--------------------------------------------------------------------------")
                    .comment("A list of blocks can't be moved and change into block entities.")
                    .defineListAllowEmpty("blocks_unmoved", List.of(
                            "minecraft:deepslate","minecraft:stone","minecraft:cobblestone",
                            "create:belt","create:mechanical_crafter"
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
                            "minecraft:water"
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
    public static int blockEntityXZMaxLength;
    public static int block_entity_max_stabilize_count;
    public static boolean enableBlockEntityExperimentPara;
    public static int blockEntityYMaxLength;
    public static float squeeze_destroy_speed;
    public static Set<String> blocks_uncrushable; // 定义为 Set<String>
    public static Set<String> blocks_uncrushableIgnore;
    public static Set<String> blocks_unmoved; // 定义为 Set<String>
    public static List<List<Object>> blocksLimitValues;
    public static Set<String> blocks_ignore;


    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        Load_cec_config();

    }
    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        Load_cec_config();
    }

    private static void Load_cec_config() {
        debug_block_entity_problem = DEBUG_BLOCK_ENTITY_PROBLEM.get();
        squeeze_destroy_speed = SQUEEZE_DESTROY_SPEED.get().floatValue()/10;
        blockEntityYMaxLength = BLOCK_ENTITY_MAX_Y_LENGTH.get();
        blockEntityXZMaxLength = BLOCK_ENTITY_MAX_XZ_LENGTH.get();
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
    }
}