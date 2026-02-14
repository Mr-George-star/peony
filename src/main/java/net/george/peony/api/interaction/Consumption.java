package net.george.peony.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.function.Consumer;
import java.util.function.Function;

public class Consumption {
    private final Consumer<ApplyContext> applier;

    private Consumption(Consumer<ApplyContext> applier) {
        this.applier = applier;
    }

    public static Consumption none() {
        return new Consumption(context -> {});
    }

    public static Consumption decrement(int amount) {
        return new Consumption(context -> context.heldStack.decrementUnlessCreative(amount, context.player));
    }

    public static Consumption all() {
        return new Consumption(context -> {
            if (!context.player.getAbilities().creativeMode) {
                context.player.setStackInHand(context.hand, ItemStack.EMPTY);
            }
        });
    }

    public static Consumption replace(ItemStack replacement) {
        return replace(stack -> replacement);
    }

    public static Consumption replace() {
        return replace(ItemReplacement::getReplacement);
    }

    public static Consumption replace(Function<ItemStack, ItemStack> replacementFunction) {
        return new Consumption(context -> {
            if (!context.player.getAbilities().creativeMode) {
                ItemStack replacement = replacementFunction.apply(context.player.getStackInHand(context.hand));
                context.player.setStackInHand(context.hand, replacement.copy());
            }
        });
    }

    public static Consumption decrementAndReplace(int amount) {
        return decrementAndReplace(amount, ItemReplacement::getReplacement);
    }

    public static Consumption decrementAndReplace(int amount, Function<ItemStack, ItemStack> replacementFunction) {
        return new Consumption(context -> {
            if (!context.player.getAbilities().creativeMode) {
                int actual = Math.min(amount, context.heldStack.getCount());
                ItemStack replacement = replacementFunction.apply(context.heldStack);
                context.heldStack.decrement(actual);

                if (!replacement.isEmpty()) {
                    ItemStack copy = replacement.copy();
                    copy.setCount(actual);

                    if (!context.player.getInventory().insertStack(copy)) {
                        context.player.dropItem(copy, false);
                    }
                }
            }
        });
    }

    public void apply(PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getStackInHand(hand);
        this.applier.accept(new ApplyContext(player, hand, heldStack));
    }

    private record ApplyContext(PlayerEntity player, Hand hand, ItemStack heldStack) {}
}
