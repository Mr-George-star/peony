package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.george.peony.block.BarleyCropBlock;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.LogStickBlock;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.data.model.PeonyModels;
import net.george.peony.item.PeonyItems;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.registry.Registries;

public class PeonyModelProvider extends FabricModelProvider {
    public PeonyModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerCrop(PeonyBlocks.BARLEY_CROP, BarleyCropBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7);

        Registries.BLOCK.stream().filter(block -> block instanceof CuttingBoardBlock).forEach(board ->
                PeonyModels.registerCuttingBoard(generator, board));
        Registries.BLOCK.stream().filter(block -> block instanceof LogStickBlock).forEach(logStick ->
                PeonyModels.registerLogStick(generator, logStick));
        PeonyModels.registerPotStand(generator, PeonyBlocks.OAK_POT_STAND, PeonyBlocks.OAK_POT_STAND_WITH_CAMPFIRE);

        generator.registerSimpleState(PeonyBlocks.NATURE_GAS);
        generator.registerSimpleState(PeonyBlocks.LARD_FLUID);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(PeonyItems.BARLEY, Models.GENERATED);
        generator.register(PeonyItems.KITCHEN_KNIFE, Models.HANDHELD);
        generator.register(PeonyItems.LARD, Models.GENERATED);
        generator.register(PeonyItems.NATURE_GAS_DETECTOR, Models.GENERATED);

        generator.register(PeonyItems.NATURE_GAS_BUCKET, Models.GENERATED);
        generator.register(PeonyItems.LARD_BUCKET, Models.GENERATED);
    }
}
