package net.george.peony.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.george.peony.api.fluid.FluidStack;
import net.george.peony.block.data.Output;
import net.minecraft.fluid.Fluids;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record BrewingRecipe(List<ItemStack> ingredients, FluidStack basicFluid, int brewingTime, Output output) implements Recipe<MixedIngredientsRecipeInput> {
    public static final FluidStack DEFAULT_FLUID_STACK = FluidStack.of(FluidVariant.of(Fluids.WATER), FluidConstants.BOTTLE);

    @Override
    public boolean matches(MixedIngredientsRecipeInput input, World world) {
        List<ItemStack> inputs = padListStream(this.ingredients, input.getSize(), ItemStack.EMPTY);

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

        return input.getFluid().equals(this.basicFluid);
    }

    @Override
    public ItemStack craft(MixedIngredientsRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
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
    public RecipeSerializer<BrewingRecipe> getSerializer() {
        return PeonyRecipes.BREWING;
    }

    @Override
    public RecipeType<BrewingRecipe> getType() {
        return PeonyRecipes.BREWING_TYPE;
    }

    public static <T> List<T> padListStream(List<T> listA, int listBSize, T defaultValue) {
        return Stream.concat(
                listA.stream(),
                Stream.generate(() -> defaultValue)
                        .limit(Math.max(0, listBSize - listA.size()))
        ).collect(Collectors.toList());
    }

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {
        public static final MapCodec<BrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.OPTIONAL_CODEC.listOf().fieldOf("ingredients").forGetter(BrewingRecipe::ingredients),
                FluidStack.CODEC.optionalFieldOf("basic_fluid", DEFAULT_FLUID_STACK).forGetter(BrewingRecipe::basicFluid),
                Codecs.NONNEGATIVE_INT.fieldOf("brewing_time").forGetter(BrewingRecipe::brewingTime),
                Output.CONTAINER_NOT_EMPTY_CODEC.fieldOf("output").forGetter(BrewingRecipe::output)
        ).apply(instance, BrewingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, BrewingRecipe> PACKET_CODEC = PacketCodec.tuple(
                ItemStack.OPTIONAL_LIST_PACKET_CODEC, BrewingRecipe::ingredients,
                FluidStack.PACKET_CODEC, BrewingRecipe::basicFluid,
                PacketCodecs.INTEGER, BrewingRecipe::brewingTime,
                Output.PACKET_CODEC, BrewingRecipe::output,
                BrewingRecipe::new
        );

        @Override
        public MapCodec<BrewingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, BrewingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
