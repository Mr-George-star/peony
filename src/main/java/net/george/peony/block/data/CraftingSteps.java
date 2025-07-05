package net.george.peony.block.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.Peony;
import net.george.peony.event.CraftingProcedureRegistryCallback;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.math.Size;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.*;

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
        return Objects.equals(a.id, b.id);
    }

    public static boolean areEqual(Procedure a, Procedure... others) {
        return Arrays.stream(others).allMatch(procedure -> areEqual(a, procedure));
    }

    @Override
    public String toString() {
        return this.steps.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.steps);
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || getClass() != another.getClass()) {
            return false;
        }
        CraftingSteps steps = (CraftingSteps) another;
        return Objects.equals(this.steps, steps.steps);
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

        @Override
        public int hashCode() {
            return Objects.hash(this.procedure, this.ingredient);
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
            return Objects.equals(this.procedure, step.procedure) && Objects.equals(this.ingredient, step.ingredient);
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
        public static final Procedure CUTTING;
        protected final Identifier id;
        protected Size guideSize = Size.create();
        protected static Map<Identifier, Size> guideSizes;

        protected Procedure(Identifier id) {
            this.id = id;
            if (guideSizes.containsKey(id)) {
                this.guideSize = guideSizes.get(id);
            }
        }

        public static Procedure create(Identifier id) {
            return create(id, Size.create());
        }

        public static Procedure create(Identifier id, Size guideSize) {
            guideSizes.put(id, guideSize);
            return new Procedure(id);
        }

        public Identifier getId() {
            return this.id;
        }

        public Identifier getTextureId() {
            return Identifier.of(this.id.getNamespace(), "textures/gui/procedure/" + this.id.getPath() + ".png");
        }

        public Identifier getGuideTextureId() {
            return Identifier.of(this.id.getNamespace(), "textures/gui/procedure/" + this.id.getPath() + "_guide.png");
        }

        public String getTranslationKey() {
            return Util.createTranslationKey("crafting_procedure", this.id);
        }

        public Size getGuideSize() {
            return this.guideSize;
        }

        @Override
        public String toString() {
            return this.id.toString();
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.id);
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
            return Objects.equals(this.id, procedure.id);
        }

        static {
            guideSizes = new HashMap<>();
            KNEADING = Procedure.create(Peony.id("kneading"), Size.create(9, 19));
            CUTTING = Procedure.create(Peony.id("cutting"));
            CraftingProcedureRegistryCallback.EVENT.invoker().interact(guideSizes);
            CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("procedure").forGetter(Procedure::getId)
            ).apply(instance, Procedure::new));
            PACKET_CODEC = PacketCodec.tuple(
                    Identifier.PACKET_CODEC, Procedure::getId,
                    Procedure::new
            );
        }
    }
}
