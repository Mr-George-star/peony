package net.george.peony.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.api.fluid.FluidStack;
import net.george.peony.api.fluid.FluidUtils;
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

        DefaultedList<ItemStack> inputItems = DefaultedList.of();
        for (int i = 0; i < input.getSize(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (!stack.isEmpty()) {
                inputItems.add(stack);
            }
        }

        if (this.fluidInput.isPresent()) {
            FluidStack recipeFluid = this.fluidInput.get();
            if (!recipeFluid.isEmpty()) {
                if (!recipeFluid.equals(inputFluid)) {
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

            boolean[] used = new boolean[inputItems.size()];
            for (Ingredient ingredient : this.ingredients) {
                boolean found = false;
                for (int i = 0; i < inputItems.size(); i++) {
                    if (!used[i] && ingredient.test(inputItems.get(i))) {
                        used[i] = true;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        } else {
            return inputItems.isEmpty();
        }

        return true;
    }

    // 辅助方法：检查输入是否匹配，考虑物品到流体的转换
    public boolean matchesWithConversion(MixedIngredientsRecipeInput input, World world) {
        // 首先尝试直接匹配
        if (this.matches(input, world)) {
            return true;
        }

        // 如果直接匹配失败，检查是否有物品可以转换成流体
        if (!this.ingredients.isEmpty() && this.fluidInput.isEmpty()) {
            // 配方只有物品输入，没有流体输入
            // 检查输入的物品是否可以转换成流体
            DefaultedList<ItemStack> inputItems = DefaultedList.of();
            for (int i = 0; i < input.getSize(); i++) {
                ItemStack stack = input.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    inputItems.add(stack);
                }
            }

            // 检查物品数量是否匹配
            if (this.ingredients.size() != inputItems.size()) {
                return false;
            }

            // 检查是否所有配方成分都能在输入中找到，并且这些物品可以转换成流体
            for (Ingredient ingredient : this.ingredients) {
                boolean found = false;
                for (ItemStack inputItem : inputItems) {
                    if (ingredient.test(inputItem)) {
                        if (FluidUtils.hasFluidInItem(inputItem)) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    return false;
                }
            }

            return true;
        }

        return false;
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
