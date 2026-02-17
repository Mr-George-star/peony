package net.george.peony.networking;

import net.george.networking.api.GameNetworking;
import net.george.peony.networking.payload.*;

import java.util.List;

public class PeonyNetworking {
    public static void registerC2SPackets() {}

    public static void registerS2CPackets() {
        GameNetworking.registerS2CReceiver(
                ItemStackSyncS2CPayload.ID,
                ItemStackSyncS2CPayload.CODEC,
                List.of(),
                ItemStackSyncS2CPayload.RECEIVER
        );
        GameNetworking.registerS2CReceiver(
                SingleStackSyncS2CPayload.ID,
                SingleStackSyncS2CPayload.CODEC,
                List.of(),
                SingleStackSyncS2CPayload.RECEIVER
        );
        GameNetworking.registerS2CReceiver(
                SkilletIngredientsSyncS2CPayload.ID,
                SkilletIngredientsSyncS2CPayload.CODEC,
                List.of(),
                SkilletIngredientsSyncS2CPayload.RECEIVER
        );
        GameNetworking.registerS2CReceiver(
                SkilletAnimationDataSyncS2CPayload.ID,
                SkilletAnimationDataSyncS2CPayload.CODEC,
                List.of(),
                SkilletAnimationDataSyncS2CPayload.RECEIVER
        );
        GameNetworking.registerS2CReceiver(
                AnimationPlayS2CPayload.ID,
                AnimationPlayS2CPayload.CODEC,
                List.of(),
                AnimationPlayS2CPayload.RECEIVER
        );
    }
}
