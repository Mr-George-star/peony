package net.george.peony.world;

import net.george.peony.Peony;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class PeonyPlacedFeatures {
    public static final RegistryKey<PlacedFeature> NATURE_GAS_LAKE = key("nature_gas_lake_placed");

    public static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, modifiers));
    }

    public static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration, PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }

    public static RegistryKey<PlacedFeature> key(String name) {
        return Peony.key(RegistryKeys.PLACED_FEATURE, name);
    }

    public static void boostrap(Registerable<PlacedFeature> context) {
        RegistryEntryLookup<ConfiguredFeature<?, ?>> configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, NATURE_GAS_LAKE, configuredFeatures.getOrThrow(PeonyConfiguredFeatures.NATURE_GAS_LAKE),
                RarityFilterPlacementModifier.of(5),
                SquarePlacementModifier.of(),
                HeightRangePlacementModifier.of(UniformHeightProvider.create(YOffset.fixed(-55), YOffset.fixed(0))),
                BlockFilterPlacementModifier.of(BlockPredicate.matchingBlockTag(Vec3i.ZERO, BlockTags.BASE_STONE_OVERWORLD)),
                BiomePlacementModifier.of());
    }
}
