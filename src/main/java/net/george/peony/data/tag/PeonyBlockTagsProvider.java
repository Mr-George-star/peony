package net.george.peony.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.PeonyBlocks;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class PeonyBlockTagsProvider extends FabricTagProvider.BlockTagProvider {
    public PeonyBlockTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(PeonyBlocks.MILLSTONE);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(PeonyBlocks.MILLSTONE);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(Registries.BLOCK.stream().filter(block -> block instanceof CuttingBoardBlock).toArray(Block[]::new));
    }
}
