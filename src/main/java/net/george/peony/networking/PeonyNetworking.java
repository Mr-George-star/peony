package net.george.peony.networking;

import net.george.networking.api.GameNetworking;
import net.george.peony.networking.payload.ClearInventoryS2CPayload;
import net.george.peony.networking.payload.ItemStackSyncS2CPayload;

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
                ClearInventoryS2CPayload.ID,
                ClearInventoryS2CPayload.CODEC,
                List.of(),
                ClearInventoryS2CPayload.RECEIVER
        );
    }
}
