package net.george.peony.block.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.api.action.Action;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.data.CraftingSteps;
import net.george.peony.block.data.RecipeStepsCursor;
import net.george.peony.item.PeonyItems;
import net.george.peony.networking.payload.ItemStackSyncS2CPayload;
import net.george.peony.recipe.PeonyRecipes;
import net.george.peony.recipe.SequentialCraftingRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.EquipmentSlot;
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

public class CuttingBoardBlockEntity extends BlockEntity implements ImplementedInventory, DirectionProvider, AccessibleInventory, BlockEntityTickerProvider {
    protected final DefaultedList<ItemStack> inventory;
    protected final RecipeManager.MatchGetter<SingleStackRecipeInput, SequentialCraftingRecipe> matchGetter;

    // Crafting state management
    protected int currentStepIndex = 0;
    protected boolean placedIngredient = false;     // Whether the ingredient for current step is placed
    protected boolean processed = false;            // Whether the current step action is completed
    protected boolean placedInitial = false;        // Whether the initial ingredient is placed
    protected int usageCountdown = 0;               // Cooldown between interactions
    @Nullable
    protected RecipeEntry<SequentialCraftingRecipe> cachedRecipe = null; // Cached recipe for performance
    protected Direction cachedDirection = Direction.NORTH;

    public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.CUTTING_BOARD, pos, state);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        // Create cached recipe matcher for better performance
        this.matchGetter = RecipeManager.createCachedMatchGetter(PeonyRecipes.SEQUENTIAL_CRAFTING_TYPE);
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

    @Override
    public Direction getDirection() {
        return this.cachedDirection;
    }

    public boolean hasPlacedIngredient() {
        return this.placedIngredient;
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
        nbt.putString("CachedDirection", this.getDirection().getName());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        this.currentStepIndex = nbt.getInt("CurrentStepIndex");
        this.placedIngredient = nbt.getBoolean("PlacedIngredient");
        this.processed = nbt.getBoolean("Processed");
        this.placedInitial = nbt.getBoolean("PlacedInitial");
        this.usageCountdown = nbt.getInt("UsageCountdown");
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
    public boolean insertItem(InteractionContext context, ItemStack givenStack) {
        World world = context.world;
        PlayerEntity user = context.user;

        ItemStack inputStack = this.getInputStack();
        ItemStack stackToBeInserted = givenStack.copyWithCount(1);
        RecipeStepsCursor<CraftingSteps.Step> cursor = this.getCurrentCursor(world);

        // Place initial item if input slot is empty
        if (inputStack.isEmpty()) {
            this.setInputStack(stackToBeInserted);
            this.updateListeners(world);
            return true;
        }

        // Stack items if they are the same type
        if (canItemStacksBeStacked(inputStack, stackToBeInserted)) {
            this.setInputStack(new ItemStack(inputStack.getItem(), inputStack.getCount() + stackToBeInserted.getCount()));
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.pos, GameEvent.Emitter.of(user, this.getCachedState()));
            this.updateListeners(world);
            return true;
        }

        // Handle recipe step logic when cooldown is over
        if (cursor != null && this.isCountdownOver()) {
            CraftingSteps.Step currentStep = cursor.getCurrentStep();
            if (currentStep != null) {
                Action action = currentStep.getAction();
                Ingredient ingredient = currentStep.getIngredient();

                Peony.LOGGER.info("Current Step - Action: {}, Ingredient: {}", action, ingredient);

                // Check for tool action
                if (action != null && action.test(context.world, givenStack)) {
                    if (this.placedIngredient && !this.processed) {
                        givenStack.damage(1, user, EquipmentSlot.MAINHAND);
                        this.processed = true;
                        this.markDirty();
                        this.resetCountdown();
                        Peony.LOGGER.info("Tool operation completed, marked as processed");
                        return true;
                    }
                }

                // Check if placing ingredient for current step
                // Skip ingredient placement if it's a placeholder
                if (!ingredient.test(PeonyItems.PLACEHOLDER.getDefaultStack()) && ingredient.test(stackToBeInserted)) {
                    if (!this.placedIngredient && !this.processed) {
                        this.placedIngredient = true;
                        this.updateListeners(world);
                        this.resetCountdown();
                        Peony.LOGGER.info("Raw materials are placed");
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean extractItem(InteractionContext context) {
        World world = context.world;
        PlayerEntity user = context.user;

        ItemStack inputStack = this.getInputStack();
        if (inputStack.isEmpty()) {
            return false;
        } else {
            // Return item to player and reset crafting state
            user.setStackInHand(context.hand, inputStack);
            this.setInputStack(ItemStack.EMPTY);
            this.resetCraftingState();
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.pos, GameEvent.Emitter.of(user, this.getCachedState()));
            world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
            return true;
        }
    }

    @Override
    public boolean useEmptyHanded(InteractionContext context) {
        World world = context.world;
        BlockPos pos = context.pos;

        RecipeStepsCursor<CraftingSteps.Step> cursor = this.getCurrentCursor(world);
        if (!this.isCountdownOver() || cursor == null) {
            return true;
        }

        CraftingSteps.Step currentStep = cursor.getCurrentStep();
        if (currentStep == null) {
            return true;
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
                    return true;
                } else if (this.placedIngredient && !this.processed) {
                    // If already auto-placed in tick, just mark as processed
                    this.processed = true;
                    this.markDirty();
                    this.resetCountdown();
                    Peony.LOGGER.debug("Processed placeholder ingredient that was auto-placed");
                    return true;
                }
            } else {
                // For non-placeholder ingredients, use normal flow
                if (!this.placedIngredient && !this.processed) {
                    // Wait for ingredient to be placed first
                    Peony.LOGGER.debug("Waiting for ingredient to be placed");
                    return true;
                }

                // Execute processing action for non-placeholder ingredients
                if (this.placedIngredient && !this.processed) {
                    ItemStack displayStack = ingredient.getMatchingStacks()[0];
                    spawnCraftingParticles(world, pos, displayStack, 5);
                    this.processed = true;
                    this.markDirty();
                    this.resetCountdown();
                    Peony.LOGGER.debug("Empty-handed processing completed");
                    return true;
                }
            }
        }

        return true;
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
        // Return cached recipe if available
        if (this.cachedRecipe != null) {
            return Optional.of(this.cachedRecipe);
        }
        Optional<RecipeEntry<SequentialCraftingRecipe>> recipe =
                this.matchGetter.getFirstMatch(new SingleStackRecipeInput(input), world);
        recipe.ifPresent(entry -> this.cachedRecipe = entry);
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
        this.cachedRecipe = null;
        this.usageCountdown = 0;
        Peony.LOGGER.info("Reset Crafting State");
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        // Update direction from block state
        if (state.contains(CuttingBoardBlock.FACING)) {
            this.cachedDirection = state.get(CuttingBoardBlock.FACING);
        }

        // Decrement cooldown
        if (this.usageCountdown > 0) {
            this.usageCountdown--;
        }

        Optional<RecipeEntry<SequentialCraftingRecipe>> recipe = this.getCurrentRecipe(world);
        RecipeStepsCursor<CraftingSteps.Step> cursor = this.getCurrentCursor(world);

        if (recipe.isPresent() && cursor != null) {
            // Initialize first step
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

            // Advance to next step when current step is completed
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
