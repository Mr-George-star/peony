package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.george.peony.block.*;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class PeonyBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected PeonyBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(PeonyBlocks.MILLSTONE);
        addDrop(PeonyBlocks.DOUGH);
        addDrop(PeonyBlocks.FLOUR);
        addDrop(PeonyBlocks.SKILLET);

        Registries.BLOCK.stream().forEach(entry -> {
            if (entry instanceof CuttingBoardBlock board) {
                addDrop(board);
            } else if (entry instanceof LogStickBlock logStick) {
                addDrop(logStick);
            } else if (entry instanceof PotStandBlock potStand) {
                addDrop(potStand);
            } else if (entry instanceof PotStandWithCampfireBlock potStandWithCampfire) {
                addDrop(potStandWithCampfire, Blocks.CAMPFIRE);
            }
        });

        // barley crop
        BlockStatePropertyLootCondition.Builder barleyLootCondition = BlockStatePropertyLootCondition.builder(PeonyBlocks.BARLEY_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(BarleyCropBlock.AGE, BarleyCropBlock.MAX_AGE));
        this.addDrop(PeonyBlocks.BARLEY_CROP, this.cropDrops(PeonyBlocks.BARLEY_CROP, PeonyItems.BARLEY, PeonyItems.BARLEY_SEEDS, barleyLootCondition));
        // peanut crop
        BlockStatePropertyLootCondition.Builder peanutLootCondition = BlockStatePropertyLootCondition.builder(PeonyBlocks.PEANUT_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(PeanutCropBlock.AGE, PeanutCropBlock.MAX_AGE));
        this.addDrop(PeonyBlocks.PEANUT_CROP, this.peanutDrops(peanutLootCondition));
        // tomato vines
        BlockStatePropertyLootCondition.Builder tomatoLootCondition = BlockStatePropertyLootCondition.builder(PeonyBlocks.TOMATO_VINES)
                .properties(StatePredicate.Builder.create().exactMatch(TomatoVinesBlock.AGE, TomatoVinesBlock.MAX_AGE));
        this.addDrop(PeonyBlocks.TOMATO_VINES, this.tomatoDrops(tomatoLootCondition));
    }

    public LootTable.Builder peanutDrops(LootCondition.Builder condition) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.applyExplosionDecay(PeonyBlocks.PEANUT_CROP,
                LootTable.builder()
                        .pool(LootPool.builder()
                                .conditionally(condition)
                                .with(ItemEntry.builder(PeonyItems.PEANUT)
                                        .apply(ApplyBonusLootFunction.binomialWithBonusCount(impl.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 5)))
                        )
        );
    }

    public LootTable.Builder tomatoDrops(LootCondition.Builder condition) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.applyExplosionDecay(PeonyBlocks.TOMATO_VINES,
                LootTable.builder()
                        .pool(LootPool.builder()
                                .conditionally(condition)
                                .with(ItemEntry.builder(PeonyItems.TOMATO)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2))))
                                        .apply(ApplyBonusLootFunction.binomialWithBonusCount(impl.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                        )
        );
    }
}
