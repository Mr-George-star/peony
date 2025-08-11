package net.george.peony.world;

import net.george.peony.Peony;
import net.george.peony.world.feature.NatureGasLakeFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.feature.Feature;

public class PeonyFeatures {
    public static final Feature<NatureGasLakeFeature.Config> NATURE_GAS_LAKE =
            Registry.register(Registries.FEATURE, Peony.id("nature_gas_lake"), new NatureGasLakeFeature(NatureGasLakeFeature.Config.CODEC));

    public static void register() {
        Peony.debug("Features");
    }
}
