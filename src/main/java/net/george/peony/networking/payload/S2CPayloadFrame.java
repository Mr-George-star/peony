package net.george.peony.networking.payload;

import net.george.peony.networking.GameNetworking;
import net.george.peony.networking.transform.PacketTransmissionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public abstract class S2CPayloadFrame<T extends S2CPayloadFrame<T>> implements PayloadFrame<T> {
    protected final Identifier id;

    public S2CPayloadFrame(Identifier id) {
        this.id = id;
    }

    @Override
    public Id<T> getId() {
        return new Id<>(this.id);
    }

    @Override
    public abstract PacketCodec<? super RegistryByteBuf, T> getCodec();

    @Override
    public abstract GameNetworking.PayloadReceiver<T> getReceiver();

    @Override
    public PacketTransmissionType getTransmissionType() {
        return PacketTransmissionType.S2C;
    }
}
