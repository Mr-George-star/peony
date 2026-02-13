package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import net.george.peony.api.interaction.InteractionContext;
import net.george.peony.api.interaction.InventoryInteraction;
import net.george.peony.block.entity.BowlBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
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

import java.util.stream.Stream;

public class BowlBlock extends BlockWithEntity {
    public static final VoxelShape SHAPE = Stream.of(
            Block.createCuboidShape(3, 0, 3, 13, 1, 13),
            Block.createCuboidShape(3, 1, 3, 13, 3, 4),
            Block.createCuboidShape(3, 1, 12, 13, 3, 13),
            Block.createCuboidShape(12, 1, 4, 13, 3, 12),
            Block.createCuboidShape(3, 1, 4, 4, 3, 12)
    ).reduce(VoxelShapes::union).get();
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final MapCodec<BowlBlock> CODEC = createCodec(BowlBlock::new);

    public BowlBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<BowlBlock> getCodec() {
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

    /* BLOCK ENTITY */

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BowlBlockEntity bowl) {
                InteractionContext context = InteractionContext.create(world, pos, player, hand);
                return InventoryInteraction.interact(bowl, context);
            }
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BowlBlockEntity) {
                if (world instanceof ServerWorld) {
                    ItemScatterer.spawn(world, pos, (BowlBlockEntity) blockEntity);
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
        return new BowlBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
