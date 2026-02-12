package net.george.peony.block.entity;

import net.george.peony.item.PeonyItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines how an item stack should be transformed
 * after being consumed.
 *
 * <p>Used to support replacement logic such as:
 * <ul>
 *     <li>Potion → Glass Bottle</li>
 *     <li>Custom bucket → Empty bucket</li>
 * </ul>
 */
@FunctionalInterface
public interface ItemExchangeBehaviour {
    Map<Item, ItemExchangeBehaviour> BEHAVIOURS = new HashMap<>();
    ItemExchangeBehaviour DEFAULT = createDefaultWithCount(1);

    /**
     * Performs the item exchange logic.
     *
     * @param world  world instance
     * @param player player performing the action
     * @param stack  original stack
     * @return resulting stack to place in hand
     */
    ItemStack exchange(World world, PlayerEntity player, ItemStack stack);

    /**
     * Creates a default exchange behavior
     * that decrements the stack by a specified count.
     *
     * <p>Respects creative mode.
     *
     * @param count amount to decrement
     * @return exchange behavior
     */
    static ItemExchangeBehaviour createDefaultWithCount(int count) {
        return (world, player, stack) -> {
            stack.decrementUnlessCreative(count, player);
            if (!stack.isEmpty()) {
                return stack;
            } else {
                return ItemStack.EMPTY;
            }
        };
    }

    static void register(Item item, ItemExchangeBehaviour behaviour) {
        BEHAVIOURS.put(item, behaviour);
    }

    /**
     * Retrieves the registered exchange behavior for the given item.
     *
     * <p>Falls back to {@link #DEFAULT} if none is registered.
     *
     * @param item target item
     * @return exchange behavior
     */
    static ItemExchangeBehaviour get(Item item) {
        return getOrElse(item, DEFAULT);
    }

    /**
     * Retrieves the registered exchange behavior,
     * or returns the provided fallback behavior.
     *
     * @param item      target item
     * @param behaviour fallback behavior
     * @return exchange behavior
     */
    static ItemExchangeBehaviour getOrElse(Item item, ItemExchangeBehaviour behaviour) {
        return BEHAVIOURS.getOrDefault(item, behaviour);
    }

    static void registerBehaviours() {
        register(Items.POTION, (world, player, stack) -> {
            PotionContentsComponent component = stack.get(DataComponentTypes.POTION_CONTENTS);
            if (component != null && component.matches(Potions.WATER)) {
                return ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE));
            } else {
                return DEFAULT.exchange(world, player, stack);
            }
        });
        register(PeonyItems.BLACK_VINEGAR, (world, player, stack) ->
                ItemUsage.exchangeStack(stack, player, new ItemStack(PeonyItems.CONDIMENT_BOTTLE)));
        register(PeonyItems.SWEET_SOUR_SAUCE, (world, player, stack) ->
                ItemUsage.exchangeStack(stack, player, new ItemStack(PeonyItems.CONDIMENT_BOTTLE)));
        register(PeonyItems.SOY_SAUCE, (world, player, stack) ->
                ItemUsage.exchangeStack(stack, player, new ItemStack(PeonyItems.CONDIMENT_BOTTLE)));

        register(PeonyItems.NATURE_GAS_BUCKET, (world, player, stack) ->
                ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
    }
}
