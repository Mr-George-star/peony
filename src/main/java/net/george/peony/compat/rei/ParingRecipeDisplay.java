package net.george.peony.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.george.peony.recipe.ParingRecipe;
import net.minecraft.recipe.RecipeEntry;

import java.util.Collections;

public class ParingRecipeDisplay extends BasicDisplay {
    public ParingRecipeDisplay(RecipeEntry<ParingRecipe> recipeEntry) {
        super(Collections.singletonList(EntryIngredients.ofIngredient(recipeEntry.value().input())),
                Collections.singletonList(EntryIngredients.of(recipeEntry.value().output())));
    }

    @Override
    public CategoryIdentifier<ParingRecipeDisplay> getCategoryIdentifier() {
        return PeonyREIPlugin.PARING;
    }
}
