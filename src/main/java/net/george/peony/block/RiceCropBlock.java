package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class RiceCropBlock extends CropBlock implements Waterloggable {
    public static final MapCodec<RiceCropBlock> CODEC = createCodec(RiceCropBlock::new);
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public RiceCropBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(AGE, 0)
                .with(HALF, DoubleBlockHalf.LOWER)
                .with(WATERLOGGED, false));
    }

    @Override
    public MapCodec<RiceCropBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return PeonyItems.BROWN_RICE;
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
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return switch (state.get(AGE)) {
                case 0 ->  ofHeight(5);
                case 1 -> ofHeight(10);
                case 2 -> ofHeight(14);
                default -> VoxelShapes.fullCube();
            };
        } else {
            return switch (state.get(AGE)) {
                case 0 -> ofHeight(9);
                case 1 -> ofHeight(13);
                case 2 -> ofHeight(14);
                default -> VoxelShapes.fullCube();
            };
        }
    }

    public static VoxelShape ofHeight(int height) {
        return Block.createCuboidShape(0, 0, 0, 16, height, 16);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            BlockState lowerState = world.getBlockState(pos.down());
            return lowerState.isOf(this) && lowerState.get(HALF) == DoubleBlockHalf.LOWER;
        } else {
            BlockPos down = pos.down();
            BlockState floor = world.getBlockState(down);
            return isInWater(world, pos) &&
                    floor.isIn(BlockTags.DIRT);
        }
    }

    private boolean isInWater(WorldView world, BlockPos pos) {
        return world.getFluidState(pos).isOf(Fluids.WATER) ||
                world.getBlockState(pos).isOf(Blocks.WATER);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            DoubleBlockHalf half = state.get(HALF);

            if (half == DoubleBlockHalf.UPPER) {
                BlockPos lowerPos = pos.down();
                BlockState lowerState = world.getBlockState(lowerPos);

                if (lowerState.isOf(this) && lowerState.get(HALF) == DoubleBlockHalf.LOWER) {
                    world.setBlockState(lowerPos, lowerState.with(AGE, MAX_AGE), Block.NOTIFY_ALL);
                }
            } else if (half == DoubleBlockHalf.LOWER) {
                BlockPos upperPos = pos.up();
                BlockState upperState = world.getBlockState(upperPos);

                if (upperState.isOf(this) && upperState.get(HALF) == DoubleBlockHalf.UPPER) {
                    world.setBlockState(upperPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }

        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos checkPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos.up() : pos;
        if (world.getBaseLightLevel(checkPos, 0) >= 8) {
            int age = this.getAge(state);
            if (age < this.getMaxAge() || (age == this.getMaxAge() && state.get(HALF) == DoubleBlockHalf.LOWER)) {
                float moisture = getMoisture(this, world, pos);

                if (random.nextInt((int)(20.0F / moisture) + 1) == 0) {
                    this.applyGrowth(world, pos, state);
                }
            }
        }
    }

    @Override
    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int currentAge = this.getAge(state);
        int newAge = currentAge + this.getGrowthAmount(world);
        int maxAge = this.getMaxAge();

        if (newAge > maxAge) {
            newAge = maxAge;
        }

        DoubleBlockHalf half = state.get(HALF);

        if (half == DoubleBlockHalf.LOWER && newAge >= maxAge) {
            BlockPos upperPos = pos.up();
            BlockState upperState = world.getBlockState(upperPos);

            if (upperState.isAir()) {
                BlockState newUpperState = this.getDefaultState()
                        .with(HALF, DoubleBlockHalf.UPPER)
                        .with(AGE, 0)
                        .with(WATERLOGGED, world.getFluidState(upperPos).isOf(Fluids.WATER));

                if (newUpperState.canPlaceAt(world, upperPos)) {
                    world.setBlockState(upperPos, newUpperState, Block.NOTIFY_ALL);
                    world.setBlockState(pos, state.with(AGE, maxAge), Block.NOTIFY_LISTENERS);
                }
            } else if (upperState.isOf(this) && upperState.get(HALF) == DoubleBlockHalf.UPPER) {
                int upperAge = upperState.get(AGE);
                if (upperAge < maxAge) {
                    int upperNewAge = upperAge + this.getGrowthAmount(world);
                    if (upperNewAge > maxAge) upperNewAge = maxAge;
                    world.setBlockState(upperPos, upperState.with(AGE, upperNewAge), Block.NOTIFY_ALL);
                }
            }
        } else if (half == DoubleBlockHalf.UPPER) {
            BlockPos lowerPos = pos.down();
            BlockState lowerState = world.getBlockState(lowerPos);
            if (lowerState.isOf(this) && lowerState.get(HALF) == DoubleBlockHalf.LOWER) {
                world.setBlockState(pos, state.with(AGE, newAge), Block.NOTIFY_LISTENERS);
            }
        } else {
            world.setBlockState(pos, state.with(AGE, newAge), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected int getGrowthAmount(World world) {
        return MathHelper.nextInt(world.random, 1, 2);
    }

    protected float getMoisture(Block block, WorldView world, BlockPos pos) {
        float baseMoisture = getAvailableMoisture(block, world, pos);
        if (isInWater(world, pos)) {
            return baseMoisture * 1.5f;
        }
        return baseMoisture;
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        DoubleBlockHalf half = state.get(HALF);

        if (half == DoubleBlockHalf.LOWER) {
            if (!this.isMature(state)) {
                return true;
            }

            BlockState upperState = world.getBlockState(pos.up());
            if (upperState.isOf(this) && upperState.get(HALF) == DoubleBlockHalf.UPPER) {
                return !this.isMature(upperState);
            } else return upperState.isAir();
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
        int currentAge = this.getAge(state);
        int maxAge = this.getMaxAge();
        DoubleBlockHalf half = state.get(HALF);

        if (half == DoubleBlockHalf.LOWER && currentAge >= maxAge) {
            BlockPos upperPos = pos.up();
            BlockState upperState = world.getBlockState(upperPos);

            if (upperState.isAir()) {
                BlockState newUpperState = this.getDefaultState()
                        .with(HALF, DoubleBlockHalf.UPPER)
                        .with(AGE, 0)
                        .with(WATERLOGGED, world.getFluidState(upperPos).isOf(Fluids.WATER));

                if (newUpperState.canPlaceAt(world, upperPos)) {
                    world.setBlockState(upperPos, newUpperState, Block.NOTIFY_ALL);
                    return;
                }
            }
        }

        this.applyGrowth(world, pos, state);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.get(HALF);

        if (direction.getAxis() == Direction.Axis.Y) {
            if (half == DoubleBlockHalf.UPPER) {
                if (direction == Direction.DOWN && (!neighborState.isOf(this) || neighborState.get(HALF) != DoubleBlockHalf.LOWER)) {
                    return Blocks.AIR.getDefaultState();
                }
            } else {
                if (direction == Direction.UP && neighborState.isOf(this) && neighborState.get(HALF) == DoubleBlockHalf.UPPER) {
                    return state;
                }
                if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
                    return Blocks.AIR.getDefaultState();
                }
            }
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();

        if (blockPos.getY() < world.getTopY() - 1 &&
                world.getBlockState(blockPos.up()).canReplace(context) &&
                isInWater(world, blockPos) &&
                canPlantOnTop(world.getBlockState(blockPos.down()), world, blockPos.down())) {
            return this.getDefaultState()
                    .with(WATERLOGGED, world.getFluidState(blockPos).isOf(Fluids.WATER));
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
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE, HALF, WATERLOGGED);
    }
}
