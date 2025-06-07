package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.george.peony.data.tag.PeonyBlockTagsProvider;

public class PeonyDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();

		pack.addProvider(PeonyModelProvider::new);
		pack.addProvider(PeonyEnglishTranslationProvider::new);
		pack.addProvider(PeonyChineseTranslationProvider::new);
		pack.addProvider(PeonyBlockTagsProvider::new);
		pack.addProvider(PeonyBlockLootTableProvider::new);
		pack.addProvider(PeonyRecipeProvider::new);
	}
}
