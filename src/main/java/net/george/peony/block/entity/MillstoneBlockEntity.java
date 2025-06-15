package net.george.peony.block.entity;

import net.george.peony.block.MillstoneBlock;
import net.george.peony.recipe.MillingRecipe;
import net.george.peony.recipe.MillingRecipeInput;
import net.george.peony.recipe.PeonyRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class MillstoneBlockEntity extends BlockEntity implements StackTransformableInventory, AccessibleInventory {
    protected final DefaultedList<ItemStack> itemBeingMilled;
    protected final RecipeManager.MatchGetter<MillingRecipeInput, MillingRecipe> matchGetter;
    protected int previousRotationTimes = 15;
    protected int rotationTimes = 0;
    protected int millingTimes = 0;
    protected int requiredMillingTimes = 0;
    protected boolean milled = false;

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
    public Direction getCurrentDirection() {
        return Objects.requireNonNull(this.world).getBlockState(this.pos).get(MillstoneBlock.FACING);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.itemBeingMilled, registryLookup);
        nbt.putInt("PreviousRotationTimes", this.previousRotationTimes);
        nbt.putInt("RotationTimes", this.rotationTimes);
        nbt.putInt("MillingTimes", this.millingTimes);
        nbt.putInt("RequiredMillingTimes", this.requiredMillingTimes);
        nbt.putBoolean("Milled", this.milled);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, this.itemBeingMilled, registryLookup);
        this.previousRotationTimes = nbt.getInt("PreviousRotationTimes");
        this.rotationTimes = nbt.getInt("RotationTimes");
        this.millingTimes = nbt.getInt("MillingTimes");
        this.requiredMillingTimes = nbt.getInt("RequiredMillingTimes");
        this.milled = nbt.getBoolean("Milled");
        super.readNbt(nbt, registryLookup);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public boolean insertItem(World world, PlayerEntity user, Hand hand, ItemStack givenStack) {
        ItemStack itemStack = getInputStack();
        Optional<RecipeEntry<MillingRecipe>> recipe = getCurrentRecipe(world, givenStack);
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
    public boolean extractItem(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = getInputStack();
        if (itemStack.isEmpty()) {
            return false;
        } else {
            user.setStackInHand(hand, itemStack);
            this.requiredMillingTimes = 0;
            this.millingTimes = 0;
            this.setInputStack(ItemStack.EMPTY);
            this.markDirty();
            return true;
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

    /* CRAFTING */

    protected void updateMillingTimes(BlockState state) {
        this.rotationTimes = state.get(MillstoneBlock.ROTATION_TIMES);
        if (this.rotationTimes != this.previousRotationTimes)  {
            if (this.rotationTimes == 0 && this.previousRotationTimes == 15 && !this.milled) {
                this.milled = true;
                this.millingTimes += 1;
            } else if (this.rotationTimes == 1 && this.previousRotationTimes == 0 && this.milled) {
                this.milled = false;
            }
        }
    }

    protected Optional<RecipeEntry<MillingRecipe>> getCurrentRecipe(World world, ItemStack input) {
        return this.matchGetter.getFirstMatch(new MillingRecipeInput(input), world);
    }

    protected Optional<RecipeEntry<MillingRecipe>> getCurrentRecipe(World world) {
        return this.getCurrentRecipe(world, this.getInputStack());
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        updateMillingTimes(state);
        Optional<RecipeEntry<MillingRecipe>> recipe = this.getCurrentRecipe(world);

        if (recipe.isPresent()) {
            if (this.milled && this.millingTimes >= this.requiredMillingTimes) {
                ItemStack result = new ItemStack(recipe.get().value().output().getItem(), this.getInputStack().getCount() * recipe.get().value().output().getCount());

                if (world.getBlockState(pos.down()).getBlock() instanceof ChestBlock) {
                    Inventory chest = HopperBlockEntity.getInventoryAt(world, pos.down());
                    boolean inserted = insert(chest, result);
                    if (!inserted) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), result);
                    }
                } else {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), result);
                }

                this.itemBeingMilled.clear();
                markDirty();
            }
        }
        this.previousRotationTimes = this.rotationTimes;
    }
}
