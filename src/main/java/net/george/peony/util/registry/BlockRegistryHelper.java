package net.george.peony.util.registry;

import net.george.peony.PeonyItemGroups;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class BlockRegistryHelper implements RegistryHelper<Block> {
    public Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings) {
        return register(name, blockFactory, settings, true);
    }

    public Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory,
                          AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        return register(name, blockFactory, settings, shouldRegisterItem ? BlockItem::new : null, shouldRegisterItem ? new Item.Settings() : null);
    }

    public Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory,
                          AbstractBlock.Settings settings, BiFunction<Block, Item.Settings, Item> blockItemFactory) {
        return register(name, blockFactory, settings, blockItemFactory, null);
    }

    public Block register(String name,
                          Function<AbstractBlock.Settings, Block> blockFactory,
                          AbstractBlock.Settings settings,
                          @Nullable BiFunction<Block, Item.Settings, Item> blockItemFactory,
                          @Nullable Item.Settings itemSettings) {
        Block instance = register(name, blockFactory.apply(settings));
        Optional.ofNullable(blockItemFactory).ifPresent(factory -> {
            PeonyItems.register(name, factory, Optional.ofNullable(itemSettings).orElse(new Item.Settings()), instance);
            PeonyItemGroups.BLOCK_LIST.add(instance);
        });
        return instance;
    }

    @Override
    public Registry<Block> getRegistry() {
        return Registries.BLOCK;
    }

    @Override
    public RegistryKey<Registry<Block>> getRegistryKey() {
        return RegistryKeys.BLOCK;
    }
}
