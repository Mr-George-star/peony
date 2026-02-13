package net.george.peony.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ItemActionResult;

public class InventoryInteraction {
    public static ItemActionResult interact(AccessibleInventory inventory, InteractionContext context) {
        ItemStack heldStack = context.user.getStackInHand(context.hand);
        InteractionResult result;
        boolean inserting = false;
        boolean extracting = false;

        if (!heldStack.isEmpty()) {
            int insertAmount = 1;

            if (context.user.isSneaking()
                    && inventory instanceof InteractionFeatures features
                    && features.supportsBulkInsert()) {

                insertAmount = heldStack.getCount();
            }

            inserting = true;
            result = inventory.insert(
                    context,
                    heldStack.copyWithCount(insertAmount)
            );
        } else if (context.user.isSneaking()) {
            extracting = true;
            result = inventory.extract(context);
        } else {
            result = inventory.emptyUse(context);
        }
        
        if (result.isSuccess()) {
            result.getConsumption().apply(context.user, context.hand);
            increaseUsageStat(context.user, heldStack);
            playInteractionSound(inventory, context, inserting, extracting);
            return ItemActionResult.SUCCESS;
        } else {
            return ItemActionResult.FAIL;
        }
    }

    private static void playInteractionSound(AccessibleInventory inventory, InteractionContext context, boolean inserting, boolean extracting) {
        SoundEvent sound;

        if (inventory instanceof InteractionSoundProvider provider) {
            if (inserting) {
                sound = provider.getInsertSound();
            } else if (extracting) {
                sound = provider.getExtractSound();
            } else {
                sound = provider.getInsertSound();
            }

            context.world.playSound(
                    null,
                    context.pos,
                    sound,
                    SoundCategory.BLOCKS,
                    provider.getSoundVolume(),
                    provider.getSoundPitch());

        } else {
            context.world.playSound(
                    null,
                    context.pos,
                    SoundEvents.ENTITY_ITEM_PICKUP,
                    SoundCategory.BLOCKS,
                    0.4F,
                    1.0F
            );
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
