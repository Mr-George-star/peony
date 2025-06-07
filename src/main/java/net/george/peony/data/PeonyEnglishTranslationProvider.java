package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class PeonyEnglishTranslationProvider extends FabricLanguageProvider {
    protected PeonyEnglishTranslationProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(PeonyItems.BARLEY, "Barley");
        translationBuilder.add(PeonyItems.BARLEY_SEEDS, "Barley Seeds");

        translationBuilder.add(PeonyBlocks.MILLSTONE, "Millstone");
        translationBuilder.add(PeonyBlocks.BARLEY_CROP, "Barley Crop");
        translationBuilder.add(PeonyBlocks.OAK_CUTTING_BOARD, "Oak Cutting Board");
        translationBuilder.add(PeonyBlocks.SPRUCE_CUTTING_BOARD, "Spruce Cutting Board");
        translationBuilder.add(PeonyBlocks.BIRCH_CUTTING_BOARD, "Birch Cutting Board");
        translationBuilder.add(PeonyBlocks.JUNGLE_CUTTING_BOARD, "Jungle Cutting Board");
        translationBuilder.add(PeonyBlocks.ACACIA_CUTTING_BOARD, "Acacia Cutting Board");
        translationBuilder.add(PeonyBlocks.DARK_OAK_CUTTING_BOARD, "Dark Oak Cutting Board");
        translationBuilder.add(PeonyBlocks.MANGROVE_CUTTING_BOARD, "Mangrove Cutting Board");
        translationBuilder.add(PeonyBlocks.DOUGH, "Dough");
        translationBuilder.add(PeonyBlocks.FLOUR, "Flour");

        translationBuilder.add(PeonyTranslationKeys.ITEM_GROUP_KEY, "Peony");
        translationBuilder.add(PeonyTranslationKeys.MILLING_RECIPE_CATEGORY_TITLE, "Milling");
        translationBuilder.add(PeonyTranslationKeys.MILLING_RECIPE_MILLING_TIMES, "Mill %d Times");
    }
}
