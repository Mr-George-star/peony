package net.george.peony.data.json;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.george.peony.block.data.CookingSteps;
import net.george.peony.block.data.Output;
import net.george.peony.recipe.SequentialCookingRecipe;
import net.george.peony.util.FluidStack;
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

public class SequentialCookingRecipeJsonBuilder implements RecipeJsonBuilder {
    private final int temperature;
    private final boolean needOil;
    private final Output output;
    private final List<CookingSteps.Step> steps = new ArrayList<>();
    private RecipeCategory category = RecipeCategory.MISC;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    protected SequentialCookingRecipeJsonBuilder(int temperature, boolean needOil, Output output) {
        this.temperature = temperature;
        this.needOil = needOil;
        this.output = output;
    }

    public static SequentialCookingRecipeJsonBuilder create(int temperature, boolean needOil, Fluid fluid, int amount, ItemConvertible container, ItemConvertible result) {
        return create(temperature, needOil, FluidVariant.of(fluid), amount, container, result);
    }

    public static SequentialCookingRecipeJsonBuilder create(int temperature, boolean needOil, FluidVariant fluid, int amount, ItemConvertible container, ItemConvertible result) {
        return create(temperature, needOil, FluidStack.of(fluid, amount), container, result);
    }

    public static SequentialCookingRecipeJsonBuilder create(int temperature, boolean needOil, FluidStack fluid, ItemConvertible container, ItemConvertible result) {
        return create(temperature, needOil, Output.createFluid(fluid, container, result));
    }

    public static SequentialCookingRecipeJsonBuilder create(int temperature, boolean needOil, ItemConvertible outputItem, ItemConvertible container) {
        return create(temperature, needOil, outputItem, 1, container);
    }

    public static SequentialCookingRecipeJsonBuilder create(int temperature, boolean needOil, ItemConvertible outputItem, int outputCount, ItemConvertible container) {
        return create(temperature, needOil, new ItemStack(outputItem, outputCount), container);
    }

    public static SequentialCookingRecipeJsonBuilder create(int temperature, boolean needOil, ItemStack outputStack, ItemConvertible container) {
        return create(temperature, needOil, Output.create(outputStack, container));
    }

    public static SequentialCookingRecipeJsonBuilder create(int temperature, boolean needOil, ItemConvertible outputItem) {
        return create(temperature, needOil, outputItem, 1);
    }

    public static SequentialCookingRecipeJsonBuilder create(int temperature, boolean needOil, ItemConvertible outputItem, int outputCount) {
        return create(temperature, needOil, new ItemStack(outputItem, outputCount));
    }

    public static SequentialCookingRecipeJsonBuilder create(int temperature, boolean needOil, ItemStack outputStack) {
        return create(temperature, needOil, Output.noContainer(outputStack));
    }

    public static SequentialCookingRecipeJsonBuilder create(int temperature, boolean needOil, Output output) {
        return new SequentialCookingRecipeJsonBuilder(temperature, needOil, output);
    }

    public SequentialCookingRecipeJsonBuilder step(CookingSteps.Step step) {
        this.steps.add(step);
        return this;
    }

    public SequentialCookingRecipeJsonBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public SequentialCookingRecipeJsonBuilder criterion(ItemConvertible item) {
        return criterion(RecipeProvider.hasItem(item), RecipeProvider.conditionsFromItem(item));
    }

    public SequentialCookingRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
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
            this.criterion(this.steps.getFirst().getIngredient().getMatchingStacks()[0].getItem());
        }
        Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        Objects.requireNonNull(this.criteria).forEach(builder::criterion);
        SequentialCookingRecipe recipe = new SequentialCookingRecipe(this.temperature, this.needOil, this.steps, this.output);
        exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }
}
