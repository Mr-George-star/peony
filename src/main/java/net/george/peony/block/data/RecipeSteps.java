package net.george.peony.block.data;

import net.minecraft.recipe.Ingredient;

import java.util.List;
import java.util.Objects;

public abstract class RecipeSteps<T extends RecipeStep> {
    protected final List<T> steps;

    protected RecipeSteps(List<T> steps) {
        this.steps = steps;
    }

    public List<T> getSteps() {
        return this.steps;
    }

    public RecipeStepsCursor<T> createCursor(int currentIndex) {
        return new RecipeStepsCursor<>(this.steps, Math.min(currentIndex, this.getSteps().size() - 1));
    }

    public List<Ingredient> getIngredients() {
        return this.steps.stream().map(T::getIngredient).toList();
    }

    @Override
    public String toString() {
        return this.steps.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.steps);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || getClass() != another.getClass()) {
            return false;
        }
        RecipeSteps<T> steps = (RecipeSteps) another;
        return Objects.equals(this.steps, steps.steps);
    }
}
