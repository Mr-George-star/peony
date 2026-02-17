package net.george.peony.api.interaction.effect;

import net.george.peony.api.interaction.effect.animation.AnimationExecutor;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record InteractionAnimation(AnimationExecutor executor) implements EffectAction {
    public void play(ServerPlayerEntity player, Hand hand, World world, BlockPos pos) {
        this.executor.execute(player, hand, world, pos);
    }
}
