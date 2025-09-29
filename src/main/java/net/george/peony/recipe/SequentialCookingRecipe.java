package net.george.peony.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.Peony;
import net.george.peony.block.data.CookingSteps;
import net.george.peony.block.data.Output;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.List;

public class SequentialCookingRecipe implements Recipe<SequentialCookingRecipeInput> {
    protected final int temperature;
    protected final boolean needOil;
    protected final CookingSteps steps;
    protected final Output output;

    public SequentialCookingRecipe(int temperature, boolean needOil, CookingSteps steps, Output output) {
        this.temperature = temperature;
        this.needOil = needOil;
        this.steps = steps;
        this.output = output;
    }

    public SequentialCookingRecipe(int temperature, boolean needOil, List<CookingSteps.Step> steps, Output output) {
        this(temperature, needOil, new CookingSteps(steps), output);
    }

    public int getTemperature() {
        return this.temperature;
    }

    public boolean isNeedOil() {
        return this.needOil;
    }

    public CookingSteps getSteps() {
        return this.steps;
    }

    public Output getOutput() {
        return this.output;
    }

    /**
     * Checks if the input matches this recipe's requirements
     * <br>- Validates against the first step's ingredient
     * <br>- Only matches on server side to prevent client-side recipe leaks
     */
    @Override
    public boolean matches(SequentialCookingRecipeInput input, World world) {
        if (!world.isClient) {
            // Check if input matches the first step's ingredient requirement
            if (this.steps.getSteps().isEmpty()) {
                return false;
            }
            Ingredient firstIngredient = this.steps.getSteps().getFirst().getIngredient();
            boolean matches = firstIngredient.test(input.getStackInSlot(0));
            Peony.LOGGER.debug("Recipe [???] matching input {}: {}", input.getStackInSlot(0).getItem(), matches);
            return matches;
        }
        return false;
    }

    @Override
    public ItemStack craft(SequentialCookingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.output.getOutputStack().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.addAll(this.steps.getIngredients());
        return list;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.output.getOutputStack().copy();
    }

    @Override
    public RecipeSerializer<SequentialCookingRecipe> getSerializer() {
        return PeonyRecipes.SEQUENTIAL_COOKING;
    }

    @Override
    public RecipeType<SequentialCookingRecipe> getType() {
        return PeonyRecipes.SEQUENTIAL_COOKING_TYPE;
    }

    @Override
    public String toString() {
        return "matchFrom " + this.steps.getSteps().getFirst().getIngredient().getMatchingStacks()[0] + " and it's " + this.needOil;
    }

    public static class Serializer implements RecipeSerializer<SequentialCookingRecipe> {
        public static final MapCodec<SequentialCookingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codecs.NONNEGATIVE_INT.fieldOf("temperature").forGetter(SequentialCookingRecipe::getTemperature),
                Codec.BOOL.fieldOf("needOil").forGetter(SequentialCookingRecipe::isNeedOil),
                CookingSteps.CODEC.forGetter(SequentialCookingRecipe::getSteps),
                Output.CODEC.fieldOf("output").forGetter(SequentialCookingRecipe::getOutput)
        ).apply(instance, SequentialCookingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, SequentialCookingRecipe> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.INTEGER, SequentialCookingRecipe::getTemperature,
                PacketCodecs.BOOL, SequentialCookingRecipe::isNeedOil,
                CookingSteps.PACKET_CODEC, SequentialCookingRecipe::getSteps,
                Output.PACKET_CODEC, SequentialCookingRecipe::getOutput,
                SequentialCookingRecipe::new
        );

        @Override
        public MapCodec<SequentialCookingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SequentialCookingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
