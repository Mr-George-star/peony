package net.george.peony.api.heat;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class HeatParticleHelper {
    public static void spawnHeatParticles(World world, BlockPos pos, Heat heat) {
        spawnHeatParticles(world, pos, heat.getLevel());
    }

    public static void spawnHeatParticles(World world, BlockPos pos, HeatLevel level) {
        if (world.isClient && level != HeatLevel.NONE) {
            Random random = world.random;
            Vec3d center = Vec3d.ofCenter(pos);

            world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS,
                    0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F,
                    false);

            int particleCount = getParticleCountForLevel(level, random);

            for (int i = 0; i < particleCount; i++) {
                double xOffset = (random.nextDouble() - 0.5) * 0.8;
                double yOffset = (random.nextDouble() - 0.5) * 0.5 + 0.5;
                double zOffset = (random.nextDouble() - 0.5) * 0.8;

                double velocityX = (random.nextDouble() - 0.5) * 0.02;
                double velocityY = random.nextDouble() * 0.03 + 0.01;
                double velocityZ = (random.nextDouble() - 0.5) * 0.02;

                ParticleEffect particle = getParticleEffectForLevel(level, random);

                world.addParticle(
                        particle,
                        center.x + xOffset,
                        center.y + yOffset,
                        center.z + zOffset,
                        velocityX,
                        velocityY,
                        velocityZ
                );
            }
        }
    }

    private static ParticleEffect getParticleEffectForLevel(HeatLevel level, Random random) {
        switch (level) {
            case SMOLDERING:
                // 80% Smoke Particle，20% Flame Particle
                return random.nextFloat() < 0.8f ?
                        ParticleTypes.SMOKE :
                        ParticleTypes.FLAME;

            case LOW:
                // 60% Flame Particle，40% Smoke Particle
                return random.nextFloat() < 0.6f ?
                        ParticleTypes.FLAME :
                        ParticleTypes.SMOKE;

            case HIGH:
                // 80% Flame Particle，20% Large Smoke Particle
                return random.nextFloat() < 0.8f ?
                        ParticleTypes.FLAME :
                        ParticleTypes.LARGE_SMOKE;

            case BLAZING:
                // 90% Flame Particle，10% Lava Particle
                if (random.nextFloat() < 0.9f) {
                    return ParticleTypes.FLAME;
                } else {
                    return ParticleTypes.LAVA;
                }

            default:
                return ParticleTypes.SMOKE;
        }
    }

    private static int getParticleCountForLevel(HeatLevel level, Random random) {
        return switch (level) {
            case SMOLDERING -> random.nextInt(2) + 1; // 1-2 particles
            case LOW -> random.nextInt(3) + 1; // 1-3 particles
            case HIGH -> random.nextInt(4) + 2; // 2-5 particles
            case BLAZING -> random.nextInt(6) + 4; // 4-9 particles
            default -> 0;
        };
    }
}
