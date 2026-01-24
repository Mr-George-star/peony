package net.george.peony.api.lookup;

import net.fabricmc.fabric.api.lookup.v1.custom.ApiLookupMap;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class FluidVariantApiLookupImpl<A, C> implements FluidVariantApiLookup<A, C> {
    private static final Logger LOGGER = LoggerFactory.getLogger("lookup/fluid-variant");
    private static final ApiLookupMap<FluidVariantApiLookup<?, ?>> LOOKUPS = ApiLookupMap.create(FluidVariantApiLookupImpl::new);
    private final Identifier identifier;
    private final Class<A> apiClass;
    private final Class<C> contextClass;
    private final ApiProviderMap<Fluid, FluidVariantApiLookup.FluidVariantApiProvider<A, C>> providerMap = ApiProviderMap.create();
    private final List<FluidVariantApiLookup.FluidVariantApiProvider<A, C>> fallbackProviders = new CopyOnWriteArrayList<>();

    @SuppressWarnings("unchecked")
    public static <A, C> FluidVariantApiLookup<A, C> get(Identifier lookupId, Class<A> apiClass, Class<C> contextClass) {
        return (FluidVariantApiLookup<A, C>) LOOKUPS.getLookup(lookupId, apiClass, contextClass);
    }

    private FluidVariantApiLookupImpl(Identifier identifier, Class<A> apiClass, Class<C> contextClass) {
        this.identifier = identifier;
        this.apiClass = apiClass;
        this.contextClass = contextClass;
    }

    @Nullable
    @Override
    public A find(FluidVariant variant, C context) {
        Objects.requireNonNull(variant, "FluidVariant may not be null.");
        FluidVariantApiLookup.FluidVariantApiProvider<A, C> provider = this.providerMap.get(variant.getFluid());
        if (provider != null) {
            A instance = provider.find(variant, context);
            if (instance != null) {
                return instance;
            }
        }

        for (FluidVariantApiLookup.FluidVariantApiProvider<A, C> fallbackProvider : this.fallbackProviders) {
            A instance = fallbackProvider.find(variant, context);
            if (instance != null) {
                return instance;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerSelf(Fluid... fluids) {
        for (Fluid fluid : fluids) {
            if (!this.apiClass.isAssignableFrom(fluid.getClass())) {
                String errorMessage = String.format(
                        "Failed to register self-implementing fluids. API class %s is not assignable from fluid class %s.",
                        this.apiClass.getCanonicalName(),
                        fluid.getClass().getCanonicalName());
                throw new IllegalArgumentException(errorMessage);
            }
        }

        this.registerForFluids((variant, context) -> (A) variant.getFluid(), fluids);
    }

    @Override
    public void registerForFluids(FluidVariantApiProvider<A, C> provider, Fluid... fluids) {
        Objects.requireNonNull(provider, "FluidVariantApiProvider may not be null");
        if (fluids.length == 0) {
            throw new IllegalArgumentException("Must register at least one Fluid instance with an FluidVariantApiProvider");
        } else {
            for (Fluid fluid : fluids) {
                Objects.requireNonNull(fluid, "Fluid may not be null.");
                if (this.providerMap.putIfAbsent(fluid, provider) != null) {
                    LOGGER.warn("Encountered duplicate API provider registration for fluid: " + Registries.FLUID.getId(fluid));
                }
            }
        }
    }

    @Override
    public void registerFallback(FluidVariantApiProvider<A, C> provider) {
        Objects.requireNonNull(provider, "FluidVariantApiProvider may not be null.");
        this.fallbackProviders.add(provider);
    }

    @Override
    public Identifier getId() {
        return this.identifier;
    }

    @Override
    public Class<A> apiClass() {
        return this.apiClass;
    }

    @Override
    public Class<C> contextClass() {
        return this.contextClass;
    }

    @Nullable
    @Override
    public FluidVariantApiProvider<A, C> getProvider(Fluid fluid) {
        return this.providerMap.get(fluid);
    }
}
