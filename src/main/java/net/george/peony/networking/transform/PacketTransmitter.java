package net.george.peony.networking.transform;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

@FunctionalInterface
public interface PacketTransmitter {
    void send(Packet<?> packet);

    static PacketTransmitter fromPlayer(ServerPlayerEntity player) {
        return (packet) ->
                Objects.requireNonNull(player, "Unable to send packet to a 'null' player!").networkHandler.sendPacket(packet);
    }

    static PacketTransmitter fromPlayers(Iterable<? extends ServerPlayerEntity> players) {
        return (packet) -> {
            for (ServerPlayerEntity player : players) {
                Objects.requireNonNull(player, "Unable to send packet to a 'null' player!").networkHandler.sendPacket(packet);
            }
        };
    }

    @Environment(EnvType.CLIENT)
    static PacketTransmitter fromClient() {
        return (packet) -> {
            if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
            } else {
                throw new IllegalStateException("Unable to send packet to the server while not in game!");
            }
        };
    }
}
