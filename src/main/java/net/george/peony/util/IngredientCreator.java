package net.george.peony.util;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class IngredientCreator {
    public static Ingredient create(ItemConvertible item) {
        return create(item, 1);
    }

    public static Ingredient create(ItemConvertible item, int count) {
        return create(new ItemStack(item, count));
    }

    public static Ingredient create(ItemStack stack) {
        return Ingredient.ofStacks(stack);
    }
}
