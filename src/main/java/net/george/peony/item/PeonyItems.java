package net.george.peony.item;

import net.george.peony.Peony;
import net.george.peony.PeonyItemGroups;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.fluid.PeonyFluids;
import net.minecraft.block.Block;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("UnusedReturnValue")
public class PeonyItems {
    public static final Item BARLEY = register("barley", Item::new, createDefaultSettings());
    public static final Item BARLEY_SEEDS = register("barley_seeds", settings ->
            new AliasedBlockItem(PeonyBlocks.BARLEY_CROP, settings), createDefaultSettings());
    public static final Item KITCHEN_KNIFE = register("kitchen_knife", settings ->
            new KitchenKnifeItem(PeonyToolMaterials.KITCHEN_KNIFE, settings), createDefaultSettings().maxCount(1));
    public static final Item LARD = register("lard", Item::new, createDefaultSettings().food(PeonyFoodComponents.LARD));
    public static final Item NATURE_GAS_DETECTOR = register("nature_gas_detector", NatureGasDetectorItem::new, createDefaultSettings().maxCount(1));

    public static final Item NATURE_GAS_BUCKET = register("nature_gas_bucket", settings ->
            new BucketItem(PeonyFluids.STILL_NATURE_GAS, settings), createDefaultSettings().maxCount(1).recipeRemainder(Items.BUCKET));
    public static final Item LARD_BUCKET = register("lard_bucket", settings ->
            new BucketItem(PeonyFluids.STILL_LARD, settings), createDefaultSettings().maxCount(1).recipeRemainder(Items.BUCKET));

    public static final Item PLACEHOLDER = register("placeholder", Item::new, createDefaultSettings(), false);

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        return register(name, itemFactory, settings, true);
    }

    public static Item register(String name, BiFunction<Block, Item.Settings, Item> itemFactory, Item.Settings settings, Block block) {
        return Registry.register(Registries.ITEM, key(name), itemFactory.apply(block, settings));
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
        PeonyFoodComponents.register();
        Peony.debug("Items");
    }
}
