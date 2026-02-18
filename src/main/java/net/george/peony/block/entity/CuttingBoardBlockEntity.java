package net.george.peony.block.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.api.action.Action;
import net.george.peony.api.interaction.ComplexAccessibleInventory;
import net.george.peony.api.interaction.Consumption;
import net.george.peony.api.interaction.InteractionContext;
import net.george.peony.api.interaction.InteractionResult;
import net.george.peony.api.interaction.effect.InteractionAnimation;
import net.george.peony.api.interaction.effect.InteractionEffect;
import net.george.peony.api.interaction.effect.animation.DefaultAnimations;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.data.CraftingSteps;
import net.george.peony.block.data.RecipeStepsCursor;
import net.george.peony.block.data.RecipeStorage;
import net.george.peony.item.PeonyItems;
import net.george.peony.networking.payload.ItemStackSyncS2CPayload;
import net.george.peony.recipe.PeonyRecipes;
import net.george.peony.recipe.SequentialCraftingRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class CuttingBoardBlockEntity extends BlockEntity implements ImplementedInventory, ComplexAccessibleInventory, BlockEntityTickerProvider {
    protected final DefaultedList<ItemStack> inventory;
    protected final RecipeManager.MatchGetter<SingleStackRecipeInput, SequentialCraftingRecipe> matchGetter;

    // Crafting state management
    protected int currentStepIndex = 0;
    protected boolean placedIngredient = false;     // Whether the ingredient for the current step is placed
    protected boolean processed = false;            // Whether the current step action is completed
    protected boolean placedInitial = false;        // Whether the initial ingredient is placed
    protected int usageCountdown = 0;               // Cooldown between interactions
    protected RecipeStorage<SingleStackRecipeInput, SequentialCraftingRecipe> cachedRecipe; // Cached recipe for performance

    public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.CUTTING_BOARD, pos, state);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        // Create cached recipe matcher for better performance
        this.matchGetter = RecipeManager.createCachedMatchGetter(PeonyRecipes.SEQUENTIAL_CRAFTING_TYPE);
        this.cachedRecipe = RecipeStorage.create(SequentialCraftingRecipe.class);
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

    public Direction getDirection() {
        if (this.world != null) {
            BlockState state = this.world.getBlockState(this.pos);
            if (state.contains(CuttingBoardBlock.FACING)) {
                return state.get(CuttingBoardBlock.FACING);
            }
        }
        return Direction.NORTH;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        nbt.putInt("CurrentStepIndex", this.currentStepIndex);
        nbt.putBoolean("PlacedIngredient", this.placedIngredient);
        nbt.putBoolean("Processed", this.processed);
        nbt.putBoolean("PlacedInitial", this.placedInitial);
        nbt.putInt("UsageCountdown", this.usageCountdown);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        this.currentStepIndex = nbt.getInt("CurrentStepIndex");
        this.placedIngredient = nbt.getBoolean("PlacedIngredient");
        this.processed = nbt.getBoolean("Processed");
        this.placedInitial = nbt.getBoolean("PlacedInitial");
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
    public void markDirty() {
        if (!Objects.requireNonNull(this.world).isClient) {
            CustomPayload payload = new ItemStackSyncS2CPayload(this.inventory.size(), this.inventory, this.pos);
            GameNetworking.sendToPlayers(PlayerLookup.world((ServerWorld) this.world), payload);
        }
        super.markDirty();
    }

    @Override
    public InteractionResult insert(InteractionContext context, ItemStack givenStack) {
        World world = context.world;
        PlayerEntity user = context.user;

        ItemStack inputStack = this.getInputStack();
        ItemStack stackToBeInserted = givenStack.copyWithCount(1);
        RecipeStepsCursor<CraftingSteps.Step> cursor = this.getCurrentCursor(world);

        // Place initial item if input slot is empty
        if (inputStack.isEmpty()) {
            this.setInputStack(stackToBeInserted);
            this.updateListeners(world);
            return InteractionResult.success(Consumption.decrementAndReplace(1));
        }

        // Stack items if they are the same type
        if (this.canItemStacksBeStacked(inputStack, stackToBeInserted)) {
            this.setInputStack(new ItemStack(inputStack.getItem(), inputStack.getCount() + stackToBeInserted.getCount()));
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.pos, GameEvent.Emitter.of(user, this.getCachedState()));
            this.updateListeners(world);
            return InteractionResult.success(Consumption.decrementAndReplace(1));
        }

        // Handle recipe step logic when cooldown is over
        if (cursor != null && this.isCountdownOver()) {
            CraftingSteps.Step currentStep = cursor.getCurrentStep();
            if (currentStep != null) {
                Action action = currentStep.getAction();
                Ingredient ingredient = currentStep.getIngredient();

                Peony.LOGGER.debug("Current Step - Action: {}, Ingredient: {}", action, ingredient);

                // Check for tool action
                if (action != null && action.test(context.world, givenStack)) {
                    if (this.placedIngredient && !this.processed) {
                        this.processed = true;
                        this.markDirty();
                        this.resetCountdown();
                        Peony.LOGGER.debug("Tool operation completed, marked as processed");
                        return InteractionResult.success(Consumption.damage(1))
                                .effect(InteractionEffect.of().and(new InteractionAnimation(DefaultAnimations.fromAction(action.getType()))));
                    }
                }

                // Check if placing ingredient for current step
                // Skip ingredient placement if it's a placeholder
                if (!ingredient.test(PeonyItems.PLACEHOLDER.getDefaultStack()) && ingredient.test(stackToBeInserted)) {
                    if (!this.placedIngredient && !this.processed) {
                        this.placedIngredient = true;
                        this.updateListeners(world);
                        this.resetCountdown();
                        Peony.LOGGER.debug("Raw materials are placed");
                        return InteractionResult.success(Consumption.decrementAndReplace(1));
                    }
                }
            }
        }

        return InteractionResult.fail();
    }

    @Override
    public InteractionResult extract(InteractionContext context) {
        World world = context.world;
        PlayerEntity user = context.user;

        ItemStack inputStack = this.getInputStack();
        if (inputStack.isEmpty()) {
            return InteractionResult.fail();
        } else {
            // Return item to player and reset crafting state
            this.setInputStack(ItemStack.EMPTY);
            this.resetCraftingState();
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.pos, GameEvent.Emitter.of(user, this.getCachedState()));
            world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
            return InteractionResult.success(Consumption.replace(inputStack));
        }
    }

    @Override
    public InteractionResult emptyUse(InteractionContext context) {
        World world = context.world;
        BlockPos pos = context.pos;

        RecipeStepsCursor<CraftingSteps.Step> cursor = this.getCurrentCursor(world);
        if (!this.isCountdownOver() || cursor == null) {
            return InteractionResult.success(Consumption.none());
        }

        CraftingSteps.Step currentStep = cursor.getCurrentStep();
        if (currentStep == null) {
            return InteractionResult.success(Consumption.none());
        }

        Action action = currentStep.getAction();
        Ingredient ingredient = currentStep.getIngredient();

        Peony.LOGGER.debug("Empty-handed operation - Action: {}, Placed Ingredient: {}, Processed: {}",
                action, this.placedIngredient, this.processed);

        // Check for empty-handed actions
        if (action != null && action.test(context.world, null)) {
            // For placeholder ingredients, skip the placement step and go directly to processing
            if (ingredient.test(PeonyItems.PLACEHOLDER.getDefaultStack())) {
                if (!this.placedIngredient && !this.processed) {
                    // Auto-place placeholder ingredients and immediately process
                    this.placedIngredient = true;
                    this.processed = true;
                    this.markDirty();
                    this.resetCountdown();
                    Peony.LOGGER.debug("Skipped placeholder ingredient placement and marked as processed");
                    return InteractionResult.success(Consumption.none())
                            .effect(InteractionEffect.of().and(new InteractionAnimation(DefaultAnimations.fromAction(action.getType()))));
                } else if (this.placedIngredient && !this.processed) {
                    // If already auto-placed in tick, mark as processed
                    this.processed = true;
                    this.markDirty();
                    this.resetCountdown();
                    Peony.LOGGER.debug("Processed placeholder ingredient that was auto-placed");
                    return InteractionResult.success(Consumption.none())
                            .effect(InteractionEffect.of().and(new InteractionAnimation(DefaultAnimations.fromAction(action.getType()))));
                }
            } else {
                // For non-placeholder ingredients, use normal flow
                if (!this.placedIngredient && !this.processed) {
                    // Wait for ingredient to be placed first
                    Peony.LOGGER.debug("Waiting for ingredient to be placed");
                    return InteractionResult.success(Consumption.none());
                }

                // Execute processing action for non-placeholder ingredients
                if (this.placedIngredient && !this.processed) {
                    ItemStack displayStack = ingredient.getMatchingStacks()[0];
                    spawnCraftingParticles(world, pos, displayStack, 5);
                    this.processed = true;
                    this.markDirty();
                    this.resetCountdown();
                    Peony.LOGGER.debug("Empty-handed processing completed");
                    return InteractionResult.success(Consumption.none());
                }
            }
        }

        return InteractionResult.success(Consumption.none());
    }

    /**
     * Spawns crafting particles at the given position
     */
    public static void spawnCraftingParticles(World world, BlockPos pos, ItemStack stack, int count) {
        for (int i = 0; i < count; ++i) {
            Vec3d vec3d = new Vec3d(
                    ((double) world.random.nextFloat() - 0.5D) * 0.1D,
                    Math.random() * 0.1D + 0.1D,
                    ((double) world.random.nextFloat() - 0.5D) * 0.1D);
            if (world instanceof ServerWorld) {
                ((ServerWorld) world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, stack),
                        pos.getX() + 0.5F,
                        pos.getY() + 0.3F,
                        pos.getZ() + 0.5F,
                        1, vec3d.x, vec3d.y + 0.05D, vec3d.z, 0.0D);
            } else {
                world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack),
                        pos.getX() + 0.5F,
                        pos.getY() + 0.3F,
                        pos.getZ() + 0.5F,
                        vec3d.x, vec3d.y + 0.05D, vec3d.z);
            }
        }
    }

    protected void updateListeners(World world) {
        this.markDirty();
        world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
    }

    protected void resetCountdown() {
        this.usageCountdown = 10;
    }

    protected boolean isCountdownOver() {
        return this.usageCountdown <= 0;
    }

    /* CRAFTING */

    /**
     * Gets the current recipe matching the input item
     */
    protected Optional<RecipeEntry<SequentialCraftingRecipe>> getCurrentRecipe(World world, ItemStack input) {
        // Return the cached recipe if available
        if (!this.cachedRecipe.isEmpty()) {
            return this.cachedRecipe.getOptionalRecipeEntry();
        }
        Optional<RecipeEntry<SequentialCraftingRecipe>> recipe =
                this.matchGetter.getFirstMatch(new SingleStackRecipeInput(input), world);
        recipe.ifPresent(this.cachedRecipe::setRecipeEntry);
        return recipe;
    }

    protected Optional<RecipeEntry<SequentialCraftingRecipe>> getCurrentRecipe(World world) {
        return this.getCurrentRecipe(world, this.getInputStack());
    }

    @Nullable
    protected CraftingSteps getCurrentCraftingSteps(World world) {
        Optional<RecipeEntry<SequentialCraftingRecipe>> recipe = this.getCurrentRecipe(world);
        return recipe.map(entry -> entry.value().getSteps()).orElse(null);
    }

    @Nullable
    protected RecipeStepsCursor<CraftingSteps.Step> getCurrentCursor(World world) {
        return this.getCurrentCursor(world, this.currentStepIndex);
    }

    @Nullable
    protected RecipeStepsCursor<CraftingSteps.Step> getCurrentCursor(World world, int index) {
        @Nullable
        CraftingSteps steps = this.getCurrentCraftingSteps(world);
        if (steps == null) {
            return null;
        } else {
            // Ensure index is within valid range
            int safeIndex = MathHelper.clamp(index, 0, steps.getSteps().size() - 1);
            return steps.createCursor(safeIndex);
        }
    }

    /**
     * Resets all crafting state to initial values
     */
    protected void resetCraftingState() {
        this.currentStepIndex = 0;
        this.placedIngredient = false;
        this.processed = false;
        this.placedInitial = false;
        this.cachedRecipe.clear();
        this.usageCountdown = 0;
        Peony.LOGGER.debug("Reset Crafting State");
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        // Decrement cooldown
        if (this.usageCountdown > 0) {
            this.usageCountdown--;
        }

        Optional<RecipeEntry<SequentialCraftingRecipe>> recipe = this.getCurrentRecipe(world);
        RecipeStepsCursor<CraftingSteps.Step> cursor = this.getCurrentCursor(world);

        if (recipe.isPresent() && cursor != null) {
            // Initialize the first step
            if (!this.placedInitial) {
                this.placedIngredient = true; // First step ingredient is the input item
                this.processed = false;
                this.placedInitial = true;
                this.resetCountdown();
                this.markDirty();
                Peony.LOGGER.debug("Initialized first step");
            }

            // Auto-skip placement for placeholder ingredients in tick
            CraftingSteps.Step currentStep = cursor.getCurrentStep();
            if (currentStep != null && !this.placedIngredient && !this.processed) {
                Ingredient ingredient = currentStep.getIngredient();
                if (ingredient.test(PeonyItems.PLACEHOLDER.getDefaultStack())) {
                    this.placedIngredient = true;
                    Peony.LOGGER.debug("Auto-skipped placeholder ingredient placement in tick");
                    this.markDirty();
                }
            }

            // Check if all steps are completed
            if (this.currentStepIndex > cursor.getLastStepIndex()) {
                Peony.LOGGER.debug("All steps completed, producing output");
                this.setInputStack(recipe.get().value().getOutput());
                this.resetCraftingState();
                this.markDirty();
                return;
            }

            // Advance to the next step when the current step is completed
            if (this.placedIngredient && this.processed) {
                Peony.LOGGER.debug("Step {} completed, advancing to next step", this.currentStepIndex);
                this.currentStepIndex++;
                this.placedIngredient = false;
                this.processed = false;

                // Check if we've reached the end after advancing
                if (this.currentStepIndex > cursor.getLastStepIndex()) {
                    Peony.LOGGER.debug("Reached final step after advancement, producing output");
                    this.setInputStack(recipe.get().value().getOutput());
                    this.resetCraftingState();
                }

                this.markDirty();
            }
        } else {
            // Reset state if no valid recipe
            if (!this.getInputStack().isEmpty()) {
                Peony.LOGGER.debug("No valid recipe found, resetting crafting state");
                this.resetCraftingState();
            }
        }
    }
}
