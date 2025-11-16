package net.george.peony.api.action;

import com.mojang.serialization.MapCodec;
import net.george.peony.Peony;
import net.george.peony.item.KitchenKnifeItem;
import net.george.peony.item.ParingKnifeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public final class ActionTypes {
    public static final ActionType<Kneading> KNEADING =
            ActionType.register("kneading", Kneading.CODEC, Kneading.PACKET_CODEC);
    public static final ActionType<Cutting> CUTTING =
            ActionType.register("cutting", Cutting.CODEC, Cutting.PACKET_CODEC);
    public static final ActionType<Slice> SLICE =
            ActionType.register("slice", Slice.CODEC, Slice.PACKET_CODEC);

    public static void register() {
        Peony.debug("Action Types");
    }

    public static Kneading kneading() {
        return Kneading.INSTANCE;
    }

    public static Cutting cutting() {
        return Cutting.INSTANCE;
    }

    public static class Kneading implements Action {
        public static final MapCodec<Kneading> CODEC = MapCodec.unit(Kneading::new);
        public static final Kneading INSTANCE = new Kneading();
        public static final PacketCodec<RegistryByteBuf, Kneading> PACKET_CODEC = PacketCodec.unit(INSTANCE);

        private Kneading() {}

        @Override
        public boolean test(ItemStack stack) {
            return true;
        }

        @Override
        public ActionType<Kneading> getType() {
            return KNEADING;
        }

        @Override
        public String toString() {
            return "Kneading";
        }

        @Override
        public boolean equals(Object another) {
            return another instanceof Kneading;
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode();
        }
    }

    public static class Cutting implements Action {
        public static final MapCodec<Cutting> CODEC = MapCodec.unit(Cutting::new);
        public static final Cutting INSTANCE = new Cutting();
        public static final PacketCodec<RegistryByteBuf, Cutting> PACKET_CODEC = PacketCodec.unit(INSTANCE);

        private Cutting() {}

        @Override
        public boolean test(ItemStack stack) {
            return stack.getItem() instanceof KitchenKnifeItem;
        }

        @Override
        public ActionType<Cutting> getType() {
            return CUTTING;
        }

        @Override
        public String toString() {
            return "Cutting";
        }

        @Override
        public boolean equals(Object another) {
            return another instanceof Cutting;
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode();
        }
    }

    public static class Slice implements Action {
        public static final MapCodec<Slice> CODEC = MapCodec.unit(Slice::new);
        public static final Slice INSTANCE = new Slice();
        public static final PacketCodec<RegistryByteBuf, Slice> PACKET_CODEC = PacketCodec.unit(INSTANCE);

        private Slice() {}

        @Override
        public boolean test(ItemStack stack) {
            return stack.getItem() instanceof ParingKnifeItem;
        }

        @Override
        public ActionType<Slice> getType() {
            return SLICE;
        }

        @Override
        public String toString() {
            return "Slice";
        }

        @Override
        public boolean equals(Object another) {
            return another instanceof Slice;
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode();
        }
    }
}
