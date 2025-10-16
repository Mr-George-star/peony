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

public class PeonyEnglishTranslationProvider extends FabricLanguageProvider {
    protected PeonyEnglishTranslationProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(PeonyItems.BARLEY, "Barley");
        translationBuilder.add(PeonyItems.BARLEY_SEEDS, "Barley Seeds");
        translationBuilder.add(PeonyItems.PEANUT, "Peanut");
        translationBuilder.add(PeonyItems.PEANUT_KERNEL, "Peanut Kernel");
        translationBuilder.add(PeonyItems.ROASTED_PEANUT_KERNEL, "Roasted Peanut Kernel");
        translationBuilder.add(PeonyItems.CRUSHED_PEANUTS, "Crushed Peanuts");
        translationBuilder.add(PeonyItems.TOMATO, "Tomato");
        translationBuilder.add(PeonyItems.TOMATO_SEEDS, "Tomato Seeds");
        translationBuilder.add(PeonyItems.LARD, "Lard");
        translationBuilder.add(PeonyItems.LARD_BOTTLE, "Lard Bottle");

        translationBuilder.add(PeonyItems.KITCHEN_KNIFE, "Kitchen Knife");
        translationBuilder.add(PeonyItems.SPATULA, "Spatula");
        translationBuilder.add(PeonyItems.IRON_PARING_KNIFE, "Iron Paring Knife");
        translationBuilder.add(PeonyItems.NATURE_GAS_DETECTOR, "Nature Gas Detector");

        translationBuilder.add(PeonyItems.NATURE_GAS_BUCKET, "Nature Gas Bucket");
        translationBuilder.add(PeonyItems.LARD_BUCKET, "Lard Bucket");

        translationBuilder.add(PeonyBlocks.MILLSTONE, "Millstone");
        translationBuilder.add(PeonyBlocks.OAK_CUTTING_BOARD, "Oak Cutting Board");
        translationBuilder.add(PeonyBlocks.SPRUCE_CUTTING_BOARD, "Spruce Cutting Board");
        translationBuilder.add(PeonyBlocks.BIRCH_CUTTING_BOARD, "Birch Cutting Board");
        translationBuilder.add(PeonyBlocks.JUNGLE_CUTTING_BOARD, "Jungle Cutting Board");
        translationBuilder.add(PeonyBlocks.ACACIA_CUTTING_BOARD, "Acacia Cutting Board");
        translationBuilder.add(PeonyBlocks.CHERRY_CUTTING_BOARD, "Cherry Cutting Board");
        translationBuilder.add(PeonyBlocks.DARK_OAK_CUTTING_BOARD, "Dark Oak Cutting Board");
        translationBuilder.add(PeonyBlocks.MANGROVE_CUTTING_BOARD, "Mangrove Cutting Board");
        translationBuilder.add(PeonyBlocks.SKILLET, "Skillet");
        translationBuilder.add(PeonyBlocks.OAK_LOG_STICK, "Oak Log Stick");
        translationBuilder.add(PeonyBlocks.SPRUCE_LOG_STICK, "Spruce Log Stick");
        translationBuilder.add(PeonyBlocks.BIRCH_LOG_STICK, "Birch Log Stick");
        translationBuilder.add(PeonyBlocks.JUNGLE_LOG_STICK, "Jungle Log Stick");
        translationBuilder.add(PeonyBlocks.ACACIA_LOG_STICK, "Acacia Log Stick");
        translationBuilder.add(PeonyBlocks.CHERRY_LOG_STICK, "Cherry Log Stick");
        translationBuilder.add(PeonyBlocks.DARK_OAK_LOG_STICK, "Dark Oak Log Stick");
        translationBuilder.add(PeonyBlocks.MANGROVE_LOG_STICK, "Mangrove Log Stick");
        translationBuilder.add(PeonyBlocks.OAK_POT_STAND, "Oak Pot Stand");
        translationBuilder.add(PeonyBlocks.SPRUCE_POT_STAND, "Spruce Pot Stand");
        translationBuilder.add(PeonyBlocks.BIRCH_POT_STAND, "Birch Pot Stand");
        translationBuilder.add(PeonyBlocks.JUNGLE_POT_STAND, "Jungle Pot Stand");
        translationBuilder.add(PeonyBlocks.ACACIA_POT_STAND, "Acacia Pot Stand");
        translationBuilder.add(PeonyBlocks.CHERRY_POT_STAND, "Cherry Pot Stand");
        translationBuilder.add(PeonyBlocks.DARK_OAK_POT_STAND, "Dark Oak Pot Stand");
        translationBuilder.add(PeonyBlocks.MANGROVE_POT_STAND, "Mangrove Pot Stand");
        translationBuilder.add(PeonyBlocks.OAK_POT_STAND_WITH_CAMPFIRE, "Oak Pot Stand With Campfire");
        translationBuilder.add(PeonyBlocks.SPRUCE_POT_STAND_WITH_CAMPFIRE, "Spruce Pot Stand With Campfire");
        translationBuilder.add(PeonyBlocks.BIRCH_POT_STAND_WITH_CAMPFIRE, "Birch Pot Stand With Campfire");
        translationBuilder.add(PeonyBlocks.JUNGLE_POT_STAND_WITH_CAMPFIRE, "Jungle Pot Stand With Campfire");
        translationBuilder.add(PeonyBlocks.ACACIA_POT_STAND_WITH_CAMPFIRE, "Acacia Pot Stand With Campfire");
        translationBuilder.add(PeonyBlocks.CHERRY_POT_STAND_WITH_CAMPFIRE, "Cherry Pot Stand With Campfire");
        translationBuilder.add(PeonyBlocks.DARK_OAK_POT_STAND_WITH_CAMPFIRE, "Dark Oak Pot Stand With Campfire");
        translationBuilder.add(PeonyBlocks.MANGROVE_POT_STAND_WITH_CAMPFIRE, "Mangrove Pot Stand With Campfire");
        translationBuilder.add(PeonyBlocks.DOUGH, "Dough");
        translationBuilder.add(PeonyBlocks.FLOUR, "Flour");
        translationBuilder.add(PeonyBlocks.BARLEY_CROP, "Barley Crop");
        translationBuilder.add(PeonyBlocks.PEANUT_CROP, "Peanut Crop");
        translationBuilder.add(PeonyBlocks.TOMATO_VINES, "Tomato Vines");

        translationBuilder.add(PeonyBlocks.NATURE_GAS, "Nature Gas");
        translationBuilder.add(PeonyBlocks.LARD_FLUID, "Lard");
        translationBuilder.add(PeonyBlocks.LARD_CAULDRON, "Lard Cauldron");

        /* PROCEDURES */
//        translationBuilder.add(CraftingSteps.Procedure.KNEADING.getTranslationKey(), "Kneading");
//        translationBuilder.add(CraftingSteps.Procedure.CUTTING.getTranslationKey(), "Cutting");

        translationBuilder.add(PeonyTranslationKeys.ITEM_GROUP_KEY, "Peony");

        translationBuilder.add(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_FOUND, "§bThere is natural gas down here!");
        translationBuilder.add(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_NOTHING, "§7There is no natural gas down here.");

        translationBuilder.add(PeonyTranslationKeys.MILLING_RECIPE_CATEGORY_TITLE, "Milling");
        translationBuilder.add(PeonyTranslationKeys.MILLING_RECIPE_MILLING_TIMES, "Mill %d Time(s)");
        translationBuilder.add(PeonyTranslationKeys.SEQUENTIAL_CRAFTING_RECIPE_CATEGORY_TITLE, "Sequential Crafting");

        /* SOUNDS */
        translationBuilder.add("sound.peony.shear_using", "Using Shear");

        /* HEATING LEVELS */
        translationBuilder.add(HeatLevel.NONE.getTranslationKey(), "None");
        translationBuilder.add(HeatLevel.SMOLDERING.getTranslationKey(), "Smoldering");
        translationBuilder.add(HeatLevel.LOW.getTranslationKey(), "Low");
        translationBuilder.add(HeatLevel.HIGH.getTranslationKey(), "High");
        translationBuilder.add(HeatLevel.BLAZING.getTranslationKey(), "Blazing");

        /* CONFIG */
        translationBuilder.add(PeonyTranslationKeys.CONFIG_SCREEN_TITLE, "Peony");
        translationBuilder.add(PeonyTranslationKeys.CONFIG_CATEGORY_COMMON, "Common");
        // options
        translationBuilder.add(PeonyTranslationKeys.OPTION_LARD_SLOWNESS_DURATION_TICKS, "Duration Ticks of Slowness in Lard");
        translationBuilder.add(PeonyTranslationKeys.OPTION_LARD_FIRE_EXTENSION_TICKS, "Fire Extension Ticks in Lard");
        // descriptions
        translationBuilder.add(PeonyTranslationKeys.CONFIG_CATEGORY_DESCRIPTION_COMMON, "Common Configuration Options");
        translationBuilder.add(PeonyTranslationKeys.OPTION_DESCRIPTION_LARD_SLOWNESS_DURATION_TICKS, "The duration of the slowness effect in lard. When the player is in lard fluid, the slowness effect will be applied. \nThe following durations are in ticks (1 second = 20 ticks).");
        translationBuilder.add(PeonyTranslationKeys.OPTION_DESCRIPTION_LARD_FIRE_EXTENSION_TICKS, "If the player is on fire, entering or jumping into lard fluid will extend the fire duration (including cauldrons containing lard). \nThe following durations are in ticks (1 second = 20 ticks).");

        translationBuilder.add(PeonyTranslationKeys.SECOND, "%d Second(s)");

        /* JADE */
        // config
        translationBuilder.add("config.jade.plugin_peony.skillet_component", "Skillet Status Display");
        translationBuilder.add("config.jade.plugin_peony.pot_stand_with_campfire_component", "Oak Pot Stand With Campfire - Temperature Data Display");

        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_COOKING_TIME, "Cooking, at %d second(s), %d second(s) left");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_COOKING_OVERFLOW_TIME, "Timed Out! At %d seconds(s), %d second(s) left");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_CONTAINER_TOOLTIP, "Cooking is finished, the container to transfer the finished product: ");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_NON_CONTAINER_TOOLTIP, "Cooking is finished");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_MELTING_OIL, "Melting the oil, at %d second(s), %d second(s) left");
        translationBuilder.add(PeonyTranslationKeys.JADE_SKILLET_TOOL_USAGE_TOOLTIP, "The tool needed to proceed to the next step: ");
        translationBuilder.add(PeonyTranslationKeys.JADE_HEAT_SOURCE_AVAILABLE_HEAT_AMOUNT, "Available heat range: %d°C-%d°C");
        translationBuilder.add(PeonyTranslationKeys.JADE_HEAT_SOURCE_HEATING_LEVEL, "Heating Level: ");
    }
}
