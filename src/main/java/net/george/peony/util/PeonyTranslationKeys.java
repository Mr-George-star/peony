package net.george.peony.util;

import net.george.peony.Peony;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

public class PeonyTranslationKeys {
    public static final String ITEM_GROUP_KEY = createTranslationKey("itemGroup", "item_group");

    public static final String NATURE_GAS_DETECTOR_ITEM_FOUND = createTranslationKey("item", "nature", "gas", "detector", "found");
    public static final String NATURE_GAS_DETECTOR_ITEM_NOTHING = createTranslationKey("item", "nature", "gas", "detector", "nothing");

    public static final String MILLING_RECIPE_CATEGORY_TITLE = createTranslationKey("category", "milling");
    public static final String MILLING_RECIPE_MILLING_TIMES = createTranslationKey("category", "milling", "milling", "times");
    public static final String SEQUENTIAL_CRAFTING_RECIPE_CATEGORY_TITLE = createTranslationKey("category", "sequential", "crafting");

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
    public static final String JADE_STEP = createTranslationKey("jade", "step");
    public static final String JADE_TIME_REMAINING = createTranslationKey("jade", "time", "remaining");
    public static final String JADE_TIME_LIMIT = createTranslationKey("jade", "time", "limit");
    public static final String JADE_REQUIRES = createTranslationKey("jade", "requires");
    public static final String JADE_NO_HEAT_SOURCE = createTranslationKey("jade", "no", "heat", "source");

    // skillet statements
    public static final String JADE_STATE_IDLE = createTranslationKey("jade", "state", "idle");
    public static final String JADE_STATE_OIL_PROCESSING = createTranslationKey("jade", "state", "oil_processing");
    public static final String JADE_STATE_COMMON_INGREDIENT_PROCESSING = createTranslationKey("jade", "state", "common_ingredient_processing");
    public static final String JADE_STATE_HEATING = createTranslationKey("jade", "state", "heating");
    public static final String JADE_STATE_STIR_FRYING = createTranslationKey("jade", "state", "stir_frying");
    public static final String JADE_STATE_OVERFLOW = createTranslationKey("jade", "state", "overflow");
    public static final String JADE_STATE_WAITING_FOR_INGREDIENT = createTranslationKey("jade", "state", "waiting_for_ingredient");
    public static final String JADE_STATE_COMPLETED = createTranslationKey("jade", "state", "completed");
    public static final String JADE_STATE_FAILED = createTranslationKey("jade", "state", "failed");

    // skillet
    public static final String JADE_SKILLET_MELTING_OIL = createTranslationKey("jade", "skillet", "melting", "oil");
    public static final String JADE_SKILLET_CONTINUE = createTranslationKey("jade", "skillet", "continue");
    public static final String JADE_SKILLET_HEATING_TIME = createTranslationKey("jade", "skillet", "heating", "time");
    public static final String JADE_SKILLET_HEATING = createTranslationKey("jade", "skillet", "heating");
    public static final String JADE_SKILLET_STIR_FRYING_COUNT = createTranslationKey("jade", "skillet", "stir", "frying", "count");
    public static final String JADE_SKILLET_STIR_FRYING = createTranslationKey("jade", "skillet", "stir", "frying");
    public static final String JADE_SKILLET_OVERFLOW_TIME = createTranslationKey("jade", "skillet", "overflow", "time");
    public static final String JADE_SKILLET_TOOL_USAGE_TOOLTIP = createTranslationKey("jade", "skillet", "tool", "usage", "tooltip");
    public static final String JADE_SKILLET_CONTAINER_TOOLTIP = createTranslationKey("jade", "skillet", "container", "tooltip");
    public static final String JADE_SKILLET_NON_CONTAINER_TOOLTIP = createTranslationKey("jade", "skillet", "non", "container", "tooltip");
    public static final String JADE_SKILLET_HAS_OIL = createTranslationKey("jade", "skillet", "has", "oil");
    public static final String JADE_SKILLET_HAS_COMMON_INGREDIENT = createTranslationKey("jade", "skillet", "has", "common", "ingredient");
    public static final String JADE_SKILLET_PREPARING_INGREDIENT = createTranslationKey("jade", "skillet", "preparing", "ingredient");
    public static final String JADE_SKILLET_ADD_INGREDIENT = createTranslationKey("jade", "skillet", "add", "ingredient");
    public static final String JADE_SKILLET_WAITING_FOR_INGREDIENT = createTranslationKey("jade", "skillet", "waiting", "for", "skillet");
    public static final String JADE_SKILLET_OVERFLOW_WARNING = createTranslationKey("jade", "skillet", "overflow", "warning");
    public static final String JADE_SKILLET_COMPLETED = createTranslationKey("jade", "skillet", "completed");
    public static final String JADE_SKILLET_FAILED = createTranslationKey("jade", "skillet", "failed");
    public static final String JADE_SKILLET_READY_TO_EXTRACT = createTranslationKey("jade", "skillet", "ready", "to", "extract");

    // heat source
    public static final String JADE_HEAT_SOURCE_AVAILABLE_HEAT_AMOUNT = createConfigTranslationKey("jade", "heat", "source", "available", "heat", "amount");
    public static final String JADE_HEAT_SOURCE_HEATING_LEVEL = createConfigTranslationKey("jade", "heat", "source", "heating", "level");

    public static String createAdvancementTranslationKey(String... items) {
        return createTranslationKey("advancement", items);
    }

    public static String createConfigTranslationKey(String... items) {
        return createTranslationKey("config", items);
    }

    public static String createTranslationKey(String type, String... items) {
        return createTranslationKey(type, StringUtils.join(items, "/"));
    }

    public static String createTranslationKey(String type, String item) {
        return Util.createTranslationKey(type, Peony.id(item));
    }
}
