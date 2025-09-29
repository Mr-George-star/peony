package net.george.peony.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.george.peony.recipe.MillingRecipe;
import net.minecraft.recipe.RecipeEntry;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class MillstoneDisplay extends BasicDisplay {
    private final int millingTimes;

    public MillstoneDisplay(RecipeEntry<MillingRecipe> recipe) {
        super(List.of(EntryIngredients.ofIngredient(recipe.value().input())),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.value().getResult(BasicDisplay.registryAccess())))));
        this.millingTimes = recipe.value().millingTimes();
    }

    public int getMillingTimes() {
        return this.millingTimes;
    }

    @Override
    public CategoryIdentifier<MillstoneDisplay> getCategoryIdentifier() {
        return MillstoneCategory.MILLSTONE;
    }
}
