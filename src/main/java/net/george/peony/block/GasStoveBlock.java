package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import net.george.peony.block.data.HeatSource;
import net.george.peony.block.entity.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class GasStoveBlock extends BlockWithEntity implements HeatSource {
    public static final MapCodec<GasStoveBlock> CODEC = createCodec(GasStoveBlock::new);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty OPENED = BooleanProperty.of("opened");
    public static final VoxelShape SHAPE = Stream.of(
            Block.createCuboidShape(1, 0, 1, 3, 12, 3),
            Block.createCuboidShape(13, 0, 1, 15, 12, 3),
            Block.createCuboidShape(13, 0, 13, 15, 12, 15),
            Block.createCuboidShape(1, 0, 13, 3, 12, 15),
            Block.createCuboidShape(1, 12, 1, 15, 15, 15),
            Block.createCuboidShape(3, 15, 3, 13, 16, 13)
    ).reduce(VoxelShapes::union).get();

    protected GasStoveBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(OPENED, false));
    }

    public static int getLuminance(BlockState state) {
        if (state.get(OPENED)) {
            return 14;
        } else {
            return 0;
        }
    }

    @Override
    protected MapCodec<GasStoveBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
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
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().rotateYClockwise());
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPENED);
    }

    /* BLOCK ENTITY */

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        world.removeBlockEntity(pos);
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GasStoveBlockEntity gasStove) {
                AccessibleInventory.InteractionContext context = AccessibleInventory.createContext(world, pos, player, hand);
                return AccessibleInventory.access(gasStove, context, ItemDecrementBehaviour.createDefault());
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GasStoveBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ?
                validateTicker(type, PeonyBlockEntities.GAS_STOVE, BlockEntityTickerProvider::clientTick) :
                validateTicker(type, PeonyBlockEntities.GAS_STOVE, BlockEntityTickerProvider::tick);
    }
}
