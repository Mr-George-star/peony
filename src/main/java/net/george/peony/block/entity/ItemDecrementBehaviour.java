package net.george.peony.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.world.World;
import org.apache.commons.lang3.function.TriFunction;

@FunctionalInterface
public interface ItemDecrementBehaviour extends TriFunction<World, PlayerEntity, Hand, Unit> {
    void effective(World world, PlayerEntity user, Hand hand);

    @Override
    default Unit apply(World world, PlayerEntity user, Hand hand) {
        this.effective(world, user, hand);
        return Unit.INSTANCE;
    }

    static ItemDecrementBehaviour createDefault() {
        return (world, user, hand) -> {
            if (!user.getAbilities().creativeMode) {
                user.setStackInHand(hand, ItemStack.EMPTY);
            }
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
