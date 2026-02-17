package net.george.peony.api.interaction.effect.animation;

import com.zigythebird.playeranim.animation.PlayerAnimResources;
import net.george.networking.api.GameNetworking;
import net.george.peony.api.action.ActionType;
import net.george.peony.networking.payload.AnimationPlayS2CPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class DefaultAnimations {
    public static AnimationExecutor swing() {
        return (player, hand, world, pos) -> player.swingHand(hand);
    }

    public static AnimationExecutor useItem() {
        return (player, hand, world, pos) -> player.setCurrentHand(hand);
    }

    public static AnimationExecutor custom(Identifier animationId) {
        return (player, hand, world, pos) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                sendAnimationPacket(serverPlayer, animationId);
            }
        };
    }

    public static void sendAnimationPacket(ServerPlayerEntity player, Identifier animationId) {
        if (PlayerAnimResources.hasAnimation(animationId)) {
            AnimationPlayS2CPayload payload = new AnimationPlayS2CPayload(player.getUuid(), animationId);
            GameNetworking.sendToPlayer(player, payload);
        }
    }

    public static AnimationExecutor fromAction(ActionType<?> type) {
        return custom(type.getId());
    }
}
