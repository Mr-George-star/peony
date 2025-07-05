package net.george.peony.combat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.recipe.MillingRecipe;
import net.george.peony.recipe.PeonyRecipes;
import net.george.peony.recipe.SequentialCraftingRecipe;
import net.minecraft.registry.Registries;

public class PeonyREISupport implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new MillstoneCategory());
        registry.add(new SequentialCraftingCategory());

        registry.addWorkstations(MillstoneCategory.MILLSTONE, EntryStacks.of(PeonyBlocks.MILLSTONE));
        Registries.BLOCK.stream().filter(block -> block instanceof CuttingBoardBlock).forEach(board ->
                registry.addWorkstations(SequentialCraftingCategory.SEQUENTIAL_CRAFTING, EntryStacks.of(board)));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(MillingRecipe.class, PeonyRecipes.MILLING_TYPE, MillstoneDisplay::new);
        registry.registerRecipeFiller(SequentialCraftingRecipe.class, PeonyRecipes.SEQUENTIAL_CRAFTING_TYPE, SequentialCraftingDisplay::new);
    }
}
