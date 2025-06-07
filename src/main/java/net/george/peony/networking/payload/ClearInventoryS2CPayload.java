package net.george.peony.networking.payload;

import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.block.entity.CuttingBoardBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public record ClearInventoryS2CPayload(BlockPos pos) implements CustomPayload {
    public static final Identifier PACKET_ID = Peony.id("clear_inventory");
    public static final Id<ClearInventoryS2CPayload> ID = new Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, ClearInventoryS2CPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, ClearInventoryS2CPayload::pos,
                    ClearInventoryS2CPayload::new
            );
    public static final GameNetworking.PayloadReceiver<ClearInventoryS2CPayload> RECEIVER = (payload, context) -> {
        BlockPos pos = payload.pos;
        context.runInClient(client -> {
            if (Objects.requireNonNull(client.world).getBlockEntity(pos) instanceof CuttingBoardBlockEntity board) {
                board.setInputStack(ItemStack.EMPTY);
            }
        });
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
