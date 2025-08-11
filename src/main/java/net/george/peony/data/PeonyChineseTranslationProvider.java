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
        translationBuilder.add(PeonyItems.LARD, "猪油");
        translationBuilder.add(PeonyItems.NATURE_GAS_DETECTOR, "天然气探测器");

        translationBuilder.add(PeonyItems.NATURE_GAS_BUCKET, "天然气桶");
        translationBuilder.add(PeonyItems.LARD_BUCKET, "猪油桶");

        translationBuilder.add(PeonyBlocks.MILLSTONE, "石磨");
        translationBuilder.add(PeonyBlocks.BARLEY_CROP, "大麦作物");
        translationBuilder.add(PeonyBlocks.OAK_CUTTING_BOARD, "橡木菜板");
        translationBuilder.add(PeonyBlocks.SPRUCE_CUTTING_BOARD, "云杉木菜板");
        translationBuilder.add(PeonyBlocks.BIRCH_CUTTING_BOARD, "白桦木菜板");
        translationBuilder.add(PeonyBlocks.JUNGLE_CUTTING_BOARD, "丛林木菜板");
        translationBuilder.add(PeonyBlocks.ACACIA_CUTTING_BOARD, "金合欢木菜板");
        translationBuilder.add(PeonyBlocks.CHERRY_CUTTING_BOARD, "樱花木菜板");
        translationBuilder.add(PeonyBlocks.DARK_OAK_CUTTING_BOARD, "深色橡木菜板");
        translationBuilder.add(PeonyBlocks.MANGROVE_CUTTING_BOARD, "红树木菜板");
        translationBuilder.add(PeonyBlocks.DOUGH, "面团");
        translationBuilder.add(PeonyBlocks.FLOUR, "面粉");
        translationBuilder.add(PeonyBlocks.OAK_LOG_STICK, "橡木原木棍");
        translationBuilder.add(PeonyBlocks.OAK_POT_STAND, "橡木锅架");
        translationBuilder.add(PeonyBlocks.OAK_POT_STAND_WITH_CAMPFIRE, "套营火的橡木锅架");
        translationBuilder.add(PeonyBlocks.SKILLET, "平底锅");

        translationBuilder.add(CraftingSteps.Procedure.KNEADING.getTranslationKey(), "揉捏");
        translationBuilder.add(CraftingSteps.Procedure.CUTTING.getTranslationKey(), "切");

        translationBuilder.add(PeonyTranslationKeys.ITEM_GROUP_KEY, "牡丹");

        translationBuilder.add(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_FOUND, "§b下方存在天然气！");
        translationBuilder.add(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_NOTHING, "§7下方没有任何天然气。");

        translationBuilder.add(PeonyTranslationKeys.MILLING_RECIPE_CATEGORY_TITLE, "磨制");
        translationBuilder.add(PeonyTranslationKeys.MILLING_RECIPE_MILLING_TIMES, "研磨%d次");
        translationBuilder.add(PeonyTranslationKeys.SEQUENTIAL_CRAFTING_RECIPE_CATEGORY_TITLE, "顺序合成");
    }
}
