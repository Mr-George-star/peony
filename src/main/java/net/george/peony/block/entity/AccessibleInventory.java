package net.george.peony.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface AccessibleInventory {
    boolean insertItem(World world, PlayerEntity user, Hand hand, ItemStack givenStack);

    boolean extractItem(World world, PlayerEntity user, Hand hand);

    static ItemActionResult access(AccessibleInventory entity, World world, BlockPos pos, PlayerEntity user, Hand hand) {
        ItemStack heldStack = user.getStackInHand(hand);
        if (!heldStack.isEmpty()) {
            if (entity.insertItem(world, user, hand, heldStack)) {
                world.playSound(user, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1F, 2F);
                if (!user.getAbilities().creativeMode) {
                    user.setStackInHand(hand, ItemStack.EMPTY);
                }
                return ItemActionResult.SUCCESS;
            }
            return ItemActionResult.CONSUME;
        } else {
            if (user.isSneaking()) {
                if (entity.extractItem(world, user, hand)) {
                    world.playSound(user, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1F, 1F);
                    return ItemActionResult.SUCCESS;
                }
                return ItemActionResult.CONSUME;
            } else {
                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }
    }
}
