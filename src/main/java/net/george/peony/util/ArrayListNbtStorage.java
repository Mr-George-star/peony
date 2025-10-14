package net.george.peony.util;

import net.george.peony.Peony;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ArrayListNbtStorage {
    public static void writeItemList(NbtCompound nbt, String key, ArrayList<ItemStack> itemList, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList nbtList = new NbtList();

        for (ItemStack stack : itemList) {
            NbtCompound stackNbt = new NbtCompound();
            ItemStack.CODEC.encodeStart(registryLookup.getOps(NbtOps.INSTANCE), stack)
                    .resultOrPartial(error ->
                            Peony.LOGGER.error("Failed to encode ItemStack: " + error)
                    ).ifPresent(encoded -> {
                        if (encoded instanceof NbtCompound compound) {
                            stackNbt.copyFrom(compound);
                        } else {
                            nbtList.add(encoded);
                        }
                    });

            if (!stackNbt.isEmpty()) {
                nbtList.add(stackNbt);
            }
        }

        nbt.put(key, nbtList);
    }

    public static ArrayList<ItemStack> readItemList(NbtCompound nbt, String key, RegistryWrapper.WrapperLookup registryLookup) {
        ArrayList<ItemStack> itemList = new ArrayList<>();

        if (!nbt.contains(key)) {
            return itemList;
        }

        NbtElement element = nbt.get(key);
        if (element instanceof NbtList nbtList) {
            for (NbtElement itemElement : nbtList) {
                ItemStack.CODEC.parse(registryLookup.getOps(NbtOps.INSTANCE), itemElement)
                        .resultOrPartial(error ->
                                Peony.LOGGER.error("Failed to decode ItemStack: " + error)
                        ).ifPresent(itemList::add);
            }
        }

        return itemList;
    }

    public static <T> void replaceList(List<T> target, List<T> source, boolean keepRemaining) {
        int minSize = Math.min(target.size(), source.size());

        for (int i = 0; i < minSize; i++) {
            target.set(i, source.get(i));
        }

        if (source.size() > target.size()) {
            for (int i = target.size(); i < source.size(); i++) {
                target.add(source.get(i));
            }
        } else if (target.size() > source.size() && !keepRemaining) {
            target.subList(source.size(), target.size()).clear();
        }
    }

    public static void writeDefaultedList(NbtCompound nbt, String key, DefaultedList<ItemStack> itemList, RegistryWrapper.WrapperLookup registryLookup) {
        writeItemList(nbt, key, new ArrayList<>(itemList), registryLookup);
    }

    public static DefaultedList<ItemStack> readDefaultedList(NbtCompound nbt, String key, int size, RegistryWrapper.WrapperLookup registryLookup) {
        ArrayList<ItemStack> arrayList = readItemList(nbt, key, registryLookup);
        DefaultedList<ItemStack> result = DefaultedList.ofSize(size, ItemStack.EMPTY);
        for (int i = 0; i < Math.min(arrayList.size(), size); i++) {
            result.set(i, arrayList.get(i));
        }
        return result;
    }
}
