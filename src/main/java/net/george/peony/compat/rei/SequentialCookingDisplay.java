package net.george.peony.compat.rei;

import com.google.common.collect.Lists;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.george.peony.block.data.CookingSteps;
import net.george.peony.block.data.StirFryingData;
import net.george.peony.item.PeonyItems;
import net.george.peony.recipe.SequentialCookingRecipe;
import net.george.peony.util.PeonyTags;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class SequentialCookingDisplay extends BasicDisplay {
    private final int temperature;
    private final List<StepInfo> steps;
    private final EntryStack<ItemStack> output;
    private final EntryIngredient container;

    public SequentialCookingDisplay(RecipeEntry<SequentialCookingRecipe> recipeEntry) {
        super(getAllInputIngredients(recipeEntry.value()),
                Collections.singletonList(EntryIngredients.of(recipeEntry.value().getOutput().getOutputStack())));

        SequentialCookingRecipe recipe = recipeEntry.value();
        CookingSteps steps = recipe.getSteps();
        this.temperature = recipe.getTemperature();
        this.steps = Lists.newArrayList();
        this.output = EntryStacks.of(recipe.getOutput().getOutputStack());
        ItemConvertible container = recipe.getOutput().getContainer();
        this.container = container.asItem() == PeonyItems.PLACEHOLDER ? EntryIngredient.empty() : EntryIngredients.of(container);

        for (int index = 0; index < steps.getSteps().size(); index++) {
            CookingSteps.Step step = steps.getSteps().get(index);

            /* ingredients */
            List<EntryIngredient> stepInputs = Lists.newArrayList();
            stepInputs.add(EntryIngredients.ofIngredient(step.getIngredient()));
            if (index == 0 && recipe.isNeedOil()) {
                stepInputs.add(EntryIngredients.ofItemTag(PeonyTags.Items.COOKING_OIL));
            }

            /* cooking data */
            CookingType type;
            StirFryingData fryingData;
            if (step.getFryingData().times() > 0) {
                type = CookingType.STIR;
                fryingData = step.getFryingData();
            } else {
                type = CookingType.HEATING;
                fryingData = StirFryingData.DEFAULT;
            }

            /* required tool */
            EntryIngredient requiredTool;
            if (step.getRequiredTool().getMatchingStacks()[0].isOf(PeonyItems.PLACEHOLDER)) {
                requiredTool = EntryIngredient.empty();
            } else {
                requiredTool = EntryIngredients.ofIngredient(step.getRequiredTool());
            }

            /* final */
            StepInfo info = new StepInfo(index + 1, stepInputs, step.getRequiredTime(), type, fryingData,
                    requiredTool);
            this.steps.add(info);
        }
    }

    private static List<EntryIngredient> getAllInputIngredients(SequentialCookingRecipe recipe) {
        List<EntryIngredient> inputs = Lists.newArrayList();
        CookingSteps steps = recipe.getSteps();
        for (int i = 0; i < steps.getSteps().size(); i++) {
            CookingSteps.Step step = steps.getSteps().get(i);
            inputs.add(EntryIngredients.ofIngredient(step.getIngredient()));
            if (i == 0 && recipe.isNeedOil()) {
                inputs.add(EntryIngredients.ofItemTag(PeonyTags.Items.COOKING_OIL));
            }
        }
        return inputs;
    }

    @Override
    public CategoryIdentifier<SequentialCookingDisplay> getCategoryIdentifier() {
        return PeonyREIPlugin.SEQUENTIAL_COOKING;
    }

    public int getTemperature() {
        return this.temperature;
    }

    public List<StepInfo> getSteps() {
        return this.steps;
    }

    public EntryStack<ItemStack> getOutput() {
        return this.output;
    }

    public EntryIngredient getContainer() {
        return this.container;
    }

    public record StepInfo(int index,
                           List<EntryIngredient> ingredients,
                           int requiredTime,
                           CookingType cookingType,
                           StirFryingData fryingData,
                           EntryIngredient requiredTool) {}

    public enum CookingType {
        HEATING,
        STIR
    }
}
