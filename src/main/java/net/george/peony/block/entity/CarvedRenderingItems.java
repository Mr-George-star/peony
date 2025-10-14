package net.george.peony.block.entity;

import net.minecraft.item.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public class CarvedRenderingItems {
    private static final CarvedRenderingItems INSTANCE = new CarvedRenderingItems();
    private final Map<Item, Float> valueMap = new HashMap<>();

    private CarvedRenderingItems() {}

    public static CarvedRenderingItems getInstance() {
        return INSTANCE;
    }

    public void register(Item item, float angle) {
        this.valueMap.put(item, angle);
    }

    public boolean contains(Item item) {
        return this.valueMap.containsKey(item);
    }

    public Optional<Float> get(Item item) {
        return this.contains(item) ? Optional.of(this.valueMap.get(item)) : Optional.empty();
    }

    public Map<Item, Float> getAll() {
        return Collections.unmodifiableMap(this.valueMap);
    }
}
