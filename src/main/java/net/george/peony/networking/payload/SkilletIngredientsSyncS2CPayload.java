package net.george.peony.networking.payload;

import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
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

public record SkilletIngredientsSyncS2CPayload(List<ItemStack> ingredients, boolean allowOilBasedRecipes, BlockPos pos) implements CustomPayload {
    public static final Identifier PACKET_ID = Peony.id("skillet_ingredients_sync");
    public static final Id<SkilletIngredientsSyncS2CPayload> ID = new Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SkilletIngredientsSyncS2CPayload> CODEC =
            PacketCodec.tuple(
                    ItemStack.OPTIONAL_PACKET_CODEC.collect(PacketCodecs.toList()), SkilletIngredientsSyncS2CPayload::ingredients,
                    PacketCodecs.BOOL, SkilletIngredientsSyncS2CPayload::allowOilBasedRecipes,
                    BlockPos.PACKET_CODEC, SkilletIngredientsSyncS2CPayload::pos,
                    SkilletIngredientsSyncS2CPayload::new
            );
    public static final GameNetworking.PayloadReceiver<SkilletIngredientsSyncS2CPayload> RECEIVER = (payload, context) -> context.runInClient(client -> {
        BlockEntity blockEntity = Objects.requireNonNull(client.world).getBlockEntity(payload.pos);
        List<ItemStack> ingredients = payload.ingredients;
        if (blockEntity instanceof SkilletBlockEntity skillet) {
            skillet.addedIngredients.clear();
            if (!ingredients.isEmpty()) {
                skillet.addedIngredients.addAll(ingredients);
            }
            skillet.context.allowOilBasedRecipes = payload.allowOilBasedRecipes;
            skillet.markDirty();
        }
    });

    @Override
    public Id<SkilletIngredientsSyncS2CPayload> getId() {
        return ID;
    }
}
