package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.george.peony.Peony;
import net.george.peony.block.PeonyJukeboxSongs;
import net.george.peony.misc.PeonyDamageTypes;
import net.george.peony.data.lang.PeonyChineseTranslationProvider;
import net.george.peony.data.lang.PeonyEnglishTranslationProvider;
import net.george.peony.data.tag.PeonyBlockTagsProvider;
import net.george.peony.data.tag.PeonyFluidTagsProvider;
import net.george.peony.data.tag.PeonyItemTagsProvider;
import net.george.peony.world.PeonyConfiguredFeatures;
import net.george.peony.world.PeonyPlacedFeatures;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class PeonyDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();

		pack.addProvider(PeonyModelProvider::new);
		pack.addProvider(PeonyEnglishTranslationProvider::new);
		pack.addProvider(PeonyChineseTranslationProvider::new);
		pack.addProvider(PeonyBlockLootTableProvider::new);
		pack.addProvider(PeonyRecipeProvider::new);
		pack.addProvider(PeonyRegistryDataProvider::new);

		pack.addProvider(PeonyItemTagsProvider::new);
		pack.addProvider(PeonyBlockTagsProvider::new);
		pack.addProvider(PeonyFluidTagsProvider::new);

		pack.addProvider(PeonyAdvancementProvider::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, PeonyConfiguredFeatures::boostrap);
		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, PeonyPlacedFeatures::boostrap);
		registryBuilder.addRegistry(RegistryKeys.JUKEBOX_SONG, PeonyJukeboxSongs::boostrap);
		registryBuilder.addRegistry(RegistryKeys.DAMAGE_TYPE, PeonyDamageTypes::bootstrap);
	}

	@Override
	public String getEffectiveModId() {
		return Peony.MOD_ID;
	}
}
