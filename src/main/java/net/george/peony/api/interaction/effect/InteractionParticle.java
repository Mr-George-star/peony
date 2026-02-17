package net.george.peony.api.interaction.effect;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record InteractionParticle(ParticleEffect particle, int count, Vec3d velocity) implements EffectAction {
    public void spawn(World world, BlockPos pos) {
        world.addParticle(this.particle, pos.getX(), pos.getY(), pos.getZ(),
                this.velocity.x, this.velocity.y, this.velocity.z);
    }
}
