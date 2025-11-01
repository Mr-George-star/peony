package net.george.peony.networking;

import net.george.networking.api.GameNetworking;
import net.george.peony.networking.payload.ItemStackSyncS2CPayload;
import net.george.peony.networking.payload.SkilletAnimationDataSyncS2CPayload;
import net.george.peony.networking.payload.SkilletIngredientsSyncS2CPayload;

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
    }
}
