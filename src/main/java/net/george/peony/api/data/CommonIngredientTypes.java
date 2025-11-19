package net.george.peony.api.data;

import com.mojang.serialization.MapCodec;
import net.george.peony.Peony;
import net.george.peony.block.data.CookingSteps;
import net.george.peony.block.data.RecipeStep;
import net.george.peony.block.data.RecipeStepTypes;
import net.george.peony.item.PeonyItems;
import net.george.peony.recipe.SequentialCookingRecipeInput;
import net.minecraft.item.ItemConvertible;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CommonIngredientTypes {
    public static final CommonIngredientType<PeeledTomato> PEELED_TOMATO = register("peeled_tomato",
            PeeledTomato.CODEC, PeeledTomato.PACKET_CODEC, CommonIngredientTypes::peeledTomato, PeonyItems.PEELED_TOMATO);

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

    public static class PeeledTomato implements CommonIngredient {
        public static final MapCodec<PeeledTomato> CODEC = MapCodec.unit(PeeledTomato::new);
        public static final PeeledTomato INSTANCE = new PeeledTomato();
        public static final PacketCodec<RegistryByteBuf, PeeledTomato> PACKET_CODEC = PacketCodec.unit(INSTANCE);

        private PeeledTomato() {}

        @Override
        public @Nullable RecipeStep getStep(RecipeStepTypes type) {
            if (type.equals(RecipeStepTypes.COOKING)) {
                return new CookingSteps.Step(100, 80, PeonyItems.SPATULA, PeonyItems.PEELED_TOMATO);
            }
            return null;
        }

        @Override
        public boolean matchesRecipe(SequentialCookingRecipeInput recipeInput, World world) {
            return recipeInput.getInputStack().isOf(PeonyItems.PEELED_TOMATO);
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
}
