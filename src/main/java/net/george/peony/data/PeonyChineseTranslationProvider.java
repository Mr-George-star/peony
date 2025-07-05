package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.data.CraftingSteps;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class PeonyChineseTranslationProvider extends FabricLanguageProvider {
    protected PeonyChineseTranslationProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(PeonyItems.BARLEY, "大麦");
        translationBuilder.add(PeonyItems.BARLEY_SEEDS, "大麦种子");
        translationBuilder.add(PeonyItems.KITCHEN_KNIFE, "菜刀");

        translationBuilder.add(PeonyBlocks.MILLSTONE, "石磨");
        translationBuilder.add(PeonyBlocks.BARLEY_CROP, "大麦作物");
        translationBuilder.add(PeonyBlocks.OAK_CUTTING_BOARD, "橡木菜板");
        translationBuilder.add(PeonyBlocks.SPRUCE_CUTTING_BOARD, "云杉木菜板");
        translationBuilder.add(PeonyBlocks.BIRCH_CUTTING_BOARD, "白桦木菜板");
        translationBuilder.add(PeonyBlocks.JUNGLE_CUTTING_BOARD, "丛林木菜板");
        translationBuilder.add(PeonyBlocks.ACACIA_CUTTING_BOARD, "金合欢木菜板");
        translationBuilder.add(PeonyBlocks.DARK_OAK_CUTTING_BOARD, "深色橡木菜板");
        translationBuilder.add(PeonyBlocks.MANGROVE_CUTTING_BOARD, "红树木菜板");
        translationBuilder.add(PeonyBlocks.DOUGH, "面团");
        translationBuilder.add(PeonyBlocks.FLOUR, "面粉");

        translationBuilder.add(CraftingSteps.Procedure.KNEADING.getTranslationKey(), "揉捏");
        translationBuilder.add(CraftingSteps.Procedure.CUTTING.getTranslationKey(), "切");

        translationBuilder.add(PeonyTranslationKeys.ITEM_GROUP_KEY, "牡丹");

        translationBuilder.add(PeonyTranslationKeys.MILLING_RECIPE_CATEGORY_TITLE, "磨制");
        translationBuilder.add(PeonyTranslationKeys.MILLING_RECIPE_MILLING_TIMES, "研磨%d次");
        translationBuilder.add(PeonyTranslationKeys.SEQUENTIAL_CRAFTING_RECIPE_CATEGORY_TITLE, "顺序合成");
    }
}
