package net.george.peony.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.ItemActionResult;

public class InventoryInteraction {
    public static ItemActionResult interact(AccessibleInventory inventory, InteractionContext context) {
        ItemStack heldStack = context.user.getStackInHand(context.hand);
        InteractionResult result;

        if (!heldStack.isEmpty()) {
            result = inventory.insert(
                    context,
                    heldStack.copy()
            );
        } else if (context.user.isSneaking()) {
            result = inventory.extract(context);
        } else {
            result = inventory.emptyUse(context);
        }
        
        if (result instanceof InteractionResult.Success success) {
            success.getConsumption().apply(context.user, context.hand);

            InteractionSound sound = success.getSound();
            if (sound != null) {
                sound.play(context.world, context.pos);
            } else if (inventory instanceof InteractionFeatures features) {
                features.getInteractionSound().play(context.world, context.pos);
            } else {
                InteractionSound.DEFAULT.play(context.world, context.pos);
            }

            increaseUsageStat(context.user, context.user.getStackInHand(context.hand));
            return ItemActionResult.SUCCESS;
        } else {
            return ItemActionResult.FAIL;
        }
    }

    /**
     * Increments the player's usage statistic for the given item.
     *
     * @param user  player
     * @param stack used item stack
     */
    public static void increaseUsageStat(PlayerEntity user, ItemStack stack) {
        user.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
    }
}
