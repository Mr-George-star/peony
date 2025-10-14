package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.george.peony.api.heat.HeatLevel;
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
        translationBuilder.add(PeonyItems.PEANUT, "花生");
        translationBuilder.add(PeonyItems.PEANUT_KERNEL, "花生仁");
        translationBuilder.add(PeonyItems.ROASTED_PEANUT_KERNEL, "熟花生仁");
        translationBuilder.add(PeonyItems.CRUSHED_PEANUTS, "花生碎");
        translationBuilder.add(PeonyItems.TOMATO, "西红柿");
        translationBuilder.add(PeonyItems.TOMATO_SEEDS, "西红柿种子");
        translationBuilder.add(PeonyItems.LARD, "猪油");
        translationBuilder.add(PeonyItems.LARD_BOTTLE, "猪油瓶");

        translationBuilder.add(PeonyItems.KITCHEN_KNIFE, "菜刀");
        translationBuilder.add(PeonyItems.SPATULA, "炒菜铲");
        translationBuilder.add(PeonyItems.IRON_PARING_KNIFE, "铁削皮刀");
        translationBuilder.add(PeonyItems.NATURE_GAS_DETECTOR, "天然气探测器");

        translationBuilder.add(PeonyItems.NATURE_GAS_BUCKET, "天然气桶");
        translationBuilder.add(PeonyItems.LARD_BUCKET, "猪油桶");

        translationBuilder.add(PeonyBlocks.MILLSTONE, "石磨");
        translationBuilder.add(PeonyBlocks.OAK_CUTTING_BOARD, "橡木菜板");
        translationBuilder.add(PeonyBlocks.SPRUCE_CUTTING_BOARD, "云杉木菜板");
        translationBuilder.add(PeonyBlocks.BIRCH_CUTTING_BOARD, "白桦木菜板");
        translationBuilder.add(PeonyBlocks.JUNGLE_CUTTING_BOARD, "丛林木菜板");
        translationBuilder.add(PeonyBlocks.ACACIA_CUTTING_BOARD, "金合欢木菜板");
        translationBuilder.add(PeonyBlocks.CHERRY_CUTTING_BOARD, "樱花木菜板");
        translationBuilder.add(PeonyBlocks.DARK_OAK_CUTTING_BOARD, "深色橡木菜板");
        translationBuilder.add(PeonyBlocks.MANGROVE_CUTTING_BOARD, "红树木菜板");
        translationBuilder.add(PeonyBlocks.SKILLET, "平底锅");
        translationBuilder.add(PeonyBlocks.OAK_LOG_STICK, "橡木原木棍");
        translationBuilder.add(PeonyBlocks.OAK_POT_STAND, "橡木锅架");
        translationBuilder.add(PeonyBlocks.OAK_POT_STAND_WITH_CAMPFIRE, "套营火的橡木锅架");
        translationBuilder.add(PeonyBlocks.DOUGH, "面团");
        translationBuilder.add(PeonyBlocks.FLOUR, "面粉");
        translationBuilder.add(PeonyBlocks.BARLEY_CROP, "大麦作物");
        translationBuilder.add(PeonyBlocks.PEANUT_CROP, "花生作物");
        translationBuilder.add(PeonyBlocks.TOMATO_VINES, "西红柿藤");

        translationBuilder.add(PeonyBlocks.NATURE_GAS, "天然气");
        translationBuilder.add(PeonyBlocks.LARD_FLUID, "猪油");
        translationBuilder.add(PeonyBlocks.LARD_CAULDRON, "装有猪油的炼药锅");

        /* Procedures */
        translationBuilder.add(CraftingSteps.Procedure.KNEADING.getTranslationKey(), "揉捏");
        translationBuilder.add(CraftingSteps.Procedure.CUTTING.getTranslationKey(), "切");

        translationBuilder.add(PeonyTranslationKeys.ITEM_GROUP_KEY, "牡丹");

        translationBuilder.add(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_FOUND, "§b下方存在天然气！");
        translationBuilder.add(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_NOTHING, "§7下方没有任何天然气。");

        translationBuilder.add(PeonyTranslationKeys.MILLING_RECIPE_CATEGORY_TITLE, "磨制");
        translationBuilder.add(PeonyTranslationKeys.MILLING_RECIPE_MILLING_TIMES, "研磨%d次");
        translationBuilder.add(PeonyTranslationKeys.SEQUENTIAL_CRAFTING_RECIPE_CATEGORY_TITLE, "顺序合成");

        /* SOUNDS */
        translationBuilder.add("sound.peony.shear_using", "使用剪刀");

        /* HEATING LEVELS */
        translationBuilder.add(HeatLevel.NONE.getTranslationKey(), "无");
        translationBuilder.add(HeatLevel.SMOLDERING.getTranslationKey(), "阴燃");
        translationBuilder.add(HeatLevel.LOW.getTranslationKey(), "低");
        translationBuilder.add(HeatLevel.HIGH.getTranslationKey(), "高");
        translationBuilder.add(HeatLevel.BLAZING.getTranslationKey(), "炽灼");

        /* CONFIG */
        translationBuilder.add(PeonyTranslationKeys.CONFIG_SCREEN_TITLE, "牡丹");
        translationBuilder.add(PeonyTranslationKeys.CONFIG_CATEGORY_COMMON, "基本配置");
        // options
        translationBuilder.add(PeonyTranslationKeys.OPTION_LARD_SLOWNESS_DURATION_TICKS, "在猪油中缓慢效果的持续时间");
        translationBuilder.add(PeonyTranslationKeys.OPTION_LARD_FIRE_EXTENSION_TICKS, "在猪油中着火时的延长持续时间”");
        // descriptions
        translationBuilder.add(PeonyTranslationKeys.CONFIG_CATEGORY_DESCRIPTION_COMMON, "基本配置选项");
        translationBuilder.add(PeonyTranslationKeys.OPTION_DESCRIPTION_LARD_SLOWNESS_DURATION_TICKS, "当玩家在猪油液体中时，将会被施加缓慢效果，持续时间是以游戏刻为单位的（1秒=20游戏刻）。");
        translationBuilder.add(PeonyTranslationKeys.OPTION_DESCRIPTION_LARD_FIRE_EXTENSION_TICKS, "如果玩家着火，进入或跳入猪油液体会延长火焰持续时间（包括装有猪油的炼药锅）。持续时间是以游戏刻为单位的（1秒=20刻）。");

        translationBuilder.add(PeonyTranslationKeys.SECOND, "%d秒");

        /* JADE */
        // config
        translationBuilder.add("config.jade.plugin_peony.skillet_component", "平底锅状态显示");
        translationBuilder.add("config.jade.plugin_peony.pot_stand_with_campfire_component", "套营火的橡木锅架 - 温度数据显示");

        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_COOKING_TIME, "烹饪中, 在第%d秒，还剩%d秒");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_COOKING_OVERFLOW_TIME, "超时了! 已超时%d秒, 还剩%d秒");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_CONTAINER_TOOLTIP, "烹饪已完成，为取出结果需要的容器：");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_NON_CONTAINER_TOOLTIP, "烹饪已完成");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_MELTING_OIL, "融化油中, 在第%d秒, 还剩%d秒");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_TOOL_USAGE_TOOLTIP, "来进入下一步所需要的工具：");
        translationBuilder.add(PeonyTranslationKeys.JADE_HEAT_SOURCE_AVAILABLE_HEAT_AMOUNT, "可提供的热量范围：%d°C-%d°C");
        translationBuilder.add(PeonyTranslationKeys.JADE_HEAT_SOURCE_HEATING_LEVEL, "热量等级：");
    }
}
