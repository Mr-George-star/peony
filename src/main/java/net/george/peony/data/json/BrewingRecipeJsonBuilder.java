package net.george.peony.data.json;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.george.peony.api.fluid.FluidStack;
import net.george.peony.block.data.Output;
import net.george.peony.recipe.BrewingRecipe;
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
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.*;

public class BrewingRecipeJsonBuilder implements RecipeJsonBuilder {
    private final List<ItemStack> ingredients;
    private FluidStack basicFluid = BrewingRecipe.DEFAULT_FLUID_STACK;
    private int brewingTime = 100;
    private final Output output;
    private RecipeCategory category = RecipeCategory.MISC;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    protected BrewingRecipeJsonBuilder(List<ItemStack> ingredients, Output output) {
        this.ingredients = ingredients;
        this.output = output;
    }

    public static BrewingRecipeJsonBuilder create(ItemConvertible output, ItemConvertible requiredContainer, ItemStack... ingredients) {
        return create(output, 1, requiredContainer, ingredients);
    }

    public static BrewingRecipeJsonBuilder create(ItemConvertible output, int outputCount, ItemConvertible requiredContainer, ItemStack... ingredients) {
        return create(Arrays.stream(ingredients).toList(), Output.create(new ItemStack(output, outputCount), requiredContainer));
    }

    public static BrewingRecipeJsonBuilder create(ItemStack output, ItemConvertible requiredContainer, ItemStack... ingredients) {
        return create(Arrays.stream(ingredients).toList(), Output.create(output, requiredContainer));
    }

    protected static BrewingRecipeJsonBuilder create(Output output, ItemStack... ingredients) {
        return create(Arrays.stream(ingredients).toList(), output);
    }

    protected static BrewingRecipeJsonBuilder create(List<ItemStack> ingredients, Output output) {
        return new BrewingRecipeJsonBuilder(ingredients, output);
    }

    public BrewingRecipeJsonBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    public BrewingRecipeJsonBuilder basicFluid(Fluid fluid, int amount) {
        return this.basicFluid(FluidStack.of(fluid, amount));
    }

    public BrewingRecipeJsonBuilder basicFluid(FluidVariant fluidVariant, int amount) {
        return this.basicFluid(FluidStack.of(fluidVariant, amount));
    }

    public BrewingRecipeJsonBuilder basicFluid(FluidStack basicFluid) {
        this.basicFluid = basicFluid;
        return this;
    }

    public BrewingRecipeJsonBuilder brewingTime(int brewingTime) {
        this.brewingTime = brewingTime;
        return this;
    }

    public BrewingRecipeJsonBuilder criterion(ItemConvertible item) {
        return criterion(RecipeProvider.hasItem(item), RecipeProvider.conditionsFromItem(item));
    }

    public BrewingRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
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
            this.criterion(this.ingredients.getFirst().getItem());
        }
        Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        Objects.requireNonNull(this.criteria).forEach(builder::criterion);
        BrewingRecipe recipe = new BrewingRecipe(this.ingredients, this.basicFluid, this.brewingTime, this.output);
        exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }
}
