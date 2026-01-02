package net.george.peony.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.block.data.Output;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.List;

public record FlavouringPreparingRecipe(List<ItemStack> ingredients, int stirringTimes, Output output) implements Recipe<ListedRecipeInput> {
    @Override
    public boolean matches(ListedRecipeInput input, World world) {
        List<ItemStack> inputs = BrewingRecipe.padListStream(this.ingredients, input.getSize(), ItemStack.EMPTY);

        for (int i = 0; i < input.getSize(); i++) {
            ItemStack recipeStack = inputs.get(i);
            ItemStack inputStack = input.getStackInSlot(i);

            if (!recipeStack.isEmpty() && !ItemStack.areItemsEqual(recipeStack, inputStack)) {
                return false;
            }
            if (recipeStack.isEmpty() && !inputStack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(ListedRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.output.getOutputStack().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.output.getOutputStack().copy();
    }

    @Override
    public RecipeSerializer<FlavouringPreparingRecipe> getSerializer() {
        return PeonyRecipes.FLAVOURING_PREPARING;
    }

    @Override
    public RecipeType<FlavouringPreparingRecipe> getType() {
        return PeonyRecipes.FLAVOURING_PREPARING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<FlavouringPreparingRecipe> {
        public static final MapCodec<FlavouringPreparingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.VALIDATED_UNCOUNTED_CODEC.listOf().fieldOf("ingredients").forGetter(FlavouringPreparingRecipe::ingredients),
                Codecs.NONNEGATIVE_INT.fieldOf("stirring_times").forGetter(FlavouringPreparingRecipe::stirringTimes),
                Output.CODEC.fieldOf("output").forGetter(FlavouringPreparingRecipe::output)
        ).apply(instance, FlavouringPreparingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, FlavouringPreparingRecipe> PACKET_CODEC = PacketCodec.tuple(
                ItemStack.LIST_PACKET_CODEC, FlavouringPreparingRecipe::ingredients,
                PacketCodecs.INTEGER, FlavouringPreparingRecipe::stirringTimes,
                Output.PACKET_CODEC, FlavouringPreparingRecipe::output,
                FlavouringPreparingRecipe::new
        );

        @Override
        public MapCodec<FlavouringPreparingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, FlavouringPreparingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
