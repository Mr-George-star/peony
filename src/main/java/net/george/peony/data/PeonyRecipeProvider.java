package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.george.peony.Peony;
import net.george.peony.api.action.ActionTypes;
import net.george.peony.api.data.CommonIngredientTypes;
import net.george.peony.block.*;
import net.george.peony.block.data.CookingSteps;
import net.george.peony.block.data.Output;
import net.george.peony.block.data.StirFryingData;
import net.george.peony.data.json.*;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.IngredientCreator;
import net.george.peony.util.PeonyTags;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .pattern("S  ")
                .pattern(" II")
                .pattern(" II")
                .group(PEONY_BLOCKS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                .offerTo(exporter, Peony.id("skillet_right"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PeonyBlocks.SKILLET)
                .input('S', PeonyTags.Items.LOG_STICKS)
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .pattern("  S")
                .pattern("II ")
                .pattern("II ")
                .group(PEONY_BLOCKS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                .offerTo(exporter, Peony.id("skillet_left"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PeonyBlocks.BREWING_BARREL)
                .input('W', ItemTags.PLANKS)
                .input('S', ItemTags.SLABS)
                .pattern("WSW")
                .pattern("WSW")
                .pattern("S S")
                .group(PEONY_BLOCKS)
                .criterion(hasItem(Blocks.OAK_PLANKS), conditionsFromTag(ItemTags.PLANKS))
                .offerTo(exporter);

        /* ITEMS */

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PeonyItems.KITCHEN_KNIFE)
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('S', Items.STICK)
                .pattern("II")
                .pattern("II")
                .pattern("S ")
                .group(PEONY_ITEMS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                .offerTo(exporter, Peony.id("kitchen_knife_right"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PeonyItems.KITCHEN_KNIFE)
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('S', Items.STICK)
                .pattern("II")
                .pattern("II")
                .pattern(" S")
                .group(PEONY_ITEMS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                .offerTo(exporter, Peony.id("kitchen_knife_left"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PeonyItems.SPATULA)
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('S', Items.STICK)
                .pattern(" II")
                .pattern(" II")
                .pattern("S  ")
                .group(PEONY_ITEMS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, PeonyItems.WOODEN_PLATE)
                .input('W', ItemTags.PLANKS)
                .pattern("WWW").group(PEONY_ITEMS)
                .criterion(hasItem(Blocks.OAK_PLANKS), conditionsFromTag(ItemTags.PLANKS))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PeonyItems.NATURE_GAS_DETECTOR)
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('R', ConventionalItemTags.REDSTONE_DUSTS)
                .input('L', Items.LIGHTNING_ROD)
                .pattern(" L ")
                .pattern("IRI")
                .pattern("IRI")
                .group(PEONY_ITEMS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PeonyItems.CONDIMENT_BOTTLE)
                .input('G', ConventionalItemTags.GLASS_BLOCKS)
                .pattern("G G")
                .pattern("GGG")
                .group(PEONY_ITEMS)
                .criterion(hasItem(Blocks.GLASS), conditionsFromTag(ConventionalItemTags.GLASS_BLOCKS))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PeonyBlocks.CHEESE_BLOCK)
                .input(PeonyItems.CHEESE, 9)
                .group(PEONY_BLOCKS)
                .criterion(hasItem(PeonyItems.CHEESE), conditionsFromItem(PeonyItems.CHEESE))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PeonyItems.CHEESE, 9)
                .input(PeonyBlocks.CHEESE_BLOCK)
                .group(PEONY_ITEMS)
                .criterion(hasItem(PeonyBlocks.CHEESE_BLOCK), conditionsFromItem(PeonyBlocks.CHEESE_BLOCK))
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

        createShredderRecipe(Items.IRON_INGOT, ConventionalItemTags.IRON_INGOTS, PeonyItems.IRON_SHREDDER, exporter);
        createShredderRecipe(Items.GOLD_INGOT, ConventionalItemTags.GOLD_INGOTS, PeonyItems.GOLD_SHREDDER, exporter);
        createShredderRecipe(Items.DIAMOND, ConventionalItemTags.DIAMOND_GEMS, PeonyItems.DIAMOND_SHREDDER, exporter);
        offerNetheriteUpgradeRecipe(exporter, PeonyItems.DIAMOND_SHREDDER, RecipeCategory.TOOLS, PeonyItems.NETHERITE_SHREDDER);

        offerShapelessRecipe(exporter, PeonyItems.TOMATO_SEEDS, PeonyItems.TOMATO, PEONY_ITEMS, 1);

        /* FURNACE */
        offerSmelting(exporter, List.of(PeonyBlocks.DOUGH), RecipeCategory.FOOD, Items.BREAD,
                0.35F, 200, PEONY_ITEMS);
        offerSmelting(exporter, List.of(PeonyBlocks.FLATBREAD), RecipeCategory.FOOD, PeonyItems.BAKED_FLATBREAD,
                0.35F, 200, PEONY_ITEMS);
        offerSmelting(exporter, List.of(PeonyBlocks.RAW_MARGHERITA_PIZZA), RecipeCategory.FOOD, PeonyBlocks.MARGHERITA_PIZZA,
                0.4F, 200, PEONY_ITEMS);

        /* CUSTOM RECIPE */
        /* MILLING */
        generateMillingRecipe(Blocks.HAY_BLOCK, Items.WHEAT, 9, 2, exporter);
        generateMillingRecipe(Items.WHEAT, PeonyBlocks.FLOUR, 2, 2, exporter);
        generateMillingRecipe(PeonyItems.ROASTED_PEANUT_KERNEL, PeonyItems.CRUSHED_PEANUTS, 2, 1, exporter);
        generateMillingRecipe(PeonyItems.RICE_PANICLE, PeonyItems.BROWN_RICE, 4, 1, exporter);
        generateMillingRecipe(PeonyItems.BROWN_RICE, PeonyItems.RICE, 1, 1, exporter);

        /* SEQUENTIAL CRAFTING */
        SequentialCraftingRecipeJsonBuilder.create(PeonyBlocks.DOUGH)
                .step(ActionTypes.kneading(), PeonyBlocks.FLOUR)
                .step(ActionTypes.kneading(), PotionContentsComponent.createStack(Items.POTION, Potions.WATER))
                .offerTo(exporter, Peony.id("dough_from_flour"));
        SequentialCraftingRecipeJsonBuilder.create(PeonyBlocks.FLATBREAD)
                .step(ActionTypes.kneading(), PeonyBlocks.DOUGH)
                .step(ActionTypes.kneading(), PeonyItems.PLACEHOLDER)
                .offerTo(exporter, Peony.id("flatbread_from_dough"));
        SequentialCraftingRecipeJsonBuilder.create(PeonyItems.HAM, 2)
                .step(ActionTypes.cutting(), Items.PORKCHOP)
                .offerTo(exporter, Peony.id("ham_from_porkchop"));
        SequentialCraftingRecipeJsonBuilder.create(PeonyItems.MINCED_GARLIC)
                .step(ActionTypes.cutting(), PeonyItems.GARLIC_SCAPE)
                .step(ActionTypes.cutting(), PeonyItems.GARLIC_SCAPE)
                .step(ActionTypes.cutting(), PeonyItems.PLACEHOLDER)
                .offerTo(exporter, Peony.id("minced_garlic_from_garlic_scape"));

        /* SEQUENTIAL COOKING */
        SequentialCookingRecipeJsonBuilder.create(550, false, PeonyItems.ROASTED_PEANUT_KERNEL)
                .step(new CookingSteps.Step(240, 20, PeonyItems.PEANUT_KERNEL))
                .offerTo(exporter);
        SequentialCookingRecipeJsonBuilder.create(550, false, PeonyItems.TOMATO_SAUCE, Items.BOWL)
                .basicIngredient(CommonIngredientTypes.PEELED_TOMATO)
                .step(new CookingSteps.Step(200, 20, PeonyItems.PEELED_TOMATO))
                .offerTo(exporter);
        SequentialCookingRecipeJsonBuilder.create(550, true, PeonyItems.SCRAMBLED_EGGS, Items.BOWL)
                .step(new CookingSteps.Step(80, 60, Items.EGG))
                .step(new CookingSteps.Step(240, 20, PeonyItems.SPATULA, Items.EGG, new StirFryingData(3)))
                .offerTo(exporter);
        SequentialCookingRecipeJsonBuilder.create(550, false, PeonyItems.SCRAMBLED_EGGS_WITH_TOMATOES, PeonyItems.WOODEN_PLATE)
                .basicIngredient(CommonIngredientTypes.PEELED_TOMATO)
                .step(new CookingSteps.Step(280, 20, PeonyItems.SPATULA, PeonyItems.SCRAMBLED_EGGS, new StirFryingData(3)))
                .step(new CookingSteps.Step(80, 60, PeonyItems.CORIANDER))
                .offerTo(exporter);
        SequentialCookingRecipeJsonBuilder.create(550, true, PeonyItems.FRIED_SHREDDED_POTATOES, PeonyItems.WOODEN_PLATE)
                .basicIngredient(CommonIngredientTypes.MINCED_GARLIC)
                .step(new CookingSteps.Step(100, 80, PeonyItems.SHREDDED_POTATO))
                .step(new CookingSteps.Step(80, 20, PeonyItems.BLACK_VINEGAR))
                .step(new CookingSteps.Step(260, 20, PeonyItems.SPATULA, PeonyItems.SHREDDED_POTATO, new StirFryingData(4)))
                .offerTo(exporter);
        SequentialCookingRecipeJsonBuilder.create(550, true, PeonyItems.SWEET_AND_SOUR_PORK, PeonyItems.WOODEN_PLATE)
                .step(new CookingSteps.Step(240, 80, PeonyItems.PORK_TENDERLOIN))
                .step(new CookingSteps.Step(100, 80, PeonyItems.SPATULA, PeonyItems.SWEET_SOUR_SAUCE, new StirFryingData(3)))
                .offerTo(exporter);

        /* PARING */
        ParingRecipeJsonBuilder.create(PeonyItems.TOMATO, PeonyItems.PEELED_TOMATO).category(RecipeCategory.FOOD)
                .criterion(PeonyItems.TOMATO).offerTo(exporter);
        ParingRecipeJsonBuilder.create(Items.POTATO, PeonyItems.PEELED_POTATO).category(RecipeCategory.FOOD)
                .criterion(Items.POTATO).offerTo(exporter);

        /* BREWING */
        BrewingRecipeJsonBuilder.create(Items.HONEY_BOTTLE, Items.GLASS_BOTTLE, Items.HONEYCOMB.getDefaultStack())
                .brewingTime(200).offerTo(exporter, Peony.id("honey_bottle_brewing_from_honeycomb"));

        /* SHREDDING */
        ShreddingRecipeJsonBuilder.create(PeonyItems.PEELED_POTATO, PeonyItems.SHREDDED_POTATO, 1).offerTo(exporter);
        ShreddingRecipeJsonBuilder.create(PeonyItems.CHEESE, PeonyItems.SHREDDED_CHEESE, 1).offerTo(exporter);

        /* PIZZA CRAFTING */
        PizzaCraftingRecipeJsonBuilder.create(PeonyBlocks.RAW_MARGHERITA_PIZZA)
                .add(Ingredient.ofItems(PeonyItems.SHREDDED_CHEESE))
                .offerTo(exporter);

        /* FLAVOURING PREPARING */
        FlavouringPreparingRecipeJsonBuilder
                .create(PeonyItems.SWEET_SOUR_SAUCE.getDefaultStack(), PeonyItems.CONDIMENT_BOTTLE, Items.SUGAR, PeonyItems.BLACK_VINEGAR, Items.POTION)
                .stirringTimes(3)
                .offerTo(exporter);

        /* FERMENTING */
        FermentingRecipeJsonBuilder.create(
                Output.noContainer(PeonyBlocks.CHEESE_BLOCK.asItem().getDefaultStack()),
                IngredientCreator.create(Items.MILK_BUCKET))
                .category(RecipeCategory.FOOD)
                .fermentingTime(1200)
                .offerTo(exporter, Peony.id("fermented_cheese_from_milk"));

        // Vanilla Extend
        createCookingRecipe(Items.BEEF, Items.COOKED_BEEF, exporter);
        createCookingRecipe(Items.PORKCHOP, Items.COOKED_PORKCHOP, exporter);
        createCookingRecipe(Items.MUTTON, Items.COOKED_MUTTON, exporter);
        createCookingRecipe(Items.RABBIT, Items.COOKED_RABBIT, exporter);
        createCookingRecipe(Items.COD, Items.COOKED_COD, exporter);
        createCookingRecipe(Items.SALMON, Items.COOKED_SALMON, exporter);
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

    public static void createShredderRecipe(ItemConvertible material, @Nullable TagKey<Item> materialTag, ItemConvertible result, RecipeExporter exporter) {
        ShapedRecipeJsonBuilder builder = ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, result).input('S', Items.STICK);
        if (materialTag == null) {
            builder.input('M', material);
        } else {
            builder.input('M', materialTag);
        }

        builder.pattern("M M")
                .pattern("MMM")
                .pattern(" S ")
                .group(PEONY_ITEMS)
                .criterion(hasItem(material), conditionsFromItem(material))
                .offerTo(exporter);
    }

    public static void createCookingRecipe(ItemConvertible input, ItemConvertible output, RecipeExporter exporter) {
        SequentialCookingRecipeJsonBuilder.create(550, true, output)
                .step(new CookingSteps.Step(240, 20, input))
                .offerTo(exporter);
    }
}
