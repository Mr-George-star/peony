package net.george.peony.fluid;

import net.george.peony.Peony;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class PeonyFluids {
    public static final FlowableFluid STILL_NATURE_GAS = register("nature_gas", new NatureGasFluid.Still());
    public static final FlowableFluid FLOWING_NATURE_GAS = register("flowing_nature_gas", new NatureGasFluid.Flowing());
    public static final FlowableFluid STILL_LARD = register("still_lard", new LardFluid.Still());
    public static final FlowableFluid FLOWING_LARD = register("flowing_lard", new LardFluid.Flowing());

    public static FlowableFluid register(String name, FlowableFluid fluid) {
        return Registry.register(Registries.FLUID, Peony.id(name), fluid);
    }

    public static void register() {
        Peony.debug("Fluids");
    }
}
