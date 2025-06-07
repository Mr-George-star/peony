package net.george.peony.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public interface SolidBlockChecker {
    default boolean checkIsSolid(WorldView world, BlockPos pos) {
        BlockPos downPos = pos.down();
        return world.getBlockState(downPos).isFullCube(world, pos);
    }
}
