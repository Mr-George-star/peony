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
    public static final RecipeSerializer<ParingRecipe> PARING = register("paring", new ParingRecipe.Serializer());
    public static final RecipeType<ParingRecipe> PARING_TYPE = register("paring");
    public static final RecipeSerializer<BrewingRecipe> BREWING = register("brewing", new BrewingRecipe.Serializer());
    public static final RecipeType<BrewingRecipe> BREWING_TYPE = register("brewing");
    public static final RecipeSerializer<ShreddingRecipe> SHREDDING = register("shredding", new ShreddingRecipe.Serializer());
    public static final RecipeType<ShreddingRecipe> SHREDDING_TYPE = register("shredding");
    public static final RecipeSerializer<PizzaCraftingRecipe> PIZZA_CRAFTING = register("pizza_crafting", new PizzaCraftingRecipe.Serializer());
    public static final RecipeType<PizzaCraftingRecipe> PIZZA_CRAFTING_TYPE = register("pizza_crafting");
    public static final RecipeSerializer<FlavouringPreparingRecipe> FLAVOURING_PREPARING = register("flavouring_preparing", new FlavouringPreparingRecipe.Serializer());
    public static final RecipeType<FlavouringPreparingRecipe> FLAVOURING_PREPARING_TYPE = register("flavouring_preparing");
    public static final RecipeSerializer<FermentingRecipe> FERMENTING = register("fermenting", new FermentingRecipe.Serializer());
    public static final RecipeType<FermentingRecipe> FERMENTING_TYPE = register("fermenting");

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
