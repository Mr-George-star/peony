package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.george.peony.block.BarleyCropBlock;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.item.PeonyItems;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
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

        Registries.BLOCK.stream().forEach(block -> {
            if (block instanceof CuttingBoardBlock board) {
                addDrop(board);
            }
        });

        // barley crop
        BlockStatePropertyLootCondition.Builder barleyLootCondition = BlockStatePropertyLootCondition.builder(PeonyBlocks.BARLEY_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(BarleyCropBlock.AGE, BarleyCropBlock.MAX_AGE));
        this.addDrop(PeonyBlocks.BARLEY_CROP, this.cropDrops(PeonyBlocks.BARLEY_CROP, PeonyItems.BARLEY, PeonyItems.BARLEY_SEEDS, barleyLootCondition));
    }
}
