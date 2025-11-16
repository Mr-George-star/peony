package net.george.peony.data.json;

import net.george.peony.recipe.ShreddingRecipe;
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ShreddingRecipeJsonBuilder implements RecipeJsonBuilder {
    private final Ingredient input;
    private final ItemStack output;
    private int durationDecrement = 1;
    private RecipeCategory category = RecipeCategory.MISC;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    protected ShreddingRecipeJsonBuilder(Ingredient input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    public static ShreddingRecipeJsonBuilder create(ItemConvertible ingredient, ItemConvertible output) {
        return create(ingredient, 1, output);
    }

    public static ShreddingRecipeJsonBuilder create(ItemConvertible ingredient, int ingredientCount, ItemConvertible output) {
        return create(new ItemStack(ingredient, ingredientCount), output, 1);
    }

    public static ShreddingRecipeJsonBuilder create(ItemStack ingredient, ItemConvertible output) {
        return create(ingredient, output, 1);
    }

    public static ShreddingRecipeJsonBuilder create(ItemConvertible ingredient, ItemConvertible output, int count) {
        return create(ingredient, 1, output, count);
    }

    public static ShreddingRecipeJsonBuilder create(ItemConvertible ingredient, int ingredientCount, ItemConvertible output, int count) {
        return create(new ItemStack(ingredient, ingredientCount), output, count);
    }

    public static ShreddingRecipeJsonBuilder create(ItemStack ingredient, ItemConvertible output, int count) {
        return create(Ingredient.ofStacks(ingredient), output, count);
    }

    public static ShreddingRecipeJsonBuilder create(Ingredient ingredient, ItemConvertible output) {
        return create(ingredient, output, 1);
    }

    public static ShreddingRecipeJsonBuilder create(Ingredient ingredient, ItemConvertible output, int count) {
        return new ShreddingRecipeJsonBuilder(ingredient, new ItemStack(output, count));
    }

    public static ShreddingRecipeJsonBuilder create(Ingredient ingredient, ItemStack output) {
        return new ShreddingRecipeJsonBuilder(ingredient, output);
    }

    public ShreddingRecipeJsonBuilder durationDecrement(int decrement) {
        this.durationDecrement = decrement;
        return this;
    }

    public ShreddingRecipeJsonBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    public ShreddingRecipeJsonBuilder criterion(ItemConvertible item) {
        return criterion(RecipeProvider.hasItem(item), RecipeProvider.conditionsFromItem(item));
    }

    public ShreddingRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
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
            this.criterion(this.input.getMatchingStacks()[0].getItem());
        }
        Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        Objects.requireNonNull(this.criteria).forEach(builder::criterion);
        ShreddingRecipe recipe = new ShreddingRecipe(this.input, this.durationDecrement, this.output);
        exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }
}
