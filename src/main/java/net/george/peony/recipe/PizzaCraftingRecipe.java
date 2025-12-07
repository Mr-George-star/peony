package net.george.peony.recipe;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.block.entity.FlatbreadBlockEntity;
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
import net.minecraft.world.World;

import java.util.List;

public class PizzaCraftingRecipe implements Recipe<PizzaCraftingRecipeInput> {
    protected final List<Ingredient> ingredients;
    protected final ItemStack output;

    public PizzaCraftingRecipe(List<Ingredient> ingredients, ItemStack output) {
        Preconditions.checkState(ingredients.size() <= FlatbreadBlockEntity.MAX_INGREDIENTS, "The count of ingredients cannot be more than 8!");
        this.ingredients = ingredients;
        this.output = output;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public boolean matches(PizzaCraftingRecipeInput input, World world) {
        if (!world.isClient) {
            if (input.getIngredients().size() == this.ingredients.size()) {
                for (int index = 0; index < this.ingredients.size(); index++) {
                    if (!this.ingredients.get(index).test(input.getStackInSlot(index))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack craft(PizzaCraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.addAll(this.ingredients);
        return list;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.output.copy();
    }

    @Override
    public RecipeSerializer<PizzaCraftingRecipe> getSerializer() {
        return PeonyRecipes.PIZZA_CRAFTING;
    }

    @Override
    public RecipeType<PizzaCraftingRecipe> getType() {
        return PeonyRecipes.PIZZA_CRAFTING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<PizzaCraftingRecipe> {
        public static final MapCodec<PizzaCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.sizeLimitedListOf(FlatbreadBlockEntity.MAX_INGREDIENTS)
                        .fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
                ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("output").forGetter(PizzaCraftingRecipe::getOutput)
        ).apply(instance, PizzaCraftingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, PizzaCraftingRecipe> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC.collect(PacketCodecs.toList(FlatbreadBlockEntity.MAX_INGREDIENTS)),
                PizzaCraftingRecipe::getIngredients,
                ItemStack.PACKET_CODEC, PizzaCraftingRecipe::getOutput,
                PizzaCraftingRecipe::new
        );

        @Override
        public MapCodec<PizzaCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PizzaCraftingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
