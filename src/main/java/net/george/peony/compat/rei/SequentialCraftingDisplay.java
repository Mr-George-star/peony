package net.george.peony.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.george.peony.block.data.CraftingSteps;
import net.george.peony.recipe.SequentialCraftingRecipe;
import net.minecraft.recipe.RecipeEntry;

import java.util.Collections;
import java.util.List;

public class SequentialCraftingDisplay extends BasicDisplay {
    private final List<CraftingSteps.Step> steps;

    public SequentialCraftingDisplay(RecipeEntry<SequentialCraftingRecipe> recipe) {
        super(EntryIngredients.ofIngredients(recipe.value().getSteps().getSteps().stream().map(CraftingSteps.Step::getIngredient).toList()),
                Collections.singletonList(EntryIngredients.of(recipe.value().getOutput())));
        this.steps = recipe.value().getSteps().getSteps();
    }

    public List<CraftingSteps.Step> getSteps() {
        return this.steps;
    }

    @Override
    public CategoryIdentifier<SequentialCraftingDisplay> getCategoryIdentifier() {
        return PeonyREIPlugin.SEQUENTIAL_CRAFTING;
    }
}
