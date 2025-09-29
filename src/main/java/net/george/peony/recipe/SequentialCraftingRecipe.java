package net.george.peony.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.block.data.CraftingSteps;
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

import java.util.List;

public class SequentialCraftingRecipe implements Recipe<SingleStackRecipeInput> {
    protected final CraftingSteps steps;
    protected final ItemStack output;

    public SequentialCraftingRecipe(CraftingSteps steps, ItemStack output) {
        this.steps = steps;
        this.output = output;
    }

    public SequentialCraftingRecipe(List<CraftingSteps.Step> steps, ItemStack output) {
        this(new CraftingSteps(steps), output);
    }

    public CraftingSteps getSteps() {
        return this.steps;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {
        if (!world.isClient) {
            return this.steps.getSteps().getFirst().getIngredient().test(input.getStackInSlot(0));
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
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.addAll(this.steps.getIngredients());
        return list;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.output.copy();
    }

    @Override
    public RecipeSerializer<SequentialCraftingRecipe> getSerializer() {
        return PeonyRecipes.SEQUENTIAL_CRAFTING;
    }

    @Override
    public RecipeType<SequentialCraftingRecipe> getType() {
        return PeonyRecipes.SEQUENTIAL_CRAFTING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<SequentialCraftingRecipe> {
        public static final MapCodec<SequentialCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CraftingSteps.CODEC.forGetter(SequentialCraftingRecipe::getSteps),
                ItemStack.CODEC.fieldOf("output").forGetter(SequentialCraftingRecipe::getOutput)
        ).apply(instance, SequentialCraftingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, SequentialCraftingRecipe> PACKET_CODEC = PacketCodec.tuple(
                CraftingSteps.PACKET_CODEC, SequentialCraftingRecipe::getSteps,
                ItemStack.PACKET_CODEC, SequentialCraftingRecipe::getOutput,
                SequentialCraftingRecipe::new
        );

        @Override
        public MapCodec<SequentialCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SequentialCraftingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
