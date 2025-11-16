package net.george.peony.util;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BlockPlacements {
    public static ActionResult placeBlock(ItemPlacementContext context, Block block) {
        if (!block.isEnabled(context.getWorld().getEnabledFeatures())) {
            return ActionResult.FAIL;
        } else if (!context.canPlace()) {
            return ActionResult.FAIL;
        } else {
            BlockState blockState = getPlacementState(block, context);
            if (blockState == null) {
                return ActionResult.FAIL;
            } else if (!place(context, blockState)) {
                return ActionResult.FAIL;
            } else {
                BlockPos pos = context.getBlockPos();
                World world = context.getWorld();
                PlayerEntity player = context.getPlayer();
                ItemStack stack = context.getStack();
                BlockState pointedState = world.getBlockState(pos);
                if (pointedState.isOf(blockState.getBlock())) {
                    pointedState = placeFromNbt(pos, world, stack, pointedState);
                    BlockItem.writeNbtToBlockEntity(world, player, pos, stack);
                    copyComponentsToBlockEntity(world, pos, stack);
                    pointedState.getBlock().onPlaced(world, pos, pointedState, player, stack);
                    if (player instanceof ServerPlayerEntity) {
                        Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, stack);
                    }
                }

                BlockSoundGroup group = pointedState.getSoundGroup();
                world.playSound(player, pos, pointedState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, (group.getVolume() + 1.0F) / 2.0F, group.getPitch() * 0.8F);
                world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(player, pointedState));
                stack.decrementUnlessCreative(1, player);
                return ActionResult.success(world.isClient);
            }
        }
    }

    public static BlockState getPlacementState(Block block, ItemPlacementContext context) {
        BlockState blockState = block.getPlacementState(context);
        return blockState != null && canPlace(context, blockState) ? blockState : null;
    }

    public static boolean canPlace(ItemPlacementContext context, BlockState state) {
        PlayerEntity playerEntity = context.getPlayer();
        ShapeContext shapeContext = playerEntity == null ? ShapeContext.absent() : ShapeContext.of(playerEntity);
        return state.canPlaceAt(context.getWorld(), context.getBlockPos()) && context.getWorld().canPlace(state, context.getBlockPos(), shapeContext);
    }

    public static boolean place(ItemPlacementContext context, BlockState state) {
        return context.getWorld().setBlockState(context.getBlockPos(), state, 11);
    }

    public static BlockState placeFromNbt(BlockPos pos, World world, ItemStack stack, BlockState state) {
        BlockStateComponent blockStateComponent = stack.getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT);
        if (blockStateComponent.isEmpty()) {
            return state;
        } else {
            BlockState blockState = blockStateComponent.applyToState(state);
            if (blockState != state) {
                world.setBlockState(pos, blockState, 2);
            }

            return blockState;
        }
    }

    public static void copyComponentsToBlockEntity(World world, BlockPos pos, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.readComponents(stack);
            blockEntity.markDirty();
        }
    }
}
