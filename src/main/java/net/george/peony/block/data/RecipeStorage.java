package net.george.peony.block.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface RecipeStorage<I extends RecipeInput, R extends Recipe<I>> {
    static<I extends RecipeInput, R extends Recipe<I>> RecipeStorage<I, R> create(Function<Recipe<?>, Boolean> isInstanceOfRecipe) {
        return new Impl<>(isInstanceOfRecipe);
    }

    Identifier getRecipeId();

    @Nullable
    R getCurrentRecipe();

    void setCurrentRecipe(@NotNull R recipe);

    boolean isEmpty();

    void clear();

    void writeNbt(World world, NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);

    void readNbt(World world, NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);

    class Impl<I extends RecipeInput, R extends Recipe<I>> implements RecipeStorage<I, R> {
        @Nullable
        protected R currentRecipe;
        protected Identifier currentRecipeId;
        protected final Function<Recipe<?>, Boolean> isInstanceOfRecipe;

        Impl(Function<Recipe<?>, Boolean> isInstanceOfRecipe) {
            this.currentRecipe = null;
            this.currentRecipeId = Identifier.of("dummy", "dummy");
            this.isInstanceOfRecipe = isInstanceOfRecipe;
        }

        @Override
        public Identifier getRecipeId() {
            return this.currentRecipeId;
        }

        @Nullable
        @Override
        public R getCurrentRecipe() {
            return this.currentRecipe;
        }

        @Override
        public void setCurrentRecipe(@NotNull R currentRecipe) {
            this.currentRecipe = currentRecipe;
        }

        @Override
        public boolean isEmpty() {
            return this.currentRecipe == null;
        }

        @Override
        public void clear() {
            this.currentRecipe = null;
        }

        public boolean isInstanceOfRecipe(Recipe<?> recipe) {
            return this.isInstanceOfRecipe.apply(recipe);
        }

        @Override
        public void writeNbt(World world, NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            if (this.currentRecipe != null && world != null) {
                nbt.putString("CurrentRecipeId", this.currentRecipeId.toString());
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void readNbt(World world, NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            if (nbt.contains("CurrentRecipeId")) {
                Identifier recipeId = Identifier.tryParse(nbt.getString("CurrentRecipeId"));
                this.currentRecipeId = recipeId;
                if (world != null) {
                    this.currentRecipe = (R) world.getRecipeManager()
                            .get(recipeId)
                            .map(RecipeEntry::value)
                            .filter(this::isInstanceOfRecipe).orElse(null);
                }
            }
        }
    }
}
