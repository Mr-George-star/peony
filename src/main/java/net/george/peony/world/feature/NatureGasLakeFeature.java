package net.george.peony.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class NatureGasLakeFeature extends Feature<NatureGasLakeFeature.Config> {
    public NatureGasLakeFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<Config> context) {
        WorldAccess world = context.getWorld();
        Random random = context.getRandom();
        BlockPos chunkOrigin = context.getOrigin().toImmutable();
        BlockState natureGas = context.getConfig().provider.get(random, chunkOrigin);

        int chunkX = chunkOrigin.getX() >> 4;
        int chunkZ = chunkOrigin.getZ() >> 4;

        // The origin is located slightly inside the center of the chunk to prevent truncation
        int originX = (chunkX << 4) + 4 + random.nextInt(8);
        int originZ = (chunkZ << 4) + 4 + random.nextInt(8);
        int originY = chunkOrigin.getY();

        // Size of egg shape
        int radiusX = 5 + random.nextInt(3);
        int radiusY = 4 + random.nextInt(2);
        int radiusZ = 5 + random.nextInt(3);

        // Noise disturbance
        SimplexNoiseSampler sampler = new SimplexNoiseSampler(random);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        boolean placed = false;

        // Check area suitability (to prevent spawning in holes/liquid)
        for (int x = -radiusX; x <= radiusX; x++) {
            for (int y = -radiusY; y <= radiusY; y++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    mutable.set(originX + x, originY + y, originZ + z);
                    BlockState existing = world.getBlockState(mutable);
                    if (!existing.isSolidBlock(world, mutable) || existing.getFluidState().isStill()) {
                        return false;
                    }
                }
            }
        }

        // Formally generate an ellipsoid (egg-shaped), including soft edge processing
        for (int x = -radiusX; x <= radiusX; x++) {
            for (int y = -radiusY; y <= radiusY; y++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    int generateX = originX + x;
                    int generateY = originY + y;
                    int generateZ = originZ + z;

                    // Soft edge processing (natural attenuation of edges)
                    int dxToEdge = Math.min(generateX - (chunkX << 4), ((chunkX + 1) << 4) - generateX - 1);
                    int dzToEdge = Math.min(generateZ - (chunkZ << 4), ((chunkZ + 1) << 4) - generateZ - 1);
                    double edgeFactor = Math.min(dxToEdge, dzToEdge) < 2 ? 0.85 : 1.0;

                    double dx = x / (double) radiusX;
                    double dy = y / (double) radiusY;
                    double dz = z / (double) radiusZ;
                    double distance = dx * dx + dy * dy + dz * dz;

                    double noiseVal = sampler.sample(generateX * 0.1, generateY * 0.1, generateZ * 0.1);
                    double threshold = 1.0 + (noiseVal * 0.25);

                    if (distance <= threshold * edgeFactor) {
                        mutable.set(generateX, generateY, generateZ);

                        if (y < -radiusY / 3) {
                            // Fill the lower part with gas
                            world.setBlockState(mutable, natureGas, Block.NOTIFY_LISTENERS);
                        } else {
                            // Hollow out the upper part
                            world.setBlockState(mutable, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                        }

                        placed = true;
                    }
                }
            }
        }

        return placed;
    }

    public record Config(BlockStateProvider provider) implements FeatureConfig {
        public static final Codec<Config> CODEC = BlockStateProvider.TYPE_CODEC
                        .fieldOf("natureGas")
                        .xmap(Config::new, config -> config.provider)
                        .codec();

        public static Config of(Block natureGas) {
            return new Config(BlockStateProvider.of(natureGas));
        }
    }
}
