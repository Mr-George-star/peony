package net.george.peony.networking.payload;

import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.block.SkilletBlock;
import net.george.peony.block.entity.SkilletBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Objects;

public record SkilletAnimationDataSyncS2CPayload(BlockPos pos, SkilletBlockEntity.AnimationData data) implements CustomPayload {
    public static final Identifier PACKET_ID = Peony.id("skillet_animation_sync");
    public static final CustomPayload.Id<SkilletAnimationDataSyncS2CPayload> ID = new CustomPayload.Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SkilletAnimationDataSyncS2CPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, SkilletAnimationDataSyncS2CPayload::pos,
                    SkilletBlockEntity.AnimationData.PACKET_CODEC, SkilletAnimationDataSyncS2CPayload::data,
                    SkilletAnimationDataSyncS2CPayload::new
            );
    public static final GameNetworking.PayloadReceiver<SkilletAnimationDataSyncS2CPayload> RECEIVER = (payload, context) -> context.runInClient(client -> {
        BlockEntity blockEntity = Objects.requireNonNull(client.world).getBlockEntity(payload.pos);
        if (blockEntity instanceof SkilletBlockEntity skillet) {
            skillet.animationData = payload.data;
            skillet.markDirty();
        }
    });

    @Override
    public CustomPayload.Id<SkilletAnimationDataSyncS2CPayload> getId() {
        return ID;
    }
}
