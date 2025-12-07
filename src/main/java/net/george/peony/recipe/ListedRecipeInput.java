package net.george.peony.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.collection.DefaultedList;

public class ListedRecipeInput implements RecipeInput {
    private final DefaultedList<ItemStack> inputs;

    public ListedRecipeInput(DefaultedList<ItemStack> inputs) {
        this.inputs = inputs;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < this.getSize() ? this.inputs.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public int getSize() {
        return this.inputs.size();
    }
}
