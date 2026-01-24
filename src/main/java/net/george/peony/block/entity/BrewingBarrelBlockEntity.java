package net.george.peony.block.entity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.george.peony.api.fluid.FluidStack;
import net.george.peony.api.item.FluidContainer;
import net.george.peony.api.util.CountdownManager;
import net.george.peony.block.SkilletBlock;
import net.george.peony.block.data.Cursor;
import net.george.peony.block.data.Output;
import net.george.peony.recipe.BrewingRecipe;
import net.george.peony.recipe.MixedIngredientsRecipeInput;
import net.george.peony.recipe.PeonyRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BrewingBarrelBlockEntity extends BlockEntity implements ImplementedInventory, DirectionProvider, AccessibleInventory, BlockEntityTickerProvider {
    protected final DefaultedList<ItemStack> inputs;
    protected ItemStack outputStack = ItemStack.EMPTY;
    protected Cursor inputCursor;
    protected boolean startedBrewing = false;
    protected int requiredBrewingTime = 0;
    protected int brewingTime = 0;
    protected CountdownManager countdownManager;
    public final SingleFluidStorage fluidStorage = SingleFluidStorage.withFixedCapacity(FluidConstants.BUCKET, this::markDirty);
    protected @Nullable RecipeEntry<BrewingRecipe> cachedRecipe = null;
    @Nullable
    protected ItemConvertible requiredContainer;
    protected Direction cachedDirection = Direction.NORTH;

    public BrewingBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.BREWING_BARREL, pos, state);
        this.inputs = DefaultedList.ofSize(5, ItemStack.EMPTY);
        this.inputCursor = Cursor.create(0, 5, "InputCursor");
        this.countdownManager = CountdownManager.create();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inputs;
    }

    @Override
    public Direction getDirection() {
        return this.cachedDirection;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inputs, registryLookup);
        nbt.putBoolean("IsOutputStackEmpty", this.outputStack.isEmpty());
        if (!this.outputStack.isEmpty()) {
            nbt.put("OutputStack", this.outputStack.encode(registryLookup));
        }
        this.inputCursor.writeNbt(nbt);
        nbt.putBoolean("StartedBrewing", this.startedBrewing);
        nbt.putInt("RequiredBrewingTime", this.requiredBrewingTime);
        nbt.putInt("BrewingTime", this.brewingTime);
        this.countdownManager.writeNbt(nbt, registryLookup);
        this.fluidStorage.writeNbt(nbt, registryLookup);
        if (this.requiredContainer != null) {
            Item item = this.requiredContainer.asItem();
            nbt.putString("RequiredContainer", Registries.ITEM.getId(item).toString());
        }
        nbt.putString("CachedDirection", this.getDirection().getName());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, this.inputs, registryLookup);
        if (nbt.getBoolean("IsOutputStackEmpty")) {
            this.outputStack = ItemStack.EMPTY;
        } else {
            this.outputStack = ItemStack.fromNbtOrEmpty(registryLookup, nbt);
        }
        this.inputCursor.readNbt(nbt);
        this.startedBrewing = nbt.getBoolean("StartedBrewing");
        this.requiredBrewingTime = nbt.getInt("RequiredBrewingTime");
        this.brewingTime = nbt.getInt("BrewingTime");
        this.countdownManager.readNbt(nbt, registryLookup);
        this.fluidStorage.readNbt(nbt, registryLookup);
        if (nbt.contains("RequiredContainer")) {
            this.requiredContainer = Registries.ITEM.get(Identifier.tryParse(nbt.getString("RequiredContainer")));
        }
        @Nullable
        Direction direction = Direction.byName(nbt.getString("CachedDirection"));
        this.cachedDirection = direction != null ? direction : Direction.NORTH;
        super.readNbt(nbt, registryLookup);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
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
    public void markDirty() {
        super.markDirty();
        if (this.world != null) {
            this.world.updateListeners(this.pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public InsertResult insertItemSpecified(InteractionContext context, ItemStack givenStack) {
        if (!this.outputStack.isEmpty() &&
                this.requiredContainer != null && givenStack.getItem() == this.requiredContainer.asItem()) {
            return extractOutputWithContainer(context, givenStack);
        }
        FluidContainer container = FluidContainer.FLUID_CONTAINERS.find(givenStack, null);
        if (container != null) {
            FluidStack fluid = container.getFluid();
            this.transferIntoFluidStorage(fluid.getFluid(), fluid.getAmount());
            Optional<RecipeEntry<BrewingRecipe>> matchedRecipe = this.getCurrentRecipe(context.world);
            if (matchedRecipe.isPresent() && this.fluidStorage.amount >= matchedRecipe.get().value().basicFluid().getAmount()) {
                this.startedBrewing = true;
            }
            return AccessibleInventory.createResult(true, -1);
        }
        return this.insertItem(context.world, givenStack);
    }

    @Override
    public boolean extractItem(InteractionContext context) {
        return this.extractItem(context.user);
    }

    protected InsertResult insertItem(World world, ItemStack givenStack) {
        if (this.inputCursor.overflowing() || !this.outputStack.isEmpty() || this.startedBrewing) {
            return AccessibleInventory.createResult(false, -1);
        } else {
            this.inputs.set(this.inputCursor.getCursoringIndex(), givenStack.copyWithCount(1));
            this.inputCursor.next();
            this.tryStartBrewing(world);

            this.markDirty();
            return AccessibleInventory.createResult(true, -1);
        }
    }

    protected void tryStartBrewing(World world) {
        Optional<RecipeEntry<BrewingRecipe>> recipe = this.findMatchingRecipe(world);
        if (recipe.isPresent() && this.fluidStorage.amount >= recipe.get().value().basicFluid().getAmount()) {
            this.startedBrewing = true;
        }
    }

    protected boolean extractItem(PlayerEntity user) {
        if (this.inputCursor.getCursoringIndex() <= this.inputCursor.getRange().getMin()) {
            return false;
        } else {
            this.inputCursor.previous();
            int extractIndex = this.inputCursor.getCursoringIndex();
            ItemStack stack = this.inputs.get(extractIndex);

            if (!stack.isEmpty()) {
                user.giveItemStack(stack.copy());
                this.inputs.set(extractIndex, ItemStack.EMPTY);
                if (this.startedBrewing) {
                    this.startedBrewing = false;
                }
                this.markDirty();
                return true;
            } else {
                this.inputCursor.next();
                return false;
            }
        }
    }

    protected InsertResult extractOutputWithContainer(InteractionContext context, ItemStack containerStack) {
        ItemStack outputStack = this.outputStack;

        int outputCount = outputStack.getCount();
        int containerCount = containerStack.getCount();
        int containersToUse = Math.min(outputCount, containerCount);

        if (containersToUse > 0) {
            ItemStack resultStack = outputStack.copy();
            resultStack.setCount(containersToUse);

            if (context.user.giveItemStack(resultStack)) {
                outputStack.decrement(containersToUse);
                if (outputStack.isEmpty()) {
                    this.outputStack = ItemStack.EMPTY;
                    this.requiredContainer = null;
                }

                this.markDirty();
                return AccessibleInventory.createResult(true, containersToUse);
            }
        }
        return AccessibleInventory.createResult(false, -1);
    }

    protected void clearInputs() {
        Collections.fill(this.inputs, ItemStack.EMPTY);
    }

    protected void transferIntoFluidStorage(FluidVariant variant, long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = this.fluidStorage.insert(variant, amount, transaction);
            if (inserted > 0) {
                transaction.commit();
            }
        }
    }

    protected void extractOutOfFluidStorage(FluidVariant variant, long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            this.fluidStorage.extract(variant, amount, transaction);
            transaction.commit();
        }
    }

    protected Optional<RecipeEntry<BrewingRecipe>> getCurrentRecipe(World world) {
        if (this.cachedRecipe != null) {
            return Optional.of(this.cachedRecipe);
        } else {
            return this.findMatchingRecipe(world);
        }
    }

    private Optional<RecipeEntry<BrewingRecipe>> findMatchingRecipe(World world) {
        List<RecipeEntry<BrewingRecipe>> allRecipes = world.getRecipeManager().listAllOfType(PeonyRecipes.BREWING_TYPE);
        Optional<RecipeEntry<BrewingRecipe>> matchedRecipe = allRecipes.stream()
                .filter(recipe -> recipe.value().matches(new MixedIngredientsRecipeInput(this.inputs, FluidStack.of(this.fluidStorage.variant, this.fluidStorage.amount)), world))
                .findFirst();
        matchedRecipe.ifPresent(this::updateRecipeData);
        return matchedRecipe;
    }

    protected void updateRecipeData(RecipeEntry<BrewingRecipe> recipe) {
        if (this.cachedRecipe == null || !this.cachedRecipe.id().equals(recipe.id())) {
            this.cachedRecipe = recipe;
            this.requiredBrewingTime = recipe.value().brewingTime();
            this.brewingTime = 0;
        }
    }

    protected void resetBrewingData() {
        this.cachedRecipe = null;
        this.requiredBrewingTime = 0;
        this.brewingTime = 0;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (state.contains(SkilletBlock.FACING)) {
            this.cachedDirection = state.get(SkilletBlock.FACING);
        }

        if (this.startedBrewing && this.cachedRecipe != null) {
            if (this.brewingTime < this.requiredBrewingTime) {
                this.brewingTime++;
                this.markDirty();
            } else {
                BrewingRecipe brewingRecipe = this.cachedRecipe.value();

                if (this.fluidStorage.amount < brewingRecipe.basicFluid().getAmount()) {
                    this.resetBrewingData();
                    return;
                }

                this.requiredContainer = Output.getRequiredContainer(brewingRecipe.output());
                this.extractOutOfFluidStorage(brewingRecipe.basicFluid().getFluid(), brewingRecipe.basicFluid().getAmount());
                this.clearInputs();
                this.outputStack = brewingRecipe.output().getOutputStack();
                this.resetBrewingData();
                this.inputCursor.reset();
                this.markDirty();
            }
        } else if (this.startedBrewing) {
            this.startedBrewing = false;
            this.brewingTime = 0;
            this.markDirty();
        }
    }
}
