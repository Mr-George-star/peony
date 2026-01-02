package net.george.peony.data;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.george.peony.block.*;
import net.george.peony.data.model.PeonyModels;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.data.client.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class PeonyModelProvider extends FabricModelProvider {
    public PeonyModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerSimpleCubeAll(PeonyBlocks.CHEESE_BLOCK);

        PeonyModels.registerPizza(generator, PeonyBlocks.RAW_MARGHERITA_PIZZA);
        PeonyModels.registerPizza(generator, PeonyBlocks.MARGHERITA_PIZZA);

        generator.registerCrop(PeonyBlocks.BARLEY_CROP, BarleyCropBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7);
        generator.registerCrop(PeonyBlocks.PEANUT_CROP, PeanutCropBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7);
        generator.registerCrop(PeonyBlocks.TOMATO_VINES, TomatoVinesBlock.AGE, 0, 1, 2, 3);
        this.registerRiceCrop(generator);
        generator.registerCrop(PeonyBlocks.CORIANDER_CROP, CorianderCropBlock.AGE, 0, 1, 2, 3, 4);
        generator.registerCrop(PeonyBlocks.GARLIC_CROP, GarlicCropBlock.AGE, 0, 1, 2, 3, 4);

        Registries.BLOCK.stream().filter(block -> block instanceof CuttingBoardBlock).forEach(board ->
                PeonyModels.registerCuttingBoard(generator, board));
        Registries.BLOCK.stream().filter(block -> block instanceof LogStickBlock).forEach(logStick ->
                PeonyModels.registerLogStick(generator, logStick));
        PeonyModels.registerPotStand(generator, PeonyBlocks.OAK_POT_STAND, PeonyBlocks.OAK_POT_STAND_WITH_CAMPFIRE);
        PeonyModels.registerPotStand(generator, PeonyBlocks.SPRUCE_POT_STAND, PeonyBlocks.SPRUCE_POT_STAND_WITH_CAMPFIRE);
        PeonyModels.registerPotStand(generator, PeonyBlocks.BIRCH_POT_STAND, PeonyBlocks.BIRCH_POT_STAND_WITH_CAMPFIRE);
        PeonyModels.registerPotStand(generator, PeonyBlocks.JUNGLE_POT_STAND, PeonyBlocks.JUNGLE_POT_STAND_WITH_CAMPFIRE);
        PeonyModels.registerPotStand(generator, PeonyBlocks.ACACIA_POT_STAND, PeonyBlocks.ACACIA_POT_STAND_WITH_CAMPFIRE);
        PeonyModels.registerPotStand(generator, PeonyBlocks.CHERRY_POT_STAND, PeonyBlocks.CHERRY_POT_STAND_WITH_CAMPFIRE);
        PeonyModels.registerPotStand(generator, PeonyBlocks.DARK_OAK_POT_STAND, PeonyBlocks.DARK_OAK_POT_STAND_WITH_CAMPFIRE);
        PeonyModels.registerPotStand(generator, PeonyBlocks.MANGROVE_POT_STAND, PeonyBlocks.MANGROVE_POT_STAND_WITH_CAMPFIRE);

        generator.registerSimpleState(PeonyBlocks.NATURE_GAS);
        generator.registerSimpleState(PeonyBlocks.LARD_FLUID);
        generator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(PeonyBlocks.LARD_CAULDRON)
                        .coordinate(BlockStateVariantMap.create(LeveledCauldronBlock.LEVEL)
                                .register(1, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_CAULDRON_LEVEL1.upload(PeonyBlocks.LARD_CAULDRON, "_level1", TextureMap.cauldron(TextureMap.getSubId(Blocks.WATER, "_still")), generator.modelCollector)))
                                .register(2, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_CAULDRON_LEVEL2.upload(PeonyBlocks.LARD_CAULDRON, "_level2", TextureMap.cauldron(TextureMap.getSubId(Blocks.WATER, "_still")), generator.modelCollector)))
                                .register(3, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_CAULDRON_FULL.upload(PeonyBlocks.LARD_CAULDRON, "_full", TextureMap.cauldron(TextureMap.getSubId(Blocks.WATER, "_still")), generator.modelCollector)))));
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(PeonyItems.BARLEY, Models.GENERATED);
        generator.register(PeonyItems.PEANUT, Models.GENERATED);
        generator.register(PeonyItems.ROASTED_PEANUT_KERNEL, Models.GENERATED);
        generator.register(PeonyItems.CRUSHED_PEANUTS, Models.GENERATED);
        generator.register(PeonyItems.TOMATO, Models.GENERATED);
        generator.register(PeonyItems.PEELED_TOMATO, Models.GENERATED);
        generator.register(PeonyItems.PEELED_POTATO, Models.GENERATED);
        generator.register(PeonyItems.SHREDDED_POTATO, Models.GENERATED);
        generator.register(PeonyItems.CORIANDER, Models.GENERATED);
        generator.register(PeonyItems.RICE_PANICLE, Models.GENERATED);
        generator.register(PeonyItems.RICE, Models.GENERATED);
        generator.register(PeonyItems.GARLIC_CLOVE, Models.GENERATED);
        generator.register(PeonyItems.GARLIC_SCAPE, Models.GENERATED);
        generator.register(PeonyItems.MINCED_GARLIC, Models.GENERATED);
        generator.register(PeonyItems.SOYBEAN, Models.GENERATED);
        generator.register(PeonyItems.SOYBEAN_POD, Models.GENERATED);
        generator.register(PeonyItems.HAM, Models.GENERATED);
        generator.register(PeonyItems.BAKED_FLATBREAD, Models.GENERATED);
        generator.register(PeonyItems.TOMATO_SAUCE, Models.GENERATED);
        generator.register(PeonyItems.SCRAMBLED_EGGS, Models.GENERATED);
        generator.register(PeonyItems.SCRAMBLED_EGGS_WITH_TOMATOES, Models.GENERATED);
        generator.register(PeonyItems.FRIED_SHREDDED_POTATOES, Models.GENERATED);
        generator.register(PeonyItems.STIR_FRIED_GARLIC_SCAPE_WITH_PORK, Models.GENERATED);
        generator.register(PeonyItems.SWEET_AND_SOUR_PORK, Models.GENERATED);
        generator.register(PeonyItems.CHEESE, Models.GENERATED);
        generator.register(PeonyItems.SHREDDED_CHEESE, Models.GENERATED);

        generator.register(PeonyItems.LARD, Models.GENERATED);
        generator.register(PeonyItems.LARD_BOTTLE, Models.GENERATED);
        generator.register(PeonyItems.PORK_TENDERLOIN, Models.GENERATED);
        generator.register(PeonyItems.CONDIMENT_BOTTLE, Models.GENERATED);
        generator.register(PeonyItems.BLACK_VINEGAR, Models.GENERATED);
        generator.register(PeonyItems.SWEET_SOUR_SAUCE, Models.GENERATED);

        generator.register(PeonyItems.KITCHEN_KNIFE, Models.HANDHELD);
        generator.register(PeonyItems.SPATULA, Models.GENERATED);
        generator.register(PeonyItems.IRON_PARING_KNIFE, Models.GENERATED);
        generator.register(PeonyItems.IRON_SHREDDER, Models.GENERATED);
        generator.register(PeonyItems.GOLD_SHREDDER, Models.GENERATED);
        generator.register(PeonyItems.DIAMOND_SHREDDER, Models.GENERATED);
        generator.register(PeonyItems.NETHERITE_SHREDDER, Models.GENERATED);
        generator.register(PeonyItems.WOODEN_PLATE, Models.GENERATED);
        generator.register(PeonyItems.NATURE_GAS_DETECTOR, Models.GENERATED);

        generator.register(PeonyItems.NATURE_GAS_BUCKET, Models.GENERATED);
        generator.register(PeonyItems.LARD_BUCKET, Models.GENERATED);

        generator.register(PeonyItems.MUSIC_DISC_SURPRISE, Models.GENERATED);
    }

    private void registerRiceCrop(BlockStateModelGenerator generator) {
        Block rice = PeonyBlocks.RICE_CROP;
        Object2ObjectMap<Pair<Integer, DoubleBlockHalf>, Identifier> values = new Object2ObjectOpenHashMap<>();
        BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(RiceCropBlock.AGE, RiceCropBlock.HALF).register((age, half) -> {
            Identifier identifier = values.computeIfAbsent(Pair.of(age, half), value ->
                    generator.createSubModel(rice,  "_" + half + "_stage" + age, Models.CROP, TextureMap::crop));
            return BlockStateVariant.create().put(VariantSettings.MODEL, identifier);
        });
        generator.registerItemModel(rice.asItem());
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(rice).coordinate(blockStateVariantMap));
    }
}
