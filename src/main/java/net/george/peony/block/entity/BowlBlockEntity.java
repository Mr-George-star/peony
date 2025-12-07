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

        if (this.isComplete) {
            if (this.currentRecipe != null) {
                return this.takeOutput(world, player, context.hand);
            }
            return AccessibleInventory.createResult(false, 0);
        }

        if (givenStack.isOf(Items.STICK)) {
            if (this.currentRecipe == null) {
                this.tryMatchRecipe(world);
            }

            if (this.currentRecipe != null) {
                if (!this.isStirring) {
                    this.isStirring = true;
                    this.stirTimes = 1;
                    Peony.LOGGER.info("Appending, {}, {}", this.stirTimes, this.requiredStirTimes);
                } else {
                    Peony.LOGGER.info("Appending, {}, {}", this.stirTimes, this.requiredStirTimes);
                    this.stirTimes++;
                }

                if (this.stirTimes >= this.requiredStirTimes) {
                    this.isStirring = false;
                    this.isComplete = true;
                    Peony.LOGGER.info("Finished, {}, {}", this.stirTimes, this.requiredStirTimes);
                }

                this.markDirty();
                this.sync();
                return AccessibleInventory.createResult(true, 0);
            }

            return AccessibleInventory.createResult(false, 0);
        }

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

        recipeOptional.map(RecipeEntry::value).ifPresentOrElse(recipe -> {
            this.currentRecipe = recipe;
            this.requiredStirTimes = recipe.stirringTimes();
        }, () -> {
            this.currentRecipe = null;
            this.requiredStirTimes = 0;
        });
        this.stirTimes = 0;
        this.isStirring = false;
        this.isComplete = false;
        Peony.LOGGER.info("Values {}, {}", this.requiredStirTimes, this.stirTimes);
        this.markDirty();
        this.sync();
    }

    @Override
    public boolean extractItem(InteractionContext context) {
        World world = context.world;
        PlayerEntity player = context.user;
        return this.takeIngredients(world, player, context.hand);
    }

    @Override
    public boolean useEmptyHanded(InteractionContext context) {
        if (this.isComplete && this.currentRecipe != null) {
            return this.takeOutput(context.world, context.user, context.hand).isSuccess();
        }
        return false;
    }

    private boolean takeIngredients(World world, PlayerEntity player, Hand hand) {
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
                return true;
            }
        }
        return false;
    }

    private InsertResult takeOutput(World world, PlayerEntity player, Hand hand) {
        if (this.currentRecipe == null || world.isClient) {
            return AccessibleInventory.createResult(false, -1);
        }

        Output output = this.currentRecipe.output();
        ItemStack outputStack = output.getOutputStack().copy();
        ItemConvertible requiredContainer = Output.getRequiredContainer(output);

        ItemStack handStack = player.getStackInHand(hand);
        if (requiredContainer == null) {
            this.giveItemToPlayer(player, hand, outputStack);
            this.clearAndReset();
            return AccessibleInventory.createResult(true, -1);
        }

        Peony.LOGGER.info(requiredContainer.toString());
        Peony.LOGGER.info(handStack.toString());
        if (handStack.isOf(requiredContainer.asItem())) {
            this.giveItemToPlayer(player, hand, outputStack);
            this.clearAndReset();
            return AccessibleInventory.createResult(true, 1);
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

    private void clearAndReset() {
        Collections.fill(this.inventory, ItemStack.EMPTY);
        this.resetStirringState();
        this.markDirty();
        this.sync();
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
