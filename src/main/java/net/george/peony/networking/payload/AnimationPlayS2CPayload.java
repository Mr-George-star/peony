package net.george.peony.networking.payload;

import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.api.animation.PeonyAnimation;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record AnimationPlayS2CPayload(UUID playerUuid, Identifier animationId) implements CustomPayload {
    public static final Identifier PACKET_ID = Peony.id("animation_play");
    public static final Id<AnimationPlayS2CPayload> ID = new Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, AnimationPlayS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING.xmap(UUID::fromString, UUID::toString), AnimationPlayS2CPayload::playerUuid,
            Identifier.PACKET_CODEC, AnimationPlayS2CPayload::animationId,
            AnimationPlayS2CPayload::new
    );
    public static final GameNetworking.PayloadReceiver<AnimationPlayS2CPayload> RECEIVER = (payload, context) -> context.runInClient(client -> {
        if (client.world == null) {
            return;
        }

        PlayerEntity player = client.world.getPlayerByUuid(payload.playerUuid);
        if (player instanceof AbstractClientPlayerEntity clientPlayer) {
            PeonyAnimation.play(clientPlayer, payload.animationId);
        }
    });

    @Override
    public Id<AnimationPlayS2CPayload> getId() {
        return ID;
    }
}
