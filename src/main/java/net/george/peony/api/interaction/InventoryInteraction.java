package net.george.peony.api.interaction;

import net.george.peony.api.interaction.effect.InteractionEffect;
import net.george.peony.api.interaction.effect.InteractionSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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

            InteractionEffect effect = success.getEffects();
            if (effect != null && context.user instanceof ServerPlayerEntity serverPlayer) {
                effect.apply(serverPlayer, context.hand, context.world, context.pos);
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
