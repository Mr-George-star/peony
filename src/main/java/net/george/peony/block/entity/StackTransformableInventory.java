package net.george.peony.block.entity;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface StackTransformableInventory extends ImplementedInventory {
    int[][] AVAILABLE_SLOTS_CACHE = new int[54][];

    Direction getCurrentDirection();

    default boolean insert(@Nullable Inventory to, ItemStack stack) {
        return insert(this, to, stack);
    }

    static boolean insert(StackTransformableInventory from, @Nullable Inventory to, ItemStack stack) {
        if (to != null) {
            Direction direction = from.getCurrentDirection().getOpposite();
            if (!isInventoryFull(to, direction)) {
                if (!stack.isEmpty()) {
                    ItemStack transferred = transfer(to, stack, direction);
                    if (transferred.isEmpty()) {
                        to.markDirty();
                        return true;
                    }
                }

            }
        }
        return false;
    }

    static ItemStack transfer(Inventory to, ItemStack stack, int slot, @Nullable Direction side) {
        ItemStack itemStack = to.getStack(slot);
        if (canInsert(to, stack, slot, side)) {
            if (itemStack.isEmpty()) {
                to.setStack(slot, stack);
                stack = ItemStack.EMPTY;
            } else if (canMergeItems(itemStack, stack)) {
                int difference = stack.getMaxCount() - itemStack.getCount();
                int count = Math.min(stack.getCount(), difference);
                stack.decrement(count);
                itemStack.increment(count);
            }
        }

        return stack;
    }


    static ItemStack transfer(Inventory to, ItemStack stack, @Nullable Direction side) {
        int slotIndex;
        if (to instanceof SidedInventory sidedInventory) {
            if (side != null) {
                int[] availableSlots = sidedInventory.getAvailableSlots(side);

                for (slotIndex = 0; slotIndex < availableSlots.length && !stack.isEmpty(); ++slotIndex) {
                    stack = transfer(to, stack, availableSlots[slotIndex], side);
                }

                return stack;
            }
        }

        int size = to.size();

        for (slotIndex = 0; slotIndex < size && !stack.isEmpty(); ++slotIndex) {
            stack = transfer(to, stack, slotIndex, side);
        }

        return stack;
    }
    
    static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
        if (!inventory.isValid(slot, stack)) {
            return false;
        } else {
            if (inventory instanceof SidedInventory sidedInventory) {
                return sidedInventory.canInsert(slot, stack, side);
            }
            return true;
        }
    }

    static boolean canMergeItems(ItemStack first, ItemStack second) {
        return first.getCount() <= first.getMaxCount() && ItemStack.areItemsAndComponentsEqual(first, second);
    }

    static int[] getAvailableSlots(Inventory inventory, Direction side) {
        if (inventory instanceof SidedInventory sidedInventory) {
            return sidedInventory.getAvailableSlots(side);
        } else {
            int i = inventory.size();
            if (i < AVAILABLE_SLOTS_CACHE.length) {
                int[] slots = AVAILABLE_SLOTS_CACHE[i];
                if (slots != null) {
                    return slots;
                } else {
                    int[] slotData = indexArray(i);
                    AVAILABLE_SLOTS_CACHE[i] = slotData;
                    return slotData;
                }
            } else {
                return indexArray(i);
            }
        }
    }

    static boolean isInventoryFull(Inventory inventory, Direction direction) {
        int[] slots = getAvailableSlots(inventory, direction);

        for (int slot : slots) {
            ItemStack itemStack = inventory.getStack(slot);
            if (itemStack.getCount() < itemStack.getMaxCount()) {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    static int[] indexArray(int size) {
        int[] indexes = new int[size];

        for (int i = 0; i < indexes.length; indexes[i] = i++) {
        }

        return indexes;
    }

    default boolean canItemStacksBeStacked(ItemStack basicStack, ItemStack givenStack) {
        if (ItemStack.areItemsEqual(basicStack, givenStack)) {
            int count = basicStack.getCount() +  givenStack.getCount();
            return count <= basicStack.getMaxCount();
        } else {
            return false;
        }
    }
}
