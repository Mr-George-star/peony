package net.george.peony.util;

import net.george.peony.Peony;
import net.george.peony.util.registry.Registration;
import net.george.peony.util.registry.TagRegistration;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public abstract class PeonyTags {
    @Registration(modId = Peony.MOD_ID)
    public static class Items implements TagRegistration<Item> {
        protected static final Items INSTANCE = new Items();

        public static final TagKey<Item> LOG_STICKS = INSTANCE.of("log_sticks");
        public static final TagKey<Item> COOKING_OIL = INSTANCE.of("cooking_oil");
        public static final TagKey<Item> CUTTING_BOARDS = INSTANCE.of("cutting_boards");
        public static final TagKey<Item> PARING_KNIVES = INSTANCE.of("paring_knives");
        public static final TagKey<Item> SHREDDERS = INSTANCE.of("shredders");
        public static final TagKey<Item> KITCHENWARE = INSTANCE.of("kitchenware");

        @Override
        public RegistryKey<Registry<Item>> getRegistryKey() {
            return RegistryKeys.ITEM;
        }
    }

    @Registration(modId = Peony.MOD_ID)
    public static class Blocks implements TagRegistration<Block> {
        protected static final Blocks INSTANCE = new Blocks();

        public static final TagKey<Block> BURNABLE_BLOCKS = INSTANCE.of("burnable_blocks");
        public static final TagKey<Block> VINE_CROPS_ATTACHABLE = INSTANCE.of("vine_crops_attachable");
        public static final TagKey<Block> CUTTING_BOARDS = INSTANCE.of("cutting_boards");
        public static final TagKey<Block> KITCHENWARE = INSTANCE.of("kitchenware");

        public static final TagKey<Block> INCORRECT_FOR_KITCHEN_KNIFE = INSTANCE.of("incorrect_for_kitchen_knife");

        @Override
        public RegistryKey<Registry<Block>> getRegistryKey() {
            return RegistryKeys.BLOCK;
        }
    }
}
