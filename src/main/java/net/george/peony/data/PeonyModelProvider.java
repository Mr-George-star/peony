package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.george.peony.block.*;
import net.george.peony.data.model.PeonyModels;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.data.client.*;
import net.minecraft.registry.Registries;

public class PeonyModelProvider extends FabricModelProvider {
    public PeonyModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerCrop(PeonyBlocks.BARLEY_CROP, BarleyCropBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7);
        generator.registerCrop(PeonyBlocks.PEANUT_CROP, PeanutCropBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7);
        generator.registerCrop(PeonyBlocks.TOMATO_VINES, TomatoVinesBlock.AGE, 0, 1, 2, 3);

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
        generator.register(PeonyItems.LARD, Models.GENERATED);
        generator.register(PeonyItems.LARD_BOTTLE, Models.GENERATED);

        generator.register(PeonyItems.KITCHEN_KNIFE, Models.HANDHELD);
        generator.register(PeonyItems.SPATULA, Models.GENERATED);
        generator.register(PeonyItems.IRON_PARING_KNIFE, Models.GENERATED);
        generator.register(PeonyItems.NATURE_GAS_DETECTOR, Models.GENERATED);

        generator.register(PeonyItems.NATURE_GAS_BUCKET, Models.GENERATED);
        generator.register(PeonyItems.LARD_BUCKET, Models.GENERATED);
    }
}
