package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;

public class DoughBlock extends FacingShapedBlock {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final MapCodec<DoughBlock> CODEC = createCodec(DoughBlock::new);

    public DoughBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<DoughBlock> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull VoxelShape getShapeAccordingToDirection(@NotNull Direction direction) {
        return switch (direction) {
            case WEST, EAST -> Block.createCuboidShape(5, 0, 4, 11, 3, 12);
            default -> Block.createCuboidShape(4, 0, 5, 12, 3, 11);
        };
    }

    @Override
    public @NotNull BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context)
                .with(WATERLOGGED, context.getWorld().getFluidState(context.getBlockPos()).isOf(Fluids.WATER));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }
}
