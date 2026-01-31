package net.george.peony.item;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.george.peony.Peony;
import net.george.peony.PeonyItemGroups;
import net.george.peony.api.item.FluidContainer;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.PeonyJukeboxSongs;
import net.george.peony.block.data.Output;
import net.george.peony.fluid.PeonyFluids;
import net.minecraft.block.Block;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("UnusedReturnValue")
public class PeonyItems {
    public static final Item WOODEN_PLATE = register("wooden_plate", Item::new,
            createDefaultSettings().maxCount(16));
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
    public static final Item PEELED_POTATO = register("peeled_potato", Item::new,
            createDefaultSettings().food(FoodComponents.POTATO));
    public static final Item SHREDDED_POTATO = register("shredded_potato", Item::new,
            createDefaultSettings().food(FoodComponents.POTATO));
    public static final Item CORIANDER = register("coriander", Item::new, createDefaultSettings());
    public static final Item CORIANDER_SEEDS = register("coriander_seeds", settings ->
            new AliasedBlockItem(PeonyBlocks.CORIANDER_CROP, settings), createDefaultSettings());
    public static final Item RICE_PANICLE = register("rice_panicle", Item::new, createDefaultSettings());
    public static final Item BROWN_RICE = register("brown_rice", settings ->
            new AliasedBlockItem(PeonyBlocks.RICE_CROP, settings), createDefaultSettings());
    public static final Item RICE = register("rice", Item::new, createDefaultSettings());
    public static final Item GARLIC = register("garlic", GarlicItem::new, createDefaultSettings());
    public static final Item GARLIC_CLOVE = register("garlic_clove", Item::new, createDefaultSettings());
    public static final Item GARLIC_SCAPE = register("garlic_scape", Item::new, createDefaultSettings());
    public static final Item MINCED_GARLIC = register("minced_garlic", Item::new, createDefaultSettings());
    public static final Item SOYBEAN = register("soybean", Item::new, createDefaultSettings());
    public static final Item SOYBEAN_POD = register("soybean_pod", SoybeanPodItem::new, createDefaultSettings());

    /* ingredients */
    public static final Item HAM = register("ham", Item::new,
            createDefaultSettings().food(PeonyFoodComponents.HAM));

    /* food */
    public static final Item BAKED_FLATBREAD = register("baked_flatbread", Item::new,
            createDefaultSettings().food(PeonyFoodComponents.BAKED_FLATBREAD));
    public static final Item TOMATO_SAUCE = register("tomato_sauce", Item::new,
            createDefaultSettings().maxCount(1).food(PeonyFoodComponents.TOMATO_SAUCE));
    public static final Item SCRAMBLED_EGGS = register("scrambled_eggs", Item::new,
            createDefaultSettings().maxCount(1).food(PeonyFoodComponents.SCRAMBLED_EGGS));
    public static final Item SCRAMBLED_EGGS_WITH_TOMATOES = register("scrambled_eggs_with_tomatoes", Item::new,
            createDefaultSettings().maxCount(1).food(PeonyFoodComponents.SCRAMBLED_EGGS_WITH_TOMATOES));
    public static final Item FRIED_SHREDDED_POTATOES = register("fried_shredded_potatoes", Item::new,
            createDefaultSettings().maxCount(1).food(PeonyFoodComponents.FRIED_SHREDDED_POTATOES));
    public static final Item STIR_FRIED_GARLIC_SCAPE_WITH_PORK = register("stir_fired_garlic_scape_with_pork", Item::new,
            createDefaultSettings().maxCount(1).food(PeonyFoodComponents.STIR_FRIED_GARLIC_SCAPE_WITH_PORK));
    public static final Item SWEET_AND_SOUR_PORK = register("sweet_and_sour_pork", Item::new,
            createDefaultSettings().maxCount(1).food(PeonyFoodComponents.SWEET_AND_SOUR_PORK));
    public static final Item CHEESE = register("cheese", Item::new,
            createDefaultSettings().food(PeonyFoodComponents.CHEESE));
    public static final Item SHREDDED_CHEESE = register("shredded_cheese", Item::new, createDefaultSettings());

    /* oil */
    public static final Item LARD = register("lard", Item::new, createDefaultSettings().food(PeonyFoodComponents.LARD));
    public static final Item LARD_BOTTLE = register("lard_bottle", Item::new, createDefaultSettings().maxCount(16));

    /* vanilla extensions */
    public static final Item PORK_TENDERLOIN = register("pork_tenderloin", Item::new,
            createDefaultSettings().food(PeonyFoodComponents.PORK_TENDERLOIN));

    /* condiments */
    public static final Item CONDIMENT_BOTTLE = register("condiment_bottle", Item::new,
            createDefaultSettings().maxCount(16));
    public static final Item BLACK_VINEGAR = register("black_vinegar", Item::new,
            createDefaultSettings().maxCount(16));
    public static final Item SWEET_SOUR_SAUCE = register("sweet_sour_sauce", Item::new,
            createDefaultSettings().maxCount(16));

    /* kitchen tools */
    public static final Item KITCHEN_KNIFE = register("kitchen_knife", settings ->
            new KitchenKnifeItem(PeonyToolMaterials.KITCHEN_KNIFE, settings), createDefaultSettings().maxCount(1));
    public static final Item SPATULA = register("spatula", settings ->
            new SpatulaItem(PeonyToolMaterials.SPATULA, settings), createDefaultSettings().maxCount(1));
    public static final Item IRON_PARING_KNIFE = register("iron_paring_knife", settings ->
            new ParingKnifeItem(ToolMaterials.IRON, settings), createDefaultSettings().maxCount(1));
    public static final Item IRON_SHREDDER = register("iron_shredder", settings ->
            new ShredderItem(ToolMaterials.IRON, settings), createDefaultSettings().maxCount(1));
    public static final Item GOLD_SHREDDER = register("gold_shredder", settings ->
            new ShredderItem(ToolMaterials.GOLD, settings), createDefaultSettings().maxCount(1));
    public static final Item DIAMOND_SHREDDER = register("diamond_shredder", settings ->
            new ShredderItem(ToolMaterials.DIAMOND, settings), createDefaultSettings().maxCount(1));
    public static final Item NETHERITE_SHREDDER = register("netherite_shredder", settings ->
            new ShredderItem(ToolMaterials.NETHERITE, settings), createDefaultSettings().maxCount(1));
    public static final Item NATURE_GAS_DETECTOR = register("nature_gas_detector", NatureGasDetectorItem::new, createDefaultSettings().maxCount(1));

    /* buckets */
    public static final Item NATURE_GAS_BUCKET = register("nature_gas_bucket",
            NatureGasBucketItem::new, createDefaultSettings().maxCount(1).recipeRemainder(Items.BUCKET));
    public static final Item LARD_BUCKET = register("lard_bucket", settings ->
            new BucketItem(PeonyFluids.STILL_LARD, settings), createDefaultSettings().maxCount(1).recipeRemainder(Items.BUCKET));

    /* misc */
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
        FluidContainer.FLUID_CONTAINERS.registerForItems((itemStack, ignored) ->
                FluidContainer.create(Fluids.WATER, FluidConstants.BOTTLE), Items.POTION);

        Output.OIL_OUTPUTS.registerForItems((itemStack, ignored) ->
                Output.create(LARD_BOTTLE.getDefaultStack(), Items.GLASS_BOTTLE), LARD, LARD_BOTTLE);
        Peony.debug("Items");
    }
}
