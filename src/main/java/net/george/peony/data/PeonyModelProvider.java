package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.george.peony.block.BarleyCropBlock;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.data.model.PeonyModels;
import net.george.peony.item.PeonyItems;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class PeonyModelProvider extends FabricModelProvider {
    public PeonyModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerCrop(PeonyBlocks.BARLEY_CROP, BarleyCropBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7);
        PeonyModels.registerCuttingBoard(generator, PeonyBlocks.OAK_CUTTING_BOARD);
        PeonyModels.registerCuttingBoard(generator, PeonyBlocks.SPRUCE_CUTTING_BOARD);
        PeonyModels.registerCuttingBoard(generator, PeonyBlocks.BIRCH_CUTTING_BOARD);
        PeonyModels.registerCuttingBoard(generator, PeonyBlocks.JUNGLE_CUTTING_BOARD);
        PeonyModels.registerCuttingBoard(generator, PeonyBlocks.ACACIA_CUTTING_BOARD);
        PeonyModels.registerCuttingBoard(generator, PeonyBlocks.DARK_OAK_CUTTING_BOARD);
        PeonyModels.registerCuttingBoard(generator, PeonyBlocks.MANGROVE_CUTTING_BOARD);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(PeonyItems.BARLEY, Models.GENERATED);
    }
}
