package net.george.peony.networking;

import net.george.peony.networking.payload.ItemStackSyncS2CPayload;

public class PeonyNetworking {
    public static void registerC2SPackets() {}

    public static void registerS2CPackets() {
        GameNetworking.registerS2CReceiver(
                ItemStackSyncS2CPayload.ID,
                ItemStackSyncS2CPayload.CODEC,
                ItemStackSyncS2CPayload.RECEIVER
        );
    }
}
