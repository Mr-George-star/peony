package net.george.peony.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Objects;

@SuppressWarnings("unused")
public class FluidStack {
    public static final Codec<FluidStack> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    FluidVariant.CODEC.fieldOf("fluid").forGetter(FluidStack::getFluid),
                    Codec.LONG.fieldOf("amount").forGetter(FluidStack::getAmount)
            ).apply(instance, FluidStack::new)
    );

    public static final PacketCodec<RegistryByteBuf, FluidStack> PACKET_CODEC =
            PacketCodec.tuple(
                    FluidVariant.PACKET_CODEC, FluidStack::getFluid,
                    PacketCodecs.VAR_LONG, FluidStack::getAmount,
                    FluidStack::new
            );
    private final FluidVariant fluid;
    private final long amount;

    protected FluidStack(FluidVariant fluid, long amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    public static FluidStack of(FluidVariant fluid, long amount) {
        return new FluidStack(fluid, amount);
    }

    public static FluidStack of(Fluid fluid, long amount) {
        return of(FluidVariant.of(fluid), amount);
    }

    public FluidVariant getFluid() {
        return this.fluid;
    }

    public long getAmount() {
        return this.amount;
    }

    public boolean isEmpty() {
        return this.amount <= 0 || this.fluid.isBlank();
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || getClass() != another.getClass()) {
            return false;
        }
        FluidStack that = (FluidStack) another;
        return this.amount == that.amount && Objects.equals(this.fluid, that.fluid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fluid, this.amount);
    }
}
