package net.george.peony.block.data;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

@SuppressWarnings({"SameParameterValue", "unused"})
public abstract class RecipeStep {
    protected final Ingredient ingredient;

    protected RecipeStep(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    protected static Ingredient ofItem(ItemConvertible item) {
        return Ingredient.ofItems(item);
    }

    protected static Ingredient ofStack(ItemStack stack) {
        return Ingredient.ofStacks(stack);
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    @Override
    public abstract boolean equals(Object obj);
}
