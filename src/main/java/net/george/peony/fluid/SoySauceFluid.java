package net.george.peony.fluid;

import net.george.peony.block.PeonyBlocks;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class SoySauceFluid extends CondimentFluid {
    @Override
    public Fluid getFlowing() {
        return PeonyFluids.FLOWING_SOY_SAUCE;
    }

    @Override
    public Fluid getStill() {
        return PeonyFluids.STILL_SOY_SAUCE;
    }

    @Override
    public Item getBucketItem() {
        return PeonyItems.SOY_SAUCE_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return PeonyBlocks.SOY_SAUCE_FLUID.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    public static class Flowing extends SoySauceFluid {
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

    public static class Still extends SoySauceFluid {
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
