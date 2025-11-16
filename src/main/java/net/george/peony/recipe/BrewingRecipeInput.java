package net.george.peony.recipe;

import net.george.peony.util.FluidStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.collection.DefaultedList;

public record BrewingRecipeInput(DefaultedList<ItemStack> stacks, FluidStack fluid) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.stacks.get(slot);
    }

    @Override
    public int getSize() {
        return this.stacks.size();
    }
}
