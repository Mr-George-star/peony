package net.george.peony.networking.payload;

import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.block.entity.CuttingBoardBlockEntity;
import net.george.peony.block.entity.SkilletBlockEntity;
import net.minecraft.block.entity.BlockEntity;
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
            BlockEntity blockEntity = Objects.requireNonNull(client.world).getBlockEntity(pos);
            if (blockEntity instanceof CuttingBoardBlockEntity board) {
                board.setInputStack(ItemStack.EMPTY);
            } else if (blockEntity instanceof SkilletBlockEntity skillet) {
                skillet.setInputStack(ItemStack.EMPTY);
                skillet.setOutputStack(ItemStack.EMPTY);
            }
        });
    };

    @Override
    public Id<ClearInventoryS2CPayload> getId() {
        return ID;
    }
}
