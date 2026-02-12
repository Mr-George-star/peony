package net.george.peony.api.fluid;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.george.peony.Peony;
import net.minecraft.fluid.Fluid;

public interface FluidContainer {
    ItemApiLookup<FluidContainer, Void> FLUID_CONTAINERS = ItemApiLookup
            .get(Peony.id("fluid_containers"), FluidContainer.class, Void.class);

    static FluidContainer create(Fluid fluid, long amount) {
        return create(FluidStack.of(fluid, amount));
    }

    static FluidContainer create(FluidStack fluid) {
        return () -> fluid;
    }

    FluidStack getFluid();
}
