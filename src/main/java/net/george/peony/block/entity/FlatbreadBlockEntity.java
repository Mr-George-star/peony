package net.george.peony.block.entity;

import net.george.peony.api.block.PizzaBlockState;
import net.george.peony.block.FlatbreadBlock;
import net.george.peony.recipe.PeonyRecipes;
import net.george.peony.recipe.PizzaCraftingRecipe;
import net.george.peony.recipe.PizzaCraftingRecipeInput;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class FlatbreadBlockEntity extends BlockEntity implements AccessibleInventory {
    public static final int MAX_INGREDIENTS = 8;
    private final DefaultedList<ItemStack> ingredients = DefaultedList.ofSize(MAX_INGREDIENTS, ItemStack.EMPTY);

    public FlatbreadBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.FLATBREAD, pos, state);
    }

    public DefaultedList<ItemStack> getIngredients() {
        return this.ingredients;
    }

    public boolean hasIngredients() {
        for (ItemStack stack : this.ingredients) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.ingredients, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.ingredients, registryLookup);
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
    public boolean insertItem(InteractionContext context, ItemStack givenStack) {
        if (this.world == null || this.world.isClient) {
            return false;
        }

        ItemStack heldStack = context.user.getStackInHand(context.hand);
        if (heldStack.isOf(Items.STICK)) {
            return this.attemptCraftPizza(context.user);
        }

        for (int i = 0; i < this.ingredients.size(); i++) {
            if (this.ingredients.get(i).isEmpty()) {
                this.ingredients.set(i, givenStack.copyWithCount(1));
                this.markDirty();
                this.world.updateListeners(this.pos, getCachedState(), getCachedState(), 3);

                this.world.playSound(null, this.pos, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean extractItem(InteractionContext context) {
        if (this.world == null || this.world.isClient) {
            return false;
        }

        for (int i = this.ingredients.size() - 1; i >= 0; i--) {
            if (!this.ingredients.get(i).isEmpty()) {
                ItemStack extracted = this.ingredients.get(i).copy();
                this.ingredients.set(i, ItemStack.EMPTY);

                if (!context.user.getInventory().insertStack(extracted)) {
                    context.user.dropItem(extracted, false);
                }

                this.markDirty();
                this.world.updateListeners(this.pos, getCachedState(), getCachedState(), 3);
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.BLOCKS, 0.8F, 1.0F);
                return true;
            }
        }
        return false;
    }

    private boolean attemptCraftPizza(PlayerEntity player) {
        if (this.world == null) {
            return false;
        }

        List<ItemStack> nonEmptyIngredients = new ArrayList<>();
        for (ItemStack stack : this.ingredients) {
            if (!stack.isEmpty()) {
                nonEmptyIngredients.add(stack);
            }
        }

        if (nonEmptyIngredients.isEmpty()) {
            player.sendMessage(Text.translatable(PeonyTranslationKeys.MESSAGE_FLATBREAD_NO_INGREDIENTS), true);
            return false;
        }

        PizzaCraftingRecipeInput input = new PizzaCraftingRecipeInput(nonEmptyIngredients);

        Optional<PizzaCraftingRecipe> recipe = this.world.getRecipeManager()
                .getFirstMatch(PeonyRecipes.PIZZA_CRAFTING_TYPE, input, this.world).map(RecipeEntry::value);

        if (recipe.isPresent()) {
            PizzaCraftingRecipe matchedRecipe = recipe.get();
            DynamicRegistryManager registryManager = this.world.getRegistryManager();
            ItemStack pizzaResult = matchedRecipe.craft(input, registryManager);

            BlockState pizzaBlockState = Optional.ofNullable(PizzaBlockState.STATES.find(pizzaResult, null))
                    .map(PizzaBlockState::get).orElse(null);

            if (pizzaBlockState != null && this.world.getBlockState(this.pos).getBlock() instanceof FlatbreadBlock) {
                this.world.setBlockState(this.pos, pizzaBlockState);
                this.ingredients.clear();
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.BLOCKS, 1.0F, 1.2F);
                player.sendMessage(Text.translatable(PeonyTranslationKeys.MESSAGE_FLATBREAD_CREATE_SUCCESS), true);
                return true;
            }
        } else {
            player.sendMessage(Text.translatable(PeonyTranslationKeys.MESSAGE_FLATBREAD_NO_RECIPE), true);
            this.world.playSound(null, this.pos, SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.BLOCKS, 1.0F, 0.8F);
        }

        return false;
    }
}
