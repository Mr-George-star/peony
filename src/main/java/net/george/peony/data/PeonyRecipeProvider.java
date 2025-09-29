package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.george.peony.Peony;
import net.george.peony.block.*;
import net.george.peony.block.data.CookingSteps;
import net.george.peony.block.data.CraftingSteps;
import net.george.peony.data.json.MillingRecipeJsonBuilder;
import net.george.peony.data.json.SequentialCookingRecipeJsonBuilder;
import net.george.peony.data.json.SequentialCraftingRecipeJsonBuilder;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.PeonyTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class PeonyRecipeProvider extends FabricRecipeProvider {
    public static final String PEONY_ITEMS = "peony-items";
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
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PeonyBlocks.SKILLET)
                .input('S', PeonyTags.Items.LOG_STICKS)
                .input('I', Items.IRON_INGOT)
                .pattern("S  ")
                .pattern(" II")
                .pattern(" II")
                .group(PEONY_BLOCKS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter, Peony.id("skillet_right"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PeonyBlocks.SKILLET)
                .input('S', PeonyTags.Items.LOG_STICKS)
                .input('I', Items.IRON_INGOT)
                .pattern("  S")
                .pattern("II ")
                .pattern("II ")
                .group(PEONY_BLOCKS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter, Peony.id("skillet_left"));

        /* ITEMS */

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PeonyItems.KITCHEN_KNIFE)
                .input('I', Items.IRON_INGOT)
                .input('S', Items.STICK)
                .pattern("II")
                .pattern("II")
                .pattern("S ")
                .group(PEONY_ITEMS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter, Peony.id("kitchen_knife_right"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PeonyItems.KITCHEN_KNIFE)
                .input('I', Items.IRON_INGOT)
                .input('S', Items.STICK)
                .pattern("II")
                .pattern("II")
                .pattern(" S")
                .group(PEONY_ITEMS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter, Peony.id("kitchen_knife_left"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PeonyItems.SPATULA)
                .input('I', Items.IRON_INGOT)
                .input('S', Items.STICK)
                .pattern(" II")
                .pattern(" II")
                .pattern("S  ")
                .group(PEONY_ITEMS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PeonyItems.NATURE_GAS_DETECTOR)
                .input('I', Items.IRON_INGOT)
                .input('R', Items.REDSTONE)
                .input('L', Items.LIGHTNING_ROD)
                .pattern(" L ")
                .pattern("IRI")
                .pattern("IRI")
                .group(PEONY_ITEMS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter);

        /* LARD CONVERTING */
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, PeonyItems.LARD_BUCKET)
                .input(Items.BUCKET)
                .input(PeonyItems.LARD)
                .group(PEONY_ITEMS)
                .criterion(hasItem(PeonyItems.LARD), conditionsFromItem(PeonyItems.LARD))
                .offerTo(exporter);

        Registries.BLOCK.stream().forEach(block -> {
            if (block instanceof CuttingBoardBlock board) {
                createCuttingBoardRecipe(board, exporter);
            } else if (block instanceof LogStickBlock logStick) {
                createLogStickRecipe(logStick, exporter);
            } else if (block instanceof PotStandBlock potStand && !(block instanceof PotStandWithCampfireBlock)) {
                createPotStandRecipe(potStand, exporter);
            }
        });

        /* CUSTOM RECIPE */
        /* MILLING */
        generateMillingRecipe(Blocks.HAY_BLOCK, Items.WHEAT, 9, 2, exporter);
        generateMillingRecipe(Items.WHEAT, PeonyBlocks.FLOUR, 2, 2, exporter);
        generateMillingRecipe(PeonyItems.ROASTED_PEANUT_KERNEL, PeonyItems.CRUSHED_PEANUTS, 2, 1, exporter);

        /* SEQUENTIAL CRAFTING */
        SequentialCraftingRecipeJsonBuilder.create(PeonyBlocks.DOUGH)
                .step(CraftingSteps.Procedure.KNEADING, PeonyBlocks.FLOUR)
                .step(CraftingSteps.Procedure.KNEADING, Items.POTION)
                .step(CraftingSteps.Procedure.KNEADING, PeonyItems.PLACEHOLDER)
                .offerTo(exporter, Peony.id("dough_from_flour"));

        /* SEQUENTIAL COOKING */
        SequentialCookingRecipeJsonBuilder.create(550, false, PeonyItems.ROASTED_PEANUT_KERNEL)
                .step(new CookingSteps.Step(240, 20, PeonyItems.PEANUT_KERNEL))
                .offerTo(exporter);

        // Vanilla Extend
        SequentialCookingRecipeJsonBuilder.create(550, true, Items.COOKED_PORKCHOP)
                .step(new CookingSteps.Step(240, 20, Items.PORKCHOP))
                .offerTo(exporter);
        SequentialCookingRecipeJsonBuilder.create(550, true, Items.COOKED_BEEF)
                .step(new CookingSteps.Step(240, 20, Items.BEEF))
                .offerTo(exporter);
        SequentialCookingRecipeJsonBuilder.create(550, true, Items.COOKED_MUTTON)
                .step(new CookingSteps.Step(240, 20, Items.MUTTON))
                .offerTo(exporter);
    }

    public void generateMillingRecipe(ItemConvertible input, ItemConvertible outputItem, int outputCount, int millingTimes, RecipeExporter exporter) {
        MillingRecipeJsonBuilder.create(input, outputItem, outputCount).millingTimes(millingTimes).criterion(input).offerTo(exporter, Peony.id(getItemPath(outputItem) + "_from_" + getItemPath(input)));
    }

    public static void createCuttingBoardRecipe(CuttingBoardBlock board, RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, board)
                .input('L', board.getLog())
                .pattern("LL")
                .group(PEONY_BLOCKS)
                .criterion(hasItem(board.getLog()), conditionsFromItem(board.getLog()))
                .offerTo(exporter);
    }

    public static void createLogStickRecipe(LogStickBlock logStick, RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, logStick, 4)
                .input('L', logStick.getLog())
                .pattern("L")
                .pattern("L")
                .group(PEONY_BLOCKS)
                .criterion(hasItem(logStick.getLog()), conditionsFromItem(logStick.getLog()))
                .offerTo(exporter);
    }

    public static void createPotStandRecipe(PotStandBlock potStand, RecipeExporter exporter) {
        LogStickBlock logStick = potStand.getLogStick();

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, potStand)
                .input('O', logStick.getLog())
                .input('S', logStick)
                .pattern("OOO")
                .pattern("S S")
                .group(PEONY_BLOCKS)
                .criterion(hasItem(logStick.getLog()), conditionsFromItem(logStick.getLog()))
                .offerTo(exporter);
    }
}
