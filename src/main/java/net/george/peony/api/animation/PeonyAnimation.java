package net.george.peony.api.animation;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.Peony;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PeonyAnimation {
    public static final Identifier GAMEPLAY_LAYER = Peony.id("gameplay");

    public static void register() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                GAMEPLAY_LAYER,
                1500,
                player -> new PlayerAnimationController(
                        player,
                        (controller, data, setter) -> PlayState.STOP
                )
        );
    }

    public static void play(AbstractClientPlayerEntity player, Identifier animationId) {
        PlayerAnimationController controller =
                (PlayerAnimationController)
                        PlayerAnimationAccess.getPlayerAnimationLayer(
                                player,
                                GAMEPLAY_LAYER
                        );

        if (controller != null) {
            controller.triggerAnimation(animationId);
        }
    }
}
