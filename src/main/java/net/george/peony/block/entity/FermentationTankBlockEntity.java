package net.george.peony.block.entity;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.george.peony.block.data.RecipeStorage;
import net.george.peony.recipe.FermentingRecipe;
import net.george.peony.recipe.MixedIngredientsRecipeInput;
import net.george.peony.recipe.PeonyRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
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

    protected final DefaultedList<ItemStack> inventory;
    protected final SingleFluidStorage fluidStorage;
    protected final RecipeManager.MatchGetter<MixedIngredientsRecipeInput, FermentingRecipe> matchGetter;
    protected RecipeStorage<MixedIngredientsRecipeInput, FermentingRecipe> recipeStorage;
    protected int fermentingTime;
    protected int fermentingTimeTotal;
    protected boolean hasSlab;

    public FermentationTankBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.FERMENTATION_TANK, pos, state);
        this.inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);
        this.fluidStorage = SingleFluidStorage.withFixedCapacity(FluidConstants.BUCKET, this::markDirty);
        this.matchGetter = RecipeManager.createCachedMatchGetter(PeonyRecipes.FERMENTING_TYPE);
        this.recipeStorage = RecipeStorage.create(recipe -> recipe instanceof FermentingRecipe);
        this.fermentingTime = 0;
        this.fermentingTimeTotal = 0;
        this.hasSlab = false;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    public SingleVariantStorage<FluidVariant> getFluidStorage() {
        return this.fluidStorage;
    }

    public int getFermentingTime() {
        return this.fermentingTime;
    }

    public int getFermentingTimeTotal() {
        return this.fermentingTimeTotal;
    }

    public boolean hasSlab() {
        return this.hasSlab;
    }

    public boolean isFermenting() {
        return this.fermentingTime > 0;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        this.fluidStorage.writeNbt(nbt, registryLookup);
        this.recipeStorage.writeNbt(this.world, nbt, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        this.fluidStorage.readNbt(nbt, registryLookup);
        this.recipeStorage.readNbt(this.world, nbt, registryLookup);
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
        return AccessibleInventory.createResult(false, -1);
    }

    @Override
    public boolean extractItem(InteractionContext context) {
        return false;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
    }
}
