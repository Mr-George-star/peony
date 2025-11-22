package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import net.george.peony.item.PeonyItems;
import net.george.peony.sound.PeonySoundEvents;
import net.george.peony.util.PeonyTags;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class TomatoVinesBlock extends CropBlock {
    public static final MapCodec<TomatoVinesBlock> CODEC = createCodec(TomatoVinesBlock::new);
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;

    public TomatoVinesBlock(Settings settings) {
        super(settings.ticksRandomly());
        this.setDefaultState(this.getDefaultState().with(AGE, 0));
    }

    @Override
    public MapCodec<TomatoVinesBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return PeonyItems.TOMATO_SEEDS;
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
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return !(world.getBlockState(pos.up()).getBlock() instanceof TomatoVinesBlock);
    }

    @Override
    protected int getGrowthAmount(World world) {
        return world.random.nextBetween(1, 2);
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int currentAge = this.getAge(state);
        int newAge = Math.min(this.getMaxAge(), currentAge + this.getGrowthAmount(world));

        if (newAge > currentAge) {
            world.setBlockState(pos, state.with(AGE, newAge), Block.NOTIFY_LISTENERS);

            if (newAge == this.getMaxAge() && this.canSpread(world, pos)) {
                this.trySpread(world, pos);
            }
        } else if (currentAge == this.getMaxAge() && this.canSpread(world, pos)) {
            this.trySpread(world, pos);
        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBaseLightLevel(pos, 0) >= 8) {
            int currentAge = this.getAge(state);

            if (currentAge < this.getMaxAge()) {
                if (random.nextInt(5) == 0) {
                    this.grow(world, random, pos, state);
                }
            } else if (currentAge == this.getMaxAge() &&
                    random.nextInt(10) == 0 &&
                    !(world.getBlockState(pos.up()).getBlock() instanceof TomatoVinesBlock) &&
                    this.canSpread(world, pos)) {
                this.trySpread(world, pos);
            }
        }
    }

    private boolean canSpread(WorldView world, BlockPos pos) {
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);

        if (!aboveState.isAir()) {
            return false;
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos checkPos = abovePos.offset(direction);
            BlockState neighborState = world.getBlockState(checkPos);

            if (neighborState.isIn(PeonyTags.Blocks.VINE_CROPS_ATTACHABLE)) {
                return true;
            }
        }
        return false;
    }

    private void trySpread(ServerWorld world, BlockPos pos) {
        BlockPos abovePos = pos.up();
        if (world.getBlockState(abovePos).isAir()) {
            world.setBlockState(abovePos, this.getDefaultState(), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof ShearsItem) {
            if (!world.isClient()) {
                player.giveItemStack(new ItemStack(PeonyItems.TOMATO));
                world.setBlockState(pos, this.getDefaultState());
                world.playSound(player, player.getX(), player.getY(), player.getZ(), PeonySoundEvents.BLOCK_SHEAR_USING, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(player, GameEvent.SHEAR, pos);
                return ItemActionResult.SUCCESS;
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return !state.canPlaceAt(world, pos) ?
                Blocks.AIR.getDefaultState() :
                super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return this.isTomatoVinesAvailable(world, pos) && hasEnoughLightAt(world, pos);
    }

    protected static boolean hasEnoughLightAt(WorldView world, BlockPos pos) {
        return world.getBaseLightLevel(pos, 0) >= 8;
    }

    private boolean isTomatoVinesAvailable(WorldView world, BlockPos pos) {
        BlockPos belowPos = pos.down();
        BlockState belowState = world.getBlockState(belowPos);

        if (this.canPlantOnTop(belowState, world, belowPos)) {
            return true;
        }

        int vineCount = 0;
        int maxVineHeight = 9;
        BlockPos currentPos = belowPos;

        while (vineCount < maxVineHeight && currentPos.getY() >= world.getBottomY()) {
            BlockState currentState = world.getBlockState(currentPos);

            if (currentState.getBlock() instanceof TomatoVinesBlock) {
                vineCount++;
                currentPos = currentPos.down();
            } else {
                return this.canPlantOnTop(currentState, world, currentPos);
            }
        }

        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}
