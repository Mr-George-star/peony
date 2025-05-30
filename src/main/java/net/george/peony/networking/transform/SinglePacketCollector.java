package net.george.peony.networking.transform;

import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SinglePacketCollector implements PacketTransmitter {
    private final @Nullable Consumer<Packet<?>> consumer;
    private Packet<?> packet;

    public SinglePacketCollector(@Nullable Consumer<Packet<?>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void send(Packet<?> packet) {
        if (this.packet == null) {
            this.packet = packet;
            if (this.consumer != null) {
                this.consumer.accept(packet);
            }

        } else {
            throw new IllegalStateException("Already accepted one packet!");
        }
    }

    public Packet<?> getPacket() {
        return this.packet;
    }
}
