package net.george.peony.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.george.peony.recipe.ShreddingRecipe;
import net.minecraft.recipe.RecipeEntry;

import java.util.Collections;

public class ShreddingRecipeDisplay extends BasicDisplay {
    private final int durationDecrement;

    public ShreddingRecipeDisplay(RecipeEntry<ShreddingRecipe> recipeEntry) {
        super(Collections.singletonList(EntryIngredients.ofIngredient(recipeEntry.value().input())),
                Collections.singletonList(EntryIngredients.of(recipeEntry.value().output())));
        this.durationDecrement = recipeEntry.value().durationDecrement();
    }

    @Override
    public CategoryIdentifier<ShreddingRecipeDisplay> getCategoryIdentifier() {
        return PeonyREIPlugin.SHREDDING;
    }

    public int getDurationDecrement() {
        return this.durationDecrement;
    }
}
