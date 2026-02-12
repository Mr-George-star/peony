package net.george.peony.api.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.george.peony.Peony;
import net.george.peony.api.util.nbt.NbtWriteable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.Objects;

@SuppressWarnings("unused")
public interface FluidStack extends NbtWriteable {
    Codec<FluidStack> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    FluidVariant.CODEC.fieldOf("fluid").forGetter(FluidStack::getFluid),
                    Codec.LONG.fieldOf("amount").forGetter(FluidStack::getAmount)
            ).apply(instance, Default::new)
    );
    PacketCodec<RegistryByteBuf, FluidStack> PACKET_CODEC =
            PacketCodec.tuple(
                    FluidVariant.PACKET_CODEC, FluidStack::getFluid,
                    PacketCodecs.VAR_LONG, FluidStack::getAmount,
                    Default::new
            );
    FluidStack EMPTY = FluidStack.of(Fluids.EMPTY, -1);

    static FluidStack of(FluidVariant fluid, long amount) {
        return new Default(fluid, amount);
    }

    static FluidStack of(Fluid fluid, long amount) {
        return of(FluidVariant.of(fluid), amount);
    }

    static FluidStack fromStorage(SingleVariantStorage<FluidVariant> fluidStorage) {
        return of(fluidStorage.variant, fluidStorage.amount);
    }

    static FluidStack readDefault(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbtCompound = nbt.getCompound("FluidStack");
        FluidVariant variant = FluidVariant.CODEC
                .parse(registries.getOps(NbtOps.INSTANCE), nbtCompound.get("FluidVariant"))
                .resultOrPartial(error -> Peony.LOGGER.error("Failed to decode fluid variant: " + error))
                .orElseThrow(() -> new RuntimeException("Failed to get the instance of decoded fluid variant"));
        return of(variant, nbtCompound.getLong("Amount"));
    }

    FluidVariant getFluid();

    long getAmount();

    boolean isEmpty();

    FluidStack grow(long amount);

    FluidStack shrink(long amount);

    FluidStack withAmount(long amount);

    FluidStack copy();

    default String getTranslationKey() {
        Identifier id = Registries.FLUID.getId(this.getFluid().getFluid());
        return "block." + id.getNamespace() + "." + id.getPath();
    }

    class Default implements FluidStack {
        private final FluidVariant fluid;
        private final long amount;

        private Default(FluidVariant fluid, long amount) {
            this.fluid = fluid;
            this.amount = amount;
        }

        @Override
        public FluidVariant getFluid() {
            return this.fluid;
        }

        @Override
        public long getAmount() {
            return this.amount;
        }

        @Override
        public boolean isEmpty() {
            return this.amount <= 0 || this.fluid.isBlank();
        }

        @Override
        public FluidStack grow(long amount) {
            return FluidStack.of(this.fluid, this.amount + amount);
        }

        @Override
        public FluidStack shrink(long amount) {
            long value = this.amount - amount;
            return FluidStack.of(this.fluid, value >= 0 ? value : 0);
        }

        @Override
        public FluidStack withAmount(long amount) {
            return FluidStack.of(this.fluid, amount);
        }

        @Override
        public FluidStack copy() {
            return FluidStack.of(this.fluid, this.amount);
        }

        @Override
        public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
            NbtCompound nbtCompound = new NbtCompound();

            NbtElement encodedVariant = FluidVariant.CODEC
                    .encodeStart(registries.getOps(NbtOps.INSTANCE), this.fluid)
                    .resultOrPartial(error -> Peony.LOGGER.error("Failed to encode fluid variant: " + error))
                    .orElseThrow(() -> new RuntimeException("Failed to get the instance of encoded fluid variant"));
            nbtCompound.put("FluidVariant", encodedVariant);
            nbtCompound.putLong("Amount", this.amount);
            nbt.put("FluidStack", nbtCompound);
        }

        @Override
        public String toString() {
            return "DefaultFluidStack[" +
                    "fluid=" + this.fluid +
                    ", amount=" + this.amount +
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
            Default that = (Default) another;
            return this.amount == that.amount && Objects.equals(this.fluid, that.fluid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.fluid, this.amount);
        }
    }
}
