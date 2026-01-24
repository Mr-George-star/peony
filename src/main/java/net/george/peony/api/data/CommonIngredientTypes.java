package net.george.peony.api.data;

import com.mojang.serialization.MapCodec;
import net.george.peony.Peony;
import net.george.peony.block.data.CookingSteps;
import net.george.peony.block.data.RecipeStep;
import net.george.peony.block.data.RecipeStepTypes;
import net.george.peony.item.PeonyItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CommonIngredientTypes {
    public static final CommonIngredientType<PeeledTomato> PEELED_TOMATO = register("peeled_tomato",
            PeeledTomato.CODEC, PeeledTomato.PACKET_CODEC, CommonIngredientTypes::peeledTomato, PeonyItems.PEELED_TOMATO);
    public static final CommonIngredientType<MincedGarlic> MINCED_GARLIC = register("minced_garlic",
            MincedGarlic.CODEC, MincedGarlic.PACKET_CODEC, CommonIngredientTypes::mincedGarlic, PeonyItems.MINCED_GARLIC);

    public static <T extends CommonIngredient> CommonIngredientType<T> register(String name, MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec, Supplier<T> factory, ItemConvertible ingredient) {
        CommonIngredientType<T> type = CommonIngredientType.register(name, codec, packetCodec, factory);
        CommonIngredientType.LOOKUP.registerForItems((itemStack, ignored) -> type, ingredient);
        return type;
    }

    public static void register() {
        Peony.debug("Common Ingredient Types");
    }

    public static PeeledTomato peeledTomato() {
        return new PeeledTomato();
    }

    public static MincedGarlic mincedGarlic() {
        return new MincedGarlic();
    }

    public static class PeeledTomato implements CommonIngredient {
        public static final MapCodec<PeeledTomato> CODEC = MapCodec.unit(PeeledTomato::new);
        public static final PeeledTomato INSTANCE = new PeeledTomato();
        public static final PacketCodec<RegistryByteBuf, PeeledTomato> PACKET_CODEC = PacketCodec.unit(INSTANCE);

        private PeeledTomato() {}

        @Override
        public RegistryEntryList<Item> getRequiredEntryList(RegistryWrapper.WrapperLookup registryLookup) {
            return RegistryEntryList.of(Registries.ITEM.createEntry(PeonyItems.PEELED_TOMATO));
        }

        @Override
        public @Nullable RecipeStep getStep(RecipeStepTypes type) {
            if (type.equals(RecipeStepTypes.COOKING)) {
                return new CookingSteps.Step(100, 80, PeonyItems.SPATULA, PeonyItems.PEELED_TOMATO);
            }
            return null;
        }

        @Override
        public CommonIngredientType<PeeledTomato> getType() {
            return PEELED_TOMATO;
        }

        @Override
        public String toString() {
            return "PeeledTomato";
        }

        @Override
        public boolean equals(Object another) {
            return another instanceof PeeledTomato;
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode();
        }
    }

    public static class MincedGarlic implements CommonIngredient {
        public static final MapCodec<MincedGarlic> CODEC = MapCodec.unit(MincedGarlic::new);
        public static final MincedGarlic INSTANCE = new MincedGarlic();
        public static final PacketCodec<RegistryByteBuf, MincedGarlic> PACKET_CODEC = PacketCodec.unit(INSTANCE);

        private MincedGarlic() {}

        @Override
        public RegistryEntryList<Item> getRequiredEntryList(RegistryWrapper.WrapperLookup registryLookup) {
            return RegistryEntryList.of(Registries.ITEM.createEntry(PeonyItems.MINCED_GARLIC));
        }

        @Override
        public @Nullable RecipeStep getStep(RecipeStepTypes type) {
            if (type.equals(RecipeStepTypes.COOKING)) {
                return new CookingSteps.Step(100, 80, PeonyItems.SPATULA, PeonyItems.MINCED_GARLIC);
            }
            return null;
        }

        @Override
        public CommonIngredientType<MincedGarlic> getType() {
            return MINCED_GARLIC;
        }

        @Override
        public String toString() {
            return "MincedGarlic";
        }

        @Override
        public boolean equals(Object another) {
            return another instanceof MincedGarlic;
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode();
        }
    }
}
