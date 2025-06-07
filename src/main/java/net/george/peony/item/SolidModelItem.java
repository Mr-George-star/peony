package net.george.peony.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;

public class SolidModelItem extends Item implements SolidModelProvider {
    protected final BlockState state;

    public SolidModelItem(Settings settings, BlockState state) {
        super(settings);
        this.state = state;
    }

    @Override
    public BlockState asRenderingState() {
        return this.state;
    }
}
