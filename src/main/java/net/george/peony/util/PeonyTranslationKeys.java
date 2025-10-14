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
    public static final String JADE_SKILLET_COOKING_TIME = createTranslationKey("jade", "tooltip", "skillet", "cooking", "time");
    public static final String JADE_SKILLET_COOKING_OVERFLOW_TIME = createTranslationKey("jade", "tooltip", "skillet", "cooking", "overflow", "time");
    public static final String JADE_SKILLET_CONTAINER_TOOLTIP = createTranslationKey("jade", "tooltip", "skillet", "container");
    public static final String JADE_SKILLET_NON_CONTAINER_TOOLTIP = createTranslationKey("jade", "tooltip", "skillet", "non", "container");
    public static final String JADE_SKILLET_MELTING_OIL = createTranslationKey("jade", "tooltip", "skillet", "melting", "oil");
    public static final String JADE_SKILLET_TOOL_USAGE_TOOLTIP = createTranslationKey("jade", "tooltip", "skillet", "tool", "usage", "tooltip");
    public static final String JADE_HEAT_SOURCE_AVAILABLE_HEAT_AMOUNT = createConfigTranslationKey("jade", "heat", "source", "available", "heat", "amount");
    public static final String JADE_HEAT_SOURCE_HEATING_LEVEL = createConfigTranslationKey("jade", "heat", "source", "heating", "level");

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
