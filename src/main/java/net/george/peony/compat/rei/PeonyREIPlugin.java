package net.george.peony.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.george.peony.Peony;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.item.ParingKnifeItem;
import net.george.peony.item.ShredderItem;
import net.george.peony.recipe.*;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

public class PeonyREIPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<MillstoneDisplay> MILLSTONE = CategoryIdentifier.of(Peony.id("millstone"));
    public static final CategoryIdentifier<SequentialCraftingDisplay> SEQUENTIAL_CRAFTING =
            CategoryIdentifier.of(Peony.id("sequential_crafting"));
    public static final CategoryIdentifier<SequentialCookingDisplay> SEQUENTIAL_COOKING =
            CategoryIdentifier.of(Peony.id("sequential_cooking"));
    public static final CategoryIdentifier<ShreddingRecipeDisplay> SHREDDING =
            CategoryIdentifier.of(Peony.id("shredding"));
    public static final CategoryIdentifier<ParingRecipeDisplay> PARING =
            CategoryIdentifier.of(Peony.id("paring"));
    public static final CategoryIdentifier<FlavouringPreparingDisplay> FLAVOURING_PREPARING =
            CategoryIdentifier.of(Peony.id("flavouring_preparing"));
    public static final CategoryIdentifier<BrewingRecipeDisplay> BREWING =
            CategoryIdentifier.of(Peony.id("brewing"));
    public static final CategoryIdentifier<PizzaCraftingRecipeDisplay> PIZZA_CRAFTING =
            CategoryIdentifier.of(Peony.id("pizza_crafting"));

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new MillstoneCategory());
        registry.add(new SequentialCraftingCategory());
        registry.add(new SequentialCookingCategory());
        registry.add(new ShreddingRecipeCategory());
        registry.add(new ParingRecipeCategory());
        registry.add(new FlavouringPreparingCategory());
        registry.add(new BrewingRecipeCategory());
        registry.add(new PizzaCraftingRecipeCategory());

        registry.addWorkstations(MILLSTONE, EntryStacks.of(PeonyBlocks.MILLSTONE));
        registry.addWorkstations(SEQUENTIAL_CRAFTING, Registries.BLOCK.stream().filter(block -> block instanceof CuttingBoardBlock)
                .map(EntryStacks::of).toArray(EntryStack[]::new));
        registry.addWorkstations(SEQUENTIAL_COOKING, EntryStacks.of(PeonyBlocks.SKILLET));
        registry.addWorkstations(SHREDDING, Registries.ITEM.stream().filter(item -> item instanceof ShredderItem)
                .map(EntryStacks::of).toArray(EntryStack[]::new));
        registry.addWorkstations(PARING, Registries.ITEM.stream().filter(item -> item instanceof ParingKnifeItem)
                .map(EntryStacks::of).toArray(EntryStack[]::new));
        registry.addWorkstations(FLAVOURING_PREPARING, EntryStacks.of(Items.BOWL));
        registry.addWorkstations(BREWING, EntryStacks.of(PeonyBlocks.BREWING_BARREL));
        registry.addWorkstations(PIZZA_CRAFTING, EntryStacks.of(PeonyBlocks.FLATBREAD));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(MillingRecipe.class, PeonyRecipes.MILLING_TYPE, MillstoneDisplay::new);
        registry.registerRecipeFiller(SequentialCraftingRecipe.class, PeonyRecipes.SEQUENTIAL_CRAFTING_TYPE, SequentialCraftingDisplay::new);
        registry.registerRecipeFiller(SequentialCookingRecipe.class, PeonyRecipes.SEQUENTIAL_COOKING_TYPE, SequentialCookingDisplay::new);
        registry.registerRecipeFiller(ShreddingRecipe.class, PeonyRecipes.SHREDDING_TYPE, ShreddingRecipeDisplay::new);
        registry.registerRecipeFiller(ParingRecipe.class, PeonyRecipes.PARING_TYPE, ParingRecipeDisplay::new);
        registry.registerRecipeFiller(FlavouringPreparingRecipe.class, PeonyRecipes.FLAVOURING_PREPARING_TYPE, FlavouringPreparingDisplay::new);
        registry.registerRecipeFiller(BrewingRecipe.class, PeonyRecipes.BREWING_TYPE, BrewingRecipeDisplay::new);
        registry.registerRecipeFiller(PizzaCraftingRecipe.class, PeonyRecipes.PIZZA_CRAFTING_TYPE, PizzaCraftingRecipeDisplay::new);
    }
}
