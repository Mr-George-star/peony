package net.george.peony.networking.transform;

import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PacketsCollector implements PacketTransmitter {
    private final @Nullable Consumer<Packet<?>> consumer;
    private final List<Packet<?>> packets = new ArrayList<>();

    public PacketsCollector(@Nullable Consumer<Packet<?>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void send(Packet<?> packet) {
        this.packets.add(packet);
        if (this.consumer != null) {
            this.consumer.accept(packet);
        }
    }

    public List<Packet<?>> getPackets() {
        return this.packets;
    }
}
