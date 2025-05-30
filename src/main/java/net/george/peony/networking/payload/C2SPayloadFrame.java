package net.george.peony.networking.payload;

import net.george.peony.networking.transform.PacketTransmissionType;

public interface C2SPayloadFrame<T extends C2SPayloadFrame<T>> extends PayloadFrame<T> {
    @Override
    default PacketTransmissionType getTransmissionType() {
        return PacketTransmissionType.C2S;
    }
}
