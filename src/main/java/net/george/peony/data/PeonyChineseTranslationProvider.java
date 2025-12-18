package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.george.peony.api.heat.HeatLevel;
import net.george.peony.block.PeonyBlocks;
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
        translationBuilder.add(PeonyItems.PEELED_TOMATO, "去皮的西红柿");
        translationBuilder.add(PeonyItems.PEELED_POTATO, "去皮的土豆");
        translationBuilder.add(PeonyItems.SHREDDED_POTATO, "土豆丝");
        translationBuilder.add(PeonyItems.CORIANDER, "香菜");
        translationBuilder.add(PeonyItems.CORIANDER_SEEDS, "香菜种子");
        translationBuilder.add(PeonyItems.RICE_PANICLE, "大米穗");
        translationBuilder.add(PeonyItems.BROWN_RICE, "糙米");
        translationBuilder.add(PeonyItems.RICE, "大米");
        translationBuilder.add(PeonyItems.GARLIC, "大蒜");
        translationBuilder.add(PeonyItems.GARLIC_CLOVE, "蒜瓣");
        translationBuilder.add(PeonyItems.GARLIC_SCAPE, "蒜薹");
        translationBuilder.add(PeonyItems.MINCED_GARLIC, "蒜末");
        translationBuilder.add(PeonyItems.SOYBEAN, "黄豆");
        translationBuilder.add(PeonyItems.HAM, "火腿片");
        translationBuilder.add(PeonyItems.BAKED_FLATBREAD, "烤过的面饼");
        translationBuilder.add(PeonyItems.TOMATO_SAUCE, "番茄酱");
        translationBuilder.add(PeonyItems.SCRAMBLED_EGGS, "炒蛋");
        translationBuilder.add(PeonyItems.SCRAMBLED_EGGS_WITH_TOMATOES, "西红柿炒鸡蛋");
        translationBuilder.add(PeonyItems.FRIED_SHREDDED_POTATOES, "炒土豆丝");
        translationBuilder.add(PeonyItems.STIR_FRIED_GARLIC_SCAPE_WITH_PORK, "蒜薹炒肉");
        translationBuilder.add(PeonyItems.SWEET_AND_SOUR_PORK, "糖醋里脊");
        translationBuilder.add(PeonyItems.CHEESE, "芝士");
        translationBuilder.add(PeonyItems.SHREDDED_CHEESE, "芝士丝");

        translationBuilder.add(PeonyItems.LARD, "猪油");
        translationBuilder.add(PeonyItems.LARD_BOTTLE, "猪油瓶");
        translationBuilder.add(PeonyItems.PORK_TENDERLOIN, "猪里脊");
        translationBuilder.add(PeonyItems.CONDIMENT_BOTTLE, "调料瓶");
        translationBuilder.add(PeonyItems.BLACK_VINEGAR, "黑醋");
        translationBuilder.add(PeonyItems.SWEET_SOUR_SAUCE, "糖醋汁");

        translationBuilder.add(PeonyItems.KITCHEN_KNIFE, "菜刀");
        translationBuilder.add(PeonyItems.SPATULA, "炒菜铲");
        translationBuilder.add(PeonyItems.IRON_PARING_KNIFE, "铁削皮刀");
        translationBuilder.add(PeonyItems.IRON_SHREDDER, "铁擦丝器");
        translationBuilder.add(PeonyItems.GOLD_SHREDDER, "金擦丝器");
        translationBuilder.add(PeonyItems.DIAMOND_SHREDDER, "钻石擦丝器");
        translationBuilder.add(PeonyItems.NETHERITE_SHREDDER, "下界合金擦丝器");
        translationBuilder.add(PeonyItems.WOODEN_PLATE, "木碟");
        translationBuilder.add(PeonyItems.NATURE_GAS_DETECTOR, "天然气探测器");

        translationBuilder.add(PeonyItems.NATURE_GAS_BUCKET, "天然气桶");
        translationBuilder.add(PeonyItems.LARD_BUCKET, "猪油桶");

        translationBuilder.add(PeonyItems.MUSIC_DISC_SURPRISE, "惊喜");

        translationBuilder.add(PeonyBlocks.DOUGH, "面团");
        translationBuilder.add(PeonyBlocks.FLOUR, "面粉");
        translationBuilder.add(PeonyBlocks.FLATBREAD, "面饼");
        translationBuilder.add(PeonyBlocks.CHEESE_BLOCK, "芝士块");

        translationBuilder.add(PeonyBlocks.RAW_MARGHERITA_PIZZA, "生芝士披萨");
        translationBuilder.add(PeonyBlocks.RAW_MARGHERITA_PIZZA.asItem(), "生芝士披萨");
        translationBuilder.add(PeonyBlocks.MARGHERITA_PIZZA, "芝士披萨");
        translationBuilder.add(PeonyBlocks.MARGHERITA_PIZZA.asItem(), "生芝士披萨");

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
        translationBuilder.add(PeonyBlocks.BREWING_BARREL, "酿造桶");
        translationBuilder.add(PeonyBlocks.OAK_LOG_STICK, "橡木原木棍");
        translationBuilder.add(PeonyBlocks.SPRUCE_LOG_STICK, "云杉原木棍");
        translationBuilder.add(PeonyBlocks.BIRCH_LOG_STICK, "白桦原木棍");
        translationBuilder.add(PeonyBlocks.JUNGLE_LOG_STICK, "丛林原木棍");
        translationBuilder.add(PeonyBlocks.ACACIA_LOG_STICK, "金合欢原木棍");
        translationBuilder.add(PeonyBlocks.CHERRY_LOG_STICK, "樱花原木棍");
        translationBuilder.add(PeonyBlocks.DARK_OAK_LOG_STICK, "深色橡木原木棍");
        translationBuilder.add(PeonyBlocks.MANGROVE_LOG_STICK, "红树原木棍");
        translationBuilder.add(PeonyBlocks.OAK_POT_STAND, "橡木锅架");
        translationBuilder.add(PeonyBlocks.SPRUCE_POT_STAND, "云杉木锅架");
        translationBuilder.add(PeonyBlocks.BIRCH_POT_STAND, "白桦木锅架");
        translationBuilder.add(PeonyBlocks.JUNGLE_POT_STAND, "丛林木锅架");
        translationBuilder.add(PeonyBlocks.ACACIA_POT_STAND, "金合欢木锅架");
        translationBuilder.add(PeonyBlocks.CHERRY_POT_STAND, "樱花木锅架");
        translationBuilder.add(PeonyBlocks.DARK_OAK_POT_STAND, "深色橡木锅架");
        translationBuilder.add(PeonyBlocks.MANGROVE_POT_STAND, "红树木锅架");
        translationBuilder.add(PeonyBlocks.OAK_POT_STAND_WITH_CAMPFIRE, "带营火的橡木锅架");
        translationBuilder.add(PeonyBlocks.SPRUCE_POT_STAND_WITH_CAMPFIRE, "带营火的云杉木锅架");
        translationBuilder.add(PeonyBlocks.BIRCH_POT_STAND_WITH_CAMPFIRE, "带营火的白桦木锅架");
        translationBuilder.add(PeonyBlocks.JUNGLE_POT_STAND_WITH_CAMPFIRE, "带营火的丛林木锅架");
        translationBuilder.add(PeonyBlocks.ACACIA_POT_STAND_WITH_CAMPFIRE, "带营火的金合欢木锅架");
        translationBuilder.add(PeonyBlocks.CHERRY_POT_STAND_WITH_CAMPFIRE, "带营火的樱花木锅架");
        translationBuilder.add(PeonyBlocks.DARK_OAK_POT_STAND_WITH_CAMPFIRE, "带营火的深色橡木锅架");
        translationBuilder.add(PeonyBlocks.MANGROVE_POT_STAND_WITH_CAMPFIRE, "带营火的红树木锅架");

        translationBuilder.add(PeonyBlocks.BARLEY_CROP, "大麦作物");
        translationBuilder.add(PeonyBlocks.PEANUT_CROP, "花生作物");
        translationBuilder.add(PeonyBlocks.TOMATO_VINES, "西红柿藤");
        translationBuilder.add(PeonyBlocks.RICE_CROP, "大米作物");
        translationBuilder.add(PeonyBlocks.CORIANDER_CROP, "香菜作物");
        translationBuilder.add(PeonyBlocks.GARLIC_CROP, "大蒜作物");

        translationBuilder.add(PeonyBlocks.BOWL, "碗");

        translationBuilder.add(PeonyBlocks.NATURE_GAS, "天然气");
        translationBuilder.add(PeonyBlocks.LARD_FLUID, "猪油");
        translationBuilder.add(PeonyBlocks.LARD_CAULDRON, "装有猪油的炼药锅");

        translationBuilder.add(PeonyTranslationKeys.ITEM_GROUP_KEY, "牡丹");

        translationBuilder.add(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_FOUND, "§b下方存在天然气！");
        translationBuilder.add(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_NOTHING, "§7下方没有任何天然气。");

        translationBuilder.add(PeonyTranslationKeys.MILLING_RECIPE_CATEGORY_TITLE, "磨制");
        translationBuilder.add(PeonyTranslationKeys.MILLING_RECIPE_MILLING_TIMES, "研磨%d次");
        translationBuilder.add(PeonyTranslationKeys.SEQUENTIAL_CRAFTING_RECIPE_CATEGORY_TITLE, "顺序合成");

        /* SOUNDS */
        translationBuilder.add("sound.peony.shear_using", "使用剪刀");
        translationBuilder.add("sound.peony.paring", "削皮");

        /* JUKEBOX SONGS */
        translationBuilder.add("jukebox_song.peony.surprise", "惊喜 (Never Gonna Give You Up) - Rick Astley");

        /* DAMAGE TYPES */
        translationBuilder.add("death.attack.scald", "%1$s被烫死了");
        translationBuilder.add("death.attack.scald.player", "%1$s在与%2$s的战斗中被烫死了");

        /* HEATING LEVELS */
        translationBuilder.add(HeatLevel.NONE.getTranslationKey(), "无");
        translationBuilder.add(HeatLevel.SMOLDERING.getTranslationKey(), "阴燃");
        translationBuilder.add(HeatLevel.LOW.getTranslationKey(), "低");
        translationBuilder.add(HeatLevel.HIGH.getTranslationKey(), "高");
        translationBuilder.add(HeatLevel.BLAZING.getTranslationKey(), "炽灼");

        /* MESSAGES */
        translationBuilder.add(PeonyTranslationKeys.MESSAGE_FLATBREAD_NO_INGREDIENTS, "没有放入任何原料");
        translationBuilder.add(PeonyTranslationKeys.MESSAGE_FLATBREAD_CREATE_SUCCESS, "成功合成披萨!");
        translationBuilder.add(PeonyTranslationKeys.MESSAGE_FLATBREAD_NO_RECIPE, "这不是一种披萨!");

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

        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_MELTING_OIL, "融化油中, 在第%d秒, 还剩%d秒");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_CONTINUE, "放入原料以进入下一步，在第%d秒，还剩%d秒");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_HEATING_TIME, "烹饪中, 在第%d秒，还剩%d秒");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_OVERFLOW_TIME, "超时了! 已超时%d秒, 还剩%d秒");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_STIR_FRYING_COUNT, "翻炒中，需翻炒%d次，已翻炒%d次");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_TOOL_USAGE_TOOLTIP, "来进入下一步所需要的工具：");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_CONTAINER_TOOLTIP, "烹饪已完成，为取出结果需要的容器：");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_NON_CONTAINER_TOOLTIP, "烹饪已完成");
        translationBuilder.add(PeonyTranslationKeys.JADE_HEAT_SOURCE_AVAILABLE_HEAT_AMOUNT, "可提供的热量范围：%d°C-%d°C");
        translationBuilder.add(PeonyTranslationKeys.JADE_HEAT_SOURCE_HEATING_LEVEL, "热量等级：");
    }
}
