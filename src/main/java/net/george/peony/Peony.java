package net.george.peony;

import net.fabricmc.api.ModInitializer;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.entity.PeonyBlockEntities;
import net.george.peony.combat.PeonyCombat;
import net.george.peony.item.PeonyItems;
import net.george.peony.networking.PeonyNetworking;
import net.george.peony.recipe.PeonyRecipes;
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
		PeonyCombat.register();
		PeonyRecipes.register();

		LOGGER.info("Hello Fabric world!");
	}

	public static void debug(String moduleName) {
		LOGGER.debug("Initializing " + moduleName + " for " + MOD_ID);
	}

	public static Identifier id(String name) {
		return Identifier.of(MOD_ID, name);
	}

	public static <T> RegistryKey<T> key(RegistryKey<Registry<T>> registry, String name) {
		return RegistryKey.of(registry, id(name));
	}
}