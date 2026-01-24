package net.george.peony.data.json;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.george.peony.api.fluid.FluidStack;
import net.george.peony.block.data.Output;
import net.george.peony.recipe.FermentingRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FermentingRecipeJsonBuilder implements RecipeJsonBuilder {
    private final List<Ingredient> ingredients;
    @Nullable
    private FluidStack fluidInput;
    private int fermentingTime = 100;
    private final Output output;
    private RecipeCategory category = RecipeCategory.MISC;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    protected FermentingRecipeJsonBuilder(List<Ingredient> ingredients, Output output) {
        this.ingredients = ingredients;
        this.output = output;
    }

    public static FermentingRecipeJsonBuilder create(Output output, Ingredient... ingredients) {
        return create(Arrays.stream(ingredients).toList(), output);
    }

    public static FermentingRecipeJsonBuilder create(List<Ingredient> ingredients, Output output) {
        return new FermentingRecipeJsonBuilder(ingredients, output);
    }

    public FermentingRecipeJsonBuilder basicFluid(@NotNull Fluid fluid, int amount) {
        return this.basicFluid(FluidStack.of(fluid, amount));
    }

    public FermentingRecipeJsonBuilder basicFluid(@NotNull FluidVariant fluidVariant, int amount) {
        return this.basicFluid(FluidStack.of(fluidVariant, amount));
    }

    public FermentingRecipeJsonBuilder basicFluid(@NotNull FluidStack fluidInput) {
        this.fluidInput = fluidInput;
        return this;
    }

    public FermentingRecipeJsonBuilder fermentingTime(int fermentingTime) {
        this.fermentingTime = fermentingTime;
        return this;
    }

    public FermentingRecipeJsonBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    public FermentingRecipeJsonBuilder criterion(ItemConvertible item) {
        return criterion(RecipeProvider.hasItem(item), RecipeProvider.conditionsFromItem(item));
    }

    public FermentingRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
        this.criteria.put(string, advancementCriterion);
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.output.getOutputStack().getItem();
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            this.criterion(this.ingredients.getFirst().getMatchingStacks()[0].getItem());
        }
        Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        Objects.requireNonNull(this.criteria).forEach(builder::criterion);
        FermentingRecipe recipe = new FermentingRecipe(this.ingredients, Optional.ofNullable(this.fluidInput), this.fermentingTime, this.output);
        exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }
}
