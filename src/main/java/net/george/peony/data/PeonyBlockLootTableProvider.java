package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.george.peony.block.*;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

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
        addDrop(PeonyBlocks.FLATBREAD);
        addDrop(PeonyBlocks.CHEESE_BLOCK);
        addDrop(PeonyBlocks.RAW_MARGHERITA_PIZZA);
        addDrop(PeonyBlocks.MARGHERITA_PIZZA);

        addDrop(PeonyBlocks.SKILLET);
        addDrop(PeonyBlocks.BREWING_BARREL);
        addDrop(PeonyBlocks.FERMENTATION_TANK);
        addDrop(PeonyBlocks.GAS_CYLINDER);
        addDrop(PeonyBlocks.GAS_STOVE);
        addDrop(PeonyBlocks.BOWL, Items.BOWL);

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
        // rice drop
        this.riceDrops();
        // coriander drop
        BlockStatePropertyLootCondition.Builder corianderLootCondition = BlockStatePropertyLootCondition.builder(PeonyBlocks.CORIANDER_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(CorianderCropBlock.AGE, CorianderCropBlock.MAX_AGE));
        this.addDrop(PeonyBlocks.CORIANDER_CROP, this.corianderDrops(corianderLootCondition));
        // garlic drop
        this.garlicDrops();
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

    public void riceDrops() {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

        LootCondition.Builder lowerHalf = BlockStatePropertyLootCondition.builder(PeonyBlocks.RICE_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(RiceCropBlock.HALF, DoubleBlockHalf.LOWER));
        LootCondition.Builder upperHalf = BlockStatePropertyLootCondition.builder(PeonyBlocks.RICE_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(RiceCropBlock.HALF, DoubleBlockHalf.UPPER));
        LootCondition.Builder isMature = BlockStatePropertyLootCondition.builder(PeonyBlocks.RICE_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(RiceCropBlock.AGE, 7));
        LootCondition.Builder notMature = isMature.invert();

        this.addDrop(PeonyBlocks.RICE_CROP, LootTable.builder()
                .pool(LootPool.builder()
                        .conditionally(lowerHalf.and(notMature))
                        .with(ItemEntry.builder(Items.AIR))
                )
                .pool(LootPool.builder()
                        .conditionally(lowerHalf.and(isMature))
                        .with(ItemEntry.builder(PeonyItems.BROWN_RICE)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)))
                        )
                )
                .pool(LootPool.builder()
                        .conditionally(upperHalf.and(notMature))
                        .with(ItemEntry.builder(PeonyItems.BROWN_RICE)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0f)))
                        )
                )
                .pool(LootPool.builder()
                        .conditionally(upperHalf.and(isMature))
                        .with(ItemEntry.builder(PeonyItems.BROWN_RICE)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0f, 3.0f)))
                        )
                        .with(ItemEntry.builder(PeonyItems.RICE_PANICLE)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)))
                                .conditionally(TableBonusLootCondition.builder(impl.getOrThrow(Enchantments.FORTUNE),
                                        0.1f, 0.14285715f, 0.25f, 1.0f))
                        )
                )
        );
    }

    public LootTable.Builder corianderDrops(LootCondition.Builder condition) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.applyExplosionDecay(PeonyBlocks.CORIANDER_CROP,
                LootTable.builder()
                        .pool(LootPool.builder()
                                .conditionally(condition)
                                .with(ItemEntry.builder(PeonyItems.CORIANDER)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 3))))
                                .apply(ApplyBonusLootFunction.binomialWithBonusCount(impl.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                        )
        );
    }

    public void garlicDrops() {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

        this.addDrop(PeonyBlocks.GARLIC_CROP, LootTable.builder()
                .pool(LootPool.builder()
                        .with(AlternativeEntry.builder(
                                ItemEntry.builder(PeonyItems.GARLIC)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 3)))
                                        .apply(ApplyBonusLootFunction.uniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE)))
                                        .conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(ItemTags.HOES))),
                                ItemEntry.builder(PeonyItems.GARLIC)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 3)))
                                        .conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(ItemTags.HOES)).invert())
                        ))
                        .build())
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(PeonyItems.GARLIC_SCAPE)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3, 4)))
                                .conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(ItemTags.HOES)).invert()))
                        .build()));
    }
}
