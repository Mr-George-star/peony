package net.george.peony.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class OilFluid extends FlowableFluid {
    @Override
    public abstract Fluid getFlowing();

    @Override
    public abstract Fluid getStill();

    @Override
    public abstract Item getBucketItem();

    @Override
    protected abstract BlockState toBlockState(FluidState state);

    @Override
    public abstract boolean isStill(FluidState state);

    @Override
    public abstract int getLevel(FluidState state);

    @Override
    protected boolean isInfinite(World world) {
        return false;
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == this.getFlowing() || fluid == this.getStill();
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return 1;
    }

    @Override
    protected int getMaxFlowDistance(WorldView world) {
        return 4;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 10;
    }

    @Override
    protected float getBlastResistance() {
        return 100F;
    }
}
