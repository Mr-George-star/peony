package net.george.peony.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;

public class PizzaBlock extends HorizontalFacingBlock {
    public static final IntProperty EATEN_STAGE = IntProperty.of("eaten_stage", 0, 3);
    public static final MapCodec<PizzaBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    createSettingsCodec(),
                    Codec.BOOL.fieldOf("isEatable").forGetter(pizza -> pizza.isEatable),
                    createPieceCodec()
            ).apply(instance, PizzaBlock::new));
    public final boolean isEatable;
    protected final FoodComponent perPiece;

    public PizzaBlock(AbstractBlock.Settings settings, boolean isEatable, FoodComponent perPiece) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(EATEN_STAGE, 0));
        this.isEatable = isEatable;
        this.perPiece = perPiece;
    }

    @Override
    protected MapCodec<PizzaBlock> getCodec() {
        return CODEC;
    }

    protected static  <B extends PizzaBlock> RecordCodecBuilder<B, FoodComponent> createPieceCodec() {
        return FoodComponent.CODEC.fieldOf("perPiece").forGetter(pizza -> pizza.perPiece);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(1, 0, 1, 15, 1, 15);
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
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && this.isEatable) {
            HungerManager manager = player.getHungerManager();
            if (manager.isNotFull()) {
                int currentStage = state.get(EATEN_STAGE);

                manager.eat(this.perPiece);
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS,
                        0.5F, world.random.nextFloat() * 0.1F + 0.9F);
                if (currentStage < 3) {
                    BlockState newState = state.with(EATEN_STAGE, currentStage + 1);
                    world.setBlockState(pos, newState, Block.NOTIFY_ALL);
                } else {
                    world.breakBlock(pos, false);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @NotNull
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState()
                .with(FACING, context.getHorizontalPlayerFacing().getOpposite())
                .with(EATEN_STAGE, 0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, EATEN_STAGE);
    }
}
