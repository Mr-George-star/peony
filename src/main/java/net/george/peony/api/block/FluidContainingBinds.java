package net.george.peony.api.block;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.george.peony.Peony;
import net.george.peony.api.lookup.FluidVariantApiLookup;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Pair;

import java.util.function.Supplier;

public interface FluidContainingBinds {
    FluidVariantApiLookup<FromFluid, Void> FROM_FLUID = FluidVariantApiLookup.get(
            Peony.id("from_fluid"), FromFluid.class, Void.class
    );
    ItemApiLookup<FromItem, Void> FROM_ITEM = ItemApiLookup.get(
            Peony.id("from_item"), FromItem.class, Void.class
    );
    ItemApiLookup<FromContainer, Void> FROM_CONTAINER = ItemApiLookup.get(
            Peony.id("from_container"), FromContainer.class, Void.class
    );

    static void register(FluidVariant variant, ItemConvertible item, ItemConvertible container) {
        FROM_FLUID.registerForFluids((ignored, context) -> () -> new Pair<>(item, container), variant.getFluid());
        FROM_ITEM.registerForItems((ignored, context) -> () -> new Pair<>(variant, container), item);
        FROM_CONTAINER.registerForItems((ignored, context) -> () -> new Pair<>(variant, item), container);
    }

    // pair<item, container>
    interface FromFluid extends Supplier<Pair<ItemConvertible, ItemConvertible>> {}

    // pair<fluid, container>
    interface FromItem extends Supplier<Pair<FluidVariant, ItemConvertible>> {}

    // pair<fluid, item>
    interface FromContainer extends Supplier<Pair<FluidVariant, ItemConvertible>> {}
}
