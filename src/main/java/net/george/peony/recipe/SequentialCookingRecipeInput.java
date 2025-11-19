package net.george.peony.recipe;

import net.george.peony.api.data.CommonIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import org.jetbrains.annotations.Nullable;

public class SequentialCookingRecipeInput implements RecipeInput {
    protected final ItemStack stack;
    protected final boolean needOil;
    @Nullable
    private final CommonIngredient commonIngredient;

    public SequentialCookingRecipeInput(ItemStack input, boolean needOil) {
        this(input, needOil, null);
    }

    public SequentialCookingRecipeInput(ItemStack input, boolean needOil, @Nullable CommonIngredient commonIngredient) {
        this.stack = input;
        this.needOil = needOil;
        this.commonIngredient = commonIngredient;
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

    @Nullable
    public CommonIngredient getCommonIngredient() {
        return this.commonIngredient;
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
