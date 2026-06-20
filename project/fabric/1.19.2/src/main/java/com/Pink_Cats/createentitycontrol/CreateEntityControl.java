package com.Pink_Cats.createentitycontrol;

import com.Pink_Cats.createentitycontrol.command.BlockifyContraptionCommand;
import com.Pink_Cats.createentitycontrol.network.CreateEntityControlNetwork;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

public class CreateEntityControl implements ModInitializer {
    public static final String MODID = "createentitycontrol";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        FabricLoader.getInstance().getModContainer(MODID)
                .ifPresent(container -> new ModConfig(ModConfig.Type.COMMON, Config.SPEC, container));
        Config.load();
        CreateEntityControlNetwork.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                BlockifyContraptionCommand.register(dispatcher));
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            if (Config.debug) {
                LOGGER.info("Create Overwrite Success!");
            }
        });
        commonSetup();
    }

    private void commonSetup() {
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
}
