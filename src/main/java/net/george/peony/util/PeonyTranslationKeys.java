package net.george.peony.util;

import net.george.peony.Peony;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

public class PeonyTranslationKeys {
    public static final String ITEM_GROUP_KEY = createTranslationKey("itemGroup", "item_group");

    public static final String NATURE_GAS_DETECTOR_ITEM_FOUND = createTranslationKey("item", "nature", "gas", "detector", "found");
    public static final String NATURE_GAS_DETECTOR_ITEM_NOTHING = createTranslationKey("item", "nature", "gas", "detector", "nothing");

    public static final String MESSAGE_FLATBREAD_NO_INGREDIENTS = createTranslationKey("message", "flatbread", "no", "ingredients");
    public static final String MESSAGE_FLATBREAD_CREATE_SUCCESS = createTranslationKey("message", "flatbread", "create", "success");
    public static final String MESSAGE_FLATBREAD_NO_RECIPE = createTranslationKey("message", "flatbread", "no", "recipe");

    /* Advancements */
    public static final String ADVANCEMENT_ROOT_TITLE = createAdvancementTranslationKey("root");
    public static final String ADVANCEMENT_ROOT_DESCRIPTION = createAdvancementTranslationKey("root", "description");
    public static final String ADVANCEMENT_COOKING_OIL_TITLE = createAdvancementTranslationKey("cooking_oil");
    public static final String ADVANCEMENT_COOKING_OIL_DESCRIPTION = createAdvancementTranslationKey("cooking_oil", "description");
    public static final String ADVANCEMENT_KITCHENWARE_TITLE = createAdvancementTranslationKey("kitchenware");
    public static final String ADVANCEMENT_KITCHENWARE_DESCRIPTION = createAdvancementTranslationKey("kitchenware", "description");
    public static final String ADVANCEMENT_SKILLET_TITLE = createAdvancementTranslationKey("skillet");
    public static final String ADVANCEMENT_SKILLET_DESCRIPTION = createAdvancementTranslationKey("skillet", "description");
    public static final String ADVANCEMENT_SKILLET_COOKING_SUCCEED_TITLE = createAdvancementTranslationKey("skillet", "cooking_succeed");
    public static final String ADVANCEMENT_SKILLET_COOKING_SUCCEED_DESCRIPTION = createAdvancementTranslationKey("skillet", "cooking_succeed", "description");

    /* CONFIG */
    public static final String CONFIG_SCREEN_TITLE = createConfigTranslationKey("title");
    public static final String CONFIG_CATEGORY_COMMON = createConfigTranslationKey("category", "common");
    // options
    public static final String OPTION_LARD_SLOWNESS_DURATION_TICKS = createConfigTranslationKey("option", "lard", "slowness", "duration", "ticks");
    public static final String OPTION_LARD_FIRE_EXTENSION_TICKS = createConfigTranslationKey("option", "lard", "fire", "extension", "ticks");
    // descriptions
    public static final String CONFIG_CATEGORY_DESCRIPTION_COMMON = createConfigTranslationKey("description", "category", "common");
    public static final String OPTION_DESCRIPTION_LARD_SLOWNESS_DURATION_TICKS = createConfigTranslationKey("description", "option", "lard", "slowness", "duration", "ticks");
    public static final String OPTION_DESCRIPTION_LARD_FIRE_EXTENSION_TICKS = createConfigTranslationKey("description", "option", "lard", "fire", "extension", "ticks");

    public static final String SECOND = createTranslationKey("unit", "second");

    /* JADE */
    // global
    public static final String JADE_STEP = createJadeTranslationKey("step");
    public static final String JADE_TIME_REMAINING = createJadeTranslationKey("time", "remaining");
    public static final String JADE_TIME_LIMIT = createJadeTranslationKey("time", "limit");
    public static final String JADE_REQUIRES = createJadeTranslationKey("requires");
    public static final String JADE_NO_HEAT_SOURCE = createJadeTranslationKey("no", "heat", "source");

    // skillet statements
    public static final String JADE_STATE_IDLE = createJadeTranslationKey("state", "idle");
    public static final String JADE_STATE_OIL_PROCESSING = createJadeTranslationKey("state", "oil_processing");
    public static final String JADE_STATE_COMMON_INGREDIENT_PROCESSING = createJadeTranslationKey("state", "common_ingredient_processing");
    public static final String JADE_STATE_HEATING = createJadeTranslationKey("state", "heating");
    public static final String JADE_STATE_STIR_FRYING = createJadeTranslationKey("state", "stir_frying");
    public static final String JADE_STATE_OVERFLOW = createJadeTranslationKey("state", "overflow");
    public static final String JADE_STATE_WAITING_FOR_INGREDIENT = createJadeTranslationKey("state", "waiting_for_ingredient");
    public static final String JADE_STATE_COMPLETED = createJadeTranslationKey("state", "completed");
    public static final String JADE_STATE_FAILED = createJadeTranslationKey("state", "failed");

    // skillet
    public static final String JADE_SKILLET_MELTING_OIL = createJadeTranslationKey("skillet", "melting", "oil");
    public static final String JADE_SKILLET_CONTINUE = createJadeTranslationKey("skillet", "continue");
    public static final String JADE_SKILLET_HEATING_TIME = createJadeTranslationKey("skillet", "heating", "time");
    public static final String JADE_SKILLET_HEATING = createJadeTranslationKey("skillet", "heating");
    public static final String JADE_SKILLET_STIR_FRYING_COUNT = createJadeTranslationKey("skillet", "stir", "frying", "count");
    public static final String JADE_SKILLET_STIR_FRYING = createJadeTranslationKey("skillet", "stir", "frying");
    public static final String JADE_SKILLET_OVERFLOW_TIME = createJadeTranslationKey("skillet", "overflow", "time");
    public static final String JADE_SKILLET_TOOL_USAGE_TOOLTIP = createJadeTranslationKey("skillet", "tool", "usage", "tooltip");
    public static final String JADE_SKILLET_CONTAINER_TOOLTIP = createJadeTranslationKey("skillet", "container", "tooltip");
    public static final String JADE_SKILLET_NON_CONTAINER_TOOLTIP = createJadeTranslationKey("skillet", "non", "container", "tooltip");
    public static final String JADE_SKILLET_HAS_OIL = createJadeTranslationKey("skillet", "has", "oil");
    public static final String JADE_SKILLET_HAS_COMMON_INGREDIENT = createJadeTranslationKey("skillet", "has", "common", "ingredient");
    public static final String JADE_SKILLET_PREPARING_INGREDIENT = createJadeTranslationKey("skillet", "preparing", "ingredient");
    public static final String JADE_SKILLET_ADD_INGREDIENT = createJadeTranslationKey("skillet", "add", "ingredient");
    public static final String JADE_SKILLET_WAITING_FOR_INGREDIENT = createJadeTranslationKey("skillet", "waiting", "for", "skillet");
    public static final String JADE_SKILLET_OVERFLOW_WARNING = createJadeTranslationKey("skillet", "overflow", "warning");
    public static final String JADE_SKILLET_COMPLETED = createJadeTranslationKey("skillet", "completed");
    public static final String JADE_SKILLET_FAILED = createJadeTranslationKey("skillet", "failed");
    public static final String JADE_SKILLET_READY_TO_EXTRACT = createJadeTranslationKey("skillet", "ready", "to", "extract");

    // heat source
    public static final String JADE_HEAT_SOURCE_AVAILABLE_HEAT_AMOUNT = createConfigTranslationKey("jade", "heat", "source", "available", "heat", "amount");
    public static final String JADE_HEAT_SOURCE_HEATING_LEVEL = createConfigTranslationKey("jade", "heat", "source", "heating", "level");

    /* REI */
    public static final String REI_CATEGORY_MILLING = createTranslationKey("category.rei", "milling");
    public static final String REI_CATEGORY_SEQUENTIAL_CRAFTING = createTranslationKey("category.rei", "sequential_crafting");
    public static final String REI_CATEGORY_SEQUENTIAL_COOKING = createTranslationKey("category.rei", "sequential_cooking");
    public static final String REI_CATEGORY_SHREDDING = createTranslationKey("category.rei", "shredding");
    public static final String REI_CATEGORY_PARING = createTranslationKey("category.rei", "paring");
    public static final String REI_CATEGORY_FLAVOURING_PREPARING = createTranslationKey("category.rei", "flavouring_preparing");
    public static final String REI_CATEGORY_BREWING = createTranslationKey("category.rei", "brewing");
    public static final String REI_CATEGORY_PIZZA_CRAFTING = createTranslationKey("category.rei", "pizza_crafting");

    public static final String REI_MILLING_TIMES = createReiTranslationKey("milling", "times");
    public static final String REI_NO_INGREDIENTS = createReiTranslationKey("no", "ingredients");
    public static final String REI_REQUIRED_INGREDIENTS = createReiTranslationKey("required", "ingredients");
    public static final String REI_ACTION = createReiTranslationKey("action");
    public static final String REI_REQUIRED_TIME = createReiTranslationKey("time");
    public static final String REI_STIR_FRYING = createReiTranslationKey("stir", "frying");
    public static final String REI_HEATING = createReiTranslationKey("heating");
    public static final String REI_REQUIRES_CONTAINER = createReiTranslationKey("requires", "container");
    public static final String REI_NO_CONTAINER = createReiTranslationKey("no_container");
    public static final String REI_TEMPERATURE = createReiTranslationKey("temperature");
    public static final String REI_STEP = createReiTranslationKey("step");
    public static final String REI_DURATION_DECREMENT = createReiTranslationKey("duration", "decrement");
    public static final String REI_STIRRING_TIMES = createReiTranslationKey("stirring", "times");
    public static final String REI_BREWING_TIMES = createReiTranslationKey("brewing", "times");

    public static String createAdvancementTranslationKey(String... items) {
        return createTranslationKey("advancement", items);
    }

    public static String createConfigTranslationKey(String... items) {
        return createTranslationKey("config", items);
    }
    
    public static String createJadeTranslationKey(String... items) {
        return createTranslationKey("jade", items);
    }

    public static String createReiTranslationKey(String... items) {
        return createTranslationKey("rei", items);
    }

    public static String createTranslationKey(String type, String... items) {
        return createTranslationKey(type, StringUtils.join(items, "/"));
    }

    public static String createTranslationKey(String type, String item) {
        return Util.createTranslationKey(type, Peony.id(item));
    }
}
