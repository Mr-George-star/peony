package net.george.peony.api.action;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.world.WorldAccess;

import java.util.function.BiPredicate;

public interface Action extends BiPredicate<WorldAccess, ItemStack> {
    Codec<Action> CODEC = ActionType.REGISTRY.getCodec()
            .dispatch("type", Action::getType, ActionType::getCodec);
    PacketCodec<RegistryByteBuf, Action> PACKET_CODEC = PacketCodec.of(
            (action, buf) -> {
                Identifier typeId = action.getType().getId();
                buf.writeIdentifier(typeId);
                encodeGenericAction(buf, action, action.getType());
            },
            buf -> {
                Identifier typeId = buf.readIdentifier();
                ActionType<?> type = ActionType.REGISTRY.get(typeId);
                if (type == null) {
                    throw new IllegalArgumentException("Unknown action type: " + typeId);
                }
                return decodeGenericAction(buf, type);
            }
    );

    @SuppressWarnings("unchecked")
    private static <T extends Action> void encodeGenericAction(RegistryByteBuf buf, Action action, ActionType<T> type) {
        T typedAction = (T) action;
        type.getPacketCodec().encode(buf, typedAction);
    }

    private static <T extends Action> Action decodeGenericAction(RegistryByteBuf buf, ActionType<T> type) {
        return type.getPacketCodec().decode(buf);
    }

    @Override
    boolean test(WorldAccess world, ItemStack stack);

    ActionType<?> getType();
}
