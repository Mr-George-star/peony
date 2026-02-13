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

public interface RecipeStorage<I extends RecipeInput, R extends Recipe<I>> {
    static<I extends RecipeInput, R extends Recipe<I>> RecipeStorage<I, R> create(Class<R> recipeClass) {
        return new Impl<>(recipeClass);
    }

    Identifier getRecipeId();

    @Nullable
    R getCurrentRecipe();

    void setCurrentRecipe(@NotNull RecipeEntry<R> recipe);

    boolean isEmpty();

    void clear();

    void writeNbt(World world, NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);

    void readNbt(World world, NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);

    class Impl<I extends RecipeInput, R extends Recipe<I>> implements RecipeStorage<I, R> {
        private static final Identifier DUMMY_ID = Identifier.of("dummy", "dummy");

        @Nullable
        protected R currentRecipe;
        protected Identifier currentRecipeId;
        private final Class<R> recipeClass;

        Impl(Class<R> recipeClass) {
            this.currentRecipe = null;
            this.currentRecipeId = DUMMY_ID;
            this.recipeClass = recipeClass;
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
        public void setCurrentRecipe(@NotNull RecipeEntry<R> currentRecipe) {
            this.currentRecipe = currentRecipe.value();
            this.currentRecipeId = currentRecipe.id();
        }

        @Override
        public boolean isEmpty() {
            return this.currentRecipe == null;
        }

        @Override
        public void clear() {
            this.currentRecipe = null;
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
                if (recipeId != null) {
                    this.currentRecipeId = recipeId;
                    if (world != null && !recipeId.equals(DUMMY_ID)) {
                        this.currentRecipe = (R) world.getRecipeManager()
                                .get(recipeId)
                                .map(RecipeEntry::value)
                                .filter(this.recipeClass::isInstance)
                                .orElse(null);
                    }
                } else {
                    this.currentRecipeId = DUMMY_ID;
                    this.currentRecipe = null;
                }
            }
        }
    }
}
