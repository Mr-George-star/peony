package net.george.peony.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.block.data.Output;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record MillingRecipe(Ingredient input, int millingTimes, Output output) implements Recipe<SingleStackRecipeInput> {
    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {
        if (!world.isClient) {
            return this.input.test(input.getStackInSlot(0));
        }
        return false;
    }

    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.output.getOutputStack().copy();
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
        return this.output.getOutputStack().copy();
    }

    @Override
    public RecipeSerializer<MillingRecipe> getSerializer() {
        return PeonyRecipes.MILLING;
    }

    @Override
    public RecipeType<MillingRecipe> getType() {
        return PeonyRecipes.MILLING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<MillingRecipe> {
        public static final MapCodec<MillingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(MillingRecipe::input),
                Codec.INT.fieldOf("milling_times").forGetter(MillingRecipe::millingTimes),
                Output.CODEC.fieldOf("output").forGetter(MillingRecipe::output)
        ).apply(instance, MillingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, MillingRecipe> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, MillingRecipe::input,
                PacketCodecs.INTEGER, MillingRecipe::millingTimes,
                Output.PACKET_CODEC, MillingRecipe::output,
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
