package net.george.peony.data.json;

import net.george.peony.block.data.Output;
import net.george.peony.recipe.FlavouringPreparingRecipe;
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
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.*;

public class FlavouringPreparingRecipeJsonBuilder implements RecipeJsonBuilder {
    private final List<ItemStack> ingredients;
    private int stirringTimes = 1;
    private final Output output;
    private RecipeCategory category = RecipeCategory.MISC;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    protected FlavouringPreparingRecipeJsonBuilder(List<ItemStack> ingredients, Output output) {
        this.ingredients = ingredients;
        this.output = output;
    }

    public static FlavouringPreparingRecipeJsonBuilder create(ItemConvertible output, ItemConvertible... ingredients) {
        return create(new ItemStack(output), Arrays.stream(ingredients).map(ItemStack::new).toArray(ItemStack[]::new));
    }

    public static FlavouringPreparingRecipeJsonBuilder create(ItemStack output, ItemStack... ingredients) {
        return create(Output.noContainer(output), ingredients);
    }

    public static FlavouringPreparingRecipeJsonBuilder create(ItemStack output, ItemConvertible requiredContainer, ItemConvertible... ingredients) {
        return create(Output.create(output, requiredContainer), Arrays.stream(ingredients).map(ItemStack::new).toArray(ItemStack[]::new));
    }

    public static FlavouringPreparingRecipeJsonBuilder create(ItemStack output, ItemConvertible requiredContainer, ItemStack... ingredients) {
        return create(Output.create(output, requiredContainer), ingredients);
    }

    public static FlavouringPreparingRecipeJsonBuilder create(Output output, ItemStack... ingredients) {
        return create(Arrays.stream(ingredients).toList(), output);
    }

    public static FlavouringPreparingRecipeJsonBuilder create(List<ItemStack> ingredients, Output output) {
        return new FlavouringPreparingRecipeJsonBuilder(ingredients, output);
    }

    public FlavouringPreparingRecipeJsonBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    public FlavouringPreparingRecipeJsonBuilder stirringTimes(int times) {
        this.stirringTimes = times;
        return this;
    }

    public FlavouringPreparingRecipeJsonBuilder criterion(ItemConvertible item) {
        return criterion(RecipeProvider.hasItem(item), RecipeProvider.conditionsFromItem(item));
    }

    public FlavouringPreparingRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
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
        FlavouringPreparingRecipe recipe = new FlavouringPreparingRecipe(this.ingredients, this.stirringTimes, this.output);
        exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }
}
