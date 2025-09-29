package net.george.peony.block.data;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.FluidStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public interface Output {
    MapCodec<Output> CODEC = new OutputContainerMapCodec();
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
    }

    class OutputContainerMapCodec extends MapCodec<Output> {
        private final MapCodec<ItemOutputImpl> itemOutputCodec = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ItemStack.CODEC.fieldOf("outputStack").forGetter(ItemOutputImpl::getOutputStack),
                        Registries.ITEM.getCodec().fieldOf("container").xmap(
                                item -> (ItemConvertible) item,
                                ItemConvertible::asItem
                        ).forGetter(ItemOutputImpl::getContainer)
                ).apply(instance, ItemOutputImpl::new)
        );
        private final MapCodec<NoContainerItemOutput> noContainerCodec = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ItemStack.CODEC.fieldOf("outputStack").forGetter(NoContainerItemOutput::getOutputStack)
                ).apply(instance, NoContainerItemOutput::new)
        );
        private final MapCodec<FluidOutputImpl> fluidOutputCodec = RecordCodecBuilder.mapCodec(instance ->
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

        @Override
        public <T> DataResult<Output> decode(DynamicOps<T> ops, MapLike<T> input) {
            if (input.get("outputFluid") != null) {
                return this.fluidOutputCodec.decode(ops, input).map(impl -> impl);
            }
            else if (input.get("container") != null) {
                return this.itemOutputCodec.decode(ops, input).map(impl -> impl);
            } else {
                return this.noContainerCodec.decode(ops, input).map(noContainer -> noContainer);
            }
        }

        @Override
        public <T> RecordBuilder<T> encode(Output input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            if (input instanceof FluidOutputImpl fluidOutput) {
                return this.fluidOutputCodec.encode(fluidOutput, ops, prefix);
            } else if (input instanceof ItemOutputImpl itemOutput) {
                return this.itemOutputCodec.encode(itemOutput, ops, prefix);
            } else if (input instanceof NoContainerItemOutput noContainer) {
                return this.noContainerCodec.encode(noContainer, ops, prefix);
            }
            return prefix;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.concat(
                    Stream.concat(
                            this.itemOutputCodec.keys(ops),
                            this.noContainerCodec.keys(ops)
                    ),
                    this.fluidOutputCodec.keys(ops)
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
