package net.george.peony.block.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.george.networking.api.GameNetworking;
import net.george.peony.Peony;
import net.george.peony.block.SkilletBlock;
import net.george.peony.block.data.Output;
import net.george.peony.networking.payload.ItemStackSyncS2CPayload;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
public class BowlBlockEntity extends BlockEntity implements ImplementedInventory, DirectionProvider, BlockEntityTickerProvider, AccessibleInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private int stirTimes = 0;
    private int requiredStirTimes = 0;
    private boolean isStirring = false;
    private boolean isComplete = false;
    @Nullable
    private FlavouringPreparingRecipe currentRecipe = null;
    protected Direction cachedDirection = Direction.NORTH;
    private final RecipeManager.MatchGetter<ListedRecipeInput, FlavouringPreparingRecipe> matchGetter;

    public BowlBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.BOWL, pos, state);
        this.matchGetter = RecipeManager.createCachedMatchGetter(PeonyRecipes.FLAVOURING_PREPARING_TYPE);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public Direction getDirection() {
        return this.cachedDirection;
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

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        nbt.putInt("StirTimes", this.stirTimes);
        nbt.putInt("RequiredStirTimes", this.requiredStirTimes);
        nbt.putBoolean("IsStirring", this.isStirring);
        nbt.putBoolean("IsComplete", this.isComplete);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.inventory, registryLookup);
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

    @Override
    public void markDirty() {
        if (!Objects.requireNonNull(this.world).isClient) {
            CustomPayload payload = new ItemStackSyncS2CPayload(this.inventory.size(), this.inventory, this.pos);
            GameNetworking.sendToPlayers(PlayerLookup.world((ServerWorld) this.world), payload);
        }
        super.markDirty();
    }

    @Override

    public InsertResult insertItemSpecified(InteractionContext context, ItemStack givenStack) {
        World world = context.world;
        PlayerEntity player = context.user;

        // If mixing is complete, no more items can be added (except for container removal).
        if (this.isComplete) {
            // If the player is holding a container, try to remove it.
            if (this.currentRecipe != null) {
                return this.takeOutput(world, player, context.hand);
            }
            return AccessibleInventory.createResult(false, 0);
        }

        // Stirring
        if (givenStack.isOf(Items.STICK)) {
            // If there is no recipe, try to match.
            if (this.currentRecipe == null) {
                this.tryMatchRecipe(world);
            }

            // If there is a recipe, you can start or continue stirring.
            if (this.currentRecipe != null) {
                if (!this.isStirring) {
                    this.isStirring = true;
                    this.stirTimes = 1;
                    Peony.LOGGER.debug("Starting mixing, current: {}, needs: {}", this.stirTimes, this.requiredStirTimes);
                } else {
                    this.stirTimes++;
                    Peony.LOGGER.debug("Continuing mixing, current: {}, needs: {}", this.stirTimes, this.requiredStirTimes);
                }

                // Check if mixing is complete
                if (this.stirTimes >= this.requiredStirTimes) {
                    this.isStirring = false;
                    this.isComplete = true;
                    Peony.LOGGER.debug("Mixing complete, generating result");
                     // Generate the result item and place it in the first slot of the inventory
                    this.generateResult();
                }

                this.markDirty();
                this.sync();
                return AccessibleInventory.createResult(true, 0);
            }

            return AccessibleInventory.createResult(false, 0);
        }

        // Put in other items - raw materials
        for (int i = 0; i < this.inventory.size(); i++) {
            if (this.inventory.get(i).isEmpty()) {
                this.inventory.set(i, givenStack.copyWithCount(1));
                this.markDirty();
                this.sync();

                return AccessibleInventory.createResult(true, 1);
            }
        }

        return AccessibleInventory.createResult(false, 0);
    }

    private void tryMatchRecipe(World world) {
        if (world.isClient) {
            return;
        }

        Optional<RecipeEntry<FlavouringPreparingRecipe>> recipeOptional = this.matchGetter
                .getFirstMatch(new ListedRecipeInput(this.inventory), world);

        if (recipeOptional.isPresent()) {
            FlavouringPreparingRecipe recipe = recipeOptional.get().value();
            this.currentRecipe = recipe;
            this.requiredStirTimes = recipe.stirringTimes();
            this.stirTimes = 0;
            this.isStirring = false;
            this.isComplete = false;
            Peony.LOGGER.debug("Recipe matched successfully, required stirring times: {}", this.requiredStirTimes);
        } else {
            this.currentRecipe = null;
            this.requiredStirTimes = 0;
            this.stirTimes = 0;
            this.isStirring = false;
            this.isComplete = false;
            Peony.LOGGER.debug("No recipe found");
        }
        this.markDirty();
        this.sync();
    }

    private void generateResult() {
        if (this.currentRecipe == null) {
            return;
        }
        Output output = this.currentRecipe.output();
        ItemStack outputStack = output.getOutputStack().copy();
        // Clear all slots
        Collections.fill(this.inventory, ItemStack.EMPTY);
        // Place the result in the first slot
        this.inventory.set(0, outputStack);
        Peony.LOGGER.debug("Generated Result: {}", outputStack.getItem().getName().getString());
        this.markDirty();
        this.sync();
    }

    @Override
    public boolean extractItem(InteractionContext context) {
        World world = context.world;
        PlayerEntity player = context.user;

        // If the crafting is complete, the first slot is for the resulting item
        if (this.isComplete) {
            ItemStack resultStack = this.inventory.getFirst();
            if (!resultStack.isEmpty()) {
                return this.takeResult(world, player, context.hand, resultStack);
            }
        }

        // Otherwise, remove the raw materials in sequence (starting from the last non-empty slot)
        return this.takeIngredients(world, player, context.hand);
    }

    @Override
    public boolean useEmptyHanded(InteractionContext context) {
        // If the mixing is complete when using it without a handle, try removing the result.
        if (this.isComplete) {
            ItemStack resultStack = this.inventory.getFirst();
            if (!resultStack.isEmpty()) {
                return this.takeResult(context.world, context.user, context.hand, resultStack);
            }
        }

        // Otherwise do nothing
        return false;
    }

    private boolean takeIngredients(World world, PlayerEntity player, Hand hand) {
        // Take out the raw materials in reverse order (skip the first slot)
        for (int i = this.inventory.size() - 1; i >= 1; i--) {
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

                // If the ingredients have been removed, reset the mixing state
                this.resetStirringState();
                this.markDirty();
                this.sync();
                return true;
            }
        }
        return false;
    }

    private boolean takeResult(World world, PlayerEntity player, Hand hand, ItemStack resultStack) {
        if (this.currentRecipe == null || world.isClient) {
            return false;
        }

        Output output = this.currentRecipe.output();
        ItemConvertible requiredContainer = Output.getRequiredContainer(output);

        // Cases where containers are not required
        if (requiredContainer == null) {
            // Give the resulting item to the player
            this.giveItemToPlayer(player, hand, resultStack.copy());
            // Clear the first slot
            this.inventory.set(0, ItemStack.EMPTY);
            this.resetStirringState();
            this.markDirty();
            this.sync();
            return true;
        }

        // Cases requiring containers
        ItemStack handStack = player.getStackInHand(hand);
        if (handStack.isOf(requiredContainer.asItem())) {
            // Consume container
            if (!player.isCreative()) {
                handStack.decrement(1);
            }

            // Give the resulting item to the player
            this.giveItemToPlayer(player, hand, resultStack.copy());
            // Clear the first slot
            this.inventory.set(0, ItemStack.EMPTY);
            this.resetStirringState();
            this.markDirty();
            this.sync();
            return true;
        }

        // The player does not have the correct container
        return false;
    }

    private InsertResult takeOutput(World world, PlayerEntity player, Hand hand) {
        if (this.currentRecipe == null || world.isClient) {
            return AccessibleInventory.createResult(false, -1);
        }

        Output output = this.currentRecipe.output();
        ItemConvertible requiredContainer = Output.getRequiredContainer(output);

        ItemStack handStack = player.getStackInHand(hand);
        if (requiredContainer == null) {
            // No container needed, retrieve the result directly
            ItemStack resultStack = this.inventory.getFirst();
            if (!resultStack.isEmpty()) {
                this.giveItemToPlayer(player, hand, resultStack.copy());
                this.inventory.set(0, ItemStack.EMPTY);
                this.resetStirringState();
                this.markDirty();
                this.sync();
                return AccessibleInventory.createResult(true, -1);
            }
        } else if (handStack.isOf(requiredContainer.asItem())) {
            // Have the correct container
            ItemStack resultStack = this.inventory.getFirst();
            if (!resultStack.isEmpty()) {
                // Consume container
                if (!player.isCreative()) {
                    handStack.decrement(1);
                }

                this.giveItemToPlayer(player, hand, resultStack.copy());
                this.inventory.set(0, ItemStack.EMPTY);
                this.resetStirringState();
                this.markDirty();
                this.sync();
                return AccessibleInventory.createResult(true, 1);
            }
        }

        return AccessibleInventory.createResult(false, -1);
    }

    private void giveItemToPlayer(PlayerEntity player, Hand hand, ItemStack stack) {
        if (!player.getWorld().isClient()) {
            if (player.getStackInHand(hand).isEmpty()) {
                player.setStackInHand(hand, stack);
            } else {
                player.giveItemStack(stack);
            }
        }
    }

    private void resetStirringState() {
        this.currentRecipe = null;
        this.requiredStirTimes = 0;
        this.stirTimes = 0;
        this.isStirring = false;
        this.isComplete = false;
    }

    private boolean hasAllItemsFilled() {
        for (ItemStack stack : this.inventory) {
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (state.contains(SkilletBlock.FACING)) {
            this.cachedDirection = state.get(SkilletBlock.FACING);
        }
    }
}
