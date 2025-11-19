package net.george.peony.block.data;

import java.util.Objects;

public final class RecipeStepTypes {
    public static final RecipeStepTypes CUTTING = create("cutting");
    public static final RecipeStepTypes COOKING = create("cooking");

    private final String name;

    private RecipeStepTypes(String name) {
        this.name = name;
    }

    public static RecipeStepTypes create(String name) {
        return new RecipeStepTypes(name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || getClass() != another.getClass()) {
            return false;
        }
        RecipeStepTypes that = (RecipeStepTypes) another;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
