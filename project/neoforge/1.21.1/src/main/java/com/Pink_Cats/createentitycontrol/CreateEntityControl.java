package com.Pink_Cats.createentitycontrol;

import com.Pink_Cats.createentitycontrol.command.BlockifyContraptionCommand;
import com.Pink_Cats.createentitycontrol.network.CreateEntityControlNetwork;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateEntityControl.MODID)
public class CreateEntityControl {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "createentitycontrol";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();


    public CreateEntityControl(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(CreateEntityControlNetwork::register);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        if (Config.debug) {
            LOGGER.info("Loading config");
            LOGGER.info("DEBUG IS ENABLED");
            LOGGER.info("squeeze limit count > {}", Config.squeeze_destroy_speed);
            LOGGER.info("mechanical bearing gear max speed > {}", Config.mechanical_bearing_gear_max_speed);
            LOGGER.info("block count limit > {}", Config.blocksLimitValues);
        }

        if (Config.debug_block_entity_problem) {
            LOGGER.info("DEBUG BLOCK ENTITY_PROBLEM IS ENABLED");
        }
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)  {
        if (Config.debug) {
        LOGGER.info("Create Overwrite Success!");
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        BlockifyContraptionCommand.register(event.getDispatcher());
    }
}
