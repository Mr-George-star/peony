package net.george.peony.block.entity;

import net.george.peony.block.PotStandBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PotStandBlockEntity extends BlockEntity implements DirectionProvider {
    public PotStandBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.POT_STAND, pos, state);
    }

    @Override
    public Direction getDirection() {
        if (this.world != null) {
            BlockState state = this.world.getBlockState(this.pos);
            if (state.getBlock() instanceof PotStandBlock) {
                return state.get(PotStandBlock.FACING);
            } else {
                return Direction.NORTH;
            }
        }
        return Direction.NORTH;
    }
}
