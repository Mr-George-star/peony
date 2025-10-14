package net.george.peony.block.entity;

import net.minecraft.item.Item;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public class NonBlockRenderingItems {
    private static final NonBlockRenderingItems INSTANCE = new NonBlockRenderingItems();
    private final Set<Item> items = new HashSet<>();

    private NonBlockRenderingItems() {}

    public static NonBlockRenderingItems getInstance() {
        return INSTANCE;
    }

    public void register(Item item) {
        this.items.add(item);
    }

    public boolean contains(Item item) {
        return this.items.contains(item);
    }

    public Set<Item> getAll() {
        return Collections.unmodifiableSet(this.items);
    }
}
