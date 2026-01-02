package net.george.peony.recipe;

import net.george.peony.util.FluidStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class MixedIngredientsRecipeInput extends ListedRecipeInput {
    private final FluidStack fluid;

    public MixedIngredientsRecipeInput(DefaultedList<ItemStack> inputs, FluidStack fluid) {
        super(inputs);
        this.fluid = fluid;
    }

    public FluidStack getFluid() {
        return this.fluid;
    }
}
