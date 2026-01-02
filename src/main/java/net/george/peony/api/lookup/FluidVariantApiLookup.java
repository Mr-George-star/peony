package net.george.peony.api.lookup;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface FluidVariantApiLookup<A, C> {
    static <A, C> FluidVariantApiLookup<A, C> get(Identifier lookupId, Class<A> apiClass, Class<C> contextClass) {
        return FluidVariantApiLookupImpl.get(lookupId, apiClass, contextClass);
    }

    @Nullable
    A find(FluidVariant variant, C context);

    void registerSelf(Fluid... fluids);

    void registerForFluids(FluidVariantApiProvider<A, C> provider, Fluid... fluids);

    void registerFallback(FluidVariantApiProvider<A, C> provider);

    Identifier getId();

    Class<A> apiClass();

    Class<C> contextClass();

    @Nullable
    FluidVariantApiProvider<A, C> getProvider(Fluid fluid);

    @FunctionalInterface
    interface FluidVariantApiProvider<A, C> {
        @Nullable
        A find(FluidVariant variant, C context);
    }
}
