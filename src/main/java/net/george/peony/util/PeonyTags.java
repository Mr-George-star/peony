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

public abstract class PeonyTags {
    @Registration(modId = Peony.MOD_ID)
    public static class Items implements TagRegistration<Item> {
        protected static final Items INSTANCE = new Items();

        public static final TagKey<Item> LOG_STICKS = INSTANCE.of("log_sticks");

        @Override
        public RegistryKey<Registry<Item>> getRegistryKey() {
            return RegistryKeys.ITEM;
        }
    }

    @Registration(modId = Peony.MOD_ID)
    public static class Blocks implements TagRegistration<Block> {
        protected static final Blocks INSTANCE = new Blocks();

        public static final TagKey<Block> BURNABLE_BLOCKS = INSTANCE.of("burnable_blocks");

        public static final TagKey<Block> INCORRECT_FOR_KITCHEN_KNIFE = INSTANCE.of("incorrect_for_kitchen_knife");

        @Override
        public RegistryKey<Registry<Block>> getRegistryKey() {
            return RegistryKeys.BLOCK;
        }
    }
}
