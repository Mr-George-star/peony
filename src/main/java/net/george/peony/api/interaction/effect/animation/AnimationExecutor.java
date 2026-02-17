package net.george.peony.api.interaction.effect.animation;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface AnimationExecutor {
    void execute(ServerPlayerEntity player, Hand hand, World world, BlockPos pos);
}
