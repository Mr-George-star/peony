package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class LogStickBlock extends FacingBlock implements Waterloggable {
    public static final MapCodec<LogStickBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(createSettingsCodec(), createLogCodec()).apply(instance, LogStickBlock::new));
    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(7, 0, 7, 9, 16, 9);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(7, 7, 0, 9, 9, 16);
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0, 7, 7, 16, 9, 9);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected final Block log;

    public LogStickBlock(Settings settings, Block log) {
        super(settings);
        this.log = log;
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false));
    }

    public Block getLog() {
        return this.log;
    }

    @Override
    public MapCodec<LogStickBlock> getCodec() {
        return CODEC;
    }

    protected static <B extends LogStickBlock> RecordCodecBuilder<B, Block> createLogCodec() {
        return Block.CODEC.fieldOf("log").forGetter(LogStickBlock::getLog);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING).getAxis()) {
            case X -> X_SHAPE;
            case Z -> Z_SHAPE;
            case Y -> Y_SHAPE;
        };
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState()
                .with(FACING, context.getSide())
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
        builder.add(FACING, WATERLOGGED);
    }
}
