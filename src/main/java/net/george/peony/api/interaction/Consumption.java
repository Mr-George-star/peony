package net.george.peony.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.function.Function;

public class Consumption {
    private final Mode mode;
    private final int amount;
    private final Function<ItemStack, ItemStack> replacementFunction;

    private Consumption(Mode mode, int amount, ItemStack replacement) {
        this(mode, amount, stack -> replacement);
    }

    private Consumption(Mode mode, int amount, Function<ItemStack, ItemStack> replacementFunction) {
        this.mode = mode;
        this.amount = amount;
        this.replacementFunction = replacementFunction;
    }

    public static Consumption none() {
        return new Consumption(Mode.NONE, 0, ItemStack.EMPTY);
    }

    public static Consumption decrement(int amount) {
        return new Consumption(Mode.DECREMENT, amount, ItemStack.EMPTY);
    }

    public static Consumption all() {
        return new Consumption(Mode.ALL, 0, ItemStack.EMPTY);
    }

    public static Consumption replace(ItemStack replacement) {
        return new Consumption(Mode.REPLACE, 1, replacement);
    }

    public static Consumption replace() {
        return replace(ItemReplacement::getReplacement);
    }

    public static Consumption replace(Function<ItemStack, ItemStack> replacementFunction) {
        return new Consumption(Mode.REPLACE, 1, replacementFunction);
    }

    public void apply(PlayerEntity player, Hand hand) {
        if (player.getAbilities().creativeMode) {
            return;
        }

        ItemStack held = player.getStackInHand(hand);

        switch (this.mode) {
            case NONE -> {}
            case DECREMENT -> held.decrement(this.amount);
            case ALL -> player.setStackInHand(hand, ItemStack.EMPTY);
            case REPLACE -> player.setStackInHand(hand, this.replacementFunction.apply(held));
        }
    }

    public enum Mode {
        NONE,
        DECREMENT,
        ALL,
        REPLACE,
        DYNAMIC_REPLACE
    }
}
