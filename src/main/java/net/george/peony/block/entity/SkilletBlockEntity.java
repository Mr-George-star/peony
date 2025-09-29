package net.george.peony.block.entity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.george.peony.Peony;
import net.george.peony.api.heat.HeatCalculationUtils;
import net.george.peony.api.heat.HeatProvider;
import net.george.peony.block.SkilletBlock;
import net.george.peony.block.data.CookingSteps;
import net.george.peony.block.data.Output;
import net.george.peony.block.data.RecipeStepsCursor;
import net.george.peony.item.PeonyItems;
import net.george.peony.recipe.PeonyRecipes;
import net.george.peony.recipe.SequentialCookingRecipe;
import net.george.peony.recipe.SequentialCookingRecipeInput;
import net.george.peony.util.PeonyTags;
import net.george.peony.util.math.Range;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"unused", "CommentedOutCode"})
public class SkilletBlockEntity extends BlockEntity implements ImplementedInventory, DirectionProvider, AccessibleInventory, BlockEntityTickerProvider {
    protected static final Identifier DUMMY_RECIPE_ID = Peony.id("dummy_recipe");
    protected final DefaultedList<ItemStack> inventory;
    protected final RecipeManager.MatchGetter<SequentialCookingRecipeInput, SequentialCookingRecipe> matchGetter;
    protected final SequentialCookingRecipe dummyRecipe = new SequentialCookingRecipe(550, false, List.of(), null);
    public final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant fluidVariant) {
            return FluidConstants.BOTTLE;
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
            if (world != null) {
                world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            }
        }
    };

    protected int currentStepIndex = 0;
    protected int heatingTime = 0;
    protected int requiredHeatingTime = 0;
    protected int overflowTime = 0;
    protected int maxOverflowTime = 0;
    protected int usageCountdown = 0;
    protected boolean hasHeat = false;
    protected boolean inOverflow = false;
    protected boolean hasIngredient = false;
    protected boolean hasOil = false;
    protected int oilProcessingStage = 0;
    protected boolean allowOilBasedRecipes = false;
    @Nullable
    protected ItemConvertible requiredContainer;
    @Nullable
    protected RecipeEntry<SequentialCookingRecipe> cachedRecipe = null;
    protected Direction cachedDirection = Direction.NORTH;

    public SkilletBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.SKILLET, pos, state);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY); // 0: input, 1: output
        this.matchGetter = RecipeManager.createCachedMatchGetter(PeonyRecipes.SEQUENTIAL_COOKING_TYPE);
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
        this.cachedRecipe = null;
        this.markDirty();
    }

    public ItemStack getOutputStack() {
        return this.getStack(1);
    }

    public void setOutputStack(ItemStack stack) {
        this.inventory.set(1, stack);
        this.markDirty();
    }

    public boolean hasStoredFluid() {
        return !this.fluidStorage.isResourceBlank() && this.fluidStorage.getAmount() > 0;
    }

    public FluidVariant getStoredFluidVariant() {
        return this.fluidStorage.getResource();
    }

    public long getStoredFluidAmount() {
        return this.fluidStorage.getAmount();
    }

    public long getFluidCapacity() {
        return this.fluidStorage.getCapacity();
    }

    @Override
    public Direction getDirection() {
        return this.cachedDirection;
    }

    @Nullable
    public ItemConvertible getRequiredContainer() {
        return this.requiredContainer;
    }

    public void writeDataToNbt(NbtCompound nbt) {
        nbt.putInt("CurrentStepIndex", this.currentStepIndex);
        nbt.putInt("HeatingTime", this.heatingTime);
        nbt.putInt("RequiredHeatingTime", this.requiredHeatingTime);
        nbt.putInt("OverflowTime", this.overflowTime);
        nbt.putInt("MaxOverflowTime", this.maxOverflowTime);
        nbt.putInt("UsageCountdown", this.usageCountdown);
        nbt.putBoolean("HasHeat", this.hasHeat);
        nbt.putBoolean("InOverflow", this.inOverflow);
        nbt.putBoolean("HasIngredient", this.hasIngredient);
        nbt.putBoolean("HasOil", this.hasOil);
        nbt.putInt("OilProcessingStage", this.oilProcessingStage);
        nbt.putBoolean("AllowOilBasedRecipes", this.allowOilBasedRecipes);
        if (this.requiredContainer != null) {
            Item item = this.requiredContainer.asItem();
            nbt.putString("RequiredContainer", Registries.ITEM.getId(item).toString());
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        SingleVariantStorage.writeNbt(this.fluidStorage, FluidVariant.CODEC, nbt, registryLookup);
        this.writeDataToNbt(nbt);
        nbt.putString("CachedDirection", this.getDirection().getName());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        SingleVariantStorage.readNbt(this.fluidStorage, FluidVariant.CODEC, FluidVariant::blank, nbt, registryLookup);
        this.currentStepIndex = nbt.getInt("CurrentStepIndex");
        this.heatingTime = nbt.getInt("HeatingTime");
        this.requiredHeatingTime = nbt.getInt("RequiredHeatingTime");
        this.overflowTime = nbt.getInt("OverflowTime");
        this.maxOverflowTime = nbt.getInt("MaxOverflowTime");
        this.usageCountdown = nbt.getInt("UsageCountdown");
        this.hasHeat = nbt.getBoolean("HasHeat");
        this.inOverflow = nbt.getBoolean("InOverflow");
        this.hasIngredient = nbt.getBoolean("HasIngredient");
        this.hasOil = nbt.getBoolean("HasOil");
        this.oilProcessingStage = nbt.getInt("OilProcessingStage");
        this.allowOilBasedRecipes = nbt.getBoolean("AllowOilBasedRecipes");
        if (nbt.contains("RequiredContainer")) {
            this.requiredContainer = () -> Registries.ITEM.get(Identifier.tryParse(nbt.getString("RequiredContainer")));
        }
        @Nullable
        Direction direction = Direction.byName(nbt.getString("CachedDirection"));
        this.cachedDirection = direction != null ? direction : Direction.NORTH;
        super.readNbt(nbt, registryLookup);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(this.getItems());
    }

    @Override
    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.getItems()));
    }

//    @SuppressWarnings("CommentedOutCode")
//    @Override
//    public void markDirty() {
//        if (!Objects.requireNonNull(this.world).isClient) {
//            CustomPayload payload = this.getInputStack().isEmpty() && this.getOutputStack().isEmpty()
//                    ? new ClearInventoryS2CPayload(this.pos)
//                    : new ItemStackSyncS2CPayload(this.inventory.size(), this.inventory, this.pos);
//
//            GameNetworking.sendToPlayers(PlayerLookup.world((ServerWorld) this.world), payload);
//        }
//        super.markDirty();
//    }

    /**
     * Handles item insertion based on current cooking state
     * <br>- Manages oil processing stages (melting and ingredient addition)
     * <br>- Handles recipe overflow recovery with tools
     * <br>- Processes normal recipe ingredient insertion
     * <br>- Starts new recipes when valid ingredients are provided
     */
    @Override
    public InsertResult insertItemSpecified(InteractionContext context, ItemStack givenStack) {
        World world = context.world;
        Optional<RecipeEntry<SequentialCookingRecipe>> recipe = this.getCurrentRecipe(world);
        RecipeStepsCursor<CookingSteps.Step> cursor = this.getCurrentCursor(world);

        // Handle output extraction with container (like bowl for stew)
        if (!this.getOutputStack().isEmpty() && this.requiredContainer != null) {
            if (givenStack.getItem() == this.requiredContainer.asItem()) {
                return this.extractOutputWithContainer(context, givenStack);
            }
        }

        // Prevent insertion if output exists or current step requires tool but none is provided
        if (!this.getOutputStack().isEmpty() || (this.hasIngredient && !this.inOverflow &&
                !this.isToolRequirementEmpty(cursor != null ? cursor.getCurrentStep() : null))) {
            return new InsertResult(false, -1);
        }

        // OIL PROCESSING LOGIC
        // Stage 0: Initial oil insertion - starts dummy recipe for oil melting
        if (this.getInputStack().isEmpty() && !this.hasOil && this.isCookingOil(givenStack)) {
            this.resetCookingState();
            this.updateRecipeData(new RecipeEntry<>(DUMMY_RECIPE_ID, this.dummyRecipe));
            this.hasOil = true;
            this.hasIngredient = true;
            this.markDirty();
            return new InsertResult(true, -1);
        }

        // Stage 0: Oil melting - requires tool to proceed to next stage
        boolean isOilStage0 = this.inOverflow && this.hasOil && this.oilProcessingStage == 0 &&
                recipe.isPresent() && recipe.get().id().equals(DUMMY_RECIPE_ID) && cursor != null;
        // Stage 1: Oil ready - waiting for ingredient to start actual recipe
        boolean isOilStage1 = this.hasOil && this.oilProcessingStage == 1 &&
                recipe.isPresent() && recipe.get().id().equals(DUMMY_RECIPE_ID);

        if (isOilStage0) {
            CookingSteps.Step currentStep = cursor.getCurrentStep();
            if (currentStep == null) {
                Peony.LOGGER.warn("Current step is null in oil stage 0");
                return new InsertResult(false, -1);
            }

            // Use tool to advance oil melting process
            Peony.LOGGER.debug("Into oil stage 0 - tool required");
            if (currentStep.getRequiredTool().test(givenStack) && !this.isToolRequirementEmpty(currentStep)) {
                Peony.LOGGER.debug("Tool used successfully, moving to stage 1");
                this.oilProcessingStage = 1;
                this.heatingTime = 0;
                this.requiredHeatingTime = 0;
                this.overflowTime = 0;
                this.updateRecipeData(new RecipeEntry<>(DUMMY_RECIPE_ID, this.dummyRecipe));
                this.markDirty();
                return new InsertResult(true, -1);
            }
        } else if (isOilStage1) {
            Peony.LOGGER.debug("Into oil stage 1 - ingredient required");
            this.allowOilBasedRecipes = true;

            // Find recipe that requires oil for the given ingredient
            Optional<RecipeEntry<SequentialCookingRecipe>> newRecipe = this.getCurrentRecipe(world, givenStack);
            if (newRecipe.isPresent()) {
                Peony.LOGGER.debug("Found matching recipe: {}", newRecipe.get().id());

                // Transition from oil processing to actual recipe
                this.hasOil = false;
                this.oilProcessingStage = 0;
                this.inOverflow = false;

                return this.startNewRecipe(newRecipe.get(), givenStack);
            } else {
                Peony.LOGGER.debug("No matching recipe found for item: {}", givenStack.getItem());
                return new InsertResult(false, -1);
            }
        }

        // OVERFLOW RECOVERY LOGIC
        // If the cooking time exceeds the expected time, you need to use the tool to proceed to the next step;
        // otherwise, the cooking process will fail.
        if (this.inOverflow && recipe.isPresent() && cursor != null) {
            CookingSteps.Step currentStep = cursor.getCurrentStep();
            if (currentStep == null) {
                return new InsertResult(false, -1);
            }

            // Use tool to advance to next step
            if (currentStep.getRequiredTool().test(givenStack) && !this.isToolRequirementEmpty(currentStep)) {
                this.inOverflow = false;
                this.overflowTime = 0;
                this.hasIngredient = false;

                // Advance to next step or complete cooking
                if (this.currentStepIndex < cursor.getLastStepIndex()) {
                    this.currentStepIndex++;
                    this.heatingTime = 0;
                    this.requiredHeatingTime = 0;
                } else {
                    this.completeCooking(world, context.pos, recipe.get());
                }

                this.markDirty();
                return new InsertResult(true, -1);
            }
        }

        // NORMAL RECIPE PROCESSING
        // Add ingredient to current recipe step
        if (recipe.isPresent() && cursor != null) {
            CookingSteps.Step currentStep = cursor.getCurrentStep();
            if (currentStep == null) {
                return new InsertResult(false, -1);
            }

            if (currentStep.getIngredient().test(givenStack)) {
                this.hasIngredient = true;
                this.markDirty();
                // Store first ingredient in input slot for recipe tracking
                if (this.currentStepIndex == 0 && this.getInputStack().isEmpty()) {
                    this.setInputStack(new ItemStack(givenStack.getItem(), 1));
                }
                return new InsertResult(true, -1);
            }
        } else {
            // START NEW RECIPE
            // No current recipe, try to start new one with given ingredient
            Optional<RecipeEntry<SequentialCookingRecipe>> newRecipe = this.getCurrentRecipe(world, givenStack);
            if (newRecipe.isPresent()) {
                return this.startNewRecipe(newRecipe.get(), givenStack);
            }
        }
        return new InsertResult(false, -1);
    }

    protected InsertResult startNewRecipe(RecipeEntry<SequentialCookingRecipe> recipe, ItemStack givenStack) {
        Peony.LOGGER.debug("Starting new recipe: {}", recipe.id());
        boolean allowed = this.allowOilBasedRecipes;
        this.resetCookingState(false);
        this.updateRecipeData(recipe);
        this.hasIngredient = true;
        this.allowOilBasedRecipes = allowed;
        this.setInputStack(givenStack.copyWithCount(1));
        Peony.LOGGER.debug("New recipe started successfully");
        this.markDirty();
        return new InsertResult(true, -1);
    }

    @Override
    public boolean extractItem(InteractionContext context) {
        PlayerEntity user = context.user;

        ItemStack outputStack = this.getOutputStack();
        if (!outputStack.isEmpty()) {
            if (this.requiredContainer != null) {
                return false;
            }
            user.setStackInHand(context.hand, outputStack);
            this.setOutputStack(ItemStack.EMPTY);
            this.markDirty();
            return true;
        }

        ItemStack inputStack = this.getInputStack();
        if (!inputStack.isEmpty()) {
            user.setStackInHand(context.hand, inputStack);
            this.setInputStack(ItemStack.EMPTY);
            this.resetCookingState();
            this.markDirty();
            return true;
        }

        return false;
    }

    protected InsertResult extractOutputWithContainer(InteractionContext context, ItemStack containerStack) {
        ItemStack outputStack = this.getOutputStack();

        int outputCount = outputStack.getCount();
        int containerCount = containerStack.getCount();
        int containersToUse = Math.min(outputCount, containerCount);

        if (containersToUse > 0) {
            ItemStack resultStack = outputStack.copy();
            resultStack.setCount(containersToUse);

            if (context.user.giveItemStack(resultStack)) {
                outputStack.decrement(containersToUse);
                if (outputStack.isEmpty()) {
                    this.setOutputStack(ItemStack.EMPTY);
                    this.requiredContainer = null;
                }

                this.markDirty();
                return new InsertResult(true, containersToUse);
            }
        }
        return new InsertResult(false, -1);
    }

    public boolean extractFluidWithContainer(InteractionContext context, ItemStack containerStack) {
        if (!this.hasStoredFluid()) {
            return false;
        }

        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = this.fluidStorage.extract(this.fluidStorage.variant, FluidConstants.BUCKET, transaction);
            if (extracted > 0) {
                long inserted = this.fluidStorage.insert(fluidStorage.variant, extracted, transaction);
                if (inserted > 0) {
                    long drained = fluidStorage.extract(fluidStorage.variant, inserted, transaction);

                    if (drained == inserted) {
                        transaction.commit();
                        context.user.setStackInHand(context.hand, containerStack);
                        this.markDirty();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean isCookingOil(ItemStack stack) {
        return stack.isIn(PeonyTags.Items.COOKING_OIL);
    }

    /* COOKING LOGIC */

    protected void updateRecipeData(RecipeEntry<SequentialCookingRecipe> recipe) {
        if (this.cachedRecipe == null || !this.cachedRecipe.id().equals(recipe.id())) {
            this.cachedRecipe = recipe;
            this.requiredHeatingTime = 0;
            this.heatingTime = 0;
            this.overflowTime = 0;
            this.maxOverflowTime = 0;
            this.inOverflow = false;
            this.oilProcessingStage = 0;
        }
    }

    protected Optional<RecipeEntry<SequentialCookingRecipe>> getCurrentRecipe(World world) {
        if (!this.getInputStack().isEmpty()) {
            return this.getCurrentRecipe(world, this.getInputStack());
        }
        if (this.cachedRecipe != null) {
            return Optional.of(this.cachedRecipe);
        }
        return Optional.empty();
    }

    protected Optional<RecipeEntry<SequentialCookingRecipe>> getCurrentRecipe(World world, ItemStack input) {
        return this.getCurrentRecipe(world, input, this.allowOilBasedRecipes);
    }

    protected Optional<RecipeEntry<SequentialCookingRecipe>> getCurrentRecipe(World world, ItemStack input, boolean needOil) {
        if (this.hasOil) {
            if (this.oilProcessingStage == 0) {
                return Optional.of(new RecipeEntry<>(DUMMY_RECIPE_ID, this.dummyRecipe));
            }
            if (this.oilProcessingStage == 1 && this.cachedRecipe != null) {
                return this.matchNewRecipe(world, input, needOil);
            }
        }
        if (this.cachedRecipe != null) {
            return Optional.of(this.cachedRecipe);
        }
        return this.matchNewRecipe(world, input, needOil);
    }

    /**
     * Finds matching recipe considering oil requirements
     * <br>- Handles oil processing dummy recipes
     * <br>- Filters recipes based on oil requirement flag
     * <br>- Caches matched recipe for performance
     */
    private Optional<RecipeEntry<SequentialCookingRecipe>> matchNewRecipe(World world, ItemStack input, boolean needOil) {
        Peony.LOGGER.debug("Allow to match needOil=true recipe: " + this.allowOilBasedRecipes);
        List<RecipeEntry<SequentialCookingRecipe>> allRecipes = world.getRecipeManager()
                .listAllOfType(PeonyRecipes.SEQUENTIAL_COOKING_TYPE);
        Peony.LOGGER.debug("Available recipes: {}", allRecipes.stream().map(r -> r.id().toString()).toList());

        // Filter recipes by oil requirement and ingredient match
        Optional<RecipeEntry<SequentialCookingRecipe>> matchedRecipe = allRecipes.stream()
                .filter(recipe -> {
                    boolean matchesOil = recipe.value().isNeedOil() == needOil;
                    boolean matchesInput = recipe.value().matches(new SequentialCookingRecipeInput(input, needOil), world);
                    Peony.LOGGER.debug("Recipe {} - needOil: {}, matchesOil: {}, matchesInput: {}",
                            recipe.id(), recipe.value().isNeedOil(), matchesOil, matchesInput);
                    return matchesOil && matchesInput;
                })
                .findFirst();
        Peony.LOGGER.debug("Matched recipe: {}", matchedRecipe.map(recipe -> recipe.id().toString()).orElse("None"));
        matchedRecipe.ifPresent(this::updateRecipeData);
        return matchedRecipe;
    }

    @Nullable
    protected CookingSteps getCurrentCookingSteps(World world) {
        if (this.hasOil) {
            ArrayList<CookingSteps.Step> steps = new ArrayList<>();
            if (this.oilProcessingStage == 0) {
                steps.add(new CookingSteps.Step(100, 100));
            } else if (this.oilProcessingStage == 1) {
                steps.add(new CookingSteps.Step(
                        0,
                        0,
                        Ingredient.ofItems(PeonyItems.PLACEHOLDER),
                        Ingredient.ofItems(PeonyItems.PLACEHOLDER)
                ));
            }
            return new CookingSteps(steps);
        }

        Optional<RecipeEntry<SequentialCookingRecipe>> recipe = this.getCurrentRecipe(world);
        return recipe.map(entry -> entry.value().getSteps()).orElse(null);
    }

    @Nullable
    protected RecipeStepsCursor<CookingSteps.Step> getCurrentCursor(World world) {
        return this.getCurrentCursor(world, this.currentStepIndex);
    }

    @Nullable
    protected RecipeStepsCursor<CookingSteps.Step> getCurrentCursor(World world, int index) {
        @Nullable
        CookingSteps steps = this.getCurrentCookingSteps(world);

        if (steps == null || steps.getSteps() == null || steps.getSteps().isEmpty()) {
            if (this.hasOil && this.oilProcessingStage == 1) {
                List<CookingSteps.Step> emptySteps = new ArrayList<>();
                emptySteps.add(new CookingSteps.Step(0, 0,
                        Ingredient.ofItems(PeonyItems.PLACEHOLDER),
                        Ingredient.ofItems(PeonyItems.PLACEHOLDER)
                ));
                return new RecipeStepsCursor<>(emptySteps, 0);
            }
            return null;
        } else {
            int safeIndex = MathHelper.clamp(index, 0, steps.getSteps().size() - 1);
            return steps.createCursor(safeIndex);
        }
    }

    protected void resetCookingState() {
        this.resetCookingState(true);
    }

    protected void resetCookingState(boolean resetContainer) {
        this.currentStepIndex = 0;
        this.heatingTime = 0;
        this.requiredHeatingTime = 0;
        this.overflowTime = 0;
        this.maxOverflowTime = 0;
        this.hasHeat = false;
        this.inOverflow = false;
        this.hasIngredient = false;
        this.hasOil = false;
        this.oilProcessingStage = 0;
        this.allowOilBasedRecipes = false;
        if (resetContainer) {
            this.requiredContainer = null;
        }
        this.cachedRecipe = null;
    }

    protected boolean checkHeatSource(World world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.down());
        if (belowState.getBlock() instanceof HeatProvider heatProvider) {
            this.hasHeat = heatProvider.getLevel().canHeatItems();
            return this.hasHeat;
        }
        this.hasHeat = false;
        return false;
    }

    protected boolean checkTemperature(World world, BlockPos pos, int requiredTemperature) {
        BlockState belowState = world.getBlockState(pos.down());
        if (belowState.getBlock() instanceof HeatProvider heatProvider) {
            Range temperatureRange = heatProvider.getTemperature();
            return temperatureRange.contains(requiredTemperature);
        }
        return false;
    }

    protected int calculateRequiredHeatingTime(World world, BlockPos pos, CookingSteps.Step step) {
        BlockState belowState = world.getBlockState(pos.down());
        if (belowState.getBlock() instanceof HeatProvider heatProvider) {
            return HeatCalculationUtils.calculateHeatingTime(step.getRequiredTime(), heatProvider);
        }
        return -1;
    }

    private boolean isToolRequirementEmpty(@Nullable CookingSteps.Step step) {
        if (step == null) {
            return true;
        }
        return step.getRequiredTool().test(PeonyItems.PLACEHOLDER.getDefaultStack());
    }

    /**
     * Main cooking tick logic
     * <br>- Updates heating progress when conditions are met
     * <br>- Manages overflow state and failure conditions
     * <br>- Handles oil processing stages
     * <br>- Completes cooking when all steps are done
     */
    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (state.contains(SkilletBlock.FACING)) {
            this.cachedDirection = state.get(SkilletBlock.FACING);
        }

        // Skip cooking logic if fluid is stored (for fluid-based recipes)
        if (this.hasStoredFluid()) {
            return;
        }

        boolean hasHeatSource = this.checkHeatSource(world, pos);
        Optional<RecipeEntry<SequentialCookingRecipe>> recipe = this.getCurrentRecipe(world);
        RecipeStepsCursor<CookingSteps.Step> cursor = this.getCurrentCursor(world);

        // OIL STAGE 1: Waiting for ingredient insertion
        if (this.hasOil && this.oilProcessingStage == 1) {
            Peony.LOGGER.debug("Oil stage 1 - waiting for ingredient");
            this.heatingTime = 0;
            this.requiredHeatingTime = 0;
            this.overflowTime = 0;
            return;
        }

        if (recipe.isPresent() && cursor != null) {
            CookingSteps.Step currentStep = cursor.getCurrentStep();

            if (currentStep == null) {
                if (!this.hasOil) {
                    this.resetCookingState();
                    this.markDirty();
                }
                return;
            }

            boolean temperatureMet = this.checkTemperature(world, pos, recipe.get().value().getTemperature());

            // HEATING PROGRESS LOGIC
            if (this.hasIngredient && hasHeatSource && temperatureMet && !this.inOverflow) {
                // Calculate required heating time on first tick with valid conditions
                if (this.requiredHeatingTime <= 0) {
                    this.requiredHeatingTime = this.calculateRequiredHeatingTime(world, pos, currentStep);
                    if (this.requiredHeatingTime <= 0) {
                        return;
                    }
                    this.maxOverflowTime = currentStep.getMaxTimeOverflow();
                }

                // Increment heating progress
                if (this.heatingTime < this.requiredHeatingTime) {
                    this.heatingTime++;
                    this.markDirty();
                } else {
                    // Enter overflow state when heating time is complete
                    this.inOverflow = true;
                    this.markDirty();
                }
            } else {
                // Cool down if conditions are not met
                if (this.heatingTime > 0 && !this.inOverflow) {
                    this.heatingTime = Math.max(0, this.heatingTime - 1);
                    this.markDirty();
                }
            }

            // OVERFLOW HANDLING
            if (this.inOverflow && this.hasOil && this.oilProcessingStage == 0) {
                Peony.LOGGER.debug("Oil melting overflow - waiting for tool");
            } else if (this.inOverflow && !this.hasOil) {
                this.overflowTime++;
                // Fail cooking if overflow time exceeds maximum allowed
                if (this.maxOverflowTime > 0 && this.overflowTime >= this.maxOverflowTime) {
                    this.failCooking(world, pos);
                    return;
                }
                // Auto-advance if no tool is required for this step
                if (this.isToolRequirementEmpty(currentStep)) {
                    this.inOverflow = false;
                    this.overflowTime = 0;
                    this.hasIngredient = false;

                    // Advance to next step or complete cooking
                    if (this.currentStepIndex < cursor.getLastStepIndex()) {
                        this.currentStepIndex++;
                        this.heatingTime = 0;
                        this.requiredHeatingTime = 0;
                        this.markDirty();
                    } else {
                        this.completeCooking(world, pos, recipe.get());
                    }
                }
            }
        } else {
            // Reset if input exists but no valid recipe is found
            if (!this.getInputStack().isEmpty()) {
                this.resetCookingState();
                this.markDirty();
            }
        }

        this.usageCountdown--;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    protected void failCooking(World world, BlockPos pos) {
        Registry<StatusEffect> effects = Registries.STATUS_EFFECT;

        ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);
        stew.set(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, new SuspiciousStewEffectsComponent(List.of(
                new SuspiciousStewEffectsComponent.StewEffect(
                        Registries.STATUS_EFFECT.getEntry(effects.stream().skip(world.random.nextInt(effects.size())).findFirst().get()), 1))));
        this.setOutputStack(stew);
        this.setInputStack(ItemStack.EMPTY);
        this.requiredContainer = Items.BOWL;
        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 0.5f);
        this.resetCookingState();
    }

    protected void completeCooking(World world, BlockPos pos, RecipeEntry<SequentialCookingRecipe> recipe) {
        Output output = recipe.value().getOutput();
        ItemStack outputStack = output.getOutputStack().copy();

        this.requiredContainer = Output.getRequiredContainer(output);
        this.setOutputStack(outputStack);
        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 1.0f);

        ItemStack inputStack = this.getInputStack();
        inputStack.decrement(1);
        if (inputStack.isEmpty()) {
            this.setInputStack(ItemStack.EMPTY);
        }

        this.resetCookingState(false);
        this.markDirty();
    }
}
