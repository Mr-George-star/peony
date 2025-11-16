package net.george.peony.block.entity;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.george.peony.Peony;
import net.george.peony.block.*;
import net.george.peony.util.registry.RegistryDataUtils;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@SuppressWarnings("SameParameterValue")
public class PeonyBlockEntities {
    public static final BlockEntityType<MillstoneBlockEntity> MILLSTONE = register("millstone",
            MillstoneBlockEntity::new, PeonyBlocks.MILLSTONE);
    public static final BlockEntityType<CuttingBoardBlockEntity> CUTTING_BOARD = register("cutting_board",
            CuttingBoardBlockEntity::new, RegistryDataUtils.BLOCK.filterToArray(block -> block instanceof CuttingBoardBlock));
    public static final BlockEntityType<SkilletBlockEntity> SKILLET = register("skillet",
            SkilletBlockEntity::new, PeonyBlocks.SKILLET);
    public static final BlockEntityType<BrewingBarrelBlockEntity> BREWING_BARREL = register("brewing_barrel",
            BrewingBarrelBlockEntity::new, PeonyBlocks.BREWING_BARREL);
    public static final BlockEntityType<PotStandBlockEntity> POT_STAND = register("pot_stand",
            PotStandBlockEntity::new, RegistryDataUtils.BLOCK.filterToArray(block -> block instanceof PotStandBlock));
    public static final BlockEntityType<PotStandWithCampfireBlockEntity> POT_STAND_WITH_CAMPFIRE = register("pot_stand_with_campfire",
            PotStandWithCampfireBlockEntity::new, RegistryDataUtils.BLOCK.filterToArray(block -> block instanceof PotStandWithCampfireBlock));

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntityFactory<T> factory, Block... blocks) {
        if (ImmutableList.copyOf(blocks).isEmpty()) {
            Peony.LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", Peony.id(name));
        }

        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Peony.id(name), BlockEntityType.Builder.create(factory, blocks).build());
    }

    public static void register() {
        Peony.debug("Block Entities");
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.fluidStorage, BREWING_BARREL);
    }
}
