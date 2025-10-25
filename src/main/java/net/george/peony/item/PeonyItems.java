package net.george.peony.item;

import net.george.peony.Peony;
import net.george.peony.PeonyItemGroups;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.PeonyJukeboxSongs;
import net.george.peony.fluid.PeonyFluids;
import net.minecraft.block.Block;
import net.minecraft.item.*;
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
    public static final Item PEANUT = register("peanut", PeanutItem::new, createDefaultSettings());
    public static final Item PEANUT_KERNEL = register("peanut_kernel", settings -> new AliasedBlockItem(PeonyBlocks.PEANUT_CROP, settings),
            createDefaultSettings().food(PeonyFoodComponents.PEANUT_KERNEL));
    public static final Item ROASTED_PEANUT_KERNEL = register("roasted_peanut_kernel", Item::new,
            createDefaultSettings().food(PeonyFoodComponents.ROASTED_PEANUT_KERNEL));
    public static final Item CRUSHED_PEANUTS = register("crushed_peanuts", Item::new,
            createDefaultSettings().food(PeonyFoodComponents.CRUSHED_PEANUTS));
    public static final Item TOMATO = register("tomato", Item::new,
            createDefaultSettings().food(PeonyFoodComponents.TOMATO));
    public static final Item TOMATO_SEEDS = register("tomato_seeds", settings ->
            new AliasedBlockItem(PeonyBlocks.TOMATO_VINES, settings), createDefaultSettings());
    public static final Item PEELED_TOMATO = register("peeled_tomato", Item::new,
            createDefaultSettings().food(PeonyFoodComponents.TOMATO));
    public static final Item TOMATO_SAUCE = register("tomato_sauce", Item::new,
            createDefaultSettings().maxCount(1).food(PeonyFoodComponents.TOMATO_SAUCE));
    public static final Item SCRAMBLED_EGGS = register("scrambled_eggs", Item::new,
            createDefaultSettings().maxCount(1).food(PeonyFoodComponents.SCRAMBLED_EGGS));
    public static final Item LARD = register("lard", Item::new, createDefaultSettings().food(PeonyFoodComponents.LARD));
    public static final Item LARD_BOTTLE = register("lard_bottle", Item::new, createDefaultSettings().maxCount(16));

    public static final Item KITCHEN_KNIFE = register("kitchen_knife", settings ->
            new KitchenKnifeItem(PeonyToolMaterials.KITCHEN_KNIFE, settings), createDefaultSettings().maxCount(1));
    public static final Item SPATULA = register("spatula", settings ->
            new SpatulaItem(PeonyToolMaterials.SPATULA, settings), createDefaultSettings().maxCount(1));
    public static final Item IRON_PARING_KNIFE = register("iron_paring_knife", settings ->
            new ParingKnifeItem(ToolMaterials.IRON, settings), createDefaultSettings().maxCount(1));
    public static final Item NATURE_GAS_DETECTOR = register("nature_gas_detector", NatureGasDetectorItem::new, createDefaultSettings().maxCount(1));

    public static final Item NATURE_GAS_BUCKET = register("nature_gas_bucket", settings ->
            new BucketItem(PeonyFluids.STILL_NATURE_GAS, settings), createDefaultSettings().maxCount(1).recipeRemainder(Items.BUCKET));
    public static final Item LARD_BUCKET = register("lard_bucket", settings ->
            new BucketItem(PeonyFluids.STILL_LARD, settings), createDefaultSettings().maxCount(1).recipeRemainder(Items.BUCKET));

    public static final Item MUSIC_DISC_SURPRISE = register("music_disc_surprise", Item::new, createDefaultSettings().maxCount(1).jukeboxPlayable(PeonyJukeboxSongs.SURPRISE_KEY));
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
