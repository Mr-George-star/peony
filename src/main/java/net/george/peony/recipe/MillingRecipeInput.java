package net.george.peony.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record MillingRecipeInput(ItemStack input) implements RecipeInput {
    public ItemStack getInputStack() {
        return getStackInSlot(0);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.input;
    }

    @Override
    public int getSize() {
        return 1;
    }
}
