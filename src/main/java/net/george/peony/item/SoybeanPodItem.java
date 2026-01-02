package net.george.peony.item;

import net.minecraft.item.ItemStack;

public class SoybeanPodItem extends DirectConvertItem {
    protected SoybeanPodItem(Settings settings) {
        super(settings, random -> {
            if (random.nextBetween(0, 1000) == 0) {
                return new ItemStack(PeonyItems.SOYBEAN, 4);
            } else {
                return new ItemStack(PeonyItems.SOYBEAN, 3);
            }
        });
    }
}
