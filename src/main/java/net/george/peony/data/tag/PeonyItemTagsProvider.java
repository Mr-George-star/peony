package net.george.peony.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.george.peony.block.LogStickBlock;
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
    }
}
