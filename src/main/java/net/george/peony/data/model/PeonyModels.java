package net.george.peony.data.model;

import net.george.peony.Peony;
import net.george.peony.block.CuttingBoardBlock;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class PeonyModels {
    public static final Model CUTTING_BOARD = new Model(Optional.of(Peony.id("block/cutting_board")),
            Optional.empty(), TextureKey.TEXTURE, TextureKey.PARTICLE);

    public static Identifier getUploaded(Block block, BlockStateModelGenerator generator, TexturedModel.Factory factory) {
        return factory.upload(block, generator.modelCollector);
    }

    private static Identifier getUploadedBoard(Block block, BlockStateModelGenerator generator) {
        return getUploaded(block, generator, PeonyModelFactories.CUTTING_BOARD);
    }

    public static void registerCuttingBoard(BlockStateModelGenerator generator, Block block) {
//        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block,
//                BlockStateVariant.create().put(VariantSettings.MODEL, getUploaded(block, generator, PeonyModelFactories.CUTTING_BOARD))));
        generator.blockStateCollector.accept(MultipartBlockStateSupplier.create(block)
                .with(When.create().set(CuttingBoardBlock.FACING, Direction.NORTH),
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, getUploadedBoard(block, generator))
                                .put(VariantSettings.Y, VariantSettings.Rotation.R0))
                .with(When.create().set(CuttingBoardBlock.FACING, Direction.EAST),
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, getUploadedBoard(block, generator))
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90))
                .with(When.create().set(CuttingBoardBlock.FACING, Direction.SOUTH),
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, getUploadedBoard(block, generator))
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .with(When.create().set(CuttingBoardBlock.FACING, Direction.WEST),
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, getUploadedBoard(block, generator))
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)));
    }
}
