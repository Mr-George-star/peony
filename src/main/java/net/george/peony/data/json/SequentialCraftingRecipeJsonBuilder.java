package net.george.peony.data.json;

import net.george.peony.api.action.Action;
import net.george.peony.block.data.CraftingSteps;
import net.george.peony.item.PeonyItems;
import net.george.peony.recipe.SequentialCraftingRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.*;

@SuppressWarnings("unused")
public class SequentialCraftingRecipeJsonBuilder implements RecipeJsonBuilder {
    private final ItemStack output;
    private final List<CraftingSteps.Step> steps = new ArrayList<>();
    private RecipeCategory category = RecipeCategory.MISC;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    protected SequentialCraftingRecipeJsonBuilder(ItemStack output) {
        this.output = output;
    }

    public static SequentialCraftingRecipeJsonBuilder create(ItemConvertible item) {
        return create(item, 1);
    }

    public static SequentialCraftingRecipeJsonBuilder create(ItemConvertible item, int count) {
        return create(new ItemStack(item, count));
    }

    public static SequentialCraftingRecipeJsonBuilder create(ItemStack output) {
        return new SequentialCraftingRecipeJsonBuilder(output);
    }

    public SequentialCraftingRecipeJsonBuilder steps(CraftingSteps.Step... steps) {
        return steps(Arrays.stream(steps).toList());
    }

    public SequentialCraftingRecipeJsonBuilder steps(Collection<CraftingSteps.Step> steps) {
        this.steps.addAll(steps);
        return this;
    }

    public SequentialCraftingRecipeJsonBuilder step(Action action) {
        return step(action, PeonyItems.PLACEHOLDER);
    }

    public SequentialCraftingRecipeJsonBuilder step(Action action, ItemConvertible item) {
        return step(action, item, 1);
    }

    public SequentialCraftingRecipeJsonBuilder step(Action action, ItemConvertible item, int count) {
        return step(action, new ItemStack(item, count));
    }

    public SequentialCraftingRecipeJsonBuilder step(Action action, ItemStack stack) {
        return step(action, Ingredient.ofStacks(stack));
    }

    public SequentialCraftingRecipeJsonBuilder step(Action action, Ingredient ingredient) {
        return step(new CraftingSteps.Step(action, ingredient));
    }

    public SequentialCraftingRecipeJsonBuilder step(CraftingSteps.Step step) {
        this.steps.add(step);
        return this;
    }

    public SequentialCraftingRecipeJsonBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public SequentialCraftingRecipeJsonBuilder criterion(ItemConvertible item) {
        return criterion(RecipeProvider.hasItem(item), RecipeProvider.conditionsFromItem(item));
    }

    public SequentialCraftingRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
        this.criteria.put(string, advancementCriterion);
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.output.getItem();
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            this.criterion(this.steps.getFirst().getIngredient().getMatchingStacks()[0].getItem());
        }
        Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        Objects.requireNonNull(this.criteria).forEach(builder::criterion);
        SequentialCraftingRecipe recipe = new SequentialCraftingRecipe(this.steps, this.output);
        exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }
}
