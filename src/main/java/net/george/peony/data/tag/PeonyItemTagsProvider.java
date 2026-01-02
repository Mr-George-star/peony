package net.george.peony.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.george.peony.block.LogStickBlock;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.PeonyTags;
import net.george.peony.util.registry.RegistryDataUtils;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class PeonyItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public PeonyItemTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFeature) {
        super(output, registriesFeature, new PeonyBlockTagsProvider(output, registriesFeature));
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(PeonyTags.Items.LOG_STICKS)
                .add(RegistryDataUtils.ITEM.filterToArray(item -> item instanceof BlockItem blockItem && blockItem.getBlock() instanceof LogStickBlock));
        getOrCreateTagBuilder(PeonyTags.Items.COOKING_OIL)
                .add(PeonyItems.LARD)
                .add(PeonyItems.LARD_BOTTLE);
        getOrCreateTagBuilder(PeonyTags.Items.CUTTING_BOARDS)
                .add(PeonyBlocks.OAK_CUTTING_BOARD.asItem()).add(PeonyBlocks.SPRUCE_CUTTING_BOARD.asItem())
                .add(PeonyBlocks.BIRCH_CUTTING_BOARD.asItem()).add(PeonyBlocks.JUNGLE_CUTTING_BOARD.asItem())
                .add(PeonyBlocks.ACACIA_CUTTING_BOARD.asItem()).add(PeonyBlocks.CHERRY_CUTTING_BOARD.asItem())
                .add(PeonyBlocks.DARK_OAK_CUTTING_BOARD.asItem()).add(PeonyBlocks.MANGROVE_CUTTING_BOARD.asItem());
        getOrCreateTagBuilder(PeonyTags.Items.PARING_KNIVES)
                .add(PeonyItems.IRON_PARING_KNIFE);
        getOrCreateTagBuilder(PeonyTags.Items.SHREDDERS)
                .add(PeonyItems.IRON_SHREDDER).add(PeonyItems.GOLD_SHREDDER)
                .add(PeonyItems.DIAMOND_SHREDDER).add(PeonyItems.NETHERITE_SHREDDER);
        getOrCreateTagBuilder(PeonyTags.Items.KITCHENWARE)
                .forceAddTag(PeonyTags.Items.PARING_KNIVES)
                .forceAddTag(PeonyTags.Items.SHREDDERS)
                .add(PeonyBlocks.MILLSTONE.asItem())
                .forceAddTag(PeonyTags.Items.CUTTING_BOARDS)
                .add(PeonyBlocks.SKILLET.asItem())
                .add(PeonyBlocks.BREWING_BARREL.asItem())
                .add(PeonyBlocks.FERMENTATION_TANK.asItem());
    }
}
