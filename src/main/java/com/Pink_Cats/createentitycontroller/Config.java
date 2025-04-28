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
@Mod.EventBusSubscriber(modid = Createentitycontroler.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<List<? extends List<?>>> BLOCKS_LIMIT;

    static {
        BUILDER.push("blocks_limit")
                .comment("The limit of blocks that are used in block entities");
        BLOCKS_LIMIT = BUILDER
                .defineList("blocks_limit", Arrays.asList
                        (
                            Arrays.asList("create:deployer", 256, 0),
                            Arrays.asList("create:mechanical_drill", 256, 0)
                ),
                        it -> it instanceof List && ((List<?>) it).get(0) instanceof String && ((List<?>) it).get(1) instanceof Number);

    }



    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);


    private static final ForgeConfigSpec.IntValue SQUEEZE_DESTROY_SPEED =
            BUILDER.comment("10x of destroy speed | If you set 14 and will calculate 1.4 as destroy speed ")
                    .defineInRange("10% of destroy speed", 14, 0, Integer.MAX_VALUE);


    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION =
            BUILDER.comment("What you want the introduction message to be for the magic number")
                    .define("magicNumberIntroduction", "The magic number is... ");


    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKS_STRING =
            BUILDER.comment("A list of blocks can't be moved and change into block entities.")
                    .defineListAllowEmpty("blocks_unmoved", List.of(
                            "minecraft:deepslate","minecraft:stone","minecraft:cobblestone",
                            "create:belt","create:mechanical_crafter"
                    ), Config::validateItemName);


    // a list of strings that are treated as resource locations for items
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCKS_UNCRUSHABLE =
            BUILDER.comment("A list of blocks can't be crushed by block entities.")
                    .defineListAllowEmpty("blocks_uncrushable", List.of(
                            "minecraft:deepslate","minecraft:stone","minecraft:cobblestone"

            ), Config::validateItemName);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean logDirtBlock;
    public static float squeeze_destroy_speed;
    public static String magicNumberIntroduction;
    public static Set<String> blocks_uncrushable; // 定义为 Set<String>
    public static Set<String> blocks_unmoved; // 定义为 Set<String>
    public static List<List<Object>> blocksLimitValues;

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
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
        logDirtBlock = LOG_DIRT_BLOCK.get();
        squeeze_destroy_speed = SQUEEZE_DESTROY_SPEED.get().floatValue()/10;
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
        blocksLimitValues = new ArrayList<>();
        for (List<?> list : BLOCKS_LIMIT.get()) {
            List<Object> castedList = new ArrayList<>(list);
            blocksLimitValues.add(castedList);
        }
        blocks_uncrushable = new HashSet<>(BLOCKS_UNCRUSHABLE.get());
        blocks_unmoved = new HashSet<>(BLACKS_STRING.get());
    }
}
