package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import net.george.peony.Peony;
import net.george.peony.block.entity.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
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
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class GasCylinderBlock extends FallingBlock implements BlockEntityProvider {
    public static final MapCodec<GasCylinderBlock> CODEC = createCodec(GasCylinderBlock::new);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = Stream.of(
            Block.createCuboidShape(3, 0, 3, 13, 1, 13),
            Block.createCuboidShape(2, 1, 2, 14, 13, 14),
            Block.createCuboidShape(3, 13, 3, 13, 14, 13)
    ).reduce(VoxelShapes::union).get();

    public GasCylinderBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<GasCylinderBlock> getCodec() {
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
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().rotateYClockwise());
    }

    @Override
    protected void configureFallingBlockEntity(FallingBlockEntity entity) {
        entity.setHurtEntities(2.0F, 40);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    public int getColor(BlockState state, BlockView world, BlockPos pos) {
        return state.getMapColor(world, pos).color;
    }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity falling) {
        int fallDistance = (int) falling.fallDistance;

        Peony.debug(String.valueOf(fallDistance));
        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;


            if (fallDistance > 3) {
                float explosionPower = Math.min(fallDistance / 10.0F + 1.0F, 5.0F);

                serverWorld.createExplosion(
                        falling,
                        falling.getX(),
                        falling.getY(),
                        falling.getZ(),
                        explosionPower,
                        World.ExplosionSourceType.BLOCK
                );
                serverWorld.spawnParticles(
                        ParticleTypes.EXPLOSION,
                        falling.getX(),
                        falling.getY(),
                        falling.getZ(),
                        20,
                        0.5, 0.5, 0.5,
                        0.5
                );
                world.emitGameEvent(falling, GameEvent.EXPLODE, pos);
                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                        SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F,
                        (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_LAND,
                        SoundCategory.BLOCKS, 0.3F,
                        world.random.nextFloat() * 0.1F + 0.9F);
            }
        }
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
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        world.removeBlockEntity(pos);
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GasCylinderBlockEntity gasCylinder) {
                AccessibleInventory.InteractionContext context = AccessibleInventory.createContext(world, pos, player, hand);
                return AccessibleInventory.access(gasCylinder, context, ItemDecrementBehaviour.createDefault());
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GasCylinderBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : (type == PeonyBlockEntities.GAS_CYLINDER ?
                (BlockEntityTicker<T>) (world1, pos, state1, blockEntity) ->
                        ((BlockEntityTickerProvider) blockEntity).tick(world1, pos, state1) :
                null);
    }
}
