package net.george.peony.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A simple {@code SidedInventory} implementation with only default methods + an item list getter.
 *
 * <h2>Reading and writing to tags</h2>
 * Use {@link Inventories#writeNbt(NbtCompound, DefaultedList, RegistryWrapper.WrapperLookup)} and
 * {@link Inventories#readNbt(NbtCompound, DefaultedList, RegistryWrapper.WrapperLookup)}
 * on {@linkplain #getItems() the item list}.
 * <p>
 * License: <a href="https://creativecommons.org/publicdomain/zero/1.0/">CC0</a>
 * @author Juuz
 */
public interface ImplementedInventory extends SidedInventory {
    /**
     * Gets the item list of this itemBeingMilled.
     * Must return the same instance every time it's called.
     *
     * @return the item list
     */
    DefaultedList<ItemStack> getItems();

    /**
     * Creates an itemBeingMilled from the item list.
     *
     * @param items the item list
     * @return a new itemBeingMilled
     */
    static ImplementedInventory of(DefaultedList<ItemStack> items) {
        return () -> items;
    }

    /**
     * Creates a new itemBeingMilled with the size.
     *
     * @param size the itemBeingMilled size
     * @return a new itemBeingMilled
     */
    static ImplementedInventory ofSize(int size) {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
    }

    // SidedInventory

    /**
     * Gets the available slots to automation on the side.
     *
     * <p>The default implementation returns an array of all slots.
     *
     * @param side the side
     * @return the available slots
     */
    @Override
    default int[] getAvailableSlots(Direction side) {
        int[] result = new int[getItems().size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }

        return result;
    }

    /**
     * Returns true if the stack can be inserted in the slot at the side.
     *
     * <p>The default implementation returns true.
     *
     * @param slot the slot
     * @param stack the stack
     * @param side the side
     * @return true if the stack can be inserted
     */
    @Override
    default boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return true;
    }

    /**
     * Returns true if the stack can be extracted from the slot at the side.
     *
     * <p>The default implementation returns true.
     *
     * @param slot the slot
     * @param stack the stack
     * @param side the side
     * @return true if the stack can be extracted
     */
    @Override
    default boolean canExtract(int slot, ItemStack stack, Direction side) {
        return true;
    }

    // Inventory

    /**
     * Returns the itemBeingMilled size.
     *
     * <p>The default implementation returns the size of {@link #getItems()}.
     *
     * @return the itemBeingMilled size
     */
    @Override
    default int size() {
        return getItems().size();
    }

    /**
     * @return true if this itemBeingMilled has only empty stacks, false otherwise
     */
    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the item in the slot.
     *
     * @param slot the slot
     * @return the item in the slot
     */
    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    /**
     * Takes a stack of the size from the slot.
     *
     * <p>(default implementation) If there are fewer items in the slot than what are requested,
     * takes all items in that slot.
     *
     * @param slot the slot
     * @param count the item count
     * @return a stack
     */
    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }

        return result;
    }

    /**
     * Removes the current stack in the {@code slot} and returns it.
     *
     * <p>The default implementation uses {@link Inventories#removeStack(List, int)}
     *
     * @param slot the slot
     * @return the removed stack
     */
    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    /**
     * Replaces the current stack in the {@code slot} with the provided stack.
     *
     * <p>If the stack is too big for this itemBeingMilled ({@link Inventory#getMaxCountPerStack()}),
     * it gets resized to this itemBeingMilled's maximum amount.
     *
     * @param slot the slot
     * @param stack the stack
     */
    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }

    /**
     * Clears {@linkplain #getItems() the item list}}.
     */
    @Override
    default void clear() {
        getItems().clear();
    }

    @Override
    default void markDirty() {
        // Override if you want behavior.
    }

    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
}
