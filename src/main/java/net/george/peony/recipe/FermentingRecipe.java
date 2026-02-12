package net.george.peony.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.api.fluid.FluidStack;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public record FermentingRecipe(List<Ingredient> ingredients, Optional<FluidStack> fluidInput, int fermentingTime, Output output) implements Recipe<MixedIngredientsRecipeInput> {
    public FermentingRecipe {
        if (ingredients.isEmpty() && (fluidInput.isEmpty() || fluidInput.get().isEmpty())) {
            throw new RuntimeException("Fermenting recipe must have either ingredients or fluid input, but both are empty");
        }

        if (fermentingTime <= 0) {
            throw new RuntimeException("Fermenting time must be positive");
        }
    }

    @Override
    public boolean matches(MixedIngredientsRecipeInput input, World world) {
        FluidStack inputFluid = input.getFluid();

        DefaultedList<ItemStack> inputItems = input.getUsedInputs();

        if (this.fluidInput.isPresent()) {
            FluidStack recipeFluid = this.fluidInput.get();
            if (!recipeFluid.isEmpty()) {
                if (!recipeFluid.equals(inputFluid)) {
                    return false;
                }
            } else {
                if (!inputFluid.isEmpty()) {
                    return false;
                }
            }
        } else {
            if (!inputFluid.isEmpty()) {
                return false;
            }
        }

        if (!this.ingredients.isEmpty()) {
            if (this.ingredients.size() != inputItems.size()) {
                return false;
            }

            for (int i = 0; i < this.ingredients.size(); i++) {
                Ingredient recipeIngredient = this.ingredients.get(i);
                ItemStack inputItem = inputItems.get(i);

                if (!recipeIngredient.test(inputItem)) {
                    return false;
                }
            }
        } else {
            return inputItems.isEmpty();
        }

        return true;
    }

    @Override
    public ItemStack craft(MixedIngredientsRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return this.output.getOutputStack().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return this.output.getOutputStack().copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PeonyRecipes.FERMENTING;
    }

    @Override
    public RecipeType<?> getType() {
        return PeonyRecipes.FERMENTING_TYPE;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.addAll(this.ingredients);
        return list;
    }

    @Nullable
    public FluidStack getFluidInput() {
        return this.fluidInput.orElse(null);
    }

    public boolean hasFluidInput() {
        return this.fluidInput.isPresent() && !this.fluidInput.get().isEmpty();
    }

    public boolean hasIngredients() {
        return !this.ingredients.isEmpty();
    }

    public int fermentingTime() {
        return this.fermentingTime;
    }

    public Output output() {
        return this.output;
    }

    public static class Serializer implements RecipeSerializer<FermentingRecipe> {
        public static final MapCodec<FermentingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
                        FluidStack.CODEC.optionalFieldOf("fluid_input").forGetter(recipe -> recipe.fluidInput),
                        Codecs.POSITIVE_INT.fieldOf("fermenting_time").forGetter(recipe -> recipe.fermentingTime),
                        Output.CODEC.fieldOf("output").forGetter(recipe -> recipe.output)
                ).apply(instance, FermentingRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, FermentingRecipe> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()), FermentingRecipe::ingredients,
                FluidStack.PACKET_CODEC.xmap(
                        fluid -> fluid.isEmpty() ? Optional.empty() : Optional.of(fluid),
                        optional -> optional.orElse(FluidStack.EMPTY)
                ), FermentingRecipe::fluidInput,
                PacketCodecs.INTEGER, FermentingRecipe::fermentingTime,
                Output.PACKET_CODEC, FermentingRecipe::output,
                FermentingRecipe::new
        );

        @Override
        public MapCodec<FermentingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, FermentingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
