package net.george.peony.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.george.peony.recipe.PizzaCraftingRecipe;
import net.minecraft.recipe.RecipeEntry;

import java.util.Collections;

public class PizzaCraftingRecipeDisplay extends BasicDisplay {
    public PizzaCraftingRecipeDisplay(RecipeEntry<PizzaCraftingRecipe> recipeEntry) {
        super(recipeEntry.value().getIngredients().stream().map(EntryIngredients::ofIngredient).toList(),
                Collections.singletonList(EntryIngredients.of(recipeEntry.value().getOutput())));
    }

    @Override
    public CategoryIdentifier<PizzaCraftingRecipeDisplay> getCategoryIdentifier() {
        return PeonyREIPlugin.PIZZA_CRAFTING;
    }
}
