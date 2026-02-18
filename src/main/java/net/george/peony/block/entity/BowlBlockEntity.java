package net.george.peony.block.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.api.interaction.ComplexAccessibleInventory;
import net.george.peony.api.interaction.Consumption;
import net.george.peony.api.interaction.InteractionContext;
import net.george.peony.api.interaction.InteractionResult;
import net.george.peony.block.BowlBlock;
import net.george.peony.block.data.Output;
import net.george.peony.block.data.RecipeStorage;
import net.george.peony.networking.payload.ItemStackSyncS2CPayload;
import net.george.peony.networking.payload.SingleStackSyncS2CPayload;
import net.george.peony.recipe.FlavouringPreparingRecipe;
import net.george.peony.recipe.ListedRecipeInput;
import net.george.peony.recipe.PeonyRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
public class BowlBlockEntity extends BlockEntity implements ImplementedInventory, ComplexAccessibleInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    protected ItemStack outputStack = ItemStack.EMPTY;
    private int stirTimes = 0;
    private int requiredStirTimes = 0;
    private boolean isStirring = false;
    private boolean isComplete = false;
    protected RecipeStorage<ListedRecipeInput, FlavouringPreparingRecipe> recipeStorage;
    private final RecipeManager.MatchGetter<ListedRecipeInput, FlavouringPreparingRecipe> matchGetter;

    public BowlBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.BOWL, pos, state);
        this.matchGetter = RecipeManager.createCachedMatchGetter(PeonyRecipes.FLAVOURING_PREPARING_TYPE);
        this.recipeStorage = RecipeStorage.create(FlavouringPreparingRecipe.class);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    /**
     * Returns the facing direction of the bowl block from its block state.
     */
    public Direction getDirection() {
        if (this.world != null) {
            BlockState state = this.world.getBlockState(this.pos);
            if (state.contains(BowlBlock.FACING)) {
                return state.get(BowlBlock.FACING);
            }
        }
        return Direction.NORTH;
    }

    public int getStirTimes() {
        return this.stirTimes;
    }

    public int getRequiredStirTimes() {
        return this.requiredStirTimes;
    }

    public boolean isStirring() {
        return this.isStirring;
    }

    public boolean isComplete() {
        return this.isComplete;
    }

    public ItemStack getOutputStack() {
        return this.outputStack;
    }

    public void setOutputStack(ItemStack outputStack) {
        this.outputStack = outputStack;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, this.inventory, registries);
        if (!(this.world == null) && !this.world.isClient) {
            nbt.putBoolean("IsOutputStackEmpty", this.outputStack.isEmpty());
            if (!this.outputStack.isEmpty()) {
                NbtCompound outputNbt = new NbtCompound();
                this.outputStack.encode(registries, outputNbt);
                nbt.put("OutputStack", outputNbt);
            }
        }
        nbt.putInt("StirTimes", this.stirTimes);
        nbt.putInt("RequiredStirTimes", this.requiredStirTimes);
        nbt.putBoolean("IsStirring", this.isStirring);
        nbt.putBoolean("IsComplete", this.isComplete);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, this.inventory, registries);
        if (!(this.world == null) && !this.world.isClient) {
            if (!nbt.getBoolean("IsOutputStackEmpty")) {
                NbtCompound outputNbt = nbt.getCompound("OutputStack");
                this.outputStack = ItemStack.fromNbtOrEmpty(registries, outputNbt);
            } else {
                this.outputStack = ItemStack.EMPTY;
            }
        }
        this.stirTimes = nbt.getInt("StirTimes");
        this.requiredStirTimes = nbt.getInt("RequiredStirTimes");
        this.isStirring = nbt.getBoolean("IsStirring");
        this.isComplete = nbt.getBoolean("IsComplete");
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
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

    private void sync() {
        if (this.world != null && !this.world.isClient()) {
            this.world.updateListeners(this.pos, getCachedState(), getCachedState(), 3);
        }
    }

    private void updateOutput() {
        if (!Objects.requireNonNull(this.world).isClient) {
            CustomPayload payload = new SingleStackSyncS2CPayload(this.outputStack, this.pos);
            GameNetworking.sendToPlayers(PlayerLookup.world((ServerWorld) this.world), payload);
        }
    }

    @Override
    public void markDirty() {
        if (!Objects.requireNonNull(this.world).isClient) {
            // Send a custom packet to sync inventory contents to all tracking players
            CustomPayload payload = new ItemStackSyncS2CPayload(this.inventory.size(), this.inventory, this.pos);
            GameNetworking.sendToPlayers(PlayerLookup.world((ServerWorld) this.world), payload);
        }
        super.markDirty();
    }

    /**
     * Handles right-click interaction when the player holds an item<br>
     * - If the bowl has a completed result, the player can take it (possibly requiring a container).<br>
     * - If the player holds a stick, it triggers stirring (and recipe matching if none exists).<br>
     * - Otherwise, the player inserts one item from the held stack into an empty slot.
     */
    @Override
    public InteractionResult insert(InteractionContext context, ItemStack givenStack) {
        World world = context.world;
        PlayerEntity player = context.user;

        // When the recipe is complete, only taking the result is allowed.
        if (this.isComplete) {
            if (!this.outputStack.isEmpty()) {
                return this.takeResult(world, player, context.hand, this.outputStack);
            }
            return InteractionResult.fail();
        }

        // Stirring with a stick.
        if (givenStack.isOf(Items.STICK)) {
            // If no recipe is active, attempt to match one using the current inventory.
            if (this.recipeStorage.isEmpty()) {
                this.tryMatchRecipe(world);
            }

            // If a recipe is now present (either just matched or already active), perform stirring.
            if (!this.recipeStorage.isEmpty()) {
                if (!this.isStirring) {
                    this.isStirring = true;
                    this.stirTimes = 1;
                    Peony.LOGGER.debug("Starting mixing, current: {}, needs: {}", this.stirTimes, this.requiredStirTimes);
                } else {
                    this.stirTimes++;
                    Peony.LOGGER.debug("Continuing mixing, current: {}, needs: {}", this.stirTimes, this.requiredStirTimes);
                }

                // Check if the required stir count has been reached.
                if (this.stirTimes >= this.requiredStirTimes) {
                    this.isStirring = false;
                    this.isComplete = true;
                    Peony.LOGGER.debug("Mixing complete, generating result");
                    this.generateResult();
                }

                this.markDirty();
                this.sync();
                return InteractionResult.success(Consumption.none()); // Stick is not consumed
            }

            return InteractionResult.fail(); // No matching recipe
        }

        // Insert ingredients into the first empty slot (one item at a time).
        for (int i = 0; i < this.inventory.size(); i++) {
            if (this.inventory.get(i).isEmpty()) {
                this.inventory.set(i, givenStack.copyWithCount(1));
                this.markDirty();
                this.sync();

                return InteractionResult.success(Consumption.decrement(1));
            }
        }

        return InteractionResult.fail(); // No empty slot
    }

    /**
     * Attempts to find a recipe that matches the current inventory.
     * If found, stores the recipe and its required stir count; otherwise clears any active recipe.
     */
    private void tryMatchRecipe(World world) {
        if (world.isClient) {
            return;
        }

        Optional<RecipeEntry<FlavouringPreparingRecipe>> recipeOptional = this.matchGetter
                .getFirstMatch(new ListedRecipeInput(this.inventory), world);

        if (recipeOptional.isPresent()) {
            RecipeEntry<FlavouringPreparingRecipe> recipe = recipeOptional.get();
            this.recipeStorage.setRecipeEntry(recipe);
            this.requiredStirTimes = recipe.value().stirringTimes();
            this.stirTimes = 0;
            this.isStirring = false;
            this.isComplete = false;
            Peony.LOGGER.debug("Recipe matched successfully, required stirring times: {}", this.requiredStirTimes);
        } else {
            this.recipeStorage.clear();
            this.requiredStirTimes = 0;
            this.stirTimes = 0;
            this.isStirring = false;
            this.isComplete = false;
            Peony.LOGGER.debug("No recipe found");
        }
        this.markDirty();
        this.sync();
    }

    /**
     * Generates the result item from the current recipe, clears the ingredient inventory,
     * and stores the result in {@code outputStack}.
     */
    private void generateResult() {
        FlavouringPreparingRecipe recipe = this.recipeStorage.getRecipe();
        if (recipe != null) {
            Output output = recipe.output();
            ItemStack outputStack = output.getOutputStack().copy();
            // Clear all ingredient slots.
            Collections.fill(this.inventory, ItemStack.EMPTY);
            this.outputStack = outputStack;
            Peony.LOGGER.debug("Generated Result: {}", outputStack.getItem().getName().getString());
            this.markDirty();
            this.sync();
            this.updateOutput();
        }
    }

    /**
     * Handles shift-right-click (extract) interaction<br>
     * - If a result is ready, attempts to take it (possibly requiring a container).<br>
     * - Otherwise, removes one ingredient from the last non-empty slot and resets the recipe state.
     */
    @Override
    public InteractionResult extract(InteractionContext context) {
        World world = context.world;
        PlayerEntity player = context.user;

        if (this.isComplete) {
            if (!this.outputStack.isEmpty()) {
                return this.takeResult(world, player, context.hand, this.outputStack);
            }
        }

        return this.takeIngredients(world, player, context.hand);
    }

    /**
     * Handles empty hand right-click (emptyUse) interaction.
     * Behaves the same as {@link #extract} when a result is ready, otherwise does nothing.
     */
    @Override
    public InteractionResult emptyUse(InteractionContext context) {
        if (this.isComplete) {
            if (!this.outputStack.isEmpty()) {
                return this.takeResult(context.world, context.user, context.hand, this.outputStack);
            }
        }
        return InteractionResult.fail();
    }

    /**
     * Removes one ingredient from the inventory, starting from the last slot.
     * After removal, resets the entire mixing state (clears recipe, stir count, etc.).
     */
    private InteractionResult takeIngredients(World world, PlayerEntity player, Hand hand) {
        for (int i = this.inventory.size() - 1; i >= 0; i--) {
            ItemStack stack = this.inventory.get(i);
            if (!stack.isEmpty()) {
                if (!world.isClient()) {
                    if (player.getStackInHand(hand).isEmpty()) {
                        player.setStackInHand(hand, stack.copy());
                    } else {
                        player.giveItemStack(stack.copy());
                    }
                }
                this.inventory.set(i, ItemStack.EMPTY);
                this.resetStirringState();
                this.markDirty();
                this.sync();
                return InteractionResult.success(Consumption.none());
            }
        }
        return InteractionResult.fail();
    }

    /**
     * Gives the result item to the player.
     * If the recipe requires a container, the player must hold it in the active hand (consumes one container).
     * After successful extraction, the mixing state is reset.
     */
    private InteractionResult takeResult(World world, PlayerEntity player, Hand hand, ItemStack resultStack) {
        FlavouringPreparingRecipe recipe = this.recipeStorage.getRecipe();
        if (recipe != null) {
            ItemConvertible requiredContainer = Output.getRequiredContainer(recipe.output());

            // No container needed.
            if (requiredContainer == null) {
                this.giveItemToPlayer(player, hand, resultStack.copy());
                this.outputStack = ItemStack.EMPTY;
                this.isComplete = false;
                this.resetStirringState();
                this.markDirty();
                this.sync();
                this.updateOutput();
                return InteractionResult.success(Consumption.none());
            }

            // Container required: check player's hand.
            ItemStack handStack = player.getStackInHand(hand);
            if (handStack.isOf(requiredContainer.asItem())) {
                if (!player.isCreative()) {
                    handStack.decrement(1);
                }
                this.giveItemToPlayer(player, hand, resultStack.copy());
                this.outputStack = ItemStack.EMPTY;
                this.isComplete = false;
                this.resetStirringState();
                this.markDirty();
                this.sync();
                this.updateOutput();
                return InteractionResult.success(Consumption.none());
            }
        }

        return InteractionResult.fail(); // No recipe or missing/incorrect container
    }

    /**
     * Utility method to give an item stack to the player.
     * If the hand is empty, the stack is placed there; otherwise it goes into the inventory (or drops).
     */
    private void giveItemToPlayer(PlayerEntity player, Hand hand, ItemStack stack) {
        if (!player.getWorld().isClient()) {
            if (player.getStackInHand(hand).isEmpty()) {
                player.setStackInHand(hand, stack);
            } else {
                player.giveItemStack(stack);
            }
        }
    }

    /**
     * Resets all mixing-related state: clears the current recipe, required stirs, current stirs,
     * and flags indicating stirring or completion.
     */
    private void resetStirringState() {
        this.recipeStorage.clear();
        this.requiredStirTimes = 0;
        this.stirTimes = 0;
        this.isStirring = false;
        this.isComplete = false;
    }

    /**
     * Checks whether all inventory slots are occupied.
     * Currently unused.
     */
    private boolean hasAllItemsFilled() {
        for (ItemStack stack : this.inventory) {
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
