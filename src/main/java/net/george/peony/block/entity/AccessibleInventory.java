package net.george.peony.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface AccessibleInventory {
    boolean insertItem(World world, PlayerEntity user, Hand hand, ItemStack givenStack, boolean isSneaking);

    boolean extractItem(World world, PlayerEntity user, Hand hand);

    default boolean useEmptyHanded(World world, PlayerEntity user, BlockPos pos, Hand hand) {
        return false;
    }

    static ItemActionResult access(AccessibleInventory entity, World world, BlockPos pos, PlayerEntity user, Hand hand) {
        return access(entity, world, pos, user, hand, ItemDecrementBehaviour.createDefault());
    }

    static ItemActionResult access(AccessibleInventory entity, World world, BlockPos pos, PlayerEntity user, Hand hand, ItemDecrementBehaviour behaviour) {
        ItemStack heldStack = user.getStackInHand(hand);
        boolean isSneaking = user.isSneaking();
        if (!heldStack.isEmpty()) {
            if (entity.insertItem(world, user, hand, heldStack, isSneaking)) {
                world.playSound(user, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1F, 2F);
                behaviour.effective(world, user, hand);
                user.incrementStat(Stats.USED.getOrCreateStat(heldStack.getItem()));
                return ItemActionResult.SUCCESS;
            }
            return ItemActionResult.FAIL;
        } else {
            if (isSneaking) {
                if (entity.extractItem(world, user, hand)) {
                    world.playSound(user, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1F, 1F);
                    return ItemActionResult.SUCCESS;
                }
                return ItemActionResult.FAIL;
            } else {
                if (entity.useEmptyHanded(world, user, pos, hand)) {
                    world.playSound(user, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1F, 1F);
                    return ItemActionResult.SUCCESS;
                } else {
                    return ItemActionResult.FAIL;
                }
            }
        }
    }
}
