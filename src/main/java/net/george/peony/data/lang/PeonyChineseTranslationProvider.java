package net.george.peony.data.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.george.peony.api.action.ActionTypes;
import net.george.peony.api.heat.HeatLevel;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class PeonyChineseTranslationProvider extends FabricLanguageProvider {
    public PeonyChineseTranslationProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder builder) {
        builder.add(PeonyItems.BARLEY, "大麦");
        builder.add(PeonyItems.BARLEY_SEEDS, "大麦种子");
        builder.add(PeonyItems.PEANUT, "花生");
        builder.add(PeonyItems.PEANUT_KERNEL, "花生仁");
        builder.add(PeonyItems.ROASTED_PEANUT_KERNEL, "熟花生仁");
        builder.add(PeonyItems.CRUSHED_PEANUTS, "花生碎");
        builder.add(PeonyItems.TOMATO, "西红柿");
        builder.add(PeonyItems.TOMATO_SEEDS, "西红柿种子");
        builder.add(PeonyItems.PEELED_TOMATO, "去皮的西红柿");
        builder.add(PeonyItems.PEELED_POTATO, "去皮的土豆");
        builder.add(PeonyItems.SHREDDED_POTATO, "土豆丝");
        builder.add(PeonyItems.CORIANDER, "香菜");
        builder.add(PeonyItems.CORIANDER_SEEDS, "香菜种子");
        builder.add(PeonyItems.RICE_PANICLE, "大米穗");
        builder.add(PeonyItems.BROWN_RICE, "糙米");
        builder.add(PeonyItems.RICE, "大米");
        builder.add(PeonyItems.GARLIC, "大蒜");
        builder.add(PeonyItems.GARLIC_CLOVE, "蒜瓣");
        builder.add(PeonyItems.GARLIC_SCAPE, "蒜薹");
        builder.add(PeonyItems.MINCED_GARLIC, "蒜末");
        builder.add(PeonyItems.SOYBEAN, "黄豆");
        builder.add(PeonyItems.SOYBEAN_POD, "黄豆荚");
        builder.add(PeonyItems.HAM, "火腿片");
        builder.add(PeonyItems.BAKED_FLATBREAD, "烤过的面饼");
        builder.add(PeonyItems.TOMATO_SAUCE, "番茄酱");
        builder.add(PeonyItems.SCRAMBLED_EGGS, "炒蛋");
        builder.add(PeonyItems.SCRAMBLED_EGGS_WITH_TOMATOES, "西红柿炒鸡蛋");
        builder.add(PeonyItems.FRIED_SHREDDED_POTATOES, "炒土豆丝");
        builder.add(PeonyItems.STIR_FRIED_GARLIC_SCAPE_WITH_PORK, "蒜薹炒肉");
        builder.add(PeonyItems.SWEET_AND_SOUR_PORK, "糖醋里脊");
        builder.add(PeonyItems.CHEESE, "芝士");
        builder.add(PeonyItems.SHREDDED_CHEESE, "芝士丝");

        builder.add(PeonyItems.LARD, "猪油");
        builder.add(PeonyItems.LARD_BOTTLE, "猪油瓶");
        builder.add(PeonyItems.PORK_TENDERLOIN, "猪里脊");
        builder.add(PeonyItems.CONDIMENT_BOTTLE, "调料瓶");
        builder.add(PeonyItems.BLACK_VINEGAR, "黑醋");
        builder.add(PeonyItems.SWEET_SOUR_SAUCE, "糖醋汁");

        builder.add(PeonyItems.KITCHEN_KNIFE, "菜刀");
        builder.add(PeonyItems.SPATULA, "炒菜铲");
        builder.add(PeonyItems.IRON_PARING_KNIFE, "铁削皮刀");
        builder.add(PeonyItems.IRON_SHREDDER, "铁擦丝器");
        builder.add(PeonyItems.GOLD_SHREDDER, "金擦丝器");
        builder.add(PeonyItems.DIAMOND_SHREDDER, "钻石擦丝器");
        builder.add(PeonyItems.NETHERITE_SHREDDER, "下界合金擦丝器");
        builder.add(PeonyItems.WOODEN_PLATE, "木碟");
        builder.add(PeonyItems.NATURE_GAS_DETECTOR, "天然气探测器");

        builder.add(PeonyItems.NATURE_GAS_BUCKET, "天然气桶");
        builder.add(PeonyItems.LARD_BUCKET, "猪油桶");

        builder.add(PeonyItems.MUSIC_DISC_SURPRISE, "惊喜");

        builder.add(PeonyBlocks.DOUGH, "面团");
        builder.add(PeonyBlocks.FLOUR, "面粉");
        builder.add(PeonyBlocks.FLATBREAD, "面饼");
        builder.add(PeonyBlocks.CHEESE_BLOCK, "芝士块");

        builder.add(PeonyBlocks.RAW_MARGHERITA_PIZZA, "生芝士披萨");
        builder.add(PeonyBlocks.RAW_MARGHERITA_PIZZA.asItem(), "生芝士披萨");
        builder.add(PeonyBlocks.MARGHERITA_PIZZA, "芝士披萨");
        builder.add(PeonyBlocks.MARGHERITA_PIZZA.asItem(), "芝士披萨");

        builder.add(PeonyBlocks.MILLSTONE, "石磨");
        builder.add(PeonyBlocks.OAK_CUTTING_BOARD, "橡木菜板");
        builder.add(PeonyBlocks.SPRUCE_CUTTING_BOARD, "云杉木菜板");
        builder.add(PeonyBlocks.BIRCH_CUTTING_BOARD, "白桦木菜板");
        builder.add(PeonyBlocks.JUNGLE_CUTTING_BOARD, "丛林木菜板");
        builder.add(PeonyBlocks.ACACIA_CUTTING_BOARD, "金合欢木菜板");
        builder.add(PeonyBlocks.CHERRY_CUTTING_BOARD, "樱花木菜板");
        builder.add(PeonyBlocks.DARK_OAK_CUTTING_BOARD, "深色橡木菜板");
        builder.add(PeonyBlocks.MANGROVE_CUTTING_BOARD, "红树木菜板");
        builder.add(PeonyBlocks.SKILLET, "平底锅");
        builder.add(PeonyBlocks.BREWING_BARREL, "酿造桶");
        builder.add(PeonyBlocks.FERMENTATION_TANK, "发酵桶");
        builder.add(PeonyBlocks.OAK_LOG_STICK, "橡木原木棍");
        builder.add(PeonyBlocks.SPRUCE_LOG_STICK, "云杉原木棍");
        builder.add(PeonyBlocks.BIRCH_LOG_STICK, "白桦原木棍");
        builder.add(PeonyBlocks.JUNGLE_LOG_STICK, "丛林原木棍");
        builder.add(PeonyBlocks.ACACIA_LOG_STICK, "金合欢原木棍");
        builder.add(PeonyBlocks.CHERRY_LOG_STICK, "樱花原木棍");
        builder.add(PeonyBlocks.DARK_OAK_LOG_STICK, "深色橡木原木棍");
        builder.add(PeonyBlocks.MANGROVE_LOG_STICK, "红树原木棍");
        builder.add(PeonyBlocks.OAK_POT_STAND, "橡木锅架");
        builder.add(PeonyBlocks.SPRUCE_POT_STAND, "云杉木锅架");
        builder.add(PeonyBlocks.BIRCH_POT_STAND, "白桦木锅架");
        builder.add(PeonyBlocks.JUNGLE_POT_STAND, "丛林木锅架");
        builder.add(PeonyBlocks.ACACIA_POT_STAND, "金合欢木锅架");
        builder.add(PeonyBlocks.CHERRY_POT_STAND, "樱花木锅架");
        builder.add(PeonyBlocks.DARK_OAK_POT_STAND, "深色橡木锅架");
        builder.add(PeonyBlocks.MANGROVE_POT_STAND, "红树木锅架");
        builder.add(PeonyBlocks.OAK_POT_STAND_WITH_CAMPFIRE, "带营火的橡木锅架");
        builder.add(PeonyBlocks.SPRUCE_POT_STAND_WITH_CAMPFIRE, "带营火的云杉木锅架");
        builder.add(PeonyBlocks.BIRCH_POT_STAND_WITH_CAMPFIRE, "带营火的白桦木锅架");
        builder.add(PeonyBlocks.JUNGLE_POT_STAND_WITH_CAMPFIRE, "带营火的丛林木锅架");
        builder.add(PeonyBlocks.ACACIA_POT_STAND_WITH_CAMPFIRE, "带营火的金合欢木锅架");
        builder.add(PeonyBlocks.CHERRY_POT_STAND_WITH_CAMPFIRE, "带营火的樱花木锅架");
        builder.add(PeonyBlocks.DARK_OAK_POT_STAND_WITH_CAMPFIRE, "带营火的深色橡木锅架");
        builder.add(PeonyBlocks.MANGROVE_POT_STAND_WITH_CAMPFIRE, "带营火的红树木锅架");

        builder.add(PeonyBlocks.BARLEY_CROP, "大麦作物");
        builder.add(PeonyBlocks.PEANUT_CROP, "花生作物");
        builder.add(PeonyBlocks.TOMATO_VINES, "西红柿藤");
        builder.add(PeonyBlocks.RICE_CROP, "大米作物");
        builder.add(PeonyBlocks.CORIANDER_CROP, "香菜作物");
        builder.add(PeonyBlocks.GARLIC_CROP, "大蒜作物");

        builder.add(PeonyBlocks.BOWL, "碗");

        builder.add(PeonyBlocks.NATURE_GAS, "天然气");
        builder.add(PeonyBlocks.LARD_FLUID, "猪油");
        builder.add(PeonyBlocks.LARD_CAULDRON, "装有猪油的炼药锅");

        builder.add(PeonyTranslationKeys.ITEM_GROUP_KEY, "牡丹");

        builder.add(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_FOUND, "§b下方存在天然气！");
        builder.add(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_NOTHING, "§7下方没有任何天然气。");

        /* SOUNDS */
        builder.add("sound.peony.shear_using", "使用剪刀");
        builder.add("sound.peony.paring", "削皮");

        /* JUKEBOX SONGS */
        builder.add("jukebox_song.peony.surprise", "惊喜 (Never Gonna Give You Up) - Rick Astley");

        /* DAMAGE TYPES */
        builder.add("death.attack.scald", "%1$s被烫死了");
        builder.add("death.attack.scald.player", "%1$s在与%2$s的战斗中被烫死了");

        /* HEATING LEVELS */
        builder.add(HeatLevel.NONE.getTranslationKey(), "无");
        builder.add(HeatLevel.SMOLDERING.getTranslationKey(), "阴燃");
        builder.add(HeatLevel.LOW.getTranslationKey(), "低");
        builder.add(HeatLevel.HIGH.getTranslationKey(), "高");
        builder.add(HeatLevel.BLAZING.getTranslationKey(), "炽灼");

        /* MESSAGES */
        builder.add(PeonyTranslationKeys.MESSAGE_FLATBREAD_NO_INGREDIENTS, "没有放入任何原料");
        builder.add(PeonyTranslationKeys.MESSAGE_FLATBREAD_CREATE_SUCCESS, "成功合成披萨!");
        builder.add(PeonyTranslationKeys.MESSAGE_FLATBREAD_NO_RECIPE, "这不是一种披萨!");

        /* STATS */
        builder.add("stat.peony.skillet_cooking_success", "平底锅烹饪成功次数");
        builder.add("stat.peony.skillet_cooking_failure", "平底锅烹饪失败次数");

        /* ADVANCEMENTS */
        builder.add(PeonyTranslationKeys.ADVANCEMENT_ROOT_TITLE, "牡丹");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_ROOT_DESCRIPTION, "制作各种食物的旅程，当然炒菜铲是必不可少的");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_COOKING_OIL_TITLE, "这油...能吃！");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_COOKING_OIL_DESCRIPTION, "获取任意一种食用油，不管是固体还是液体");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_KITCHENWARE_TITLE, "厨神之路");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_KITCHENWARE_DESCRIPTION, "获取任意一种厨具以开启厨神之路");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_SKILLET_TITLE, "煎锅！");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_SKILLET_DESCRIPTION, "煎锅，烹饪的核心之一，一个很重要的厨具");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_SKILLET_COOKING_SUCCEED_TITLE, "烹饪成功！");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_SKILLET_COOKING_SUCCEED_DESCRIPTION, "使用煎锅成功烹饪任何一道菜");

        /* ACTIONS */
        builder.add(ActionTypes.KNEADING.createTranslationKey(), "揉捏");
        builder.add(ActionTypes.CUTTING.createTranslationKey(), "切割");
        builder.add(ActionTypes.SLICING.createTranslationKey(), "切片");

        /* CONFIG */
        builder.add(PeonyTranslationKeys.CONFIG_SCREEN_TITLE, "牡丹");
        builder.add(PeonyTranslationKeys.CONFIG_CATEGORY_COMMON, "基本配置");
        // options
        builder.add(PeonyTranslationKeys.OPTION_LARD_SLOWNESS_DURATION_TICKS, "在猪油中缓慢效果的持续时间");
        builder.add(PeonyTranslationKeys.OPTION_LARD_FIRE_EXTENSION_TICKS, "在猪油中着火时的延长持续时间”");
        // descriptions
        builder.add(PeonyTranslationKeys.CONFIG_CATEGORY_DESCRIPTION_COMMON, "基本配置选项");
        builder.add(PeonyTranslationKeys.OPTION_DESCRIPTION_LARD_SLOWNESS_DURATION_TICKS, "当玩家在猪油液体中时，将会被施加缓慢效果，持续时间是以游戏刻为单位的（1秒=20游戏刻）。");
        builder.add(PeonyTranslationKeys.OPTION_DESCRIPTION_LARD_FIRE_EXTENSION_TICKS, "如果玩家着火，进入或跳入猪油液体会延长火焰持续时间（包括装有猪油的炼药锅）。持续时间是以游戏刻为单位的（1秒=20刻）。");

        builder.add(PeonyTranslationKeys.SECOND, "%d秒");

        /* JADE */
        // config
        builder.add("config.jade.plugin_peony.skillet_component", "平底锅状态显示");
        builder.add("config.jade.plugin_peony.pot_stand_with_campfire_component", "套营火的橡木锅架 - 温度数据显示");

        // global
        builder.add(PeonyTranslationKeys.JADE_STEP, "步骤: %d / %d");
        builder.add(PeonyTranslationKeys.JADE_TIME_REMAINING, "剩余时间: %d秒");
        builder.add(PeonyTranslationKeys.JADE_TIME_LIMIT, "时间限制: %d秒");
        builder.add(PeonyTranslationKeys.JADE_REQUIRES, "需要");
        builder.add(PeonyTranslationKeys.JADE_NO_HEAT_SOURCE, "⚠ 没有热源");

        // skillet statements
        builder.add(PeonyTranslationKeys.JADE_STATE_IDLE, "空闲");
        builder.add(PeonyTranslationKeys.JADE_STATE_OIL_PROCESSING, "处理油");
        builder.add(PeonyTranslationKeys.JADE_STATE_COMMON_INGREDIENT_PROCESSING, "处理基本食材");
        builder.add(PeonyTranslationKeys.JADE_STATE_HEATING, "加热中");
        builder.add(PeonyTranslationKeys.JADE_STATE_STIR_FRYING, "翻炒中");
        builder.add(PeonyTranslationKeys.JADE_STATE_OVERFLOW, "溢出警告");
        builder.add(PeonyTranslationKeys.JADE_STATE_WAITING_FOR_INGREDIENT, "等待食材");
        builder.add(PeonyTranslationKeys.JADE_STATE_COMPLETED, "已完成");
        builder.add(PeonyTranslationKeys.JADE_STATE_FAILED, "已失败");

        // skillet
        builder.add(PeonyTranslationKeys.JADE_SKILLET_MELTING_OIL, "融化油: %d / %d");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_CONTINUE, "继续: %d / %d");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_HEATING_TIME, "加热中: %d / %d");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_HEATING, "加热");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_STIR_FRYING_COUNT, "翻炒: %d / %d");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_STIR_FRYING, "翻炒");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_OVERFLOW_TIME, "溢出: %d / %d");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_TOOL_USAGE_TOOLTIP, "使用工具继续");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_CONTAINER_TOOLTIP, "使用容器取出");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_NON_CONTAINER_TOOLTIP, "可以取出");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_HAS_OIL, "已加入油");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_HAS_COMMON_INGREDIENT, "已加入基础食材");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_PREPARING_INGREDIENT, "准备食材");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_ADD_INGREDIENT, "请加入食材");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_WAITING_FOR_INGREDIENT, "等待食材: %d秒");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_OVERFLOW_WARNING, "⚠ 即将烧焦!");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_COMPLETED, "✓ 烹饪完成");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_FAILED, "✗ 烹饪失败");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_READY_TO_EXTRACT, "可以取出");

        // heat source
        builder.add(PeonyTranslationKeys.JADE_HEAT_SOURCE_AVAILABLE_HEAT_AMOUNT, "可提供的热量范围: %d°C-%d°C");
        builder.add(PeonyTranslationKeys.JADE_HEAT_SOURCE_HEATING_LEVEL, "热量等级: ");

        /* REI */
        builder.add(PeonyTranslationKeys.REI_CATEGORY_MILLING, "磨制");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_SEQUENTIAL_CRAFTING, "顺序合成");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_SEQUENTIAL_COOKING, "顺序烹饪");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_SHREDDING, "擦丝");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_PARING, "削皮");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_FLAVOURING_PREPARING, "调料调制");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_BREWING, "酿造");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_PIZZA_CRAFTING, "披萨制作");

        builder.add(PeonyTranslationKeys.REI_MILLING_TIMES, "研磨%d次");
        builder.add(PeonyTranslationKeys.REI_NO_INGREDIENTS, "无需添加原料");
        builder.add(PeonyTranslationKeys.REI_REQUIRED_INGREDIENTS, "需要的食材");
        builder.add(PeonyTranslationKeys.REI_ACTION, "需要的操作");
        builder.add(PeonyTranslationKeys.REI_REQUIRED_TIME, "%d秒");
        builder.add(PeonyTranslationKeys.REI_STIR_FRYING, "翻炒%d次");
        builder.add(PeonyTranslationKeys.REI_HEATING, "加热");
        builder.add(PeonyTranslationKeys.REI_REQUIRES_CONTAINER, "需要容器取出");
        builder.add(PeonyTranslationKeys.REI_NO_CONTAINER, "可直接取出");
        builder.add(PeonyTranslationKeys.REI_TEMPERATURE, "温度: %d°C");
        builder.add(PeonyTranslationKeys.REI_STEP, "步骤%d");
        builder.add(PeonyTranslationKeys.REI_DURATION_DECREMENT, "耐久损耗: %d");
        builder.add(PeonyTranslationKeys.REI_STIRRING_TIMES, "搅拌次数: %d");
        builder.add(PeonyTranslationKeys.REI_BREWING_TIMES, "酿造时间: %d");
    }
}
