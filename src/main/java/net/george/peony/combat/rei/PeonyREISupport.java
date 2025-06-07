package net.george.peony.combat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.recipe.MillingRecipe;
import net.george.peony.recipe.PeonyRecipes;

public class PeonyREISupport implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new MillstoneCategory());

        registry.addWorkstations(MillstoneCategory.MILLSTONE, EntryStacks.of(PeonyBlocks.MILLSTONE));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(MillingRecipe.class, PeonyRecipes.MILLING_TYPE, MillstoneDisplay::new);
    }
}
