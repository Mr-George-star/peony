package net.george.peony.recipe;

import net.george.peony.Peony;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class PeonyRecipes {
    public static final RecipeSerializer<MillingRecipe> MILLING = register("milling", new MillingRecipe.Serializer());
    public static final RecipeType<MillingRecipe> MILLING_TYPE = register("milling");
    public static final RecipeSerializer<SequentialCraftingRecipe> SEQUENTIAL_CRAFTING = register(
            "sequential_crafting", new SequentialCraftingRecipe.Serializer());
    public static final RecipeType<SequentialCraftingRecipe> SEQUENTIAL_CRAFTING_TYPE = register("sequential_crafting");
    public static final RecipeSerializer<SequentialCookingRecipe> SEQUENTIAL_COOKING = register(
            "sequential_cooking", new SequentialCookingRecipe.Serializer());
    public static final RecipeType<SequentialCookingRecipe> SEQUENTIAL_COOKING_TYPE = register("sequential_cooking");

    public static <T extends Recipe<?>> RecipeSerializer<T> register(String name, RecipeSerializer<T> serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, Peony.id(name), serializer);
    }

    public static <T extends Recipe<?>> RecipeType<T> register(String name) {
        return Registry.register(Registries.RECIPE_TYPE, Peony.id(name), new RecipeType<T>() {
            @Override
            public String toString() {
                return name;
            }
        });
    }

    public static void register() {
        Peony.debug("Recipes");
    }
}
