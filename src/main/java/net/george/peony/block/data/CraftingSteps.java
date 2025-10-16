package net.george.peony.block.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.api.action.Action;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;

import java.util.*;

public class CraftingSteps extends RecipeSteps<CraftingSteps.Step> {
    public static final MapCodec<CraftingSteps> CODEC;
    public static final PacketCodec<RegistryByteBuf, CraftingSteps> PACKET_CODEC;

    public CraftingSteps(List<Step> steps) {
        super(steps);
    }

    protected void write(RegistryByteBuf buf) {
        buf.writeVarInt(this.steps.size());
        for (Step step : this.steps) {
            Step.PACKET_CODEC.encode(buf, step);
        }
    }

    protected static CraftingSteps read(RegistryByteBuf buf) {
        int count = buf.readVarInt();
        List<Step> steps = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            steps.add(Step.PACKET_CODEC.decode(buf));
        }
        return new CraftingSteps(steps);
    }

    static {
        Codec<List<Step>> stepsCodec = Codec.list(Step.CODEC.codec());
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            stepsCodec.fieldOf("steps").forGetter(CraftingSteps::getSteps)
        ).apply(instance, CraftingSteps::new));
        PACKET_CODEC = PacketCodec.of(CraftingSteps::write, CraftingSteps::read);
    }

    public static class Step extends RecipeStep {
        public static final MapCodec<Step> CODEC;
        public static final PacketCodec<RegistryByteBuf, Step> PACKET_CODEC;
        final Action action;

        public Step(Action action, Ingredient ingredient) {
            super(ingredient);
            this.action = action;
        }

        public Action getAction() {
            return this.action;
        }

        protected void write(RegistryByteBuf buf) {
            Action.PACKET_CODEC.encode(buf, this.action);
            Ingredient.PACKET_CODEC.encode(buf, this.ingredient);
        }

        protected static Step read(RegistryByteBuf buf) {
            return new Step(Action.PACKET_CODEC.decode(buf), Ingredient.PACKET_CODEC.decode(buf));
        }

        @Override
        public String toString() {
            return "Step[" +
                    "action=" + this.action +
                    ", ingredient=" + this.ingredient.getMatchingStacks()[0] +
                    ']';
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.action, this.ingredient);
        }

        @Override
        public boolean equals(Object another) {
            if (this == another) {
                return true;
            }
            if (another == null || getClass() != another.getClass()) {
                return false;
            }
            Step step = (Step) another;
            return Objects.equals(this.action, step.action) && Objects.equals(this.ingredient, step.ingredient);
        }

        static {
            CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Action.CODEC.fieldOf("action").forGetter(Step::getAction),
                    Ingredient.DISALLOW_EMPTY_CODEC.optionalFieldOf("ingredient")
                            .xmap(
                                    optional -> optional.orElseGet(Step::getDefaultIngredient),
                                    ingredient -> ingredient.equals(getDefaultIngredient()) ? Optional.empty() : Optional.of(ingredient)
                            ).forGetter(Step::getIngredient)
            ).apply(instance, Step::new));
            PACKET_CODEC = PacketCodec.of(Step::write, Step::read);
        }
    }
}
