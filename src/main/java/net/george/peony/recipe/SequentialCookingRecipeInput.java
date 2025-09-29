package net.george.peony.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public class SequentialCookingRecipeInput implements RecipeInput {
    protected final ItemStack stack;
    protected final boolean needOil;

    public SequentialCookingRecipeInput(ItemStack stack, boolean needOil) {
        this.stack = stack;
        this.needOil = needOil;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? this.stack : ItemStack.EMPTY;
    }

    public ItemStack getInputStack() {
        return this.stack;
    }

    public boolean isNeedOil() {
        return this.needOil;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public String toString() {
        return "Input[stack=" + this.stack + ", needOil=" + this.needOil + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SequentialCookingRecipeInput other)) {
            return false;
        }
        return ItemStack.areEqual(this.stack, other.stack) && this.needOil == other.needOil;
    }

    @Override
    public int hashCode() {
        return 31 * this.stack.getItem().hashCode() + Boolean.hashCode(this.needOil);
    }
}
