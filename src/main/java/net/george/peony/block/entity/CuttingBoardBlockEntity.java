package net.george.peony.block.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.george.networking.api.GameNetworking;
import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.data.CraftingSteps;
import net.george.peony.block.data.CraftingStepsFetcher;
import net.george.peony.item.KitchenKnifeItem;
import net.george.peony.item.PeonyItems;
import net.george.peony.networking.payload.ClearInventoryS2CPayload;
import net.george.peony.networking.payload.ItemStackSyncS2CPayload;
import net.george.peony.recipe.PeonyRecipes;
import net.george.peony.recipe.SequentialCraftingRecipe;
import net.george.peony.recipe.SequentialCraftingRecipeInput;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class CuttingBoardBlockEntity extends BlockEntity implements StackTransformableInventory, AccessibleInventory {
    protected final DefaultedList<ItemStack> inventory;
    protected final RecipeManager.MatchGetter<SequentialCraftingRecipeInput, SequentialCraftingRecipe> matchGetter;
    protected final Random random;
    protected final BlockState state;
    protected int currentStepIndex = 0;
    protected boolean hasBeenPlacedIngredient = false;
    protected boolean hasBeenProcessed = false;
    protected boolean hasBeenPlacedInitial = false;
    protected int usageCooldown = 0;

    public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.CUTTING_BOARD, pos, state);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        this.matchGetter = RecipeManager.createCachedMatchGetter(PeonyRecipes.SEQUENTIAL_CRAFTING_TYPE);
        this.random = Random.create();
        this.state = state;
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
    }

    @Override
    public Direction getDirection() {
        return Objects.requireNonNull(this.world).getBlockState(this.pos).get(CuttingBoardBlock.FACING);
    }

    public boolean isHasBeenPlacedIngredient() {
        return this.hasBeenPlacedIngredient;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        nbt.putInt("CurrentStepIndex", this.currentStepIndex);
        nbt.putBoolean("HasBeenPlacedIngredient", this.hasBeenPlacedIngredient);
        nbt.putBoolean("HasBeenProcessed", this.hasBeenProcessed);
        nbt.putBoolean("HasBeenPlacedInitial", this.hasBeenPlacedInitial);
        nbt.putInt("UsageCooldown", this.usageCooldown);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        this.currentStepIndex = nbt.getInt("CurrentStepIndex");
        this.hasBeenPlacedIngredient = nbt.getBoolean("HasBeenPlacedIngredient");
        this.hasBeenProcessed = nbt.getBoolean("HasBeenProcessed");
        this.hasBeenPlacedInitial = nbt.getBoolean("HasBeenPlacedInitial");
        this.usageCooldown = nbt.getInt("UsageCooldown");
        super.readNbt(nbt, registryLookup);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public void markDirty() {
        if (!Objects.requireNonNull(this.world).isClient) {
            if (!this.getInputStack().isEmpty()) {
                GameNetworking.sendToPlayers(PlayerLookup.world((ServerWorld) this.world),
                        new ItemStackSyncS2CPayload(this.inventory.size(), this.inventory, this.getPos()));
            }
        }
        super.markDirty();
    }

    @Override
    public boolean insertItem(World world, PlayerEntity user, Hand hand, ItemStack givenStack, boolean isSneaking) {
        ItemStack inputStack = this.getInputStack();
        ItemStack stackToBeInserted = new ItemStack(givenStack.getItem());
        @Nullable
        CraftingStepsFetcher fetcher = this.getCurrentFetcher(world);
        boolean isFetcherEmpty = fetcher == null;

        if (!isFetcherEmpty) {
            CraftingSteps.Procedure procedure = fetcher.getCurrentStep().getProcedure();
            if (this.isCooldownComplete()) {
                if (CraftingSteps.areEqual(procedure, CraftingSteps.Procedure.CUTTING) && givenStack.getItem() instanceof KitchenKnifeItem) {
                    givenStack.damage(1, user, EquipmentSlot.MAINHAND);
                    this.hasBeenProcessed = true;
                    this.markDirty();
                    this.resetCooldown();
                }
            }
        }

        if (inputStack.isEmpty()) {
            this.setInputStack(stackToBeInserted);
            this.updateListeners(world);
            return true;
        } else if (canItemStacksBeStacked(inputStack, stackToBeInserted)) {
            this.setInputStack(new ItemStack(inputStack.getItem(), inputStack.getCount() + stackToBeInserted.getCount()));
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.pos, GameEvent.Emitter.of(user, this.getCachedState()));
            this.updateListeners(world);
            return true;
        } else if (!isFetcherEmpty) {
            if (fetcher.getCurrentStep().getIngredient().test(stackToBeInserted)) {
                if (!this.hasBeenPlacedIngredient && !this.hasBeenProcessed) {
                    this.hasBeenPlacedIngredient = true;
                    this.updateListeners(world);
                    this.resetCooldown();
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean extractItem(World world, PlayerEntity user, Hand hand) {
        ItemStack inputStack = this.getInputStack();
        if (inputStack.isEmpty()) {
            return false;
        } else {
            user.setStackInHand(hand, inputStack);
            this.setInputStack(ItemStack.EMPTY);
            this.resetDetails();
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.pos, GameEvent.Emitter.of(user, this.getCachedState()));
            world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
            return true;
        }
    }

    @Override
    public boolean useEmptyHanded(World world, PlayerEntity user, BlockPos pos, Hand hand) {
        @Nullable
        CraftingStepsFetcher fetcher = this.getCurrentFetcher(world);
        if (!this.isCooldownComplete() || fetcher == null) {
            return true;
        }

        CraftingSteps.Procedure procedure = fetcher.getCurrentStep().getProcedure();
        if (CraftingSteps.areEqual(procedure, CraftingSteps.Procedure.KNEADING)) {
            if (this.hasBeenPlacedIngredient && !this.hasBeenProcessed) {
                spawnCuttingParticles(world, pos, fetcher.getCurrentStep().getIngredient().getMatchingStacks()[0], 5);
                this.hasBeenProcessed = true;
                this.markDirty();
                this.resetCooldown();
            }
        }

        if (!this.hasBeenPlacedIngredient && !this.hasBeenProcessed) {
            if (fetcher.getCurrentStep().getIngredient().test(PeonyItems.PLACEHOLDER.getDefaultStack())) {
                this.hasBeenPlacedIngredient = true;
                this.markDirty();
                this.resetCooldown();
            }
        }
        return true;
    }

    public static void spawnCuttingParticles(World world, BlockPos pos, ItemStack stack, int count) {
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

    protected void resetCooldown() {
        this.usageCooldown = 10;
    }

    protected boolean isCooldownComplete() {
        return this.usageCooldown <= 0;
    }

    /* CRAFTING */

    protected Optional<RecipeEntry<SequentialCraftingRecipe>> getCurrentRecipe(World world, ItemStack input) {
        return this.matchGetter.getFirstMatch(new SequentialCraftingRecipeInput(input), world);
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
    protected CraftingStepsFetcher getCurrentFetcher(World world) {
        return this.getCurrentFetcher(world, this.currentStepIndex);
    }

    @Nullable
    protected CraftingStepsFetcher getCurrentFetcher(World world, int index) {
        @Nullable
        CraftingSteps steps = this.getCurrentCraftingSteps(world);
        if (steps == null) {
            return null;
        } else {
            return steps.createFetcher(index);
        }
    }

    protected void resetDetails() {
        this.currentStepIndex = 0;
        this.hasBeenPlacedIngredient = false;
        this.hasBeenProcessed = false;
        this.hasBeenPlacedInitial = false;
    }

    @SuppressWarnings("unused")
    public void tick(World world, BlockPos pos, BlockState state) {
        if (this.getInputStack().isEmpty()) {
            GameNetworking.sendToPlayers(PlayerLookup.world((ServerWorld) world),
                    new ClearInventoryS2CPayload(pos));
        }

        Optional<RecipeEntry<SequentialCraftingRecipe>> recipe = this.getCurrentRecipe(world);
        @Nullable
        CraftingStepsFetcher fetcher = this.getCurrentFetcher(world);

        if (recipe.isPresent() && fetcher != null) {
            if (!this.hasBeenPlacedInitial) {
                this.hasBeenPlacedIngredient = true;
                this.hasBeenProcessed = false;
                this.hasBeenPlacedInitial = true;
                this.resetCooldown();
            }

            if (this.currentStepIndex >= fetcher.getLastStepIndex()) {
                this.resetDetails();
                this.setInputStack(recipe.get().value().getOutput());
                this.markDirty();
            }

            if (this.hasBeenPlacedIngredient && this.hasBeenProcessed) {
                this.currentStepIndex++;
                this.hasBeenPlacedIngredient = false;
                this.hasBeenProcessed = false;
            }
        } else {
            this.resetDetails();
        }

        this.usageCooldown--;
    }
}
