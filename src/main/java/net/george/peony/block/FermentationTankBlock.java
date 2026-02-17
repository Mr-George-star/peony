package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import net.george.peony.api.interaction.InteractionContext;
import net.george.peony.api.interaction.InventoryInteraction;
import net.george.peony.block.entity.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class FermentationTankBlock extends BlockWithEntity {
    public static final VoxelShape SHAPE = Stream.of(
            Block.createCuboidShape(1, 0, 1, 15, 1, 15),
            Block.createCuboidShape(0, 0, 0, 16, 16, 1),
            Block.createCuboidShape(0, 0, 15, 16, 16, 16),
            Block.createCuboidShape(15, 0, 1, 16, 16, 15),
            Block.createCuboidShape(0, 0, 1, 1, 16, 15)
    ).reduce(VoxelShapes::union).get();
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final MapCodec<FermentationTankBlock> CODEC = createCodec(FermentationTankBlock::new);

    protected FermentationTankBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<FermentationTankBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
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
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState down = world.getBlockState(pos.down());
        return down.isFullCube(world, pos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /* BLOCK ENTITY */

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            ItemStack heldStack = player.getStackInHand(hand);
            if (heldStack.getItem() instanceof BucketItem) {
                return ItemActionResult.SUCCESS;
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FermentationTankBlockEntity tank) {
                InteractionContext context = InteractionContext.create(world, pos, player, hand);
                return InventoryInteraction.interact(tank, context);
            }
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FermentationTankBlockEntity) {
                if (world instanceof ServerWorld) {
                    ItemScatterer.spawn(world, pos, (FermentationTankBlockEntity) blockEntity);
                }
                world.updateComparators(pos, this);
            } else {
                super.onStateReplaced(state, world, pos, newState, moved);
            }
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        world.removeBlockEntity(pos);
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FermentationTankBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ?
                validateTicker(type, PeonyBlockEntities.FERMENTATION_TANK, BlockEntityTickerProvider::clientTick) :
                validateTicker(type, PeonyBlockEntities.FERMENTATION_TANK, BlockEntityTickerProvider::tick);
    }
}
