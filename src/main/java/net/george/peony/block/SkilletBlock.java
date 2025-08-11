package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import net.george.peony.api.heat.HeatParticleHelper;
import net.george.peony.api.heat.HeatProvider;
import net.george.peony.block.entity.SkilletBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class SkilletBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = Stream.of(
            Block.createCuboidShape(1, 0, 1, 15, 1, 15),
            Block.createCuboidShape(1, 1, 1, 15, 4, 2),
            Block.createCuboidShape(1, 1, 14, 15, 4, 15),
            Block.createCuboidShape(14, 1, 2, 15, 4, 14),
            Block.createCuboidShape(1, 1, 2, 2, 4, 14)
    ).reduce((first, second) -> VoxelShapes.combineAndSimplify(first, second, BooleanBiFunction.OR)).get();
    public static final MapCodec<SkilletBlock> CODEC = createCodec(SkilletBlock::new);

    public SkilletBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<SkilletBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockState down = world.getBlockState(pos.down());
        if (down.getBlock() instanceof HeatProvider heatProvider) {
            HeatParticleHelper.spawnHeatParticles(world, pos.down(), heatProvider.getHeat());
        }
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState down = world.getBlockState(pos.down());
        return down.isFullCube(world, pos) || down.getBlock() instanceof HeatProvider;
    }

    @NotNull
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState()
                .with(FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /* BLOCK ENTITY */

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SkilletBlockEntity(pos, state);
    }
}
