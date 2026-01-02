package net.george.peony.item;

import net.george.peony.block.PeonyBlocks;
import net.minecraft.item.ItemStack;

public class GarlicItem extends InstantHarvestItem {
    public GarlicItem(Settings settings) {
        super(PeonyBlocks.GARLIC_CROP, settings, random -> {
            if (random.nextBetween(0, 1000) == 0) {
                return new ItemStack(PeonyItems.GARLIC_CLOVE, 7);
            } else {
                return new ItemStack(PeonyItems.GARLIC_CLOVE, 6);
            }
        });
    }
}
