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

@FunctionalInterface
public interface ItemExchangeBehaviour {
    Map<Item, ItemExchangeBehaviour> BEHAVIOURS = new HashMap<>();
    ItemExchangeBehaviour DEFAULT = createDefaultWithCount(1);

    ItemStack exchange(World world, PlayerEntity player, ItemStack stack);

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

    static ItemExchangeBehaviour get(Item item) {
        return getOrElse(item, DEFAULT);
    }

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
                ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
    }
}
