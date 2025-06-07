package net.george.peony.block.entity;

import com.google.common.collect.ImmutableList;
import net.george.peony.Peony;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.PeonyBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@SuppressWarnings("SameParameterValue")
public class PeonyBlockEntities {
    public static final BlockEntityType<MillstoneBlockEntity> MILLSTONE = register(
            "millstone", MillstoneBlockEntity::new, PeonyBlocks.MILLSTONE);
    public static final BlockEntityType<CuttingBoardBlockEntity> CUTTING_BOARD = register(
            "cutting_board", CuttingBoardBlockEntity::new, Registries.BLOCK.stream().filter(block -> block instanceof CuttingBoardBlock).toArray(CuttingBoardBlock[]::new));

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntityFactory<T> factory, Block... blocks) {
        if (ImmutableList.copyOf(blocks).isEmpty()) {
            Peony.LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", Peony.id(name));
        }

        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Peony.id(name), BlockEntityType.Builder.create(factory, blocks).build());
    }

    public static void register() {
        Peony.debug("Block Entities");
    }
}
