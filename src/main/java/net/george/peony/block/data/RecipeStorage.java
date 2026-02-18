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

import java.util.Optional;

@SuppressWarnings("unused")
public interface RecipeStorage<I extends RecipeInput, R extends Recipe<I>> {
    static<I extends RecipeInput, R extends Recipe<I>> RecipeStorage<I, R> create(Class<R> recipeClass) {
        return new Impl<>(recipeClass);
    }

    Identifier getRecipeId();

    @Nullable
    R getRecipe();

    Optional<R> getOptionalRecipe();

    @Nullable
    RecipeEntry<R> getRecipeEntry();

    Optional<RecipeEntry<R>> getOptionalRecipeEntry();

    void setRecipeEntry(@NotNull RecipeEntry<R> recipe);

    boolean isEmpty();

    void clear();

    void writeNbt(World world, NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);

    void readNbt(World world, NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);

    class Impl<I extends RecipeInput, R extends Recipe<I>> implements RecipeStorage<I, R> {
        private static final Identifier DUMMY_ID = Identifier.of("dummy", "dummy");

        @Nullable
        protected RecipeEntry<R> currentRecipe;
        private final Class<R> recipeClass;

        Impl(Class<R> recipeClass) {
            this.currentRecipe = null;
            this.recipeClass = recipeClass;
        }

        @Override
        public Identifier getRecipeId() {
            return Optional.ofNullable(this.currentRecipe).map(RecipeEntry::id).orElse(DUMMY_ID);
        }

        @Nullable
        @Override
        public R getRecipe() {
            return Optional.ofNullable(this.currentRecipe).map(RecipeEntry::value).orElse(null);
        }

        @Override
        public Optional<R> getOptionalRecipe() {
            return Optional.ofNullable(this.currentRecipe).map(RecipeEntry::value);
        }

        @Nullable
        @Override
        public RecipeEntry<R> getRecipeEntry() {
            return this.currentRecipe;
        }

        @Override
        public Optional<RecipeEntry<R>> getOptionalRecipeEntry() {
            return Optional.ofNullable(this.currentRecipe);
        }

        @Override
        public void setRecipeEntry(@NotNull RecipeEntry<R> currentRecipe) {
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

        @Override
        public void writeNbt(World world, NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            if (this.getRecipeId() != null && world != null) {
                nbt.putString("CurrentRecipeId", this.getRecipeId().toString());
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void readNbt(World world, NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            if (nbt.contains("CurrentRecipeId")) {
                Identifier recipeId = Identifier.tryParse(nbt.getString("CurrentRecipeId"));
                if (recipeId != null) {
                    R recipe = null;
                    if (world != null && !recipeId.equals(DUMMY_ID)) {
                        recipe = (R) world.getRecipeManager()
                                .get(recipeId)
                                .map(RecipeEntry::value)
                                .filter(this.recipeClass::isInstance)
                                .orElse(null);
                    }
                    this.currentRecipe = new RecipeEntry<>(recipeId, recipe);
                } else {
                    this.currentRecipe = new RecipeEntry<>(DUMMY_ID, null);
                }
            }
        }
    }
}
