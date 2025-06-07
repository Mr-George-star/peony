package net.george.peony;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PeonyItemGroups {
    public static final RegistryKey<ItemGroup> KEY = Peony.key(RegistryKeys.ITEM_GROUP, "item_group");
    public static List<Item> ITEM_LIST = new ArrayList<>();
    public static List<Block> BLOCK_LIST = new ArrayList<>();

    public static void register() {
        Peony.debug("Item Groups");
        Registry.register(Registries.ITEM_GROUP, KEY, FabricItemGroup.builder()
                .icon(() -> new ItemStack(PeonyBlocks.MILLSTONE))
                .displayName(Text.translatable(PeonyTranslationKeys.ITEM_GROUP_KEY))
                .build());

        ItemGroupEvents.modifyEntriesEvent(KEY).register(entries -> {
            ITEM_LIST.forEach(entries::add);
            BLOCK_LIST.forEach(entries::add);
        });
    }
}
