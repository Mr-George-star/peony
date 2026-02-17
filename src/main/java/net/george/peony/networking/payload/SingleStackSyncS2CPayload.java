package net.george.peony.networking.payload;

import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.block.entity.BowlBlockEntity;
import net.george.peony.block.entity.FermentationTankBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public record SingleStackSyncS2CPayload(ItemStack stack, BlockPos pos) implements CustomPayload {
    public static final Identifier PACKET_ID = Peony.id("single_stack_sync");
    public static final Id<SingleStackSyncS2CPayload> ID = new Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SingleStackSyncS2CPayload> CODEC = PacketCodec.tuple(
            ItemStack.OPTIONAL_PACKET_CODEC, SingleStackSyncS2CPayload::stack,
            BlockPos.PACKET_CODEC, SingleStackSyncS2CPayload::pos,
            SingleStackSyncS2CPayload::new
    );
    public static final GameNetworking.PayloadReceiver<SingleStackSyncS2CPayload> RECEIVER = (payload, context) -> {
        BlockPos pos = payload.pos;

        context.runInClient(client -> {
            BlockEntity blockEntity = Objects.requireNonNull(client.world).getBlockEntity(pos);
            if (blockEntity instanceof BowlBlockEntity bowl) {
                bowl.setOutputStack(payload.stack);
            } else if (blockEntity instanceof FermentationTankBlockEntity tank) {
                tank.setOutputStack(payload.stack);
            }
        });
    };

    @Override
    public Id<SingleStackSyncS2CPayload> getId() {
        return ID;
    }
}
