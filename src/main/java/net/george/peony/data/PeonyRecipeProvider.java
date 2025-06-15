package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.george.peony.Peony;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.data.CraftingSteps;
import net.george.peony.data.json.MillingRecipeJsonBuilder;
import net.george.peony.data.json.SequentialCraftingRecipeJsonBuilder;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class PeonyRecipeProvider extends FabricRecipeProvider {
    public static final String PEONY_BLOCKS = "peony-blocks";

    public PeonyRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PeonyBlocks.MILLSTONE)
                .input('S', Blocks.SMOOTH_STONE)
                .input('W', Blocks.DARK_OAK_PLANKS)
                .pattern("WSS")
                .pattern(" SS")
                .group(PEONY_BLOCKS)
                .criterion(hasItem(Blocks.SMOOTH_STONE), conditionsFromItem(Blocks.SMOOTH_STONE))
                .offerTo(exporter);
        Registries.BLOCK.stream().forEach(block -> {
            if (block instanceof CuttingBoardBlock board) {
                createCuttingBoardRecipe(board, exporter);
            }
        });

        generateMillingRecipe(Blocks.HAY_BLOCK, Items.WHEAT, 9, 2, exporter);
        generateMillingRecipe(Items.WHEAT, PeonyBlocks.FLOUR, 2, 2, exporter);

        SequentialCraftingRecipeJsonBuilder.create(PeonyBlocks.DOUGH)
                .step(CraftingSteps.Procedure.KNEADING, PeonyBlocks.FLOUR)
                .step(CraftingSteps.Procedure.KNEADING, Items.POTION)
                .step(CraftingSteps.Procedure.KNEADING, PeonyItems.PLACEHOLDER)
                .offerTo(exporter, Peony.id("dough_from_flour"));
    }

    public void generateMillingRecipe(ItemConvertible input, ItemConvertible outputItem, int outputCount, int millingTimes, RecipeExporter exporter) {
        MillingRecipeJsonBuilder.create(input, outputItem, outputCount).millingTimes(millingTimes).criterion(input).offerTo(exporter, Peony.id(getItemPath(outputItem) + "_from_" + getItemPath(input)));
    }

    public static void createCuttingBoardRecipe(CuttingBoardBlock board, RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, board)
                .input('L', board.getLogMadeFrom())
                .pattern("LL")
                .group(PEONY_BLOCKS)
                .criterion(hasItem(board.getLogMadeFrom()), conditionsFromItem(board.getLogMadeFrom()))
                .offerTo(exporter);
    }
}
