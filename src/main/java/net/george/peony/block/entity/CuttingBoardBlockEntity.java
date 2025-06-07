package net.george.peony.block.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.george.networking.api.GameNetworking;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.networking.payload.ClearInventoryS2CPayload;
import net.george.peony.networking.payload.ItemStackSyncS2CPayload;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Objects;

public class CuttingBoardBlockEntity extends BlockEntity implements StackTransformableInventory, AccessibleInventory {
    protected final DefaultedList<ItemStack> inventory;

    public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.CUTTING_BOARD, pos, state);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public ItemStack getInputStack() {
        return this.getStack(0);
    }

    public void setInputStack(ItemStack stack) {
        this.inventory.set(0, stack);
    }

    public void setInventory(DefaultedList<ItemStack> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            this.inventory.set(i, inventory.get(i));
        }
    }

    @Override
    public Direction getCurrentDirection() {
        return Objects.requireNonNull(this.world).getBlockState(this.pos).get(CuttingBoardBlock.FACING);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        super.readNbt(nbt, registryLookup);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public void markDirty() {
        if (!Objects.requireNonNull(this.world).isClient) {
            GameNetworking.sendToPlayers(PlayerLookup.world((ServerWorld) this.world),
                    new ItemStackSyncS2CPayload(this.inventory.size(), this.inventory, this.getPos()));
        }
        super.markDirty();
    }

    @Override
    public boolean insertItem(World world, PlayerEntity user, Hand hand, ItemStack givenStack) {
        ItemStack itemStack = getInputStack();
        if (itemStack.isEmpty()) {
            this.setInputStack(givenStack);
            this.markDirty();
            return true;
        } else if (canItemStacksBeStacked(itemStack, givenStack)) {
            this.setInputStack(new ItemStack(itemStack.getItem(), itemStack.getCount() + givenStack.getCount()));
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.pos, GameEvent.Emitter.of(user, this.getCachedState()));
            this.updateListeners(world);
            return true;
        }
        return false;
    }

    @Override
    public boolean extractItem(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = getInputStack();
        if (itemStack.isEmpty()) {
            return false;
        } else {
            user.setStackInHand(hand, itemStack);
            this.setInputStack(ItemStack.EMPTY);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.pos, GameEvent.Emitter.of(user, this.getCachedState()));
            this.updateListeners(world);
            return true;
        }
    }

    protected void updateListeners(World world) {
        this.markDirty();
        world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (this.getInputStack().isEmpty()) {
            GameNetworking.sendToPlayers(PlayerLookup.world((ServerWorld) world),
                    new ClearInventoryS2CPayload(pos));
        }
    }
}
