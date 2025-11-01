package net.george.peony.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.api.heat.HeatCalculationUtils;
import net.george.peony.api.heat.HeatLevel;
import net.george.peony.api.heat.HeatParticleHelper;
import net.george.peony.api.heat.HeatProvider;
import net.george.peony.api.util.CountdownManager;
import net.george.peony.api.util.NbtSerializable;
import net.george.peony.block.SkilletBlock;
import net.george.peony.block.data.CookingSteps;
import net.george.peony.block.data.Output;
import net.george.peony.block.data.RecipeStepsCursor;
import net.george.peony.block.data.StirFryingData;
import net.george.peony.compat.PeonyDamageTypes;
import net.george.peony.item.PeonyItems;
import net.george.peony.networking.payload.ItemStackSyncS2CPayload;
import net.george.peony.networking.payload.SkilletAnimationDataSyncS2CPayload;
import net.george.peony.networking.payload.SkilletIngredientsSyncS2CPayload;
import net.george.peony.recipe.PeonyRecipes;
import net.george.peony.recipe.SequentialCookingRecipe;
import net.george.peony.recipe.SequentialCookingRecipeInput;
import net.george.peony.util.ArrayListNbtStorage;
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
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Block entity for the Skillet block that handles sequential cooking recipes
 * with multiple steps, ingredient management, and countdown timers.
 * <br>
 * Features include:<br>
 * - State-based cooking process (IDLE, HEATING, STIR_FRYING, etc.)<br>
 * - Oil processing for oil-based recipes<br>
 * - Heat source detection and temperature management<br>
 * - Two heating modes: regular HEATING and STIR_FRYING (with tool interaction)<br>
 * - Network synchronization for multiplayer<br>
 * - NBT persistence for game saves<br>
 */
@SuppressWarnings({"unused"})
public class SkilletBlockEntity extends BlockEntity implements ImplementedInventory, DirectionProvider, AccessibleInventory, BlockEntityTickerProvider {

    /** Dummy recipe ID used for oil processing */
    protected static final Identifier DUMMY_RECIPE_ID = Peony.id("dummy_recipe");

    /** Inventory slots: 0 = input, 1 = output */
    protected final DefaultedList<ItemStack> inventory;

    /** List of ingredients currently added to the skillet */
    public ArrayList<ItemStack> addedIngredients;

    /** Recipe matching utility for sequential cooking recipes */
    protected final RecipeManager.MatchGetter<SequentialCookingRecipeInput, SequentialCookingRecipe> matchGetter;

    /** Dummy recipe used for oil processing stages */
    protected final SequentialCookingRecipe dummyRecipe = new SequentialCookingRecipe(550, false, List.of(), null);

    /** Manages various countdown timers during cooking process */
    protected CountdownManager countdownManager;

    /** Container required to extract the output (e.g., bowl for stew) */
    @Nullable
    protected ItemConvertible requiredContainer;

    /** Cached recipe for performance optimization */
    @Nullable
    protected RecipeEntry<SequentialCookingRecipe> cachedRecipe = null;

    /** Main cooking context containing all cooking state */
    public CookingContext context;

    /** Animation queue data during stir-frying */
    public AnimationData animationData;

    /** Cached block direction for rendering and interactions */
    protected Direction cachedDirection = Direction.NORTH;

    /**
     * Creates a new SkilletBlockEntity at the specified position and state.
     *
     * @param pos the block position
     * @param state the block state
     */
    public SkilletBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.SKILLET, pos, state);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.addedIngredients = new ArrayList<>();
        this.matchGetter = RecipeManager.createCachedMatchGetter(PeonyRecipes.SEQUENTIAL_COOKING_TYPE);
        this.countdownManager = CountdownManager.create();
        this.countdownManager.add("IngredientPlacement", 100);
        this.context = new CookingContext(this, this.inventory, this.countdownManager);
        this.animationData = new AnimationData();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    /**
     * Gets the list of ingredients currently added to the skillet.
     *
     * @return the list of added ingredients
     */
    public ArrayList<ItemStack> getAddedIngredients() {
        return this.addedIngredients;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    /**
     * Gets the input stack from the inventory.
     *
     * @return the input item stack
     */
    public ItemStack getInputStack() {
        return this.getStack(0);
    }

    /**
     * Sets the input stack and marks the block entity as dirty.
     *
     * @param stack the new input stack
     */
    public void setInputStack(ItemStack stack) {
        this.inventory.set(0, stack);
        this.cachedRecipe = null;
        this.markDirty();
    }

    /**
     * Gets the output stack from the inventory.
     *
     * @return the output item stack
     */
    public ItemStack getOutputStack() {
        return this.getStack(1);
    }

    /**
     * Sets the output stack and marks the block entity as dirty.
     *
     * @param stack the new output stack
     */
    public void setOutputStack(ItemStack stack) {
        this.inventory.set(1, stack);
        this.markDirty();
    }

    @Override
    public Direction getDirection() {
        return this.cachedDirection;
    }

    /**
     * Gets the container required to extract the output.
     *
     * @return the required container item, or null if none required
     */
    @Nullable
    public ItemConvertible getRequiredContainer() {
        return this.requiredContainer;
    }

    public void setRequiredContainer(@Nullable ItemConvertible item) {
        this.requiredContainer = item;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        ArrayListNbtStorage.writeItemList(nbt, "AddedIngredients", this.addedIngredients, registryLookup);
        this.updateAddedItems();

        // Save cooking context
        NbtCompound contextNbt = new NbtCompound();
        this.context.writeNbt(contextNbt, registryLookup);
        nbt.put("CookingContext", contextNbt);

        nbt.putString("CachedDirection", this.getDirection().getName());

        // Save countdown manager state
        NbtCompound countdownNbt = new NbtCompound();
        this.countdownManager.writeNbt(countdownNbt, registryLookup);
        nbt.put("CountdownManager", countdownNbt);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        this.addedIngredients = ArrayListNbtStorage.readItemList(nbt, "AddedIngredients", registryLookup);

        // Read cooking context
        if (nbt.contains("CookingContext")) {
            this.context.readNbt(nbt.getCompound("CookingContext"), registryLookup);
        }

        @Nullable
        Direction direction = Direction.byName(nbt.getString("CachedDirection"));
        this.cachedDirection = direction != null ? direction : Direction.NORTH;

        // Read countdown manager state
        if (nbt.contains("CountdownManager")) {
            this.countdownManager.readNbt(nbt.getCompound("CountdownManager"), registryLookup);
        }

        super.readNbt(nbt, registryLookup);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
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

    @Override
    public void markDirty() {
        if (!Objects.requireNonNull(this.world).isClient) {
            CustomPayload payload = new ItemStackSyncS2CPayload(this.inventory.size(), this.inventory, this.pos);
            GameNetworking.sendToPlayers(PlayerLookup.world((ServerWorld) this.world), payload);
            this.updateAddedItems();
        }
        super.markDirty();
    }

    @Override
    public InsertResult insertItemSpecified(InteractionContext context, ItemStack givenStack) {
        // First check if output can be extracted with container
        if (!this.getOutputStack().isEmpty() && this.requiredContainer != null) {
            if (givenStack.getItem() == this.requiredContainer.asItem()) {
                return this.extractOutputWithContainer(context, givenStack);
            }
        }

        // Otherwise delegate to state handling
        return this.context.state.getState().onItemInserted(this.context, context, givenStack);
    }

    /**
     * Synchronizes the added ingredients with clients.
     */
    protected void updateAddedItems() {
        this.updateAddedItems(this.addedIngredients);
    }

    /**
     * Synchronizes the specified ingredients with clients.
     *
     * @param addedIngredients the ingredients to synchronize
     */
    protected void updateAddedItems(List<ItemStack> addedIngredients) {
        CustomPayload payload = new SkilletIngredientsSyncS2CPayload(addedIngredients, this.context.allowOilBasedRecipes, this.requiredContainer == null ? ItemStack.EMPTY : this.requiredContainer.asItem().getDefaultStack(), this.pos);
        GameNetworking.sendToPlayers(PlayerLookup.world((ServerWorld) this.world), payload);
    }

    @Override
    public boolean extractItem(InteractionContext context) {
        PlayerEntity user = context.user;

        ItemStack outputStack = this.getOutputStack();
        if (!outputStack.isEmpty()) {
            // If output exists and requires container, extract with container
            if (this.requiredContainer != null) {
                ItemStack heldStack = user.getStackInHand(context.hand);
                if (heldStack.getItem() == this.requiredContainer.asItem()) {
                    InsertResult result = this.extractOutputWithContainer(context, heldStack);
                    return result.isSuccess();
                }
                return false;
            }

            // No container required, extract directly
            user.setStackInHand(context.hand, outputStack);
            this.setOutputStack(ItemStack.EMPTY);
            this.resetCookingVariables();
            this.addedIngredients.clear();
            this.updateAddedItems();
            this.markDirty();
            return true;
        }

        ItemStack inputStack = this.getInputStack();
        if (!inputStack.isEmpty()) {
            user.setStackInHand(context.hand, inputStack);
            user.damage(PeonyDamageTypes.of(context.world, PeonyDamageTypes.SCALD), context.world.random.nextBetween(1, 2));
            this.setInputStack(ItemStack.EMPTY);
            this.resetCookingVariables();
            this.addedIngredients.clear();
            this.updateAddedItems();
            this.markDirty();
            return true;
        }

        return false;
    }

    /**
     * Extracts output using a container item (e.g., bowl for stew).
     *
     * @param context the interaction context
     * @param containerStack the container stack to use
     * @return the insertion result
     */
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

                // Consume container
                if (!context.user.getAbilities().creativeMode) {
                    containerStack.decrement(1);
                }

                if (outputStack.isEmpty()) {
                    this.setOutputStack(ItemStack.EMPTY);
                    this.requiredContainer = null;

                    // Reset state when all output is extracted
                    this.resetCookingVariables();
                }

                this.markDirty();
                this.addedIngredients.clear();
                this.updateAddedItems();
                return new InsertResult(true, 1); // Consume 1 container
            }
        }
        return new InsertResult(false, -1);
    }

    /**
     * Checks if the given stack is cooking oil.
     *
     * @param stack the item stack to check
     * @return true if the stack is cooking oil
     */
    public static boolean isCookingOil(ItemStack stack) {
        return stack.isIn(PeonyTags.Items.COOKING_OIL);
    }

    /* COOKING LOGIC */

    /**
     * Updates recipe data when a new recipe is started.
     *
     * @param recipe the recipe to update to
     */
    protected void updateRecipeData(RecipeEntry<SequentialCookingRecipe> recipe) {
        if (this.cachedRecipe == null || !this.cachedRecipe.id().equals(recipe.id())) {
            this.cachedRecipe = recipe;
            this.context.cachedRecipe = recipe;
            this.context.requiredHeatingTime = 0;
            this.context.heatingTime = 0;
            this.context.overflowTime = 0;
            this.context.maxOverflowTime = 0;
            this.context.inOverflow = false;
            this.context.oilProcessingStage = 0;
            this.countdownManager.reset("IngredientPlacement");
            Peony.LOGGER.debug("Updated recipe data: {}", recipe.id());
        }
    }

    /**
     * Gets the current recipe based on the input and cooking state.
     *
     * @param world the world
     * @return the current recipe, if any
     */
    protected Optional<RecipeEntry<SequentialCookingRecipe>> getCurrentRecipe(World world) {
        if (!this.getInputStack().isEmpty()) {
            return this.getCurrentRecipe(world, this.getInputStack());
        }
        if (this.cachedRecipe != null) {
            return Optional.of(this.cachedRecipe);
        }
        if (this.context.hasOil) {
            if (this.context.oilProcessingStage == 0) {
                return Optional.of(new RecipeEntry<>(DUMMY_RECIPE_ID, this.dummyRecipe));
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the current recipe for the given input stack.
     *
     * @param world the world
     * @param input the input stack
     * @return the current recipe, if any
     */
    protected Optional<RecipeEntry<SequentialCookingRecipe>> getCurrentRecipe(World world, ItemStack input) {
        return this.getCurrentRecipe(world, input, this.context.allowOilBasedRecipes);
    }

    /**
     * Gets the current recipe considering oil requirements.
     *
     * @param world the world
     * @param input the input stack
     * @param needOil whether oil-based recipes should be considered
     * @return the current recipe, if any
     */
    protected Optional<RecipeEntry<SequentialCookingRecipe>> getCurrentRecipe(World world, ItemStack input, boolean needOil) {
        if (this.context.hasOil) {
            if (this.context.oilProcessingStage == 0) {
                return Optional.of(new RecipeEntry<>(DUMMY_RECIPE_ID, this.dummyRecipe));
            }
            if (this.context.oilProcessingStage == 1 && this.cachedRecipe != null) {
                return this.findMatchingRecipe(world, input, needOil);
            }
        }
        if (this.cachedRecipe != null) {
            return Optional.of(this.cachedRecipe);
        }
        return this.findMatchingRecipe(world, input, needOil);
    }

    /**
     * Finds matching recipe considering oil requirements.
     *
     * @param world the world
     * @param input the input stack
     * @param needOil whether oil-based recipes should be considered
     * @return the matching recipe, if any
     */
    private Optional<RecipeEntry<SequentialCookingRecipe>> findMatchingRecipe(World world, ItemStack input, boolean needOil) {
        Peony.LOGGER.debug("Allow to match needOil=true recipe: " + this.context.allowOilBasedRecipes);
        List<RecipeEntry<SequentialCookingRecipe>> allRecipes = world.getRecipeManager()
                .listAllOfType(PeonyRecipes.SEQUENTIAL_COOKING_TYPE);

        // Filter recipes by oil requirement and ingredient match
        Optional<RecipeEntry<SequentialCookingRecipe>> matchedRecipe = allRecipes.stream()
                .filter(recipe -> {
                    boolean matchesOil = recipe.value().isNeedOil() == needOil;
                    boolean matchesInput = recipe.value().matches(new SequentialCookingRecipeInput(input, needOil), world);
                    return matchesOil && matchesInput;
                })
                .findFirst();

        matchedRecipe.ifPresent(this::updateRecipeData);
        return matchedRecipe;
    }

    /**
     * Gets the current cooking steps based on the cooking state.
     *
     * @param world the world
     * @return the current cooking steps, or null if none
     */
    @Nullable
    protected CookingSteps getCurrentCookingSteps(World world) {
        if (this.context.hasOil) {
            ArrayList<CookingSteps.Step> steps = new ArrayList<>();
            if (this.context.oilProcessingStage == 0) {
                steps.add(new CookingSteps.Step(100, 100));
            } else if (this.context.oilProcessingStage == 1) {
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

    /**
     * Gets the current recipe steps cursor at the specified index.
     *
     * @param world the world
     * @param index the step index
     * @return the recipe steps cursor, or null if none
     */
    @Nullable
    protected RecipeStepsCursor<CookingSteps.Step> getCurrentCursor(World world, int index) {
        @Nullable
        CookingSteps steps = this.getCurrentCookingSteps(world);

        if (steps == null || steps.getSteps() == null || steps.getSteps().isEmpty()) {
            if (this.context.hasOil && this.context.oilProcessingStage == 1) {
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

    /**
     * Resets all cooking variables to their initial state.
     */
    protected void resetCookingVariables() {
        this.resetCookingVariables(true);
    }

    /**
     * Resets all cooking state variables to their initial values.
     *
     * @param resetContainer whether to reset the required container
     */
    protected void resetCookingVariables(boolean resetContainer) {
        this.context.reset();
        if (resetContainer) {
            this.requiredContainer = null;
        }
        this.cachedRecipe = null;
        // Ensure state is correctly set to IDLE
        if (this.context.state != CookingStates.IDLE) {
            this.context.state = CookingStates.IDLE;
        }
        Peony.LOGGER.debug("Reset cooking variables");
    }

    /**
     * Checks if there is a valid heat source below the skillet.
     *
     * @param world the world
     * @param pos the block position
     * @return true if there is a valid heat source
     */
    protected boolean checkHeatSource(World world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.down());
        if (belowState.getBlock() instanceof HeatProvider heatProvider) {
            this.context.hasHeat = heatProvider.getLevel().canHeatItems();
            return this.context.hasHeat;
        }
        this.context.hasHeat = false;
        return false;
    }

    /**
     * Checks if the temperature meets the recipe requirements.
     *
     * @param world the world
     * @param pos the block position
     * @param requiredTemperature the required temperature
     * @return true if the temperature meets requirements
     */
    protected boolean checkTemperature(World world, BlockPos pos, int requiredTemperature) {
        BlockState belowState = world.getBlockState(pos.down());
        if (belowState.getBlock() instanceof HeatProvider heatProvider) {
            Range temperatureRange = heatProvider.getTemperature();
            return temperatureRange.contains(requiredTemperature);
        }
        return false;
    }

    /**
     * Calculates the required heating time based on heat source and recipe step.
     *
     * @param world the world
     * @param pos the block position
     * @param step the cooking step
     * @return the required heating time, or -1 if invalid
     */
    public static int calculateRequiredHeatingTime(World world, BlockPos pos, CookingSteps.Step step) {
        return calculateRequiredHeatingTime(world, pos, step.getRequiredTime());
    }

    /**
     * Calculates the required heating time based on heat source and recipe step.
     *
     * @param world the world
     * @param pos the block position
     * @param requiredTime the regular heating time
     * @return the required heating time, or -1 if invalid
     */
    public static int calculateRequiredHeatingTime(World world, BlockPos pos, int requiredTime) {
        BlockState belowState = world.getBlockState(pos.down());
        if (belowState.getBlock() instanceof HeatProvider heatProvider) {
            return HeatCalculationUtils.calculateHeatingTime(requiredTime, heatProvider);
        }
        return -1;
    }

    /**
     * Checks if the tool requirement for a step is empty (no tool required).
     *
     * @param step the cooking step to check
     * @return true if no tool is required
     */
    private boolean isToolRequirementEmpty(@Nullable CookingSteps.Step step) {
        if (step == null) {
            return true;
        }
        return step.getRequiredTool().test(PeonyItems.PLACEHOLDER.getDefaultStack());
    }

    /**
     * Determines which heating mode to use based on the current step's frying data.
     * Returns STIR_FRYING if frying data exists and is not default, otherwise HEATING.
     *
     * @param world the world
     * @return the appropriate cooking state for heating
     */
    protected CookingStates determineHeatingMode(World world) {
        CookingSteps.Step currentStep = this.context.getCurrentStep(world);
        if (currentStep != null && currentStep.getFryingData() != null &&
                !currentStep.getFryingData().equals(StirFryingData.DEFAULT)) {
            Peony.LOGGER.debug("Step requires stir-frying mode, using STIR_FRYING state");
            return CookingStates.STIR_FRYING;
        } else {
            Peony.LOGGER.debug("Step requires regular heating mode, using HEATING state");
            return CookingStates.HEATING;
        }
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (state.contains(SkilletBlock.FACING)) {
            this.cachedDirection = state.get(SkilletBlock.FACING);
        }

        // Debug logging for state tracking
        Peony.LOGGER.debug("Skillet tick - State: {}, Step: {}, Heating: {}/{}, HasIngredient: {}",
                this.context.state, this.context.currentStepIndex,
                this.context.heatingTime, this.context.requiredHeatingTime,
                this.context.hasIngredient);

        // Delegate to current state for processing
        this.context.state.getState().tick(this.context, world, pos, state);
    }

    /**
     * Handles cooking failure by creating suspicious stew.
     *
     * @param world the world
     * @param pos the block position
     */
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
        this.resetCookingVariables(false);
        this.updateAddedItems();
        this.markDirty();
    }

    /**
     * Completes the cooking process and produces the output.
     *
     * @param world the world
     * @param pos the block position
     * @param recipe the completed recipe
     */
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

        // Transition to completed state instead of resetting
        this.context.transitionTo(CookingStates.COMPLETED);
        this.updateAddedItems();
        this.markDirty();

        Peony.LOGGER.debug("Cooking completed successfully, output: {}", outputStack.getItem());
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(World world, BlockPos pos, BlockState state) {
        BlockState down = world.getBlockState(pos.down());
        if (down.getBlock() instanceof HeatProvider heatProvider) {
            if (world.random.nextInt(15) == 0) {
                if (this.context.hasOil) {
                    HeatParticleHelper.spawnHeatParticles(world, pos.down(), HeatLevel.BLAZING);
                } else if (!this.getInputStack().isEmpty()) {
                    HeatParticleHelper.spawnHeatParticles(world, pos.down(), heatProvider.getHeat());
                }
            }
        }
    }

    /**
     * Starts a new cooking recipe with the given recipe and ingredient.
     * Automatically chooses between HEATING and STIR_FRYING based on step requirements.
     *
     * @param recipe the recipe to start
     * @param givenStack the ingredient stack
     * @return the insertion result
     */
    public InsertResult startRecipe(RecipeEntry<SequentialCookingRecipe> recipe, ItemStack givenStack) {
        Peony.LOGGER.debug("Starting new recipe: {}", recipe.id());
        boolean allowed = context.allowOilBasedRecipes;

        this.resetCookingVariables();
        this.updateRecipeData(recipe);
        this.setInputStack(givenStack.copyWithCount(1));
        this.addedIngredients.add(givenStack.copyWithCount(1));
        this.context.hasIngredient = true;
        this.context.allowOilBasedRecipes = allowed;
        this.updateAddedItems();
        this.markDirty();

        // Determine which heating mode to use based on the first step
        CookingStates heatingMode = this.determineHeatingMode(this.world);
        this.context.transitionTo(heatingMode);

        Peony.LOGGER.debug("New recipe started successfully with mode: {}", heatingMode);
        return new InsertResult(true, -1);
    }

    /**
     * Starts oil processing with the given oil stack.
     *
     * @param oilStack the oil stack to process
     * @return the insertion result
     */
    public InsertResult startOilProcessing(ItemStack oilStack) {
        this.resetCookingVariables();
        this.updateRecipeData(new RecipeEntry<>(DUMMY_RECIPE_ID, this.dummyRecipe));
        this.addedIngredients.add(oilStack.copyWithCount(1));
        this.context.hasOil = true;
        this.context.hasIngredient = true;
        this.updateAddedItems();
        this.markDirty();

        this.context.transitionTo(CookingStates.OIL_PROCESSING);
        return new InsertResult(true, -1);
    }

    /**
     * Cooking context containing all cooking state and references.
     * This class manages the complete state of the cooking process.
     */
    public static class CookingContext implements NbtSerializable {
        // Basic cooking state
        public int currentStepIndex = 0;
        public int heatingTime = 0;
        public int requiredHeatingTime = 0;
        public int overflowTime = 0;
        public int maxOverflowTime = 0;
        public boolean hasHeat = false;
        public boolean inOverflow = false;
        public boolean hasIngredient = false;
        public boolean hasOil = false;
        public int oilProcessingStage = 0;
        public boolean allowOilBasedRecipes = false;

        // Stir-frying related state
        public boolean inStirFrying = false;
        public int stirFryingCount = 0;
        public int requiredStirFryingCount = 0;
        public int stirFryingTime = 0;

        // References to parent and data structures
        public final SkilletBlockEntity skillet;
        public final DefaultedList<ItemStack> inventory;
        public final CountdownManager countdownManager;
        public CookingStates state;
        @Nullable
        public RecipeEntry<SequentialCookingRecipe> cachedRecipe;

        /**
         * Creates a new cooking context.
         *
         * @param skillet the parent skillet block entity
         * @param inventory the inventory
         * @param countdownManager the countdown manager
         */
        CookingContext(SkilletBlockEntity skillet, DefaultedList<ItemStack> inventory, CountdownManager countdownManager) {
            this.skillet = skillet;
            this.inventory = inventory;
            this.countdownManager = countdownManager;
            this.state = CookingStates.IDLE;
        }

        /**
         * Gets the input stack from the inventory.
         *
         * @return the input stack
         */
        public ItemStack getInputStack() {
            return this.inventory.getFirst();
        }

        /**
         * Sets the input stack and marks the skillet as dirty.
         *
         * @param stack the new input stack
         */
        public void setInputStack(ItemStack stack) {
            this.inventory.set(0, stack);
            this.skillet.cachedRecipe = null;
            this.skillet.markDirty();
        }

        /**
         * Gets the output stack from the inventory.
         *
         * @return the output stack
         */
        public ItemStack getOutputStack() {
            return this.inventory.get(1);
        }

        /**
         * Sets the output stack and marks the skillet as dirty.
         *
         * @param stack the new output stack
         */
        public void setOutputStack(ItemStack stack) {
            this.inventory.set(1, stack);
            this.skillet.markDirty();
        }

        /**
         * Transitions to a new cooking state.
         *
         * @param newState the state to transition to
         */
        public void transitionTo(CookingStates newState) {
            World world = this.skillet.world;
            BlockPos pos = this.skillet.getPos();
            if (world != null && pos != null && this.state != newState) {
                Peony.LOGGER.debug("Transitioning from {} to {}", this.state, newState);

                // Call exit method of old state
                this.state.getState().onExit(this, world, pos);

                // Save old state for debugging
                CookingStates oldState = this.state;

                // Update state
                this.state = newState;

                // Call enter method of new state
                this.state.getState().onEnter(this, world, pos);

                this.skillet.markDirty();

                Peony.LOGGER.debug("Successfully transitioned from {} to {}", oldState, newState);
            }
        }

        /**
         * Gets the current cooking step.
         *
         * @param world the world
         * @return the current step, or null if none
         */
        @Nullable
        public CookingSteps.Step getCurrentStep(World world) {
            RecipeStepsCursor<CookingSteps.Step> cursor = this.getCurrentCursor(world);
            return cursor != null ? cursor.getCurrentStep() : null;
        }

        /**
         * Gets the current recipe steps cursor.
         *
         * @param world the world
         * @return the current cursor, or null if none
         */
        @Nullable
        public RecipeStepsCursor<CookingSteps.Step> getCurrentCursor(World world) {
            return this.skillet.getCurrentCursor(world, this.currentStepIndex);
        }

        /**
         * A convenient method for obtaining animation data
         * @return the animation data
         */
        public AnimationData getAnimationData() {
            return this.skillet.animationData;
        }

        /**
         * Resets all cooking variables to their initial state.
         */
        public void reset() {
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
            this.inStirFrying = false;
            this.stirFryingCount = 0;
            this.requiredStirFryingCount = 0;
            this.stirFryingTime = 0;
            this.countdownManager.reset("IngredientPlacement");
            this.cachedRecipe = null;
            this.state = CookingStates.IDLE;
        }

        @Override
        public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            nbt.putString("State", this.state.name());
            nbt.putInt("CurrentStepIndex", this.currentStepIndex);
            nbt.putInt("HeatingTime", this.heatingTime);
            nbt.putInt("RequiredHeatingTime", this.requiredHeatingTime);
            nbt.putInt("OverflowTime", this.overflowTime);
            nbt.putInt("MaxOverflowTime", this.maxOverflowTime);
            nbt.putBoolean("HasHeat", this.hasHeat);
            nbt.putBoolean("InOverflow", this.inOverflow);
            nbt.putBoolean("HasIngredient", this.hasIngredient);
            nbt.putBoolean("HasOil", this.hasOil);
            nbt.putInt("OilProcessingStage", this.oilProcessingStage);
            nbt.putBoolean("AllowOilBasedRecipes", this.allowOilBasedRecipes);

            // Stir-fry status
            nbt.putBoolean("InStirFrying", this.inStirFrying);
            nbt.putInt("StirFryingCount", this.stirFryingCount);
            nbt.putInt("RequiredStirFryingCount", this.requiredStirFryingCount);
            nbt.putInt("StirFryingTime", this.stirFryingTime);
        }

        @Override
        public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            if (nbt.contains("State")) {
                try {
                    String stateName = nbt.getString("State");
                    this.state = CookingStates.valueOf(stateName);
                    Peony.LOGGER.debug("Restored state from NBT: {}", stateName);
                } catch (IllegalArgumentException exception) {
                    this.state = CookingStates.IDLE;
                    Peony.LOGGER.warn("Failed to restore state from NBT, defaulting to IDLE");
                }
            } else {
                this.state = CookingStates.IDLE;
            }
            this.currentStepIndex = nbt.getInt("CurrentStepIndex");
            this.heatingTime = nbt.getInt("HeatingTime");
            this.requiredHeatingTime = nbt.getInt("RequiredHeatingTime");
            this.overflowTime = nbt.getInt("OverflowTime");
            this.maxOverflowTime = nbt.getInt("MaxOverflowTime");
            this.hasHeat = nbt.getBoolean("HasHeat");
            this.inOverflow = nbt.getBoolean("InOverflow");
            this.hasIngredient = nbt.getBoolean("HasIngredient");
            this.hasOil = nbt.getBoolean("HasOil");
            this.oilProcessingStage = nbt.getInt("OilProcessingStage");
            this.allowOilBasedRecipes = nbt.getBoolean("AllowOilBasedRecipes");

            // Read stir-fry status
            this.inStirFrying = nbt.getBoolean("InStirFrying");
            this.stirFryingCount = nbt.getInt("StirFryingCount");
            this.requiredStirFryingCount = nbt.getInt("RequiredStirFryingCount");
            this.stirFryingTime = nbt.getInt("StirFryingTime");
        }
    }

    /**
     * Enum representing all possible cooking states for the skillet.
     */
    public enum CookingStates implements StringIdentifiable {
        IDLE("idle", new IdleState()),
        OIL_PROCESSING("oil_processing", new OilProcessingState()),
        HEATING("heating", new HeatingState()),
        STIR_FRYING("stir_frying", new StirFryingState()),
        OVERFLOW("overflow", new OverflowState()),
        WAITING_FOR_INGREDIENT("waiting_for_ingredient", new WaitingForIngredientState()),
        COMPLETED("completed", new CompletedState()),
        FAILED("failed", new FailedState());

        private final String name;
        private final CookingState state;

        CookingStates(String name, CookingState state) {
            this.name = name;
            this.state = state;
        }

        /**
         * Gets the state implementation.
         *
         * @return the cooking state implementation
         */
        public CookingState getState() {
            return this.state;
        }

        @Override
        public String asString() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    /**
     * Idle state - waiting for initial input.
     */
    @ApiStatus.NonExtendable
    public static class IdleState implements CookingState {
        public static final IdleState INSTANCE = new IdleState();

        @Override
        public void tick(CookingContext context, World world, BlockPos pos, BlockState state) {
            // Idle state does nothing
        }

        @Override
        public InsertResult onItemInserted(CookingContext context, InteractionContext interactionContext, ItemStack givenStack) {
            SkilletBlockEntity skillet = context.skillet;
            World world = interactionContext.world;

            // First check if it's oil
            if (SkilletBlockEntity.isCookingOil(givenStack)) {
                return skillet.startOilProcessing(givenStack);
            }

            // Then check if we can start a new recipe
            Optional<RecipeEntry<SequentialCookingRecipe>> recipe = skillet.getCurrentRecipe(world, givenStack);
            if (recipe.isPresent()) {
                return skillet.startRecipe(recipe.get(), givenStack);
            }

            return new InsertResult(false, -1);
        }

        @Override
        public void onEnter(CookingContext context, World world, BlockPos pos) {
            Peony.LOGGER.debug("Entered IDLE state");
        }

        @Override
        public void onExit(CookingContext context, World world, BlockPos pos) {
            Peony.LOGGER.debug("Exited IDLE state");
        }

        @Override
        public CookingStates asState() {
            return CookingStates.IDLE;
        }
    }

    /**
     * Oil processing state - handles oil heating and preparation.
     */
    @ApiStatus.NonExtendable
    public static class OilProcessingState implements CookingState {
        public static final OilProcessingState INSTANCE = new OilProcessingState();

        @Override
        public void tick(CookingContext context, World world, BlockPos pos, BlockState state) {
            boolean hasHeatSource = context.skillet.checkHeatSource(world, pos);

            if (context.oilProcessingStage == 0) {
                if (context.hasIngredient && hasHeatSource) {
                    if (context.requiredHeatingTime <= 0) {
                        context.requiredHeatingTime = calculateRequiredHeatingTime(world, pos, 100);
                        context.maxOverflowTime = 100;
                    }

                    if (context.heatingTime < context.requiredHeatingTime) {
                        context.heatingTime++;
                        context.skillet.markDirty();
                    } else {
                        context.inOverflow = true;
                        context.skillet.markDirty();
                    }
                } else {
                    if (context.heatingTime > 0 && !context.inOverflow) {
                        context.heatingTime = Math.max(0, context.heatingTime - 1);
                        context.skillet.markDirty();
                    }
                }
            } else if (context.oilProcessingStage == 1) {
                context.heatingTime = 0;
                context.requiredHeatingTime = 0;
                context.overflowTime = 0;
            }
        }

        @Override
        public InsertResult onItemInserted(CookingContext context, InteractionContext interactionContext, ItemStack givenStack) {
            World world = interactionContext.world;

            if (context.oilProcessingStage == 0 && context.inOverflow) {
                CookingSteps.Step currentStep = context.getCurrentStep(world);
                if (currentStep != null && currentStep.getRequiredTool().test(givenStack) &&
                        !context.skillet.isToolRequirementEmpty(currentStep)) {
                    Peony.LOGGER.debug("Tool used successfully, moving to stage 1");
                    context.oilProcessingStage = 1;
                    context.heatingTime = 0;
                    context.requiredHeatingTime = 0;
                    context.overflowTime = 0;
                    context.inOverflow = false;
                    context.allowOilBasedRecipes = true;
                    context.skillet.updateRecipeData(new RecipeEntry<>(SkilletBlockEntity.DUMMY_RECIPE_ID, context.skillet.dummyRecipe));
                    context.skillet.updateAddedItems();
                    context.skillet.markDirty();
                    return new InsertResult(true, -1);
                }
            } else if (context.oilProcessingStage == 1) {
                Peony.LOGGER.debug("Oil stage 1 - ingredient required");

                Optional<RecipeEntry<SequentialCookingRecipe>> newRecipe = context.skillet.getCurrentRecipe(world, givenStack);
                if (newRecipe.isPresent()) {
                    Peony.LOGGER.debug("Found matching recipe: {}", newRecipe.get().id());

                    context.hasOil = false;
                    context.oilProcessingStage = 0;
                    context.inOverflow = false;

                    return startNewRecipe(context, newRecipe.get(), givenStack);
                } else {
                    Peony.LOGGER.debug("No matching recipe found for item: {}", givenStack.getItem());
                    return new InsertResult(false, -1);
                }
            }

            return new InsertResult(false, -1);
        }

        @Override
        public void onEnter(CookingContext context, World world, BlockPos pos) {
            Peony.LOGGER.debug("Entered OIL_PROCESSING state, stage: {}", context.oilProcessingStage);
            context.skillet.updateRecipeData(new RecipeEntry<>(SkilletBlockEntity.DUMMY_RECIPE_ID, context.skillet.dummyRecipe));
        }

        @Override
        public void onExit(CookingContext context, World world, BlockPos pos) {
            Peony.LOGGER.debug("Exited OIL_PROCESSING state");
        }

        @Override
        public CookingStates asState() {
            return CookingStates.OIL_PROCESSING;
        }

        private InsertResult startNewRecipe(CookingContext context, RecipeEntry<SequentialCookingRecipe> recipe, ItemStack givenStack) {
            Peony.LOGGER.debug("Starting new recipe with oil: {}", recipe.id());
            boolean allowed = context.allowOilBasedRecipes;

            context.reset();
            context.skillet.updateRecipeData(recipe);
            context.setInputStack(givenStack.copyWithCount(1));
            context.skillet.addedIngredients.add(givenStack);
            context.hasIngredient = true;
            context.allowOilBasedRecipes = allowed;
            context.skillet.updateAddedItems();
            context.skillet.markDirty();

            // Determine which heating mode to use based on the first step
            CookingStates heatingMode = context.skillet.determineHeatingMode(context.skillet.world);
            context.transitionTo(heatingMode);

            Peony.LOGGER.debug("New recipe with oil started successfully with mode: {}", heatingMode);
            return new InsertResult(true, -1);
        }
    }

    /**
     * Heating state - handles the regular heating phase of cooking.
     * This is used for steps that don't require stir-frying.
     */
    @ApiStatus.NonExtendable
    public static class HeatingState implements CookingState {
        public static final HeatingState INSTANCE = new HeatingState();

        @Override
        public void tick(CookingContext context, World world, BlockPos pos, BlockState state) {
            Optional<RecipeEntry<SequentialCookingRecipe>> recipe = context.skillet.getCurrentRecipe(world);
            if (recipe.isEmpty()) {
                context.transitionTo(CookingStates.IDLE);
                return;
            }

            boolean hasHeatSource = context.skillet.checkHeatSource(world, pos);
            boolean temperatureMet = context.skillet.checkTemperature(world, pos, recipe.get().value().getTemperature());

            if (context.hasIngredient && hasHeatSource && temperatureMet) {
                if (context.requiredHeatingTime <= 0) {
                    CookingSteps.Step currentStep = context.getCurrentStep(world);
                    if (currentStep != null) {
                        context.requiredHeatingTime = calculateRequiredHeatingTime(world, pos, currentStep);
                        context.maxOverflowTime = currentStep.getMaxTimeOverflow();
                        Peony.LOGGER.debug("Set required heating time: {}", context.requiredHeatingTime);
                    }
                }

                if (context.requiredHeatingTime > 0 && context.heatingTime < context.requiredHeatingTime) {
                    context.heatingTime++;
                    context.skillet.markDirty();
                } else if (context.heatingTime >= context.requiredHeatingTime) {
                    // Regular heating completed, move to overflow for tool interaction
                    Peony.LOGGER.debug("Regular heating completed, moving to OVERFLOW");
                    context.transitionTo(CookingStates.OVERFLOW);
                }
            } else {
                // Cooling logic
                if (context.heatingTime > 0) {
                    context.heatingTime = Math.max(0, context.heatingTime - 1);
                    context.skillet.markDirty();
                }
            }
        }

        @Override
        public InsertResult onItemInserted(CookingContext context, InteractionContext interactionContext, ItemStack givenStack) {
            // Do not accept items during heating
            return new InsertResult(false, -1);
        }

        @Override
        public void onEnter(CookingContext context, World world, BlockPos pos) {
            Peony.LOGGER.debug("Entered HEATING state (regular heating mode)");
        }

        @Override
        public void onExit(CookingContext context, World world, BlockPos pos) {
            Peony.LOGGER.debug("Exited HEATING state");
        }

        @Override
        public CookingStates asState() {
            return CookingStates.HEATING;
        }
    }

    /**
     * Stir-frying state - handles the stir-frying mechanics with tool interaction.
     * This is an alternative to regular heating mode for steps that require stir-frying.
     */
    @ApiStatus.NonExtendable
    public static class StirFryingState implements CookingState {
        public static final StirFryingState INSTANCE = new StirFryingState();

        @Override
        public void tick(CookingContext context, World world, BlockPos pos, BlockState state) {
            Optional<RecipeEntry<SequentialCookingRecipe>> recipe = context.skillet.getCurrentRecipe(world);
            if (recipe.isEmpty()) {
                context.transitionTo(CookingStates.IDLE);
                return;
            }

            CookingSteps.Step currentStep = context.getCurrentStep(world);
            if (currentStep == null) {
                context.transitionTo(CookingStates.IDLE);
                return;
            }

            // Stir-frying timer - this replaces the heating timer in stir-fry mode
            context.stirFryingTime++;

            // Check for timeout - use the step's required time as the stir-frying time limit
            if (context.stirFryingTime >= currentStep.getRequiredTime()) {
                // Time ended, check if stir count is sufficient
                if (context.stirFryingCount >= context.requiredStirFryingCount) {
                    // Stir-frying successful, advance to next step
                    Peony.LOGGER.debug("Stir-frying completed successfully, advancing to next step");
                    advanceToNextStep(context, world, pos);
                } else {
                    // Stir-frying failed - not enough stirs within time limit
                    Peony.LOGGER.debug("Stir frying failed: required {} times, but only {} times within time limit",
                            context.requiredStirFryingCount, context.stirFryingCount);
                    context.transitionTo(CookingStates.FAILED);
                }
                return;
            }

            context.skillet.markDirty();
        }

        @Override
        public InsertResult onItemInserted(CookingContext context, InteractionContext interactionContext, ItemStack givenStack) {
            World world = interactionContext.world;
            CookingSteps.Step currentStep = context.getCurrentStep(world);

            if (currentStep != null && currentStep.getRequiredTool().test(givenStack) &&
                    !context.skillet.isToolRequirementEmpty(currentStep)) {
                context.getAnimationData().seed = System.currentTimeMillis();
                updateAnimationData(context);
                // Use tool for stir-frying - each use counts as one stir
                context.stirFryingCount++;
                context.skillet.markDirty();
                Peony.LOGGER.debug("Stir frying count: {}/{} (time: {}/{})",
                        context.stirFryingCount, context.requiredStirFryingCount,
                        context.stirFryingTime, currentStep.getRequiredTime());

                // Check if required stir count is reached
                if (context.stirFryingCount >= context.requiredStirFryingCount) {
                    // Stir-frying complete, immediately advance to next step
                    Peony.LOGGER.debug("Stir frying completed successfully with tool");
                    advanceToNextStep(context, world, interactionContext.pos);
                }

                return new InsertResult(true, -1);
            }

            return new InsertResult(false, -1);
        }

        @Override
        public void onEnter(CookingContext context, World world, BlockPos pos) {
            CookingSteps.Step currentStep = context.getCurrentStep(world);
            if (currentStep != null && currentStep.getFryingData() != null) {
                context.requiredStirFryingCount = currentStep.getFryingData().times();
                context.stirFryingCount = 0;
                context.stirFryingTime = 0;
                context.inStirFrying = true;
                Peony.LOGGER.debug("Entered STIR_FRYING state, required stirs: {}, time limit: {}",
                        context.requiredStirFryingCount, currentStep.getRequiredTime());
            } else {
                // No stir-fry data, this shouldn't happen in stir-frying state
                Peony.LOGGER.warn("Entered STIR_FRYING state but no frying data found, advancing to next step");
                advanceToNextStep(context, world, pos);
            }
        }

        @Override
        public void onExit(CookingContext context, World world, BlockPos pos) {
            context.inStirFrying = false;
            context.stirFryingCount = 0;
            context.requiredStirFryingCount = 0;
            context.stirFryingTime = 0;
            Peony.LOGGER.debug("Exited STIR_FRYING state");
        }

        @Override
        public CookingStates asState() {
            return CookingStates.STIR_FRYING;
        }

        private void advanceToNextStep(CookingContext context, World world, BlockPos pos) {
            Optional<RecipeEntry<SequentialCookingRecipe>> recipe = context.skillet.getCurrentRecipe(world);

            if (recipe.isPresent()) {
                if (context.currentStepIndex < Objects.requireNonNull(context.getCurrentCursor(world)).getLastStepIndex()) {
                    // Advance to next step
                    context.currentStepIndex++;
                    context.heatingTime = 0;
                    context.requiredHeatingTime = 0;
                    context.hasIngredient = false;

                    // Start ingredient placement countdown for next step
                    context.countdownManager.start("IngredientPlacement");
                    context.transitionTo(CookingStates.WAITING_FOR_INGREDIENT);
                    Peony.LOGGER.debug("Advanced to next step: {}", context.currentStepIndex);
                } else {
                    // Recipe complete
                    context.skillet.completeCooking(world, pos, recipe.get());
                }
            }
        }

        private void updateAnimationData(CookingContext context) {
            CustomPayload payload = new SkilletAnimationDataSyncS2CPayload(context.skillet.pos, context.getAnimationData());
            GameNetworking.sendToPlayers(PlayerLookup.world((ServerWorld) context.skillet.world), payload);
        }
    }

    /**
     * Overflow state - handles the overflow period after regular heating completion.
     * Note: Stir-frying mode does NOT use overflow state.
     */
    @ApiStatus.NonExtendable
    public static class OverflowState implements CookingState {
        public static final OverflowState INSTANCE = new OverflowState();

        @Override
        public void tick(CookingContext context, World world, BlockPos pos, BlockState state) {
            context.overflowTime++;

            // Check if max overflow time exceeded
            if (context.maxOverflowTime > 0 && context.overflowTime >= context.maxOverflowTime) {
                Peony.LOGGER.debug("Overflow timeout");
                context.transitionTo(CookingStates.FAILED);
                return;
            }

            // If no tool required, auto-advance to next step
            CookingSteps.Step currentStep = context.getCurrentStep(world);
            if (currentStep != null && context.skillet.isToolRequirementEmpty(currentStep)) {
                advanceToNextStep(context, world, pos);
            }

            context.skillet.markDirty();
        }

        @Override
        public InsertResult onItemInserted(CookingContext context, InteractionContext interactionContext, ItemStack givenStack) {
            World world = interactionContext.world;
            CookingSteps.Step currentStep = context.getCurrentStep(world);

            if (currentStep != null && currentStep.getRequiredTool().test(givenStack) &&
                    !context.skillet.isToolRequirementEmpty(currentStep)) {
                // Use tool to advance to next step
                advanceToNextStep(context, world, interactionContext.pos);
                return new InsertResult(true, -1);
            }

            return new InsertResult(false, -1);
        }

        @Override
        public void onEnter(CookingContext context, World world, BlockPos pos) {
            context.inOverflow = true;
            context.overflowTime = 0;
            Peony.LOGGER.debug("Entered OVERFLOW state (regular heating completion)");
        }

        @Override
        public void onExit(CookingContext context, World world, BlockPos pos) {
            context.inOverflow = false;
            context.overflowTime = 0;
            Peony.LOGGER.debug("Exited OVERFLOW state");
        }

        @Override
        public CookingStates asState() {
            return CookingStates.OVERFLOW;
        }

        private void advanceToNextStep(CookingContext context, World world, BlockPos pos) {
            CookingSteps.Step currentStep = context.getCurrentStep(world);
            Optional<RecipeEntry<SequentialCookingRecipe>> recipe = context.skillet.getCurrentRecipe(world);

            if (currentStep != null && recipe.isPresent()) {
                context.hasIngredient = false;

                if (context.currentStepIndex < Objects.requireNonNull(context.getCurrentCursor(world)).getLastStepIndex()) {
                    context.currentStepIndex++;
                    context.heatingTime = 0;
                    context.requiredHeatingTime = 0;

                    // Check next step to determine which heating mode to use
                    CookingSteps.Step nextStep = context.getCurrentStep(world);

                    // Start ingredient placement countdown for next step
                    context.countdownManager.start("IngredientPlacement");
                    context.transitionTo(CookingStates.WAITING_FOR_INGREDIENT);
                    Peony.LOGGER.debug("Advanced to next step: {}", context.currentStepIndex);
                } else {
                    // Recipe complete
                    context.skillet.completeCooking(world, pos, recipe.get());
                }
            }
        }
    }

    /**
     * Waiting for ingredient state - waits for player to add next ingredient.
     * After ingredient is added, automatically chooses between HEATING and STIR_FRYING modes.
     */
    @ApiStatus.NonExtendable
    public static class WaitingForIngredientState implements CookingState {
        public static final WaitingForIngredientState INSTANCE = new WaitingForIngredientState();

        @Override
        public void tick(CookingContext context, World world, BlockPos pos, BlockState state) {
            // Update countdown
            context.countdownManager.tick();

            // Check if countdown expired
            if (context.countdownManager.isOver("IngredientPlacement")) {
                Peony.LOGGER.debug("Ingredient placement timeout");
                context.transitionTo(CookingStates.FAILED);
            }
        }

        @Override
        public InsertResult onItemInserted(CookingContext context, InteractionContext interactionContext, ItemStack givenStack) {
            World world = interactionContext.world;
            CookingSteps.Step currentStep = context.getCurrentStep(world);

            if (currentStep != null && currentStep.getIngredient().test(givenStack)) {
                context.skillet.addedIngredients.add(givenStack);
                context.hasIngredient = true;

                // Reset countdown
                context.countdownManager.reset("IngredientPlacement");

                context.skillet.updateAddedItems();
                context.skillet.markDirty();

                // Store first ingredient
                if (context.currentStepIndex == 0 && context.getInputStack().isEmpty()) {
                    context.setInputStack(new ItemStack(givenStack.getItem(), 1));
                }

                // Determine which heating mode to use based on current step requirements
                CookingStates heatingMode = context.skillet.determineHeatingMode(world);
                context.transitionTo(heatingMode);

                Peony.LOGGER.debug("Ingredient placed successfully, moving to {} state", heatingMode);
                return new InsertResult(true, -1);
            }

            return new InsertResult(false, -1);
        }

        @Override
        public void onEnter(CookingContext context, World world, BlockPos pos) {
            // Start countdown
            context.countdownManager.start("IngredientPlacement");
            Peony.LOGGER.debug("Entered WAITING_FOR_INGREDIENT state, starting countdown");
        }

        @Override
        public void onExit(CookingContext context, World world, BlockPos pos) {
            // Reset countdown
            context.countdownManager.reset("IngredientPlacement");
            Peony.LOGGER.debug("Exited WAITING_FOR_INGREDIENT state");
        }

        @Override
        public CookingStates asState() {
            return CookingStates.WAITING_FOR_INGREDIENT;
        }
    }

    /**
     * Completed state - cooking process finished successfully.
     */
    @ApiStatus.NonExtendable
    public static class CompletedState implements CookingState {
        public static final CompletedState INSTANCE = new CompletedState();

        @Override
        public void tick(CookingContext context, World world, BlockPos pos, BlockState state) {
            // Completed state does nothing
        }

        @Override
        public InsertResult onItemInserted(CookingContext context, InteractionContext interactionContext, ItemStack givenStack) {
            return new InsertResult(false, -1);
        }

        @Override
        public void onEnter(CookingContext context, World world, BlockPos pos) {
            Peony.LOGGER.debug("Entered COMPLETED state");
        }

        @Override
        public void onExit(CookingContext context, World world, BlockPos pos) {
            Peony.LOGGER.debug("Exited COMPLETED state");
        }

        @Override
        public CookingStates asState() {
            return CookingStates.COMPLETED;
        }
    }

    /**
     * Failed state - cooking process failed.
     */
    @ApiStatus.NonExtendable
    public static class FailedState implements CookingState {
        public static final FailedState INSTANCE = new FailedState();

        @Override
        public void tick(CookingContext context, World world, BlockPos pos, BlockState state) {
            // Failed state does nothing
        }

        @Override
        public InsertResult onItemInserted(CookingContext context, InteractionContext interactionContext, ItemStack givenStack) {
            return new InsertResult(false, -1);
        }

        @Override
        public void onEnter(CookingContext context, World world, BlockPos pos) {
            Peony.LOGGER.debug("Entered FAILED state");
            context.skillet.failCooking(world, pos);
        }

        @Override
        public void onExit(CookingContext context, World world, BlockPos pos) {
            Peony.LOGGER.debug("Exited FAILED state");
        }

        @Override
        public CookingStates asState() {
            return CookingStates.FAILED;
        }
    }

    /**
     * Interface for all cooking states in the skillet.
     * Defines the contract for state behavior during cooking process.
     */
    public interface CookingState {
        /**
         * Called every tick to update the state.
         *
         * @param context the cooking context
         * @param world the world
         * @param pos the block position
         * @param state the block state
         */
        void tick(CookingContext context, World world, BlockPos pos, BlockState state);

        /**
         * Called when an item is inserted into the skillet.
         *
         * @param context the cooking context
         * @param interactionContext the interaction context
         * @param givenStack the item stack being inserted
         * @return the insertion result
         */
        InsertResult onItemInserted(CookingContext context, InteractionContext interactionContext, ItemStack givenStack);

        /**
         * Called when the state is entered.
         *
         * @param context the cooking context
         * @param world the world
         * @param pos the block position
         */
        void onEnter(CookingContext context, World world, BlockPos pos);

        /**
         * Called when the state is exited.
         *
         * @param context the cooking context
         * @param world the world
         * @param pos the block position
         */
        void onExit(CookingContext context, World world, BlockPos pos);

        /**
         * Gets the corresponding cooking state enum.
         *
         * @return the cooking state enum
         */
        CookingStates asState();
    }

    @ApiStatus.NonExtendable
    public static class AnimationData {
        public static final PacketCodec<RegistryByteBuf, float[]> FLOAT_ARRAY = PacketCodec.of((array, buf) -> {
            buf.writeVarInt(array.length);
            for (float value : array) {
                buf.writeFloat(value);
            }
        }, buf -> {
            int length = buf.readVarInt();
            float[] array = new float[length];
            for (int i = 0; i < length; i++) {
                array[i] = buf.readFloat();
            }
            return array;
        });
        public static final PacketCodec<RegistryByteBuf, AnimationData> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.VAR_LONG, data -> data.seed,
                PacketCodecs.VAR_LONG, data -> data.preSeed,
                PacketCodecs.VAR_LONG, data -> data.timestamp,
                FLOAT_ARRAY, data -> data.randomHeights,
                AnimationData::new
        );
        public long seed = -1L;                         // Current seed value
        public long preSeed = -1L;                      // Previous seed value
        public long timestamp = -1L;                    // Animation start timestamp
        public float[] randomHeights = new float[]{};   // Random height of each ingredient

        public AnimationData() {}

        public AnimationData(long seed, long preSeed, long timestamp, float[] randomHeights) {
            this.seed = seed;
            this.preSeed = preSeed;
            this.timestamp = timestamp;
            this.randomHeights = randomHeights;
        }
    }
}