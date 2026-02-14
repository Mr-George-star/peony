package net.george.peony.api.interaction;

import net.minecraft.item.ItemStack;

public interface ComplexAccessibleInventory extends AccessibleInventory, InteractionFeatures {
    @Override
    default InteractionResult insert(InteractionContext context, ItemStack givenStack) {
        return InteractionResult.success(Consumption.none());
    }

    @Override
    default InteractionResult extract(InteractionContext context) {
        return InteractionResult.success(Consumption.none());
    }

    @Override
    default InteractionResult emptyUse(InteractionContext context) {
        return InteractionResult.success(Consumption.none());
    }
}
