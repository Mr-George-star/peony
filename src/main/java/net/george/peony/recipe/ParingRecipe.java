package net.george.peony.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record ParingRecipe(Ingredient input, ItemStack output) implements Recipe<SingleStackRecipeInput> {
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
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.output.copy();
    }

    @Override
    public RecipeSerializer<ParingRecipe> getSerializer() {
        return PeonyRecipes.PARING;
    }

    @Override
    public RecipeType<ParingRecipe> getType() {
        return PeonyRecipes.PARING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<ParingRecipe> {
        public static final MapCodec<ParingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter(ParingRecipe::input),
                ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("output").forGetter(ParingRecipe::output)
        ).apply(instance, ParingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, ParingRecipe> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, ParingRecipe::input,
                ItemStack.PACKET_CODEC, ParingRecipe::output,
                ParingRecipe::new
        );

        @Override
        public MapCodec<ParingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ParingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
