package net.george.peony.api.interaction.effect;

import com.google.common.collect.Lists;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class InteractionEffect {
    private final List<EffectAction> actions;

    InteractionEffect(List<EffectAction> actions) {
        this.actions = actions;
    }

    public static InteractionEffect of() {
        return new InteractionEffect(List.of());
    }

    public InteractionEffect and(EffectAction action) {
        List<EffectAction> newList = Lists.newArrayList(this.actions);
        newList.add(action);
        return new InteractionEffect(newList);
    }

    public void apply(ServerPlayerEntity player, Hand hand, World world, BlockPos pos) {
        for (EffectAction action : this.actions) {
            switch (action) {
                case InteractionSound sound -> sound.play(world, pos);
                case InteractionParticle particle -> particle.spawn(world, pos);
                case InteractionAnimation animation -> animation.play(player, hand, world, pos);
                default -> {}
            }
        }
    }
}
