package net.george.peony.block.entity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.george.peony.block.data.Openable;
import net.george.peony.fluid.PeonyFluids;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GasCylinderBlockEntity extends BlockEntity implements AccessibleInventory, Openable, BlockEntityTickerProvider {
    protected final SingleFluidStorage fluidStorage;
    @Nullable
    private BlockPos connectedStovePos;
    private boolean opened;
    private int usageCountdown = 0;

    public GasCylinderBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.GAS_CYLINDER, pos, state);
        this.fluidStorage = SingleFluidStorage.withFixedCapacity(FluidConstants.BUCKET, this::markDirty);
        this.opened = false;
    }

    public Optional<BlockPos> getConnectedStovePos() {
        return Optional.ofNullable(this.connectedStovePos);
    }

    public void setConnectedStovePos(BlockPos pos) {
        this.connectedStovePos = pos;
        markDirty();
    }

    public void clearConnection() {
        this.connectedStovePos = null;
        markDirty();
    }

    protected void resetCountdown() {
        this.usageCountdown = 10;
    }

    protected boolean isCountdownOver() {
        return this.usageCountdown <= 0;
    }

    @Override
    public boolean isOpened() {
        return this.opened;
    }

    @Override
    public void open() {
        this.opened = true;
    }

    @Override
    public void close() {
        this.opened = false;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world != null && !this.world.isClient) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
        }
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        this.fluidStorage.writeNbt(nbt, registryLookup);
        if (this.connectedStovePos != null) {
            nbt.put("ConnectedStove", NbtHelper.fromBlockPos(this.connectedStovePos));
        }
        nbt.putBoolean("Opened", this.opened);
        nbt.putInt("UsageCountdown", this.usageCountdown);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.fluidStorage.readNbt(nbt, registryLookup);
        if (nbt.contains("ConnectedStove")) {
            this.connectedStovePos = NbtHelper.toBlockPos(nbt, "ConnectedStove").orElse(null);
        }
        this.opened = nbt.getBoolean("Opened");
        this.usageCountdown = nbt.getInt("UsageCountdown");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
    }

    @Override
    public InsertResult insertItemSpecified(InteractionContext context, ItemStack givenStack) {
        if (givenStack.isOf(Items.STICK)) {
            if (this.isCountdownOver()) {
                if (this.isOpened()) {
                    this.close();
                } else {
                    this.open();
                }
                this.markDirty();
                this.resetCountdown();
                return AccessibleInventory.createResult(true, 0, false);
            }
        }
        return AccessibleInventory.super.insertItemSpecified(context, givenStack);
    }

    @Override
    public boolean insertItem(InteractionContext context, ItemStack givenStack) {
        if (givenStack.isOf(PeonyItems.NATURE_GAS_BUCKET)) {
            if (this.fluidStorage.amount == 0L) {
                this.transferIntoFluidStorage(FluidConstants.BUCKET);
                AccessibleInventory.playUsageSound(context, SoundEvents.ENTITY_ITEM_PICKUP, 1F, 2F);
                ItemDecrementBehaviour.createDefault().effective(context.world, context.user, context.hand);
                AccessibleInventory.increaseUsageStat(context.user, context.user.getStackInHand(context.hand));
                return true;
            }
        }
        return false;
    }

    public void transferIntoFluidStorage(long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = this.fluidStorage.insert(
                    FluidVariant.of(PeonyFluids.STILL_NATURE_GAS),
                    amount, transaction);
            if (inserted > 0) {
                transaction.commit();
                this.markDirty();
            }
        }
    }

    public void extractOutOfFluidStorage(long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            this.fluidStorage.extract(FluidVariant.of(
                            PeonyFluids.STILL_NATURE_GAS),
                    amount, transaction);
            transaction.commit();
            this.markDirty();
        }
    }

    public void consumeGas(World world) {
        if (world.random.nextInt(20) == 0) {
            extractOutOfFluidStorage(1);
            if (this.fluidStorage.amount == 0) {
                this.close();
            }
        }
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        this.usageCountdown--;
        if (this.getConnectedStovePos().isEmpty()) {
            if (this.isOpened() && this.fluidStorage.amount > 0) {
                this.consumeGas(world);
            }
        }
    }
}
