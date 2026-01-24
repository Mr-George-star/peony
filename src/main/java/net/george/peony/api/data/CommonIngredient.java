package net.george.peony.api.data;

import com.mojang.serialization.Codec;
import net.george.peony.block.data.RecipeStep;
import net.george.peony.block.data.RecipeStepTypes;
import net.george.peony.recipe.SequentialCookingRecipeInput;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface CommonIngredient {
    Codec<CommonIngredient> CODEC = CommonIngredientType.REGISTRY.getCodec()
            .dispatch("type", CommonIngredient::getType, CommonIngredientType::getCodec);
    PacketCodec<RegistryByteBuf, CommonIngredient> PACKET_CODEC = PacketCodec.of(
            (commonIngredient, buf) -> {
                Identifier typeId = commonIngredient.getType().getId();
                buf.writeIdentifier(typeId);
                encodeGenericAction(buf, commonIngredient, commonIngredient.getType());
            },
            buf -> {
                Identifier typeId = buf.readIdentifier();
                CommonIngredientType<?> type = CommonIngredientType.REGISTRY.get(typeId);
                if (type == null) {
                    throw new IllegalArgumentException("Unknown common ingredient type: " + typeId);
                }
                return decodeGenericAction(buf, type);
            }
    );

    @SuppressWarnings("unchecked")
    private static <T extends CommonIngredient> void encodeGenericAction(RegistryByteBuf buf, CommonIngredient action, CommonIngredientType<T> type) {
        T typedAction = (T) action;
        type.getPacketCodec().encode(buf, typedAction);
    }

    private static <T extends CommonIngredient> CommonIngredient decodeGenericAction(RegistryByteBuf buf, CommonIngredientType<T> type) {
        return type.getPacketCodec().decode(buf);
    }

    RegistryEntryList<Item> getRequiredEntryList(RegistryWrapper.WrapperLookup registryLookup);

    @Nullable
    RecipeStep getStep(RecipeStepTypes type);

    CommonIngredientType<?> getType();

    default boolean matchesRecipe(SequentialCookingRecipeInput recipeInput, World world) {
        return recipeInput.getInputStack().isIn(this.getRequiredEntryList(world.getRegistryManager()));
    }
}
