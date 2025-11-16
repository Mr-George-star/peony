package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class RiceCropBlock extends CropBlock {
    public static final MapCodec<RiceCropBlock> CODEC = createCodec(RiceCropBlock::new);
    public static final int MAX_AGE = 7;
    public static final IntProperty AGE = Properties.AGE_7;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public RiceCropBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(AGE, 0)
                .with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public MapCodec<RiceCropBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return PeonyItems.BARLEY_SEEDS;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            BlockState lowerState = world.getBlockState(pos.down());
            return lowerState.isOf(this) && lowerState.get(HALF) == DoubleBlockHalf.LOWER;
        } else {
            BlockPos down = pos.down();
            BlockState floor = world.getBlockState(down);
            return super.canPlaceAt(state, world, pos) &&
                    isInWater(world, pos) &&
                    floor.isIn(BlockTags.DIRT);
        }
    }

    private boolean isInWater(WorldView world, BlockPos pos) {
        return world.getFluidState(pos).isOf(Fluids.WATER) ||
                world.getBlockState(pos).isOf(Blocks.WATER);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!world.isClient) {
            DoubleBlockHalf half = state.get(HALF);
            boolean isMature = this.isMature(state);

            if (!player.isCreative() && isMature) {
                dropStacks(state, world, pos, null, player, player.getMainHandStack());

                int seedCount = world.random.nextInt(2) + 1;
                ItemStack seeds = new ItemStack(getSeedsItem(), seedCount);
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), seeds);
            }

            BlockPos otherHalfPos = half == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
            BlockState otherHalf = world.getBlockState(otherHalfPos);

            if (otherHalf.isOf(this) && otherHalf.get(HALF) != half) {
                if (half == DoubleBlockHalf.UPPER) {
                    world.setBlockState(otherHalfPos, otherHalf.with(AGE, MAX_AGE), Block.NOTIFY_ALL);
                } else {
                    world.setBlockState(otherHalfPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }
        super.afterBreak(world, player, pos, state, blockEntity, tool);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            int age = this.getAge(state);
            if (age < this.getMaxAge()) {
                float moisture = getAvailableMoisture(this, world, pos);
                if (random.nextInt((int)(25.0F / moisture) + 1) == 0) {
                    this.applyGrowth(world, pos, state);
                }
            }
        }
    }

    @Override
    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int newAge = this.getAge(state) + this.getGrowthAmount(world);
        int maxAge = this.getMaxAge();

        if (newAge > maxAge) {
            newAge = maxAge;
        }

        DoubleBlockHalf half = state.get(HALF);

        if (half == DoubleBlockHalf.LOWER && newAge >= maxAge) {
            BlockPos upperPos = pos.up();
            BlockState upperState = world.getBlockState(upperPos);

            if (upperState.isAir() && canPlaceAt(upperState, world, upperPos)) {
                world.setBlockState(upperPos,
                        this.getDefaultState()
                                .with(HALF, DoubleBlockHalf.UPPER)
                                .with(AGE, 0),
                        Block.NOTIFY_ALL);
            }
        } else if (half == DoubleBlockHalf.LOWER && newAge < maxAge) {
            BlockPos upperPos = pos.up();
            BlockState upperState = world.getBlockState(upperPos);

            if (upperState.isOf(this) && upperState.get(HALF) == DoubleBlockHalf.UPPER) {
                int upperAge = upperState.get(AGE);
                if (upperAge < maxAge) {
                    world.setBlockState(upperPos, upperState.with(AGE, Math.min(upperAge + 1, maxAge)), Block.NOTIFY_ALL);
                }
            }
        }

        world.setBlockState(pos, state.with(AGE, newAge), Block.NOTIFY_LISTENERS);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        DoubleBlockHalf half = state.get(HALF);

        if (half == DoubleBlockHalf.LOWER) {
            if (!this.isMature(state)) {
                return true;
            }

            BlockState upperState = world.getBlockState(pos.up());
            return upperState.isOf(this) &&
                    upperState.get(HALF) == DoubleBlockHalf.UPPER &&
                    !this.isMature(upperState);
        } else {
            return !this.isMature(state);
        }
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.applyGrowth(world, pos, state);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.get(HALF);

        if (direction.getAxis() == Direction.Axis.Y &&
                half == DoubleBlockHalf.LOWER == (direction == Direction.UP) &&
                (!neighborState.isOf(this) || neighborState.get(HALF) == half)) {
            return Blocks.AIR.getDefaultState();
        } else {
            return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos) ?
                    Blocks.AIR.getDefaultState() :
                    super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();

        if (blockPos.getY() < world.getTopY() - 1 &&
                world.getBlockState(blockPos.up()).canReplace(ctx) &&
                isInWater(world, blockPos) &&
                canPlantOnTop(world.getBlockState(blockPos.down()), world, blockPos.down())) {
            return super.getPlacementState(ctx);
        }
        return null;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isIn(BlockTags.DIRT);
    }

    @Override
    protected long getRenderingSeed(BlockState state, BlockPos pos) {
        Vec3i position = new Vec3i(pos.getX(), pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
        return position.hashCode();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE, HALF);
    }
}
