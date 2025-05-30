package net.george.peony.util;

import net.george.peony.Peony;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

public class PeonyTranslationKeys {
    public static final String ITEM_GROUP_KEY = createTranslationKey("itemGroup", "item_group");
    public static final String MILLING_RECIPE_CATEGORY_TITLE = createTranslationKey("category", "milling");
    public static final String MILLING_RECIPE_MILLING_TIMES = createTranslationKey("category", "milling", "milling", "times");

    public static String createTranslationKey(String type, String... items) {
        return createTranslationKey(type, StringUtils.join(items, "/"));
    }

    public static String createTranslationKey(String type, String item) {
        return Util.createTranslationKey(type, Peony.id(item));
    }
}
