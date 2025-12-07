package net.george.peony.data.json;

import com.google.common.collect.Lists;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.recipe.PizzaCraftingRecipe;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PizzaCraftingRecipeJsonBuilder implements RecipeJsonBuilder {
    private List<Ingredient> ingredients = Lists.newArrayList();
    private final ItemStack output;
    private RecipeCategory category = RecipeCategory.MISC;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    protected PizzaCraftingRecipeJsonBuilder(ItemStack output) {
        this.output = output;
    }

    public static PizzaCraftingRecipeJsonBuilder create(ItemConvertible outputItem) {
        return create(new ItemStack(outputItem, 1));
    }

    public static PizzaCraftingRecipeJsonBuilder create(ItemConvertible outputItem, int outputCount) {
        return create(new ItemStack(outputItem, outputCount));
    }

    public static PizzaCraftingRecipeJsonBuilder create(ItemStack output) {
        return new PizzaCraftingRecipeJsonBuilder(output);
    }

    public PizzaCraftingRecipeJsonBuilder add(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public PizzaCraftingRecipeJsonBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    public PizzaCraftingRecipeJsonBuilder criterion(ItemConvertible item) {
        return criterion(RecipeProvider.hasItem(item), RecipeProvider.conditionsFromItem(item));
    }

    public PizzaCraftingRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
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
            this.criterion(PeonyBlocks.FLATBREAD);
        }
        Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        Objects.requireNonNull(this.criteria).forEach(builder::criterion);
        PizzaCraftingRecipe recipe = new PizzaCraftingRecipe(this.ingredients, this.output);
        exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }
}
