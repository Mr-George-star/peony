package net.george.peony.networking.transform;

import net.fabricmc.api.EnvType;

public enum PacketTransmissionType {
    C2S,
    S2C;

    public static PacketTransmissionType from(EnvType environment) {
        return environment == EnvType.CLIENT ? C2S : S2C;
    }

    public static PacketTransmissionType to(EnvType environment) {
        return environment == EnvType.CLIENT ? S2C : C2S;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
