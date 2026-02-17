package net.george.peony.networking.payload;

import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.block.entity.BowlBlockEntity;
import net.george.peony.block.entity.CuttingBoardBlockEntity;
import net.george.peony.block.entity.FermentationTankBlockEntity;
import net.george.peony.block.entity.SkilletBlockEntity;
import net.minecraft.block.entity.BlockEntity;
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
                    ItemStack.OPTIONAL_PACKET_CODEC.collect(PacketCodecs.toList()), ItemStackSyncS2CPayload::stacks,
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
            BlockEntity blockEntity = Objects.requireNonNull(client.world).getBlockEntity(pos);
            if (blockEntity instanceof CuttingBoardBlockEntity board) {
                board.setInputStack(stacks.getFirst());
            } else if (blockEntity instanceof SkilletBlockEntity skillet) {
                skillet.setInputStack(stacks.getFirst());
                skillet.setOutputStack(stacks.get(1));
            } else if (blockEntity instanceof BowlBlockEntity bowl) {
                for (int slot = 0; slot < inventorySize; slot++) {
                    bowl.setStack(slot, stacks.get(slot));
                }
            } else if (blockEntity instanceof FermentationTankBlockEntity tank) {
                for (int slot = 0; slot < inventorySize; slot++) {
                    tank.setStack(slot, stacks.get(slot));
                }
            }
        });
    };

    @Override
    public Id<ItemStackSyncS2CPayload> getId() {
        return ID;
    }
}
