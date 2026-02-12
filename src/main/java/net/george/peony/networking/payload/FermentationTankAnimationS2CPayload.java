package net.george.peony.networking.payload;

import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.block.entity.FermentationTankBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record FermentationTankAnimationS2CPayload(BlockPos pos, long fluidAmount) implements CustomPayload {
    public static final Identifier PACKET_ID = Peony.id("fermentation_tank_animation");
    public static final Id<FermentationTankAnimationS2CPayload> ID = new Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, FermentationTankAnimationS2CPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, FermentationTankAnimationS2CPayload::pos,
                    PacketCodecs.VAR_LONG, FermentationTankAnimationS2CPayload::fluidAmount,
                    FermentationTankAnimationS2CPayload::new
            );
    public static final GameNetworking.PayloadReceiver<FermentationTankAnimationS2CPayload> RECEIVER = (payload, context) -> {
        context.runInClient(client -> {
            ClientWorld world = client.world;
            if (world != null) {
//                BlockEntity blockEntity = world.getBlockEntity(payload.pos);
//                if (blockEntity instanceof FermentationTankBlockEntity tank) {
//                    tank.getFluidStorage().amount = payload.fluidAmount;
//                    tank.animationStartTime = System.currentTimeMillis();
//                    tank.isAnimating = true;
//
//                    System.out.println("[CLIENT] Received fluid update: " + payload.fluidAmount);
//                }
            }
        });
    };

    @Override
    public Id<FermentationTankAnimationS2CPayload> getId() {
        return ID;
    }
}
