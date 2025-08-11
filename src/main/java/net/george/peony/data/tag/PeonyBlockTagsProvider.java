package net.george.peony.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.george.peony.block.*;
import net.george.peony.util.PeonyTags;
import net.george.peony.util.registry.RegistryDataUtils;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.AbstractTorchBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class PeonyBlockTagsProvider extends FabricTagProvider.BlockTagProvider {
    public PeonyBlockTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(PeonyTags.Blocks.BURNABLE_BLOCKS)
                .add(RegistryDataUtils.BLOCK.filterToArray(block ->
                        block instanceof AbstractTorchBlock
                        || block instanceof AbstractFireBlock
                        || block instanceof CampfireBlock))
                .add(Blocks.MAGMA_BLOCK).add(Blocks.LAVA).add(Blocks.LAVA_CAULDRON);
        getOrCreateTagBuilder(BlockTags.CAMPFIRES)
                .add(RegistryDataUtils.BLOCK.filterToArray(block -> block instanceof PotStandWithCampfireBlock));

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(PeonyBlocks.MILLSTONE);

        getOrCreateTagBuilder(PeonyTags.Blocks.INCORRECT_FOR_KITCHEN_KNIFE)
                .addOptionalTag(BlockTags.INCORRECT_FOR_IRON_TOOL);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(PeonyBlocks.MILLSTONE);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(RegistryDataUtils.BLOCK.filterToArray(block -> block instanceof CuttingBoardBlock))
                .add(RegistryDataUtils.BLOCK.filterToArray(block -> block instanceof LogStickBlock))
                .add(RegistryDataUtils.BLOCK.filterToArray(block -> block instanceof PotStandBlock || block instanceof PotStandWithCampfireBlock));
    }
}
