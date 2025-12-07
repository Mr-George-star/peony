package net.george.peony.api.block;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.george.peony.Peony;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import java.util.function.Supplier;

public interface PizzaBlockState extends Supplier<BlockState> {
    ItemApiLookup<PizzaBlockState, Void> STATES = ItemApiLookup
            .get(Peony.id("pizza_block_states"), PizzaBlockState.class, Void.class);

    static PizzaBlockState create(Block block) {
        return create(block.getDefaultState());
    }

    static PizzaBlockState create(BlockState state) {
        return () -> state;
    }

    @Override
    BlockState get();
}
