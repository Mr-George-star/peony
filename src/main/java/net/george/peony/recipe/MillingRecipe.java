package net.george.peony.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record MillingRecipe(Ingredient input, int millingTimes, ItemStack output) implements Recipe<MillingRecipeInput> {
    @Override
    public boolean matches(MillingRecipeInput input, World world) {
        if (!world.isClient) {
            return this.input.test(input.getInputStack());
        }
        return false;
    }

    @Override
    public ItemStack craft(MillingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
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
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PeonyRecipes.MILLING;
    }

    @Override
    public RecipeType<?> getType() {
        return PeonyRecipes.MILLING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<MillingRecipe> {
        public static final MapCodec<MillingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(MillingRecipe::input),
                Codec.INT.fieldOf("milling_times").forGetter(MillingRecipe::millingTimes),
                ItemStack.CODEC.fieldOf("output").forGetter(MillingRecipe::output)
        ).apply(instance, MillingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, MillingRecipe> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, MillingRecipe::input,
                PacketCodecs.INTEGER, MillingRecipe::millingTimes,
                ItemStack.PACKET_CODEC, MillingRecipe::output,
                MillingRecipe::new
        );

        @Override
        public MapCodec<MillingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MillingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
