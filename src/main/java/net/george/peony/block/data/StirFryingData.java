package net.george.peony.block.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Objects;

public record StirFryingData(int times) {
    public static final Codec<StirFryingData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("times").forGetter(StirFryingData::times)
            ).apply(instance, StirFryingData::new));
    public static final PacketCodec<RegistryByteBuf, StirFryingData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, StirFryingData::times,
            StirFryingData::new
    );
    public static final StirFryingData DEFAULT = new StirFryingData(-1);

    @Override
    public String toString() {
        return "StirFryingData[" +
                "times=" + this.times +
                ']';
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || getClass() != another.getClass()) {
            return false;
        }
        StirFryingData that = (StirFryingData) another;
        return this.times == that.times;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.times);
    }
}
