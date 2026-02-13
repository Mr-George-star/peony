package net.george.peony.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.ItemActionResult;

public class InventoryInteraction {
    public static ItemActionResult interact(AccessibleInventory inventory, InteractionContext context) {
        ItemStack heldStack = context.user.getStackInHand(context.hand);
        InteractionResult result;

        if (!heldStack.isEmpty()) {
            int insertAmount = 1;

            if (context.user.isSneaking()
                    && inventory instanceof InteractionFeatures features
                    && features.supportsBulkInsert()) {

                insertAmount = heldStack.getCount();
            }

            result = inventory.insert(
                    context,
                    heldStack.copyWithCount(insertAmount)
            );
        } else if (context.user.isSneaking()) {
            result = inventory.extract(context);
        } else {
            result = inventory.emptyUse(context);
        }
        
        if (result.isSuccess()) {
            result.getConsumption().apply(context.user, context.hand);
            increaseUsageStat(context.user, heldStack);
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

    /**
     * Plays a usage sound at the interaction position.
     *
     * @param context interaction context
     * @param sound   sound event
     * @param volume  sound volume
     * @param pitch   sound pitch
     */
    public static void playUsageSound(InteractionContext context, SoundEvent sound, float volume, float pitch) {
        context.world.playSound(context.user, context.pos, sound, SoundCategory.BLOCKS, volume, pitch);
    }
}
