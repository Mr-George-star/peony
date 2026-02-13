package net.george.peony.api.interaction;

import net.minecraft.item.ItemStack;

public interface AccessibleInventory {
    /**
     * Attempts to insert items into this inventory.
     *
     * @param context    interaction context
     * @param givenStack stack to insert (can be modified copy)
     * @return result describing success and consumption behavior
     */
    InteractionResult insert(InteractionContext context, ItemStack givenStack);

    /**
     * Attempts to extract items.
     */
    InteractionResult extract(InteractionContext context);

    /**
     * Called when player right-clicks with empty hand and not sneaking.
     */
    InteractionResult emptyUse(InteractionContext context);
}
