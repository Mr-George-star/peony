package net.george.peony.networking.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record BuffedPayload<T extends CustomPayload>(CustomPayload.Id<T> id, byte[] payloadData) implements CustomPayload {
    @Override
    public Id<T> getId() {
        return this.id;
    }

    public static <T extends CustomPayload> PacketCodec<ByteBuf, BuffedPayload<T>> streamCodec(CustomPayload.Id<T> id) {
        return PacketCodecs.BYTE_ARRAY.xmap(bytes -> new BuffedPayload<>(id, bytes), BuffedPayload::payloadData);
    }
}
