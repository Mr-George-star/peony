package net.george.peony.block.data;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.george.peony.Peony;
import net.george.peony.api.fluid.FluidStack;
import net.george.peony.item.PeonyItems;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public interface Output {
    ItemApiLookup<Output, Void> OIL_OUTPUTS = ItemApiLookup.get(Peony.id("oil_outputs"), Output.class, Void.class);
    MapCodec<Output> CODEC = new OutputMapCodec();
    MapCodec<Output> CONTAINER_NOT_EMPTY_CODEC = new NotEmptyOutputMapCodec();
    MapCodec<ItemOutputImpl> ITEM_OUTPUT_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ItemStack.CODEC.fieldOf("outputStack").forGetter(ItemOutputImpl::getOutputStack),
                    Registries.ITEM.getCodec().fieldOf("container").xmap(
                            item -> (ItemConvertible) item,
                            ItemConvertible::asItem
                    ).forGetter(ItemOutputImpl::getContainer)
            ).apply(instance, ItemOutputImpl::new)
    );
    MapCodec<NoContainerItemOutput> NO_CONTAINER_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ItemStack.CODEC.fieldOf("outputStack").forGetter(NoContainerItemOutput::getOutputStack)
            ).apply(instance, NoContainerItemOutput::new)
    );
    MapCodec<FluidOutputImpl> FLUID_OUTPUT_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    FluidStack.CODEC.fieldOf("outputFluid").forGetter(FluidOutputImpl::getOutputFluid),
                    Registries.ITEM.getCodec().fieldOf("container").xmap(
                            item -> (ItemConvertible) item,
                            ItemConvertible::asItem
                    ).forGetter(FluidOutputImpl::getContainer),
                    Registries.ITEM.getCodec().fieldOf("result").xmap(
                            item -> (ItemConvertible) item,
                            ItemConvertible::asItem
                    ).forGetter(FluidOutputImpl::getOutputItem)
            ).apply(instance, FluidOutputImpl::new)
    );
    PacketCodec<RegistryByteBuf, Output> PACKET_CODEC = new OutputContainerPacketCodec();

    ItemStack getOutputStack();

    ItemConvertible getContainer();

    @Nullable
    FluidStack getOutputFluid();

    static Output create(ItemStack outputStack, ItemConvertible container) {
        return new ItemOutputImpl(outputStack, container);
    }

    static Output noContainer(ItemStack outputStack) {
        return new NoContainerItemOutput(outputStack);
    }

    static Output createFluid(FluidStack outputFluid, ItemConvertible container, ItemConvertible result) {
        return new FluidOutputImpl(outputFluid, container, result);
    }

    @Nullable
    static ItemConvertible getRequiredContainer(Output output) {
        return output.getContainer() == PeonyItems.PLACEHOLDER ? null : output.getContainer();
    }

    class ItemOutputImpl implements Output {
        protected final ItemStack outputStack;
        protected final ItemConvertible container;

        protected ItemOutputImpl(ItemStack outputStack, ItemConvertible container) {
            this.outputStack = outputStack;
            this.container = container;
        }

        @Override
        public ItemStack getOutputStack() {
            return this.outputStack;
        }

        @Override
        public ItemConvertible getContainer() {
            return this.container;
        }

        @Override
        @Nullable
        public FluidStack getOutputFluid() {
            return null;
        }

        @Override
        public String toString() {
            return "ItemOutputImpl[" +
                    "outputStack=" + this.outputStack +
                    ", container=" + this.container +
                    ']';
        }
    }

    class NoContainerItemOutput implements Output {
        protected final ItemStack outputStack;

        protected NoContainerItemOutput(ItemStack outputStack) {
            this.outputStack = outputStack;
        }

        @Override
        public ItemStack getOutputStack() {
            return this.outputStack;
        }

        @Override
        public ItemConvertible getContainer() {
            return PeonyItems.PLACEHOLDER;
        }

        @Override
        @Nullable
        public FluidStack getOutputFluid() {
            return null;
        }

        @Override
        public String toString() {
            return "NoContainerItemOutput[" +
                    "outputStack=" + this.outputStack +
                    ']';
        }
    }

    class FluidOutputImpl implements Output {
        protected final FluidStack outputFluid;
        protected final ItemConvertible container;
        protected final ItemConvertible result;

        protected FluidOutputImpl(FluidStack outputFluid, ItemConvertible container, ItemConvertible result) {
            this.outputFluid = outputFluid;
            this.container = container;
            this.result = result;
        }

        @Override
        public ItemStack getOutputStack() {
            return new ItemStack(this.result);
        }

        @Override
        public ItemConvertible getContainer() {
            return this.container;
        }

        @Override
        public FluidStack getOutputFluid() {
            return this.outputFluid;
        }

        public ItemConvertible getOutputItem() {
            return this.getOutputStack().getItem();
        }

        @Override
        public String toString() {
            return "FluidOutputImpl[" +
                    "outputFluid=" + this.outputFluid +
                    ", container=" + this.container +
                    ", result=" + this.result +
                    ']';
        }
    }

    class OutputMapCodec extends MapCodec<Output> {
        @Override
        public <T> DataResult<Output> decode(DynamicOps<T> ops, MapLike<T> input) {
            if (input.get("outputFluid") != null) {
                return FLUID_OUTPUT_CODEC.decode(ops, input).map(impl -> impl);
            }
            else if (input.get("container") != null) {
                return ITEM_OUTPUT_CODEC.decode(ops, input).map(impl -> impl);
            } else {
                return NO_CONTAINER_CODEC.decode(ops, input).map(noContainer -> noContainer);
            }
        }

        @Override
        public <T> RecordBuilder<T> encode(Output input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            if (input instanceof FluidOutputImpl fluidOutput) {
                return FLUID_OUTPUT_CODEC.encode(fluidOutput, ops, prefix);
            } else if (input instanceof ItemOutputImpl itemOutput) {
                return ITEM_OUTPUT_CODEC.encode(itemOutput, ops, prefix);
            } else if (input instanceof NoContainerItemOutput noContainer) {
                return NO_CONTAINER_CODEC.encode(noContainer, ops, prefix);
            }
            return prefix;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.concat(
                    Stream.concat(
                            ITEM_OUTPUT_CODEC.keys(ops),
                            NO_CONTAINER_CODEC.keys(ops)
                    ),
                    FLUID_OUTPUT_CODEC.keys(ops)
            );
        }
    }

    class NotEmptyOutputMapCodec extends MapCodec<Output> {
        @Override
        public <T> DataResult<Output> decode(DynamicOps<T> ops, MapLike<T> input) {
            if (input.get("container") == null) {
                return DataResult.error(() -> "Container cannot be placeholder!");
            }
            if (input.get("outputFluid") != null) {
                return FLUID_OUTPUT_CODEC.decode(ops, input).map(impl -> impl);
            }
            return ITEM_OUTPUT_CODEC.decode(ops, input).map(impl -> impl);
        }

        @Override
        public <T> RecordBuilder<T> encode(Output input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            if (input.getContainer() == PeonyItems.PLACEHOLDER) {
                return new RecordBuilder.MapBuilder<>(ops).mapError(error -> "Container cannot be placeholder! Error: " + error);
            }
            return switch (input) {
                case FluidOutputImpl fluidOutput -> FLUID_OUTPUT_CODEC.encode(fluidOutput, ops, prefix);
                case ItemOutputImpl itemOutput -> ITEM_OUTPUT_CODEC.encode(itemOutput, ops, prefix);
                default -> prefix;
            };
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.concat(
                    Stream.concat(
                            ITEM_OUTPUT_CODEC.keys(ops),
                            NO_CONTAINER_CODEC.keys(ops)
                    ),
                    FLUID_OUTPUT_CODEC.keys(ops)
            );
        }
    }

    class OutputContainerPacketCodec implements PacketCodec<RegistryByteBuf, Output> {
        private final PacketCodec<RegistryByteBuf, ItemConvertible> itemPacketCodec =
                ItemStack.PACKET_CODEC.xmap(ItemStack::getItem, ItemStack::new);

        @Override
        public Output decode(RegistryByteBuf buf) {
            byte type = buf.readByte();

            if (type == 0) {
                return new ItemOutputImpl(
                        ItemStack.PACKET_CODEC.decode(buf),
                        this.itemPacketCodec.decode(buf)
                );
            } else if (type == 1) {
                return new NoContainerItemOutput(ItemStack.PACKET_CODEC.decode(buf));
            } else if (type == 2) {
                return new FluidOutputImpl(
                        FluidStack.PACKET_CODEC.decode(buf),
                        this.itemPacketCodec.decode(buf),
                        this.itemPacketCodec.decode(buf)
                );
            } else {
                throw new IllegalStateException("Unknown output type: " + type);
            }
        }

        @Override
        public void encode(RegistryByteBuf buf, Output value) {
            if (value instanceof ItemOutputImpl impl) {
                buf.writeByte(0);
                ItemStack.PACKET_CODEC.encode(buf, impl.outputStack);
                this.itemPacketCodec.encode(buf, impl.container);
            } else if (value instanceof NoContainerItemOutput noContainer) {
                buf.writeByte(1);
                ItemStack.PACKET_CODEC.encode(buf, noContainer.outputStack);
            } else if (value instanceof FluidOutputImpl fluidOutput) {
                buf.writeByte(2);
                FluidStack.PACKET_CODEC.encode(buf, fluidOutput.outputFluid);
                this.itemPacketCodec.encode(buf, fluidOutput.container);
                this.itemPacketCodec.encode(buf, fluidOutput.result);
            }
        }
    }
}
