package net.george.peony.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public record PizzaCraftingRecipeInput(List<ItemStack> ingredients) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.ingredients.get(slot);
    }

    @Override
    public int getSize() {
        return this.ingredients.size();
    }

    public List<ItemStack> getIngredients() {
        return this.ingredients;
    }
}
