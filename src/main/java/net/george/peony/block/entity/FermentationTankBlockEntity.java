package net.george.peony.block.entity;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.george.peony.Peony;
import net.george.peony.api.fluid.FluidStack;
import net.george.peony.block.data.Cursor;
import net.george.peony.block.data.Output;
import net.george.peony.block.data.RecipeStorage;
import net.george.peony.recipe.FermentingRecipe;
import net.george.peony.recipe.MixedIngredientsRecipeInput;
import net.george.peony.recipe.PeonyRecipes;
import net.george.peony.util.math.animation.LerpedFloat;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused"})
public class FermentationTankBlockEntity extends BlockEntity implements ImplementedInventory, AccessibleInventory, BlockEntityTickerProvider {
    public static final List<Block> MUSHROOMS = Collections.unmodifiableList(Lists.newArrayList(
            Blocks.MUSHROOM_STEM, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK
    ));
    public static final int OUTPUT_TIMEOUT_TICKS = 100;

    protected final DefaultedList<ItemStack> inventory;
    protected ItemStack outputStack = ItemStack.EMPTY;
    protected final SingleFluidStorage fluidStorage;
    protected final RecipeManager.MatchGetter<MixedIngredientsRecipeInput, FermentingRecipe> matchGetter;
    protected RecipeStorage<MixedIngredientsRecipeInput, FermentingRecipe> recipeStorage;
    protected Cursor inputCursor;
    protected boolean isFermenting = false;
    protected int fermentTime = 0;
    protected int outputTimeout = 0;
    protected boolean hasSlab = false;

    /* Animation Fields */
    public final LerpedFloat animatedFluidHeight = LerpedFloat.linear()
            .startWithValue(0.125F);
    private float targetFluidHeight = 0.125F;

    public FermentationTankBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.FERMENTATION_TANK, pos, state);
        this.inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);
        this.fluidStorage = SingleFluidStorage.withFixedCapacity(FluidConstants.BUCKET, this::markDirty);
        this.matchGetter = RecipeManager.createCachedMatchGetter(PeonyRecipes.FERMENTING_TYPE);
        this.recipeStorage = RecipeStorage.create(recipe -> recipe instanceof FermentingRecipe);
        this.inputCursor = Cursor.create(0, 6, "InputCursor");
    }

    private void onChange() {
        this.markDirty();
        this.updateFluidHeightAnimation();
    }

    private void updateFluidHeightAnimation() {
        if (this.world == null) {
            return;
        }

        long amount = this.fluidStorage.amount;
        long capacity = this.fluidStorage.getCapacity();

        float targetHeight;
        if (amount <= 0) {
            targetHeight = 0.125F;
        } else {
            float fillPercentage = Math.max(0.0F, Math.min(1.0F, (float) amount / (float) capacity));
            targetHeight = 0.125F + (0.75F * fillPercentage);
        }

        float currentTarget = this.animatedFluidHeight.getChaseTarget();
        if (Math.abs(currentTarget - targetHeight) > 0.001f) {
            this.animatedFluidHeight.updateChaseTarget(targetHeight);

            float heightDifference = Math.abs(targetHeight - this.animatedFluidHeight.getValue());
            float speed = Math.min(0.2F, 0.05F + heightDifference * 0.3F);

            this.animatedFluidHeight.chase(targetHeight, speed, LerpedFloat.Chaser.LINEAR);

            this.markDirty();
            if (!this.world.isClient) {
                this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
            }
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    public SingleVariantStorage<FluidVariant> getFluidStorage() {
        return this.fluidStorage;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        this.writeFermentingData(nbt, registries);
        Inventories.writeNbt(nbt, this.inventory, registries);

        /* Fluid Storage */
        NbtCompound fluidNbt = new NbtCompound();
        this.fluidStorage.writeNbt(fluidNbt, registries);
        nbt.put("FluidStorage", fluidNbt);

        this.recipeStorage.writeNbt(this.world, nbt, registries);
        this.inputCursor.writeNbt(nbt);

        NbtCompound animationNbt = new NbtCompound();
        this.animatedFluidHeight.writeNbt(animationNbt, registries);
        nbt.put("AnimatedHeight", animationNbt);
        nbt.putFloat("TargetFluidHeight", this.targetFluidHeight);
    }

    public void writeFermentingData(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.putBoolean("IsOutputStackEmpty", this.outputStack.isEmpty());
        if (!this.outputStack.isEmpty()) {
            NbtCompound outputNbt = new NbtCompound();
            this.outputStack.encode(registries, outputNbt);
            nbt.put("OutputStack", outputNbt);
        }
        nbt.putBoolean("HasSlab", this.hasSlab);
        nbt.putBoolean("IsFermenting", this.isFermenting);
        nbt.putInt("FermentTime", this.fermentTime);
        nbt.putInt("OutputTimeout", this.outputTimeout);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.readFermentingData(nbt, registries);
        Inventories.readNbt(nbt, this.inventory, registries);

        /* Fluid Storage */
        if (nbt.contains("FluidStorage")) {
            NbtCompound fluidNbt = nbt.getCompound("FluidStorage");
            this.fluidStorage.readNbt(fluidNbt, registries);
        }

        this.recipeStorage.readNbt(this.world, nbt, registries);
        this.inputCursor.readNbt(nbt);

        if (nbt.contains("AnimatedHeight")) {
            NbtCompound animationNbt = nbt.getCompound("AnimatedHeight");
            this.animatedFluidHeight.readNbt(animationNbt, registries);
        } else {
            this.updateFluidHeightAnimation();
        }

        if (nbt.contains("TargetFluidHeight")) {
            this.targetFluidHeight = nbt.getFloat("TargetFluidHeight");
        } else {
            long amount = this.fluidStorage.amount;
            long capacity = this.fluidStorage.getCapacity();
            this.targetFluidHeight = amount <= 0 ? 0.125F :
                    0.125F + (0.75F * Math.max(0.0F, Math.min(1.0F, (float) amount / (float) capacity)));
        }
    }

    protected void readFermentingData(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        if (!nbt.getBoolean("IsOutputStackEmpty")) {
            NbtCompound outputNbt = nbt.getCompound("OutputStack");
            this.outputStack = ItemStack.fromNbtOrEmpty(registries, outputNbt);
        } else {
            this.outputStack = ItemStack.EMPTY;
        }
        this.hasSlab = nbt.getBoolean("HasSlab");
        this.isFermenting = nbt.getBoolean("IsFermenting");
        this.fermentTime = nbt.getInt("FermentTime");
        this.outputTimeout = nbt.getInt("OutputTimeout");
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
        InsertResult result = this.handleFluid(context, givenStack);
        if (result.isSuccess()) {
            return result;
        }

        return this.insertItem(context.world, givenStack);
    }

    private InsertResult handleFluid(InteractionContext context, ItemStack givenStack) {
        if (this.isFermenting || !this.outputStack.isEmpty()) {
            return AccessibleInventory.createResult(false, -1);
        }

        ContainerItemContext handContext = ContainerItemContext.ofPlayerHand(context.user, context.hand);
        Storage<FluidVariant> handFluidStorage = handContext.find(FluidStorage.ITEM);

        if (handFluidStorage == null) {
            return AccessibleInventory.createResult(false, -1);
        }

        ItemStack heldStack = context.user.getStackInHand(context.hand);
        boolean isHoldingEmptyContainer = this.isContainerEmpty(handFluidStorage);
        boolean hasFluid = !this.fluidStorage.isResourceBlank() && this.fluidStorage.amount > 0;

        Peony.LOGGER.debug("[DEBUG] Held item: " + heldStack.getItem()
                + ", Empty container: " + isHoldingEmptyContainer
                + ", Fermentation Tank has fluid: " + hasFluid
                + " (" + this.fluidStorage.amount + " droplets)");

        if (isHoldingEmptyContainer && hasFluid) {
            Peony.LOGGER.debug("[DEBUG] Attempting to extract from fermentation tank to empty container");
            return this.extractFluidToContainer(context, handContext, handFluidStorage);
        } else if (!isHoldingEmptyContainer) {
            Peony.LOGGER.debug("[DEBUG] Attempting to insert from full container to fermentation tank");
            return this.insertFluidFromContainer(context, handContext, handFluidStorage);
        }
        return AccessibleInventory.createResult(false, -1);
    }

    private boolean isContainerEmpty(Storage<FluidVariant> storage) {
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<FluidVariant> view : storage.nonEmptyViews()) {
                if (!view.isResourceBlank() && view.getAmount() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private InsertResult insertFluidFromContainer(InteractionContext context, ContainerItemContext handContext, Storage<FluidVariant> handFluidStorage) {
        try (Transaction transaction = Transaction.openOuter()) {
            FluidVariant extractedResource = null;
            long extractedAmount = 0;

            for (StorageView<FluidVariant> view : handFluidStorage.nonEmptyViews()) {
                if (!view.isResourceBlank()) {
                    extractedResource = view.getResource();
                    long available = view.getAmount();

                    long maxInsert = Math.min(available,
                            this.fluidStorage.getCapacity() - this.fluidStorage.amount);

                    if (maxInsert > 0) {
                        extractedAmount = view.extract(extractedResource, maxInsert, transaction);
                        Peony.LOGGER.debug("[DEBUG] Extracted from container: " + extractedAmount + " droplets");
                        break;
                    }
                }
            }

            if (extractedAmount > 0 && extractedResource != null) {
                long insertedAmount = this.fluidStorage.insert(extractedResource, extractedAmount, transaction);

                if (insertedAmount == extractedAmount) {
                    transaction.commit();
                    Peony.LOGGER.debug("[DEBUG] Insert successful. New fermentation tank amount: " + this.fluidStorage.amount);

                    context.world.playSound(null, context.pos,
                            SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    this.updateFluidHeightAnimation();
                    return AccessibleInventory.createResult(true, 0);
                }
            }
        }

        return AccessibleInventory.createResult(false, -1);
    }

    private InsertResult extractFluidToContainer(InteractionContext context, ContainerItemContext handContext, Storage<FluidVariant> handFluidStorage) {
        try (Transaction transaction = Transaction.openOuter()) {
            FluidVariant fluidVariant = this.fluidStorage.getResource();
            long availableAmount = this.fluidStorage.amount;

            if (availableAmount <= 0) {
                return AccessibleInventory.createResult(false, -1);
            }

            long insertedIntoContainer = handFluidStorage.insert(
                    fluidVariant,
                    Math.min(availableAmount, FluidConstants.BUCKET),
                    transaction
            );

            Peony.LOGGER.debug("[DEBUG] Attempting to insert " + insertedIntoContainer
                    + " droplets into container");

            if (insertedIntoContainer > 0) {
                long extracted = this.fluidStorage.extract(
                        fluidVariant,
                        insertedIntoContainer,
                        transaction
                );

                if (extracted == insertedIntoContainer) {
                    transaction.commit();
                    Peony.LOGGER.debug("[DEBUG] Extract successful. New fermentation tank amount: "
                            + this.fluidStorage.amount);
                    context.world.playSound(null, context.pos,
                            SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    this.updateFluidHeightAnimation();
                    return AccessibleInventory.createResult(true, 0);
                }
            }
        }

        return AccessibleInventory.createResult(false, -1);
    }

    protected InsertResult insertItem(World world, ItemStack givenStack) {
        if (this.inputCursor.overflowing() || !this.outputStack.isEmpty() || this.isFermenting) {
            return AccessibleInventory.createResult(false, -1);
        } else {
            this.inventory.set(this.inputCursor.getCursoringIndex(), givenStack.copyWithCount(1));
            this.inputCursor.next();

            this.markDirty();
            return AccessibleInventory.createResult(true, -1);
        }
    }

    @Override
    public boolean extractItem(InteractionContext context) {
        return this.extractItem(context.user);
    }

    protected boolean extractItem(PlayerEntity user) {
        if (this.isFermenting || !this.outputStack.isEmpty()) {
            return false;
        }
        if (this.inputCursor.getCursoringIndex() <= this.inputCursor.getRange().getMin()) {
            return false;
        } else {
            this.inputCursor.previous();
            int extractIndex = this.inputCursor.getCursoringIndex();
            ItemStack stack = this.inventory.get(extractIndex);

            if (!stack.isEmpty()) {
                user.giveItemStack(stack.copy());
                this.inventory.set(extractIndex, ItemStack.EMPTY);
                this.markDirty();
                return true;
            } else {
                this.inputCursor.next();
                return false;
            }
        }
    }

    @Override
    public boolean useEmptyHanded(InteractionContext context) {
        if (!this.outputStack.isEmpty()) {
            return this.handleOutputExtraction(context);
        }
        return this.extractItem(context);
    }

    private boolean handleOutputExtraction(InteractionContext context) {
        PlayerEntity player = context.user;
        ItemStack heldStack = player.getStackInHand(context.hand);
        player.giveItemStack(this.outputStack.copy());
        this.outputStack = ItemStack.EMPTY;
        this.outputTimeout = 0;
        this.markDirty();

        context.world.playSound(null, this.pos, SoundEvents.ENTITY_ITEM_PICKUP,
                SoundCategory.BLOCKS, 0.2F, 1.0F);
        return true;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        boolean previousHasSlab = this.hasSlab;
        this.hasSlab = this.checkForWoodenSlab(world, pos);

        if (!previousHasSlab && this.hasSlab && !this.isFermenting && this.outputStack.isEmpty()) {
            Peony.LOGGER.debug("[FERMENTATION] Slab placed, trying to start fermenting");
            this.tryStartFermenting(world);
        }

        if (this.isFermenting && this.fermentTime > 0) {
            this.fermentTime--;
            if (this.fermentTime <= 0) {
                Peony.LOGGER.debug("[FERMENTATION] Fermentation Finished");
                this.finishFermenting(world);
            } else {
                if (!this.hasSlab) {
                    this.stopFermenting(world);
                }
            }
            this.markDirty();
        }

        if (!this.outputStack.isEmpty() && this.outputTimeout > 0) {
            this.outputTimeout--;
            if (this.outputTimeout <= 0) {
                this.handleOutputTimeout(world);
            }
            this.markDirty();
        }
    }

    private boolean checkForWoodenSlab(World world, BlockPos pos) {
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);

        if (aboveState.getBlock() instanceof SlabBlock) {
            SlabType slabType = aboveState.get(SlabBlock.TYPE);
            if (slabType == SlabType.BOTTOM || slabType == SlabType.DOUBLE) {
                return aboveState.isIn(BlockTags.WOODEN_SLABS);
            }
        }
        return false;
    }

    private void tryStartFermenting(World world) {
        Peony.LOGGER.debug("[FERMENTATION] Starting fermentation check");

        boolean hasInput = false;
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack stack = this.inventory.get(i);
            if (!stack.isEmpty()) {
                hasInput = true;
                Peony.LOGGER.debug("[FERMENTATION] Slot " + i + ": " + stack.getItem());
            }
        }

        Peony.LOGGER.debug("[FERMENTATION] Has item input: " + hasInput);
        Peony.LOGGER.debug("[FERMENTATION] Has fluid input: " +
                (!this.fluidStorage.isResourceBlank() && this.fluidStorage.amount > 0));

        if (!hasInput && (this.fluidStorage.isResourceBlank() || this.fluidStorage.amount <= 0)) {
            Peony.LOGGER.debug("[FERMENTATION] No input, skipping");
            return;
        }

        MixedIngredientsRecipeInput input = new MixedIngredientsRecipeInput(
                DefaultedList.copyOf(ItemStack.EMPTY, this.inventory.toArray(new ItemStack[0])),
                FluidStack.fromStorage(this.fluidStorage)
        );

        List<RecipeEntry<FermentingRecipe>> allRecipes = world.getRecipeManager()
                .listAllOfType(PeonyRecipes.FERMENTING_TYPE);

        Peony.LOGGER.debug("[FERMENTATION] Total loaded recipes: " + allRecipes.size());

        RecipeEntry<FermentingRecipe> matchedRecipe = null;
        for (RecipeEntry<FermentingRecipe> recipe : allRecipes) {
            Peony.LOGGER.debug("[FERMENTATION] Checking recipe: " + recipe.id());
            if (recipe.value().matches(input, world)) {
                Peony.LOGGER.debug("[FERMENTATION] Recipe " + recipe.id() + " matches!");
                matchedRecipe = recipe;
                break;
            } else {
                Peony.LOGGER.debug("[FERMENTATION] Recipe " + recipe.id() + " does not match");
            }
        }

        if (matchedRecipe != null) {
            Peony.LOGGER.debug("[FERMENTATION] Found matching recipe: " + matchedRecipe.id());
            startFermenting(matchedRecipe.value());
        } else {
            Peony.LOGGER.debug("[FERMENTATION] No matching recipe found");
        }
    }

    private void startFermenting(FermentingRecipe recipe) {
        this.isFermenting = true;
        this.fermentTime = recipe.fermentingTime();
        this.recipeStorage.setCurrentRecipe(recipe);

        if (this.world != null) {
            this.world.playSound(null, this.pos, SoundEvents.BLOCK_BREWING_STAND_BREW,
                    SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        this.markDirty();
    }

    private void stopFermenting(World world) {
        this.recipeStorage.clear();
        this.fermentTime = 0;
        this.isFermenting = false;
        this.outputTimeout = 0;
    }

    private void finishFermenting(World world) {
        this.isFermenting = false;

        FermentingRecipe recipe = this.recipeStorage.getCurrentRecipe();
        if (recipe != null) {
            Output output = recipe.output();

            Collections.fill(this.inventory, ItemStack.EMPTY);
            this.inputCursor.reset();

            try (Transaction transaction = Transaction.openOuter()) {
                this.fluidStorage.extract(this.fluidStorage.getResource(),
                        this.fluidStorage.amount, transaction);
                transaction.commit();
            }

            this.updateFluidHeightAnimation();

            if (output.containsFluid()) {
                FluidStack outputFluid = output.getOutputFluid();
                if (outputFluid != null) {
                    try (Transaction transaction = Transaction.openOuter()) {
                        this.fluidStorage.insert(outputFluid.getFluid(), outputFluid.getAmount(), transaction);
                        transaction.commit();
                    }
                }
            } else {
                this.outputStack = output.getOutputStack().copy();
                this.outputTimeout = OUTPUT_TIMEOUT_TICKS;
            }

            world.playSound(null, this.pos, SoundEvents.BLOCK_BREWING_STAND_BREW,
                    SoundCategory.BLOCKS, 1.0F, 0.5F);
        }

        this.recipeStorage.clear();
        this.markDirty();
    }

    private void handleOutputTimeout(World world) {
        Block randomMushroom = MUSHROOMS.get(world.random.nextInt(MUSHROOMS.size()));
        this.outputStack = new ItemStack(randomMushroom);
        this.outputTimeout = 0;

        world.playSound(null, this.pos, SoundEvents.BLOCK_FUNGUS_BREAK,
                SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void clientTick(World world, BlockPos pos, BlockState state) {
        this.animatedFluidHeight.tickChaser();
    }
}
