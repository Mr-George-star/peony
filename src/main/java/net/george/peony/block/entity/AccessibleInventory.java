package net.george.peony.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface AccessibleInventory {
    default InsertResult insertItemSpecified(InteractionContext context, ItemStack givenStack) {
        return createResult(insertItem(context, givenStack), -1);
    }

    default boolean insertItem(InteractionContext context, ItemStack givenStack) {
        return true;
    }

    default boolean extractItem(InteractionContext context) {
        return true;
    }

    default boolean useEmptyHanded(InteractionContext context) {
        return false;
    }

    static ItemActionResult access(AccessibleInventory entity, InteractionContext context) {
        return access(entity, context, ItemDecrementBehaviour.createDefault());
    }

    static ItemActionResult access(AccessibleInventory entity, InteractionContext context, ItemDecrementBehaviour behaviour) {
        ItemStack heldStack = context.user.getStackInHand(context.hand);

        if (!heldStack.isEmpty()) {
            return handleInsertAction(entity, context, heldStack, behaviour);
        } else {
            return handleEmptyHandAction(entity, context);
        }
    }

    static InteractionContext createContext(World world, BlockPos pos, PlayerEntity user, Hand hand) {
        return new InteractionContext(world, pos, user, hand);
    }

    static InsertResult createResult(boolean result, int decrementCount) {
        return new InsertResult(result, decrementCount);
    }

    private static ItemActionResult handleInsertAction(AccessibleInventory entity, InteractionContext context, ItemStack heldStack, ItemDecrementBehaviour behaviour) {
        InsertResult result = entity.insertItemSpecified(context, heldStack);
        if (result.isSuccess()) {
            playUsageSound(context, SoundEvents.ENTITY_ITEM_PICKUP, 1F, 2F);
            if (result.decrementCount > -1) {
                ItemDecrementBehaviour.createDecreaseSpecified(result.decrementCount).effective(context.world, context.user, context.hand);
            } else {
                behaviour.effective(context.world, context.user, context.hand);
            }
            increaseUsageStat(context.user, heldStack);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }

    private static ItemActionResult handleEmptyHandAction(AccessibleInventory entity, InteractionContext context) {
        if (context.isSneaking()) {
            return handleExtractAction(entity, context);
        } else {
            return handleEmptyHandedUseAction(entity, context);
        }
    }

    private static ItemActionResult handleExtractAction(AccessibleInventory entity, InteractionContext context) {
        if (entity.extractItem(context)) {
            playUsageSound(context, SoundEvents.ENTITY_ITEM_PICKUP, 1F, 1F);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }

    private static ItemActionResult handleEmptyHandedUseAction(AccessibleInventory entity, InteractionContext context) {
        if (entity.useEmptyHanded(context)) {
            playUsageSound(context, SoundEvents.ENTITY_ITEM_PICKUP, 1F, 1F);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }

    static void playUsageSound(InteractionContext context, SoundEvent sound, float volume, float pitch) {
        context.world.playSound(context.user, context.pos, sound, SoundCategory.BLOCKS, volume, pitch);
    }

    static void increaseUsageStat(PlayerEntity user, ItemStack stack) {
        user.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
    }

    class InteractionContext {
        public World world;
        public BlockPos pos;
        public PlayerEntity user;
        public Hand hand;

        InteractionContext(World world, BlockPos pos, PlayerEntity user, Hand hand) {
            this.world = world;
            this.pos = pos;
            this.user = user;
            this.hand = hand;
        }

        public boolean isSneaking() {
            return this.user.isSneaking();
        }
    }

    class InsertResult {
        public boolean result;
        public int decrementCount;

        InsertResult(boolean result, int decrementCount) {
            this.result = result;
            this.decrementCount = decrementCount;
        }

        public boolean isSuccess() {
            return this.result;
        }
    }
}
