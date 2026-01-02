package net.george.peony.item;

import net.minecraft.item.ItemStack;

public class PeanutItem extends DirectConvertItem {
    public PeanutItem(Settings settings) {
        super(settings, random -> {
            if (random.nextBetween(0, 1000) == 0) {
                return new ItemStack(PeonyItems.PEANUT_KERNEL, 3);
            } else {
                return new ItemStack(PeonyItems.PEANUT_KERNEL, 2);
            }
        });
    }
}
