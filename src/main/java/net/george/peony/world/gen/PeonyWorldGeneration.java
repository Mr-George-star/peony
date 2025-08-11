package net.george.peony.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.george.peony.world.PeonyPlacedFeatures;
import net.minecraft.world.gen.GenerationStep;

public class PeonyWorldGeneration {
    public static void generate() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.LAKES,
                PeonyPlacedFeatures.NATURE_GAS_LAKE);
    }
}
