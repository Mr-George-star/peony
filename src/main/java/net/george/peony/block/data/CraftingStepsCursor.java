package net.george.peony.block.data;

import java.util.List;

@SuppressWarnings("unused")
public final class CraftingStepsCursor {
    final List<CraftingSteps.Step> steps;
    final int currentStepIndex;

    CraftingStepsCursor(List<CraftingSteps.Step> steps, int currentStepIndex) {
        this.steps = steps;
        this.currentStepIndex = currentStepIndex;
    }

    public List<CraftingSteps.Step> getSteps() {
        return this.steps;
    }

    public CraftingSteps.Step getCurrentStep() {
        if (this.currentStepIndex < 0 || this.currentStepIndex >= this.steps.size()) {
            return steps.getFirst();
        }
        return this.steps.get(this.currentStepIndex);
    }

    public int getCurrentStepIndex() {
        return this.currentStepIndex;
    }

    public int getLastStepIndex() {
        return this.getSteps().size() - 1;
    }
}
