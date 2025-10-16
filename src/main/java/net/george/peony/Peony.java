package net.george.peony;

import net.fabricmc.api.ModInitializer;
import net.george.peony.api.action.ActionType;
import net.george.peony.api.action.ActionTypes;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.entity.PeonyBlockEntities;
import net.george.peony.compat.PeonyCompat;
import net.george.peony.item.PeonyItems;
import net.george.peony.networking.PeonyNetworking;
import net.george.peony.recipe.PeonyRecipes;
import net.george.peony.sound.PeonySoundEvents;
import net.george.peony.world.PeonyFeatures;
import net.george.peony.world.gen.PeonyWorldGeneration;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Peony implements ModInitializer {
	public static final String MOD_ID = "peony";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PeonyNetworking.registerC2SPackets();
		PeonyItems.register();
		PeonyBlocks.register();
		PeonyBlockEntities.register();
		PeonyItemGroups.register();
		PeonyRecipes.register();
		PeonySoundEvents.register();
		ActionTypes.register();
		PeonyFeatures.register();
		PeonyCompat.register();
		PeonyWorldGeneration.generate();

		LOGGER.info("Hello Fabric world!");
	}

	public static void debug(String moduleName) {
		LOGGER.debug("Initializing " + moduleName + " for " + MOD_ID);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static <T> RegistryKey<T> key(RegistryKey<Registry<T>> registry, String path) {
		return RegistryKey.of(registry, id(path));
	}

	public static PeonyConfig getConfig() {
		return PeonyConfig.HANDLER.instance();
	}
}