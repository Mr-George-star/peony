package net.george.peony.world;

import net.george.peony.Peony;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.world.feature.NatureGasLakeFeature;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class PeonyConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> NATURE_GAS_LAKE = key("nature_gas_lake");

    public static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context, RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> key(String name) {
        return Peony.key(RegistryKeys.CONFIGURED_FEATURE, name);
    }

    public static void boostrap(Registerable<ConfiguredFeature<?, ?>> context) {
        register(context, NATURE_GAS_LAKE, PeonyFeatures.NATURE_GAS_LAKE, NatureGasLakeFeature.Config.of(PeonyBlocks.NATURE_GAS));
    }
}
