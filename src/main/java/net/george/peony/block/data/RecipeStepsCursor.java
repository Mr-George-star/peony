package net.george.peony.block.data;

import java.util.List;

@SuppressWarnings("unused")
public final class RecipeStepsCursor<T extends RecipeStep> {
    private final List<T> steps;
    private final int currentStepIndex;

    public RecipeStepsCursor(List<T> steps, int currentStepIndex) {
        this.steps = steps;
        this.currentStepIndex = currentStepIndex;
    }

    public List<T> getSteps() {
        return this.steps;
    }

    public T getCurrentStep() {
        if (this.steps == null || this.steps.isEmpty() || this.currentStepIndex >= this.steps.size()) {
            return null;
        }
        if (this.currentStepIndex < 0) {
            return this.steps.getFirst();
        }
        return this.steps.get(this.currentStepIndex);
    }

    public int getCurrentStepIndex() {
        return this.currentStepIndex;
    }

    public int getLastStepIndex() {
        if (this.steps == null || this.steps.isEmpty()) {
            return 0;
        }
        return this.getSteps().size() - 1;
    }
}
