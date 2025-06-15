package net.george.peony.block.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.item.PeonyItems;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class CraftingSteps {
    public static final MapCodec<CraftingSteps> CODEC;
    public static final PacketCodec<RegistryByteBuf, CraftingSteps> PACKET_CODEC;

    protected List<Step> steps;

    public CraftingSteps(List<Step> steps) {
        this.steps = steps;
    }

    @SuppressWarnings("unused")
    public CraftingSteps(Step... steps) {
        this(Arrays.stream(steps).toList());
    }

    public List<Step> getSteps() {
        return this.steps;
    }

    public int getLength() {
        return this.steps.size();
    }

    public CraftingStepsFetcher createFetcher(int currentIndex) {
        if (currentIndex <= (this.getSteps().size() - 1)) {
            return new CraftingStepsFetcher(this.steps, currentIndex);
        } else {
            return new CraftingStepsFetcher(this.steps, this.getSteps().size() - 1);
        }
    }

    public List<Ingredient> getIngredients() {
        return this.steps.stream().map(Step::getIngredient).toList();
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

    public static boolean areEqual(Procedure a, Procedure b) {
        return Objects.equals(a.name, b.name);
    }

    public static boolean areEqual(Procedure a, Procedure... others) {
        return Arrays.stream(others).allMatch(procedure -> areEqual(a, procedure));
    }

    static {
        Codec<List<Step>> stepsCodec = Codec.list(Step.CODEC.codec());
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            stepsCodec.fieldOf("steps").forGetter(CraftingSteps::getSteps)
        ).apply(instance, CraftingSteps::new));
        PACKET_CODEC = PacketCodec.of(CraftingSteps::write, CraftingSteps::read);
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class Step {
        public static final MapCodec<Step> CODEC;
        public static final PacketCodec<RegistryByteBuf, Step> PACKET_CODEC;
        final Procedure procedure;
        final Ingredient ingredient;

        public Step(Procedure procedure, Ingredient ingredient) {
            this.procedure = procedure;
            this.ingredient = ingredient;
        }

        public Procedure getProcedure() {
            return this.procedure;
        }

        public Ingredient getIngredient() {
            return this.ingredient;
        }

        protected void write(RegistryByteBuf buf) {
            Procedure.PACKET_CODEC.encode(buf, this.procedure);
            Ingredient.PACKET_CODEC.encode(buf, this.ingredient);
        }

        protected static Step read(RegistryByteBuf buf) {
            return new Step(Procedure.PACKET_CODEC.decode(buf), Ingredient.PACKET_CODEC.decode(buf));
        }

        @Override
        public String toString() {
            return "Step[" +
                    "procedure=" + this.procedure +
                    ", ingredient=" + this.ingredient.getMatchingStacks()[0] +
                    ']';
        }

        static {
            CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Procedure.CODEC.forGetter(Step::getProcedure),
                    Ingredient.DISALLOW_EMPTY_CODEC.optionalFieldOf("ingredient",
                            Ingredient.ofStacks(PeonyItems.PLACEHOLDER.getDefaultStack())).forGetter(Step::getIngredient)
            ).apply(instance, Step::new));
            PACKET_CODEC = PacketCodec.of(Step::write, Step::read);
        }
    }

    public static class Procedure {
        public static final MapCodec<Procedure> CODEC;
        public static final PacketCodec<RegistryByteBuf, Procedure> PACKET_CODEC;
        public static final Procedure KNEADING;
        protected final String name;

        protected Procedure(String name) {
            this.name = name;
        }

        public static Procedure create(String name) {
            return new Procedure(name);
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            Procedure procedure = (Procedure) other;
            return Objects.equals(this.name, procedure.name);
        }

        public String getName() {
            return this.name;
        }

        public boolean isNormalProcedure() {
            return areEqual(this, KNEADING);
        }

        static {
            KNEADING = Procedure.create("kneading");
            CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codecs.NON_EMPTY_STRING.fieldOf("procedure").forGetter(Procedure::getName)
            ).apply(instance, Procedure::new));
            PACKET_CODEC = PacketCodec.tuple(
                    PacketCodecs.STRING, Procedure::getName,
                    Procedure::new
            );
        }
    }
}
