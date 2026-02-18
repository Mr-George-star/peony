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

public class PeonyEnglishTranslationProvider extends FabricLanguageProvider {
    public PeonyEnglishTranslationProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder builder) {
        builder.add(PeonyItems.BARLEY, "Barley");
        builder.add(PeonyItems.BARLEY_SEEDS, "Barley Seeds");
        builder.add(PeonyItems.PEANUT, "Peanut");
        builder.add(PeonyItems.PEANUT_KERNEL, "Peanut Kernel");
        builder.add(PeonyItems.ROASTED_PEANUT_KERNEL, "Roasted Peanut Kernel");
        builder.add(PeonyItems.CRUSHED_PEANUTS, "Crushed Peanuts");
        builder.add(PeonyItems.TOMATO, "Tomato");
        builder.add(PeonyItems.TOMATO_SEEDS, "Tomato Seeds");
        builder.add(PeonyItems.PEELED_TOMATO, "Peeled Tomato");
        builder.add(PeonyItems.PEELED_POTATO, "Peeled Potato");
        builder.add(PeonyItems.SHREDDED_POTATO, "Shredded Potato");
        builder.add(PeonyItems.CORIANDER, "Coriander");
        builder.add(PeonyItems.CORIANDER_SEEDS, "Coriander Seeds");
        builder.add(PeonyItems.RICE_PANICLE, "Rice Panicle");
        builder.add(PeonyItems.BROWN_RICE, "Brown Rice");
        builder.add(PeonyItems.RICE, "Rice");
        builder.add(PeonyItems.GARLIC, "Garlic");
        builder.add(PeonyItems.GARLIC_CLOVE, "Garlic Clove");
        builder.add(PeonyItems.GARLIC_SCAPE, "Garlic Scape");
        builder.add(PeonyItems.MINCED_GARLIC, "Minced Garlic");
        builder.add(PeonyItems.SOYBEAN, "Soybean");
        builder.add(PeonyItems.SOYBEAN_POD, "Soybean Pod");
        builder.add(PeonyItems.HAM, "Ham");
        builder.add(PeonyItems.BAKED_FLATBREAD, "Baked Flatbread");
        builder.add(PeonyItems.TOMATO_SAUCE, "Tomato Sauce");
        builder.add(PeonyItems.SCRAMBLED_EGGS, "Scrambled Eggs");
        builder.add(PeonyItems.SCRAMBLED_EGGS_WITH_TOMATOES, "Scrambled Eggs With Tomatoes");
        builder.add(PeonyItems.FRIED_SHREDDED_POTATOES, "Fried Shredded Potatoes");
        builder.add(PeonyItems.STIR_FRIED_GARLIC_SCAPE_WITH_PORK, "Stir-Fried Garlic Scape With Pork");
        builder.add(PeonyItems.SWEET_AND_SOUR_PORK, "Sweet And Sour Pork");
        builder.add(PeonyItems.CHEESE, "Cheese");
        builder.add(PeonyItems.SHREDDED_CHEESE, "Shredded Cheese");

        builder.add(PeonyItems.LARD, "Lard");
        builder.add(PeonyItems.LARD_BOTTLE, "Lard Bottle");
        builder.add(PeonyItems.PORK_TENDERLOIN, "Pork Tenderloin");
        builder.add(PeonyItems.CONDIMENT_BOTTLE, "Condiment Bottle");
        builder.add(PeonyItems.BLACK_VINEGAR, "Black Vinegar");
        builder.add(PeonyItems.SWEET_SOUR_SAUCE, "Sweet Sour Sauce");
        builder.add(PeonyItems.SOY_SAUCE, "Soy Sauce");

        builder.add(PeonyItems.KITCHEN_KNIFE, "Kitchen Knife");
        builder.add(PeonyItems.SPATULA, "Spatula");
        builder.add(PeonyItems.IRON_PARING_KNIFE, "Iron Paring Knife");
        builder.add(PeonyItems.IRON_SHREDDER, "Iron Shredder");
        builder.add(PeonyItems.GOLD_SHREDDER, "Gold Shredder");
        builder.add(PeonyItems.DIAMOND_SHREDDER, "Diamond Shredder");
        builder.add(PeonyItems.NETHERITE_SHREDDER, "Netherite Shredder");
        builder.add(PeonyItems.WOODEN_PLATE, "Wooden Plate");
        builder.add(PeonyItems.NATURE_GAS_DETECTOR, "Nature Gas Detector");

        builder.add(PeonyItems.NATURE_GAS_BUCKET, "Nature Gas Bucket");
        builder.add(PeonyItems.LARD_BUCKET, "Lard Bucket");
        builder.add(PeonyItems.SOY_SAUCE_BUCKET, "Soy Sauce Bucket");

        builder.add(PeonyItems.MUSIC_DISC_SURPRISE, "Surprise");

        // blocks
        builder.add(PeonyBlocks.DOUGH, "Dough");
        builder.add(PeonyBlocks.FLOUR, "Flour");
        builder.add(PeonyBlocks.FLATBREAD, "Flatbread");
        builder.add(PeonyBlocks.CHEESE_BLOCK, "Block of Cheese");

        builder.add(PeonyBlocks.RAW_MARGHERITA_PIZZA, "Raw Margherita Pizza");
        builder.add(PeonyBlocks.RAW_MARGHERITA_PIZZA.asItem(), "Raw Margherita Pizza");
        builder.add(PeonyBlocks.MARGHERITA_PIZZA, "Margherita Pizza");
        builder.add(PeonyBlocks.MARGHERITA_PIZZA.asItem(), "Margherita Pizza");

        builder.add(PeonyBlocks.MILLSTONE, "Millstone");
        builder.add(PeonyBlocks.OAK_CUTTING_BOARD, "Oak Cutting Board");
        builder.add(PeonyBlocks.SPRUCE_CUTTING_BOARD, "Spruce Cutting Board");
        builder.add(PeonyBlocks.BIRCH_CUTTING_BOARD, "Birch Cutting Board");
        builder.add(PeonyBlocks.JUNGLE_CUTTING_BOARD, "Jungle Cutting Board");
        builder.add(PeonyBlocks.ACACIA_CUTTING_BOARD, "Acacia Cutting Board");
        builder.add(PeonyBlocks.CHERRY_CUTTING_BOARD, "Cherry Cutting Board");
        builder.add(PeonyBlocks.DARK_OAK_CUTTING_BOARD, "Dark Oak Cutting Board");
        builder.add(PeonyBlocks.MANGROVE_CUTTING_BOARD, "Mangrove Cutting Board");
        builder.add(PeonyBlocks.SKILLET, "Skillet");
        builder.add(PeonyBlocks.BREWING_BARREL, "Brewing Barrel");
        builder.add(PeonyBlocks.FERMENTATION_TANK, "Fermentation Tank");
        builder.add(PeonyBlocks.GAS_CYLINDER, "Gas Cylinder");
        builder.add(PeonyBlocks.GAS_STOVE, "Gas Stove");
        builder.add(PeonyBlocks.OAK_LOG_STICK, "Oak Log Stick");
        builder.add(PeonyBlocks.SPRUCE_LOG_STICK, "Spruce Log Stick");
        builder.add(PeonyBlocks.BIRCH_LOG_STICK, "Birch Log Stick");
        builder.add(PeonyBlocks.JUNGLE_LOG_STICK, "Jungle Log Stick");
        builder.add(PeonyBlocks.ACACIA_LOG_STICK, "Acacia Log Stick");
        builder.add(PeonyBlocks.CHERRY_LOG_STICK, "Cherry Log Stick");
        builder.add(PeonyBlocks.DARK_OAK_LOG_STICK, "Dark Oak Log Stick");
        builder.add(PeonyBlocks.MANGROVE_LOG_STICK, "Mangrove Log Stick");
        builder.add(PeonyBlocks.OAK_POT_STAND, "Oak Pot Stand");
        builder.add(PeonyBlocks.SPRUCE_POT_STAND, "Spruce Pot Stand");
        builder.add(PeonyBlocks.BIRCH_POT_STAND, "Birch Pot Stand");
        builder.add(PeonyBlocks.JUNGLE_POT_STAND, "Jungle Pot Stand");
        builder.add(PeonyBlocks.ACACIA_POT_STAND, "Acacia Pot Stand");
        builder.add(PeonyBlocks.CHERRY_POT_STAND, "Cherry Pot Stand");
        builder.add(PeonyBlocks.DARK_OAK_POT_STAND, "Dark Oak Pot Stand");
        builder.add(PeonyBlocks.MANGROVE_POT_STAND, "Mangrove Pot Stand");
        builder.add(PeonyBlocks.OAK_POT_STAND_WITH_CAMPFIRE, "Oak Pot Stand With Campfire");
        builder.add(PeonyBlocks.SPRUCE_POT_STAND_WITH_CAMPFIRE, "Spruce Pot Stand With Campfire");
        builder.add(PeonyBlocks.BIRCH_POT_STAND_WITH_CAMPFIRE, "Birch Pot Stand With Campfire");
        builder.add(PeonyBlocks.JUNGLE_POT_STAND_WITH_CAMPFIRE, "Jungle Pot Stand With Campfire");
        builder.add(PeonyBlocks.ACACIA_POT_STAND_WITH_CAMPFIRE, "Acacia Pot Stand With Campfire");
        builder.add(PeonyBlocks.CHERRY_POT_STAND_WITH_CAMPFIRE, "Cherry Pot Stand With Campfire");
        builder.add(PeonyBlocks.DARK_OAK_POT_STAND_WITH_CAMPFIRE, "Dark Oak Pot Stand With Campfire");
        builder.add(PeonyBlocks.MANGROVE_POT_STAND_WITH_CAMPFIRE, "Mangrove Pot Stand With Campfire");

        builder.add(PeonyBlocks.BARLEY_CROP, "Barley Crop");
        builder.add(PeonyBlocks.PEANUT_CROP, "Peanut Crop");
        builder.add(PeonyBlocks.TOMATO_VINES, "Tomato Vines");
        builder.add(PeonyBlocks.RICE_CROP, "Rice Crop");
        builder.add(PeonyBlocks.CORIANDER_CROP, "Coriander Crop");
        builder.add(PeonyBlocks.GARLIC_CROP, "Garlic Crop");

        builder.add(PeonyBlocks.NATURE_GAS, "Nature Gas");
        builder.add(PeonyBlocks.LARD_FLUID, "Lard");
        builder.add(PeonyBlocks.SOY_SAUCE_FLUID, "Soy Sauce");
        builder.add(PeonyBlocks.LARD_CAULDRON, "Lard Cauldron");

        builder.add(PeonyBlocks.BOWL, "Bowl");

        builder.add(PeonyTranslationKeys.ITEM_GROUP_KEY, "Peony");

        builder.add(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_FOUND, "§bThere is natural gas down here!");
        builder.add(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_NOTHING, "§7There is no natural gas down here.");

        /* SOUNDS */
        builder.add("sound.peony.shear_using", "Using Shear");
        builder.add("sound.peony.paring", "Paring");

        /* JUKEBOX SONGS */
        builder.add("jukebox_song.peony.surprise", "Surprise (Never Gonna Give You Up) - Rick Astley");

        /* DAMAGE TYPES */
        builder.add("death.attack.scald", "%1$s was scalded to death");
        builder.add("death.attack.scald.player", "%1$s was scalded to death in a fight with %2$s");

        /* HEATING LEVELS */
        builder.add(HeatLevel.NONE.getTranslationKey(), "None");
        builder.add(HeatLevel.SMOLDERING.getTranslationKey(), "Smoldering");
        builder.add(HeatLevel.LOW.getTranslationKey(), "Low");
        builder.add(HeatLevel.HIGH.getTranslationKey(), "High");
        builder.add(HeatLevel.BLAZING.getTranslationKey(), "Blazing");

        /* MESSAGES */
        builder.add(PeonyTranslationKeys.MESSAGE_FLATBREAD_NO_INGREDIENTS, "No ingredients were added");
        builder.add(PeonyTranslationKeys.MESSAGE_FLATBREAD_CREATE_SUCCESS, "Successfully created a pizza!");
        builder.add(PeonyTranslationKeys.MESSAGE_FLATBREAD_NO_RECIPE, "This is not a kind of pizza!");

        /* STATS */
        builder.add("stat.peony.skillet_cooking_success", "Skillet Cooking Successes");
        builder.add("stat.peony.skillet_cooking_failure", "Skillet Cooking Failures");

        /* ADVANCEMENTS */
        builder.add(PeonyTranslationKeys.ADVANCEMENT_ROOT_TITLE, "Peony");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_ROOT_DESCRIPTION, "In the journey of preparing various foods, a spatula is of course indispensable");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_COOKING_OIL_TITLE, "This oil... is edible!");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_COOKING_OIL_DESCRIPTION, "Obtain any type of cooking oil, whether solid or liquid");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_KITCHENWARE_TITLE, "The Road of Culinary Mastery");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_KITCHENWARE_DESCRIPTION, "Acquire any type of kitchen utensil to begin your journey to becoming a culinary master");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_SKILLET_TITLE, "Skillet");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_SKILLET_DESCRIPTION, "The skillet, one of the core components of cooking, is a very important kitchen utensil");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_SKILLET_COOKING_SUCCEED_TITLE, "Cooking succeed!");
        builder.add(PeonyTranslationKeys.ADVANCEMENT_SKILLET_COOKING_SUCCEED_DESCRIPTION, "Successfully cook any dish using a skillet");

        /* ACTION */
        builder.add(ActionTypes.KNEADING.createTranslationKey(), "Kneading");
        builder.add(ActionTypes.CUTTING.createTranslationKey(), "Cutting");
        builder.add(ActionTypes.SLICING.createTranslationKey(), "Slicing");

        /* CONFIG */
        builder.add(PeonyTranslationKeys.CONFIG_SCREEN_TITLE, "Peony");
        builder.add(PeonyTranslationKeys.CONFIG_CATEGORY_COMMON, "Common");
        builder.add(PeonyTranslationKeys.CONFIG_CATEGORY_EXPERIMENTAL, "Experimental Options");
        // options
        builder.add(PeonyTranslationKeys.OPTION_LARD_SLOWNESS_DURATION_TICKS, "Duration Ticks of Slowness in Lard");
        builder.add(PeonyTranslationKeys.OPTION_LARD_FIRE_EXTENSION_TICKS, "Fire Extension Ticks in Lard");
        builder.add(PeonyTranslationKeys.OPTION_DEBUG_COMMANDS, "Enable Commands for Debugging");
        // descriptions
        builder.add(PeonyTranslationKeys.CONFIG_CATEGORY_DESCRIPTION_COMMON, "Common Configuration Options");
        builder.add(PeonyTranslationKeys.CONFIG_CATEGORY_DESCRIPTION_EXPERIMENTAL, "Experimental and Configuration Options for Debugging");

        builder.add(PeonyTranslationKeys.OPTION_DESCRIPTION_LARD_SLOWNESS_DURATION_TICKS, "The duration of the slowness effect in lard. When the player is in lard fluid, the slowness effect will be applied. \nThe following durations are in ticks (1 second = 20 ticks).");
        builder.add(PeonyTranslationKeys.OPTION_DESCRIPTION_LARD_FIRE_EXTENSION_TICKS, "If the player is on fire, entering or jumping into lard fluid will extend the fire duration (including cauldrons containing lard). \nThe following durations are in ticks (1 second = 20 ticks).");

        builder.add(PeonyTranslationKeys.SECOND, "%d Second(s)");

        /* JADE */
        // config
        builder.add("config.jade.plugin_peony.skillet_component", "Skillet Status Display");
        builder.add("config.jade.plugin_peony.default_heat_source_component", "Default Temperature Data Display");
        builder.add("config.jade.plugin_peony.default_openable_component", "Default Opening State Display");
        builder.add("config.jade.plugin_peony.fermentation_tank_component", "Fermenting Data Display");

        // global
        builder.add(PeonyTranslationKeys.JADE_STEP, "Step: %d / %d");
        builder.add(PeonyTranslationKeys.JADE_TIME_REMAINING, "Time remaining: %d Seconds");
        builder.add(PeonyTranslationKeys.JADE_TIME_LIMIT, "Time limit: %d Seconds");
        builder.add(PeonyTranslationKeys.JADE_REQUIRES, "Requires");
        builder.add(PeonyTranslationKeys.JADE_NO_HEAT_SOURCE, "⚠ No heat source");
        builder.add(PeonyTranslationKeys.JADE_OPENING_STATE, "Opening State: ");
        builder.add(PeonyTranslationKeys.JADE_OPENED, "Opened");
        builder.add(PeonyTranslationKeys.JADE_CLOSED, "Closed");
        builder.add(PeonyTranslationKeys.JADE_FERMENT_REMAINING_TIME, "Ferment Remaining Time: %d Seconds");
        builder.add(PeonyTranslationKeys.JADE_OUTPUT_STACK, "Output: ");

        // skillet statements
        builder.add(PeonyTranslationKeys.JADE_STATE_IDLE, "Idle");
        builder.add(PeonyTranslationKeys.JADE_STATE_OIL_PROCESSING, "Processing Oil");
        builder.add(PeonyTranslationKeys.JADE_STATE_COMMON_INGREDIENT_PROCESSING, "Processing basic ingredient");
        builder.add(PeonyTranslationKeys.JADE_STATE_HEATING, "Heating");
        builder.add(PeonyTranslationKeys.JADE_STATE_STIR_FRYING, "Stir-frying");
        builder.add(PeonyTranslationKeys.JADE_STATE_OVERFLOW, "Overflow warning");
        builder.add(PeonyTranslationKeys.JADE_STATE_WAITING_FOR_INGREDIENT, "Waiting for ingredient");
        builder.add(PeonyTranslationKeys.JADE_STATE_COMPLETED, "Completed");
        builder.add(PeonyTranslationKeys.JADE_STATE_FAILED, "Failed");

        // skillet
        builder.add(PeonyTranslationKeys.JADE_SKILLET_MELTING_OIL, "Melting oil");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_CONTINUE, "Continue: %d / %d");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_HEATING_TIME, "Heating: %d / %d");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_HEATING, "Heating");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_STIR_FRYING_COUNT, "Stir-frying：%d / %d");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_STIR_FRYING, "Stir-frying");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_OVERFLOW_TIME, "Overflow: %d / %d");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_TOOL_USAGE_TOOLTIP, "Use tool to continue");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_CONTAINER_TOOLTIP, "Use container to extract");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_NON_CONTAINER_TOOLTIP, "Ready to extract");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_HAS_OIL, "Oil added");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_HAS_COMMON_INGREDIENT, "Basic ingredient added");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_PREPARING_INGREDIENT, "Preparing ingredient");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_ADD_INGREDIENT, "Please add ingredient");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_WAITING_FOR_INGREDIENT, "Waiting for ingredient: %d Seconds");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_OVERFLOW_WARNING, "⚠ About to burn!");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_COMPLETED, "✓ Cooking completed");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_FAILED, "✗ Cooking failed");
        builder.add(PeonyTranslationKeys.JADE_SKILLET_READY_TO_EXTRACT, "Ready to extract");

        // heat source
        builder.add(PeonyTranslationKeys.JADE_HEAT_SOURCE_AVAILABLE_HEAT_AMOUNT, "Available heat range: %d°C-%d°C");
        builder.add(PeonyTranslationKeys.JADE_HEAT_SOURCE_HEATING_LEVEL, "Heating Level: ");

        /* REI */
        builder.add(PeonyTranslationKeys.REI_CATEGORY_MILLING, "Milling");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_SEQUENTIAL_CRAFTING, "Sequential Crafting");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_SEQUENTIAL_COOKING, "Sequential Cooking");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_SHREDDING, "Shredding");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_PARING, "Paring");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_FLAVOURING_PREPARING, "Flavouring Preparing");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_BREWING, "Brewing");
        builder.add(PeonyTranslationKeys.REI_CATEGORY_PIZZA_CRAFTING, "Pizza Crafting");

        builder.add(PeonyTranslationKeys.REI_MILLING_TIMES, "Mill %d Time(s)");
        builder.add(PeonyTranslationKeys.REI_NO_INGREDIENTS, "No Ingredients Required");
        builder.add(PeonyTranslationKeys.REI_REQUIRED_INGREDIENTS, "Required Ingredients");
        builder.add(PeonyTranslationKeys.REI_ACTION, "Required Action");
        builder.add(PeonyTranslationKeys.REI_REQUIRED_TIME, "%ds");
        builder.add(PeonyTranslationKeys.REI_STIR_FRYING, "Stir %dx");
        builder.add(PeonyTranslationKeys.REI_HEATING, "Heating");
        builder.add(PeonyTranslationKeys.REI_REQUIRES_CONTAINER, "Requires Container");
        builder.add(PeonyTranslationKeys.REI_NO_CONTAINER, "Can Extract Directly");
        builder.add(PeonyTranslationKeys.REI_TEMPERATURE, "Heat: %d°C");
        builder.add(PeonyTranslationKeys.REI_STEP, "Step %d");
        builder.add(PeonyTranslationKeys.REI_DURATION_DECREMENT, "Duration Decrement: %d");
        builder.add(PeonyTranslationKeys.REI_STIRRING_TIMES, "Stirring Times: %d");
        builder.add(PeonyTranslationKeys.REI_BREWING_TIMES, "Brewing Times: %d Seconds");
    }
}
