package net.george.peony.block.data;

import net.minecraft.recipe.Ingredient;

import java.util.List;

@SuppressWarnings("unused")
public final class CraftingStepsFetcher {
    final List<CraftingSteps.Step> steps;
    final int currentStepIndex;

    CraftingStepsFetcher(List<CraftingSteps.Step> steps, int currentStepIndex) {
        this.steps = steps;
        this.currentStepIndex = currentStepIndex;
    }

    public Ingredient getInitialIngredient() {
        return this.steps.getFirst().ingredient;
    }

    public List<CraftingSteps.Step> getSteps() {
        return this.steps;
    }

    public CraftingSteps.Step getCurrentStep() {
        return this.steps.get(this.currentStepIndex);
    }

    public int getCurrentStepIndex() {
        return this.currentStepIndex;
    }

    public int getLastStepIndex() {
        return this.getSteps().size() - 1;
    }
}
