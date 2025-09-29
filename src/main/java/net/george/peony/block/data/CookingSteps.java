package net.george.peony.block.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.item.PeonyItems;
import net.minecraft.item.ItemConvertible;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class CookingSteps extends RecipeSteps<CookingSteps.Step> {
    public static final MapCodec<CookingSteps> CODEC;
    public static final PacketCodec<RegistryByteBuf, CookingSteps> PACKET_CODEC;

    public CookingSteps(List<Step> steps) {
        super(steps);
    }

    protected void write(RegistryByteBuf buf) {
        buf.writeVarInt(this.steps.size());
        for (Step step : this.steps) {
            Step.PACKET_CODEC.encode(buf, step);
        }
    }

    protected static CookingSteps read(RegistryByteBuf buf) {
        int count = buf.readVarInt();
        List<Step> steps = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            steps.add(Step.PACKET_CODEC.decode(buf));
        }
        return new CookingSteps(steps);
    }

    static {
        Codec<List<Step>> stepsCodec = Codec.list(Step.CODEC.codec());
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                stepsCodec.fieldOf("steps").forGetter(CookingSteps::getSteps)
        ).apply(instance, CookingSteps::new));
        PACKET_CODEC = PacketCodec.of(CookingSteps::write, CookingSteps::read);
    }

    public static class Step extends RecipeStep {
        public static final MapCodec<Step> CODEC;
        public static final PacketCodec<RegistryByteBuf, Step> PACKET_CODEC;
        final int requiredTime;
        final int maxTimeOverflow;
        final Ingredient requiredTool;
        final Ingredient ingredient;
        final boolean oilPlacingStep;

        public Step(int requiredTime, int maxTimeOverflow, Ingredient requiredTool, Ingredient ingredient) {
            super(ingredient);
            this.requiredTime = requiredTime;
            this.maxTimeOverflow = maxTimeOverflow;
            this.requiredTool = requiredTool;
            this.ingredient = ingredient;
            this.oilPlacingStep = false;
        }

        /**
         * Special constructor for oil melting steps
         * <br>- Uses predefined ingredients (LARD and SPATULA)
         * <br>- Marks step as oilPlacingStep for special handling
         */
        public Step(int requiredTime, int maxTimeOverflow) {
            super(ofItem(PeonyItems.LARD));
            this.requiredTime = requiredTime;
            this.maxTimeOverflow = maxTimeOverflow;
            this.requiredTool = ofItem(PeonyItems.SPATULA);
            this.ingredient = ofItem(PeonyItems.LARD);
            this.oilPlacingStep = true;
        }

        public Step(int requiredTime, int maxTimeOverflow, ItemConvertible requiredTool, ItemConvertible ingredient) {
            this(requiredTime, maxTimeOverflow, ofItem(requiredTool), ofItem(ingredient));
        }

        public Step(int requiredTime, int maxTimeOverflow, ItemConvertible ingredient) {
            this(requiredTime, maxTimeOverflow, ofItem(PeonyItems.PLACEHOLDER), ofItem(ingredient));
        }

        public int getRequiredTime() {
            return this.requiredTime;
        }

        public int getMaxTimeOverflow() {
            return this.maxTimeOverflow;
        }

        public Ingredient getRequiredTool() {
            return this.requiredTool;
        }

        public boolean isOilPlacingStep() {
            return this.oilPlacingStep;
        }

        protected void write(RegistryByteBuf buf) {
            PacketCodecs.INTEGER.encode(buf, this.requiredTime);
            PacketCodecs.INTEGER.encode(buf, this.maxTimeOverflow);
            Ingredient.PACKET_CODEC.encode(buf, this.requiredTool);
            Ingredient.PACKET_CODEC.encode(buf, this.ingredient);
        }

        protected static CookingSteps.Step read(RegistryByteBuf buf) {
            int requiredTime = PacketCodecs.INTEGER.decode(buf);
            int maxTimeOverflow = PacketCodecs.INTEGER.decode(buf);
            if (requiredTime < 0) {
                requiredTime = 0;
            }
            if (maxTimeOverflow < 0) {
                maxTimeOverflow = 0;
            }
            return new CookingSteps.Step(requiredTime, maxTimeOverflow, Ingredient.PACKET_CODEC.decode(buf), Ingredient.PACKET_CODEC.decode(buf));
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.requiredTime, this.maxTimeOverflow, this.requiredTool, this.ingredient);
        }

        @Override
        public String toString() {
            return "Step[" +
                    "requiredTime=" + this.requiredTime +
                    ", maxTimeOverflow=" + this.maxTimeOverflow +
                    ", requiredTool=" + this.requiredTool +
                    ", ingredient=" + this.ingredient +
                    ']';
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
            return this.requiredTime == step.requiredTime &&
                    this.maxTimeOverflow == step.maxTimeOverflow &&
                    Objects.equals(this.requiredTool, step.requiredTool) &&
                    Objects.equals(this.ingredient, step.ingredient);
        }

        static {
            CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codecs.NONNEGATIVE_INT.optionalFieldOf("requiredTime", 0)
                            .forGetter(Step::getRequiredTime),
                    Codecs.NONNEGATIVE_INT.optionalFieldOf("maxTimeOverflow", 0)
                            .forGetter(Step::getMaxTimeOverflow),
                    Ingredient.DISALLOW_EMPTY_CODEC.optionalFieldOf("requiredTool", ofItem(PeonyItems.PLACEHOLDER))
                            .forGetter(Step::getRequiredTool),
                    Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(Step::getIngredient)
            ).apply(instance, Step::new));
            PACKET_CODEC = PacketCodec.of(Step::write, Step::read);
        }
    }
}
