package net.george.peony.data.json;

import net.george.peony.recipe.MillingRecipe;
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

@SuppressWarnings("unused")
public class MillingRecipeJsonBuilder implements RecipeJsonBuilder {
    private final ItemConvertible input;
    private final ItemStack output;
    private int millingTimes = 1;
    private RecipeCategory category = RecipeCategory.MISC;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    protected MillingRecipeJsonBuilder(ItemConvertible input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    public static MillingRecipeJsonBuilder create(ItemConvertible input, ItemConvertible outputItem) {
        return create(input, outputItem, 1);
    }

    public static MillingRecipeJsonBuilder create(ItemConvertible input, ItemConvertible outputItem, int outputCount) {
        return create(input, new ItemStack(outputItem, outputCount));
    }

    public static MillingRecipeJsonBuilder create(ItemConvertible input, ItemStack output) {
        return new MillingRecipeJsonBuilder(input, output);
    }

    public MillingRecipeJsonBuilder millingTimes(int times) {
        this.millingTimes = times;
        return this;
    }

    public MillingRecipeJsonBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    public MillingRecipeJsonBuilder criterion(ItemConvertible item) {
        return criterion(RecipeProvider.hasItem(item), RecipeProvider.conditionsFromItem(item));
    }

    public MillingRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
        this.criteria.put(string, advancementCriterion);
        return this;
    }

    public Item getOutputItem() {
        return this.output.getItem();
    }

    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
        Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        Objects.requireNonNull(this.criteria).forEach(builder::criterion);
        MillingRecipe recipe = new MillingRecipe(Ingredient.ofItems(this.input), this.millingTimes, this.output);
        exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }
}
