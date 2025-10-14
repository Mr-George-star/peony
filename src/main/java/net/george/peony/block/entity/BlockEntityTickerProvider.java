package net.george.peony.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockEntityTickerProvider {
    void tick(World world, BlockPos pos, BlockState state);

    @Environment(EnvType.CLIENT)
    default void clientTick(World world, BlockPos pos, BlockState state) {}

    static <T extends BlockEntity & BlockEntityTickerProvider> void tick(World world, BlockPos pos, BlockState state, T blockEntity) {
        blockEntity.tick(world, pos, state);
    }

    @Environment(EnvType.CLIENT)
    static <T extends BlockEntity & BlockEntityTickerProvider> void clientTick(World world, BlockPos pos, BlockState state, T blockEntity) {
        blockEntity.clientTick(world, pos, state);
    }
}
