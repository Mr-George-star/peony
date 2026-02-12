package net.george.peony.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.world.World;
import org.apache.commons.lang3.function.TriFunction;

/**
 * Defines how an item stack should be modified
 * after a successful interaction.
 *
 * <p>This allows flexible handling of:
 * <ul>
 *     <li>Full consumption</li>
 *     <li>Partial decrement</li>
 *     <li>Item exchange (e.g. bucket → empty bucket)</li>
 * </ul>
 */
@FunctionalInterface
public interface ItemDecrementBehaviour extends TriFunction<World, PlayerEntity, Hand, Unit> {
    /**
     * Applies the decrement logic.
     *
     * @param world world instance
     * @param user  player
     * @param hand  used hand
     */
    void effective(World world, PlayerEntity user, Hand hand);

    /**
     * Delegates to {@link #effective}.
     *
     * @return {@link Unit#INSTANCE}
     */
    @Override
    default Unit apply(World world, PlayerEntity user, Hand hand) {
        this.effective(world, user, hand);
        return Unit.INSTANCE;
    }

    /**
     * Creates a decrement behavior using {@link ItemExchangeBehaviour}
     * associated with the held item.
     *
     * <p>This supports item replacement patterns such as:<br>
     * bucket → empty bucket.
     *
     * @return decrement behavior
     */
    static ItemDecrementBehaviour createDefault() {
        return (world, user, hand) -> {
            ItemStack heldStack = user.getStackInHand(hand);
            user.setStackInHand(hand, ItemExchangeBehaviour.get(heldStack.getItem()).exchange(world, user, heldStack));
        };
    }

    /**
     * Creates a decrement behavior that removes the entire held stack,
     * except in creative mode.
     *
     * @return decrement behavior
     */
    static ItemDecrementBehaviour createAllConsumed() {
        return (world, user, hand) -> {
            if (!user.getAbilities().creativeMode) {
                user.setStackInHand(hand, ItemStack.EMPTY);
            }
        };
    }

    /**
     * Creates a decrement behavior that reduces
     * the held stack by a specific amount.
     *
     * @param decrement amount to reduce
     * @return decrement behavior
     */
    static ItemDecrementBehaviour createDecreaseSpecified(int decrement) {
        return (world, user, hand) -> {
            ItemStack heldStack = user.getStackInHand(hand);
            user.setStackInHand(hand, ItemExchangeBehaviour.createDefaultWithCount(decrement).exchange(world, user, heldStack));
        };
    }

    static ItemDecrementBehaviour createCuttingBoard(CuttingBoardBlockEntity board) {
        boolean previous = board.hasPlacedIngredient();
        return (world, user, hand) -> {
            ItemStack heldStack = user.getStackInHand(hand);
            if (!previous && board.hasPlacedIngredient()) {
                user.setStackInHand(hand, ItemExchangeBehaviour.get(heldStack.getItem()).exchange(world, user, heldStack));
                return;
            }
            ItemExchangeBehaviour.DEFAULT.exchange(world, user, heldStack);
        };
    }
}
