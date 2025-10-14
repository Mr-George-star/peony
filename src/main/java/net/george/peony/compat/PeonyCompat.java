package net.george.peony.compat;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.george.peony.Peony;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.LogStickBlock;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.PotStandBlock;
import net.george.peony.block.entity.CarvedRenderingItems;
import net.george.peony.block.entity.NonBlockRenderingItems;
import net.george.peony.item.KitchenKnifeItem;
import net.george.peony.item.PeonyItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.TridentItem;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public class PeonyCompat {
    public static final Identifier SHORT_GRASS_LOOT = Identifier.ofVanilla("blocks/short_grass");
    public static final Identifier PIG_LOOT = Identifier.ofVanilla("entities/pig");

    private static void registerCompostingChances() {
        CompostingChanceRegistry registry = CompostingChanceRegistry.INSTANCE;

        registry.add(PeonyItems.BARLEY, 0.65F);
        registry.add(PeonyItems.BARLEY_SEEDS, 0.3F);
        registry.add(PeonyItems.PEANUT, 0.2F);
        registry.add(PeonyItems.PEANUT_KERNEL, 0.1F);
        registry.add(PeonyItems.TOMATO, 0.5F);
        registry.add(PeonyBlocks.DOUGH, 0.6F);
    }

    private static void registerBurning() {
        FlammableBlockRegistry instance = FlammableBlockRegistry.getDefaultInstance();

        Registries.BLOCK.stream().forEach(block -> {
            if (block instanceof CuttingBoardBlock board) {
                instance.add(board, 5, 5);
            }
            if (block instanceof LogStickBlock logStick) {
                instance.add(logStick, 10, 5);
            }
            if (block instanceof PotStandBlock potStand) {
                instance.add(potStand, 5, 5);
            }
        });
    }

    private static void registerNonBlockRenderingItems() {
        NonBlockRenderingItems instance = NonBlockRenderingItems.getInstance();

        Registries.ITEM.stream().forEach(item -> {
            if (Registries.ITEM.getId(item).getNamespace().equals(Peony.MOD_ID)) {
                if (item instanceof AliasedBlockItem) {
                    instance.register(item);
                }
            }
        });
    }

    private static void registerCarvedRenderingItems() {
        CarvedRenderingItems instance = CarvedRenderingItems.getInstance();

        Registries.ITEM.stream().forEach(item -> {
            if (item instanceof PickaxeItem || item instanceof HoeItem) {
                instance.register(item, 225F);
            } else if (item instanceof TridentItem) {
                instance.register(item, 135F);
            } else if (item instanceof KitchenKnifeItem) {
                instance.register(item, 180F);
            }
        });
    }

    private static void modifyLootTables() {
        LootTableEvents.MODIFY.register((registryKey, builder, lootTableSource, wrapperLookup) -> {
            RegistryWrapper.Impl<Enchantment> enchantmentImpl = wrapperLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

            if (registryKey.getValue().equals(SHORT_GRASS_LOOT)) {
                LootPool.Builder pool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.125F))
                        .apply(ApplyBonusLootFunction.uniformBonusCount(enchantmentImpl.getOrThrow(Enchantments.FORTUNE), 2))
                        .with(ItemEntry.builder(PeonyItems.BARLEY_SEEDS));
                builder.pool(pool);
            } else if (registryKey.getValue().equals(PIG_LOOT)) {
                LootPool.Builder pool = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(3, 5))
                        .with(ItemEntry.builder(PeonyItems.LARD));
                builder.pool(pool);
            }
        });
    }

    public static void register() {
        Peony.debug("Combats");
        registerCompostingChances();
        registerBurning();
        registerNonBlockRenderingItems();
        registerCarvedRenderingItems();
        modifyLootTables();
    }
}
