package net.george.peony.api.interaction;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ItemReplacement {
    private static final ItemApiLookup<Replacement, Void> REPLACEMENTS =
            ItemApiLookup.get(Identifier.of("peony-interaction"), Replacement.class, Void.class);

    public static ItemStack getReplacement(ItemStack target) {
        Replacement replacement = REPLACEMENTS.find(target, null);
        if (replacement == null) {
            return target.getRecipeRemainder();
        } else {
            return replacement.get().getDefaultStack();
        }
    }

    public static void registerReplacement(ItemConvertible target, ItemConvertible replaced) {
        REPLACEMENTS.registerForItems((givenStack, context) -> replaced::asItem, target);
    }

    @FunctionalInterface
    public interface Replacement extends Supplier<Item> {
        @Override
        Item get();
    }
}
