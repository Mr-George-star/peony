package net.george.peony.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.george.peony.api.fluid.FluidStack;
import net.george.peony.block.data.Output;
import net.george.peony.recipe.BrewingRecipe;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class BrewingRecipeDisplay extends BasicDisplay {
    private final FluidStack basicFluid;
    private final int brewingTime;
    @Nullable
    private final ItemConvertible container;

    public BrewingRecipeDisplay(RecipeEntry<BrewingRecipe> recipeEntry) {
        super(recipeEntry.value().ingredients().stream().map(EntryIngredients::of).toList(),
                Collections.singletonList(EntryIngredients.of(recipeEntry.value().output().getOutputStack())));

        BrewingRecipe recipe = recipeEntry.value();
        this.basicFluid = recipe.basicFluid();
        this.brewingTime = recipe.brewingTime();
        this.container = Output.getRequiredContainer(recipe.output());
    }

    @Override
    public CategoryIdentifier<BrewingRecipeDisplay> getCategoryIdentifier() {
        return PeonyREIPlugin.BREWING;
    }

    public FluidStack getBasicFluid() {
        return this.basicFluid;
    }

    public int getBrewingTime() {
        return this.brewingTime;
    }

    @Nullable
    public ItemConvertible getContainer() {
        return this.container;
    }
}
