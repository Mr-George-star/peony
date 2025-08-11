package net.george.peony.fluid;

import net.george.peony.block.PeonyBlocks;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class NatureGasFluid extends FlowableFluid {
    @Override
    public Fluid getFlowing() {
        return PeonyFluids.FLOWING_NATURE_GAS;
    }

    @Override
    public Fluid getStill() {
        return PeonyFluids.STILL_NATURE_GAS;
    }

    @Override
    public Item getBucketItem() {
        return PeonyItems.NATURE_GAS_BUCKET;
    }

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
        return 30;
    }

    @Override
    protected float getBlastResistance() {
        return 100F;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return PeonyBlocks.NATURE_GAS.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    @Override
    public abstract boolean isStill(FluidState state);

    @Override
    public abstract int getLevel(FluidState state);

    public static class Flowing extends NatureGasFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }
    }

    public static class Still extends NatureGasFluid {
        @Override
        public boolean isStill(FluidState state) {
            return true;
        }

        @Override
        public int getLevel(FluidState state) {
            return 8;
        }
    }
}
