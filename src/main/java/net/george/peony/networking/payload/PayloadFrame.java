package net.george.peony.networking.payload;

import net.george.peony.networking.GameNetworking;
import net.george.peony.networking.transform.PacketTransmissionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public interface PayloadFrame<T extends PayloadFrame<T>> extends CustomPayload {
    @Override
    Id<T> getId();

    PacketCodec<? super RegistryByteBuf, T> getCodec();

    GameNetworking.PayloadReceiver<T> getReceiver();

    PacketTransmissionType getTransmissionType();
}
