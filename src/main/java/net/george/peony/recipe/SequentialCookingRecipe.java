package net.george.peony.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.Peony;
import net.george.peony.api.data.CommonIngredientType;
import net.george.peony.block.data.CookingSteps;
import net.george.peony.block.data.Output;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class SequentialCookingRecipe implements Recipe<SequentialCookingRecipeInput> {
    protected final int temperature;
    protected final boolean needOil;
    @Nullable
    protected final CommonIngredientType<?> basicIngredient;
    protected final CookingSteps steps;
    protected final Output output;

    public SequentialCookingRecipe(int temperature, boolean needOil, @Nullable CommonIngredientType<?> basicIngredient, CookingSteps steps, Output output) {
        this.temperature = temperature;
        this.needOil = needOil;
        this.basicIngredient = basicIngredient;
        this.steps = steps;
        this.output = output;
    }

    public SequentialCookingRecipe(int temperature, boolean needOil, @Nullable CommonIngredientType<?> basicIngredient, List<CookingSteps.Step> steps, Output output) {
        this(temperature, needOil, basicIngredient, new CookingSteps(steps), output);
    }

    public SequentialCookingRecipe(int temperature, boolean needOil, Optional<CommonIngredientType<?>> basicIngredient, CookingSteps steps, Output output) {
        this(temperature, needOil, basicIngredient.orElse(null), steps, output);
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
//
//    public CookingSteps getStepsWithCommonIngredient(RecipeStepTypes type) {
//        if (this.basicIngredient == null) {
//            return this.steps;
//        } else {
//            CookingSteps.Step step = (CookingSteps.Step) this.basicIngredient.createInstance().getStep(type);
//            List<CookingSteps.Step> steps = Lists.newArrayList(step);
//            steps.addAll(this.steps.getSteps());
//            return new CookingSteps(steps);
//        }
//    }

    public Output getOutput() {
        return this.output;
    }

    @Nullable
    public CommonIngredientType<?> getBasicIngredient() {
        return this.basicIngredient;
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
            boolean basicIngredientMatches = this.basicIngredient == null || input.getCommonIngredient() == null || this.basicIngredient.equals(input.getCommonIngredient().getType());
            return matches && basicIngredientMatches;
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

    public static class Serializer implements RecipeSerializer<SequentialCookingRecipe> {
        public static final MapCodec<SequentialCookingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codecs.NONNEGATIVE_INT.fieldOf("temperature").forGetter(SequentialCookingRecipe::getTemperature),
                Codec.BOOL.fieldOf("need_oil").forGetter(SequentialCookingRecipe::isNeedOil),
                CommonIngredientType.REGISTRY.getCodec().optionalFieldOf("basic_ingredient").forGetter(recipe ->
                        Optional.ofNullable(recipe.getBasicIngredient())),
                CookingSteps.CODEC.forGetter(SequentialCookingRecipe::getSteps),
                Output.CODEC.fieldOf("output").forGetter(SequentialCookingRecipe::getOutput)
        ).apply(instance, SequentialCookingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, SequentialCookingRecipe> PACKET_CODEC = PacketCodec.of(
                SequentialCookingRecipe.Serializer::encode,
                SequentialCookingRecipe.Serializer::decode
        );

        private static void encode(SequentialCookingRecipe recipe, RegistryByteBuf buf) {
            buf.writeInt(recipe.temperature);
            buf.writeBoolean(recipe.needOil);

            if (recipe.basicIngredient != null) {
                buf.writeBoolean(true);
                buf.writeIdentifier(recipe.basicIngredient.getId());
            } else {
                buf.writeBoolean(false);
            }

            CookingSteps.PACKET_CODEC.encode(buf, recipe.steps);
            Output.PACKET_CODEC.encode(buf, recipe.output);
        }

        private static SequentialCookingRecipe decode(RegistryByteBuf buf) {
            int temperature = buf.readInt();
            boolean needOil = buf.readBoolean();

            CommonIngredientType<?> basicIngredient = null;
            boolean hasBasicIngredient = buf.readBoolean();
            if (hasBasicIngredient) {
                Identifier typeId = buf.readIdentifier();
                basicIngredient = CommonIngredientType.REGISTRY.get(typeId);
            }

            CookingSteps steps = CookingSteps.PACKET_CODEC.decode(buf);
            Output output = Output.PACKET_CODEC.decode(buf);

            return new SequentialCookingRecipe(temperature, needOil, basicIngredient, steps, output);
        }

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
