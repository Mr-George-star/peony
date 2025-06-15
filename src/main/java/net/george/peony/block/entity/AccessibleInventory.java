package net.george.peony.block.entity;

import net.george.peony.block.data.ItemDecrementBehaviour;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public interface AccessibleInventory {
    boolean insertItem(World world, PlayerEntity user, Hand hand, ItemStack givenStack);

    boolean extractItem(World world, PlayerEntity user, Hand hand);

    default boolean onUseWithEmptyHand(World world, PlayerEntity user, Hand hand) {
        return false;
    }

    static ItemActionResult access(AccessibleInventory entity, World world, BlockPos pos, PlayerEntity user, Hand hand) {
        return access(entity, world, pos, user, hand, ItemDecrementBehaviour.createDefault());
    }

    static ItemActionResult access(AccessibleInventory entity, World world, BlockPos pos, PlayerEntity user, Hand hand, ItemDecrementBehaviour behaviour) {
        ItemStack heldStack = user.getStackInHand(hand);
        if (!heldStack.isEmpty()) {
            if (entity.insertItem(world, user, hand, heldStack)) {
                world.playSound(user, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1F, 2F);
                behaviour.interact(world, user, hand);
                return ItemActionResult.SUCCESS;
            }
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else {
            if (user.isSneaking()) {
                if (entity.extractItem(world, user, hand)) {
                    world.playSound(user, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1F, 1F);
                    return ItemActionResult.SUCCESS;
                }
                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            } else {
                if (entity.onUseWithEmptyHand(world, user, hand)) {
                    return ItemActionResult.SUCCESS;
                } else {
                    return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
            }
        }
    }
}
