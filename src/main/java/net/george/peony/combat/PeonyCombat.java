package net.george.peony.combat;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.george.peony.Peony;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.item.PeonyItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public class PeonyCombat {
    public static final Identifier SHORT_GRASS_LOOT = Identifier.ofVanilla("blocks/short_grass");

    private static void registerCompostingChances() {
        CompostingChanceRegistry.INSTANCE.add(PeonyItems.BARLEY, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(PeonyItems.BARLEY_SEEDS, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(PeonyBlocks.DOUGH, 0.6F);
    }

    private static void registerBurning() {
        FlammableBlockRegistry instance = FlammableBlockRegistry.getDefaultInstance();

        Registries.BLOCK.stream().forEach(block -> {
            if (block instanceof CuttingBoardBlock board) {
                instance.add(board, 5, 5);
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
            }
        });
    }

    public static void register() {
        Peony.debug("Combats");
        registerCompostingChances();
        registerBurning();
        modifyLootTables();
    }
}
