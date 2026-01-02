package net.george.peony.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public record ShreddingRecipe(Ingredient input, int durationDecrement, ItemStack output) implements Recipe<SingleStackRecipeInput> {
    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {
        if (!world.isClient) {
            return this.input.test(input.getStackInSlot(0));
        }
        return false;
    }

    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        ingredients.add(this.input);
        return ingredients;
    }

    @Override
    public RecipeSerializer<ShreddingRecipe> getSerializer() {
        return PeonyRecipes.SHREDDING;
    }

    @Override
    public RecipeType<ShreddingRecipe> getType() {
        return PeonyRecipes.SHREDDING_TYPE;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.output.copy();
    }

    public static class Serializer implements RecipeSerializer<ShreddingRecipe> {
        public static final MapCodec<ShreddingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter(ShreddingRecipe::input),
                        Codecs.NONNEGATIVE_INT.optionalFieldOf("duration_decrement", 1).forGetter(ShreddingRecipe::durationDecrement),
                        ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("output").forGetter(ShreddingRecipe::output)
                ).apply(instance, ShreddingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, ShreddingRecipe> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, ShreddingRecipe::input,
                PacketCodecs.INTEGER, ShreddingRecipe::durationDecrement,
                ItemStack.PACKET_CODEC, ShreddingRecipe::output,
                ShreddingRecipe::new
        );

        @Override
        public MapCodec<ShreddingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ShreddingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
