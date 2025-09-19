package com.Pink_Cats.createentitycontroller;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(createentitycontroller.MODID)
public class createentitycontroller {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "createentitycontroller";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();


    public createentitycontroller(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("Loading config");

        if (Config.debug_block_entity_problem) LOGGER.info("DEBUG BLOCK ENTITY_PROBLEM IS ENABLED");
        LOGGER.info("squeeze limit count > {}" ,Config.squeeze_destroy_speed);
        LOGGER.info("block count limit > {}", Config.blocksLimitValues);

    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)  {
        LOGGER.info("Create Overwrite Success!");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
           }
    }
}
