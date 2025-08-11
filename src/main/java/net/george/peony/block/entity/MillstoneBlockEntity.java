package net.george.peony.block.entity;

import net.george.peony.block.MillstoneBlock;
import net.george.peony.recipe.MillingRecipe;
import net.george.peony.recipe.MillingRecipeInput;
import net.george.peony.recipe.PeonyRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MillstoneBlockEntity extends BlockEntity implements ImplementedInventory, DirectionProvider, AccessibleInventory {
    protected final DefaultedList<ItemStack> itemBeingMilled;
    protected final RecipeManager.MatchGetter<MillingRecipeInput, MillingRecipe> matchGetter;
    protected int previousRotationTimes = 15;
    protected int rotationTimes = 0;
    protected int millingTimes = 0;
    protected int requiredMillingTimes = 0;
    protected int usageCountdown = 0;

    public MillstoneBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.MILLSTONE, pos, state);
        this.itemBeingMilled = DefaultedList.ofSize(1, ItemStack.EMPTY);
        this.matchGetter = RecipeManager.createCachedMatchGetter(PeonyRecipes.MILLING_TYPE);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.itemBeingMilled;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    protected ItemStack getInputStack() {
        return this.getStack(0);
    }

    protected void setInputStack(ItemStack stack) {
        this.itemBeingMilled.set(0, stack);
    }

    @Override
    public Direction getDirection() {
        if (this.world != null) {
            BlockState state = this.world.getBlockState(this.pos);
            if (state.contains(MillstoneBlock.FACING)) {
                return state.get(MillstoneBlock.FACING);
            }
        }
        return Direction.NORTH;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.itemBeingMilled, registryLookup);
        nbt.putInt("PreviousRotationTimes", this.previousRotationTimes);
        nbt.putInt("RotationTimes", this.rotationTimes);
        nbt.putInt("MillingTimes", this.millingTimes);
        nbt.putInt("RequiredMillingTimes", this.requiredMillingTimes);
        nbt.putInt("UsageCountdown", this.usageCountdown);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, this.itemBeingMilled, registryLookup);
        this.previousRotationTimes = nbt.getInt("PreviousRotationTimes");
        this.rotationTimes = nbt.getInt("RotationTimes");
        this.millingTimes = nbt.getInt("MillingTimes");
        this.requiredMillingTimes = nbt.getInt("RequiredMillingTimes");
        this.usageCountdown = nbt.getInt("UsageCountdown");
        super.readNbt(nbt, registryLookup);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    protected void readComponents(BlockEntity.ComponentsAccess components) {
        super.readComponents(components);
        components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(this.getItems());
    }

    @Override
    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.getItems()));
    }

    @Override
    public boolean insertItem(InteractionContext context, ItemStack givenStack) {
        ItemStack itemStack = getInputStack();
        Optional<RecipeEntry<MillingRecipe>> recipe = getCurrentRecipe(context.world, givenStack);
        if (recipe.isPresent()) {
            if (itemStack.isEmpty()) {
                this.requiredMillingTimes = recipe.get().value().millingTimes();
                this.millingTimes = 0;
                this.setInputStack(givenStack);
                this.markDirty();
                return true;
            } else if (canItemStacksBeStacked(itemStack, givenStack)) {
                this.requiredMillingTimes = recipe.get().value().millingTimes();
                this.millingTimes = 0;
                this.setInputStack(new ItemStack(itemStack.getItem(), itemStack.getCount() + givenStack.getCount()));
                this.markDirty();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean extractItem(InteractionContext context) {
        ItemStack itemStack = getInputStack();
        if (itemStack.isEmpty()) {
            return false;
        } else {
            context.user.setStackInHand(context.hand, itemStack);
            this.requiredMillingTimes = 0;
            this.millingTimes = 0;
            this.setInputStack(ItemStack.EMPTY);
            this.markDirty();
            return true;
        }
    }

    @Override
    public boolean useEmptyHanded(InteractionContext context) {
        if (this.isCountdownOver()) {
            context.world.setBlockState(context.pos, context.world.getBlockState(context.pos).cycle(MillstoneBlock.ROTATION_TIMES));
            this.resetCountdown();
            return true;
        } else {
            return false;
        }
    }

    protected Direction getRotatedDirection(Direction side, Direction localDirection) {
        return switch (localDirection) {
            default -> side.getOpposite();
            case EAST -> side.rotateYClockwise();
            case SOUTH -> side;
            case WEST -> side.rotateYCounterclockwise();
        };
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        Direction localDirection = Objects.requireNonNull(this.world).getBlockState(this.pos).get(MillstoneBlock.FACING);
        if (side == Direction.UP || side == Direction.DOWN) {
            return false;
        }

        Direction rotatedDirection = getRotatedDirection(side, localDirection);
        if (rotatedDirection == Direction.NORTH || rotatedDirection == Direction.WEST || rotatedDirection == Direction.EAST) {
            Optional<RecipeEntry<MillingRecipe>> recipe = getCurrentRecipe(world, stack);
            if (recipe.isPresent()) {
                this.requiredMillingTimes = recipe.get().value().millingTimes();
                this.millingTimes = 0;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return false;
    }

    protected void resetCountdown() {
        this.usageCountdown = 2;
    }

    protected boolean isCountdownOver() {
        return this.usageCountdown <= 0;
    }

    /* CRAFTING */

    protected void updateMillingTimes(BlockState state) {
        this.rotationTimes = state.get(MillstoneBlock.ROTATION_TIMES);
        if (this.rotationTimes != this.previousRotationTimes)  {
            if (this.rotationTimes == 0 && this.previousRotationTimes >= 15) {
                this.millingTimes++;
            }
        }
    }

    protected Optional<RecipeEntry<MillingRecipe>> getCurrentRecipe(World world, ItemStack input) {
        return this.matchGetter.getFirstMatch(new MillingRecipeInput(input), world);
    }

    protected Optional<RecipeEntry<MillingRecipe>> getCurrentRecipe(World world) {
        return this.getCurrentRecipe(world, this.getInputStack());
    }

    protected List<ItemStack> getOutputStacks(MillingRecipe recipe) {
        ItemStack inputStack = this.getInputStack();

        int inputCount = inputStack.getCount();

        Item outputItem = recipe.output().getItem();
        int totalOutputCount = inputCount * recipe.output().getCount();

        int maxStackSize = outputItem.getMaxCount();

        List<ItemStack> outputStacks = new ArrayList<>();

        while (totalOutputCount > 0) {
            int stackSize = Math.min(maxStackSize, totalOutputCount);
            ItemStack stack = new ItemStack(outputItem, stackSize);
            outputStacks.add(stack);
            totalOutputCount -= stackSize;
        }
        return outputStacks;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        updateMillingTimes(state);
        Optional<RecipeEntry<MillingRecipe>> recipe = this.getCurrentRecipe(world);

        if (recipe.isPresent() && this.millingTimes >= this.requiredMillingTimes) {
            List<ItemStack> outputStacks = this.getOutputStacks(recipe.get().value());

            Inventory container = null;
            if (world.getBlockState(pos.down()).getBlock() instanceof ChestBlock) {
                container = HopperBlockEntity.getInventoryAt(world, pos.down());
            }

            for (ItemStack stack : outputStacks) {
                if (container != null) {
                    ItemStack remainder = HopperBlockEntity.transfer(null, container, stack, Direction.UP);
                    if (!remainder.isEmpty()) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), remainder);
                    }
                } else {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }

            this.itemBeingMilled.clear();
            this.millingTimes = 0;
            this.requiredMillingTimes = 0;
            markDirty();
        }
        this.previousRotationTimes = this.rotationTimes;
        this.usageCountdown--;
    }
}
