package net.george.peony.item;

import net.george.peony.Peony;
import net.george.peony.PeonyItemGroups;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.util.DoubleParamsFunction;
import net.minecraft.block.Block;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

@SuppressWarnings("UnusedReturnValue")
public class PeonyItems {
    public static final Item BARLEY = register("barley", Item::new, createDefaultSettings());
    public static final Item BARLEY_SEEDS = register("barley_seeds", settings ->
            new AliasedBlockItem(PeonyBlocks.BARLEY_CROP, settings), createDefaultSettings());

    public static final Item PLACEHOLDER = register("placeholder", Item::new, createDefaultSettings(), false);

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        return register(name, itemFactory, settings, true);
    }

    public static Item register(String name, DoubleParamsFunction<Item.Settings, Block, Item> itemFactory, Item.Settings settings, Block block) {
        return Registry.register(Registries.ITEM, key(name), itemFactory.apply(settings, block));
    }

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings, boolean shouldAddToItemGroup) {
        Item instance = Registry.register(Registries.ITEM, key(name), itemFactory.apply(settings));
        if (shouldAddToItemGroup) {
            PeonyItemGroups.ITEM_LIST.add(instance);
        }
        return instance;
    }

    public static Item.Settings createDefaultSettings() {
        return new Item.Settings();
    }

    public static RegistryKey<Item> key(String name) {
        return Peony.key(RegistryKeys.ITEM, name);
    }

    public static void register() {
        Peony.debug("Items");
    }
}
