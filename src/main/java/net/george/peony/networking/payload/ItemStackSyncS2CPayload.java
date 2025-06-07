package net.george.peony.networking.payload;

import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.block.entity.CuttingBoardBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Objects;

public record ItemStackSyncS2CPayload(int inventorySize, List<ItemStack> stacks, BlockPos pos) implements CustomPayload {
    public static final Identifier PACKET_ID = Peony.id("item_stack_sync");
    public static final Id<ItemStackSyncS2CPayload> ID = new Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, ItemStackSyncS2CPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, ItemStackSyncS2CPayload::inventorySize,
                    ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()), ItemStackSyncS2CPayload::stacks,
                    BlockPos.PACKET_CODEC, ItemStackSyncS2CPayload::pos,
                    ItemStackSyncS2CPayload::new
            );
    public static final GameNetworking.PayloadReceiver<ItemStackSyncS2CPayload> RECEIVER = (payload, context) -> {
        int inventorySize = payload.inventorySize;
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        BlockPos pos = payload.pos;

        for (int i = 0; i < inventorySize; i++) {
            stacks.set(i, payload.stacks.get(i));
        }
        context.runInClient(client -> {
            if (Objects.requireNonNull(client.world).getBlockEntity(pos) instanceof CuttingBoardBlockEntity board) {
                board.setInputStack(stacks.getFirst());
            }
        });
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
