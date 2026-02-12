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

/**
 * Represents a block entity that supports player interaction
 * such as inserting and extracting items via right-click actions.
 *
 * <p>This interface centralizes the interaction pipeline between
 * a player and a custom inventory-like block entity, including:
 * <ul>
 *     <li>Item insertion</li>
 *     <li>Item extraction</li>
 *     <li>Empty-hand interactions</li>
 *     <li>Item consumption behavior</li>
 * </ul>
 *
 * <p>All interactions are funneled through {@link #access}
 * which handles sound playback, stat increment, and item decrement logic.
 */
public interface AccessibleInventory {
    /**
     * Attempts to insert the specified stack into this inventory.
     *
     * <p>This method allows returning a detailed {@link InsertResult}
     * which controls:
     * <ul>
     *     <li>Whether insertion succeeded</li>
     *     <li>How many items should be decremented</li>
     *     <li>Whether the held item should be consumed at all</li>
     * </ul>
     *
     * <p>By default, this delegates to {@link #insertItem}
     * and wraps the boolean result.
     *
     * @param context    interaction context
     * @param givenStack stack being inserted
     * @return insertion result descriptor
     */
    default InsertResult insertItemSpecified(InteractionContext context, ItemStack givenStack) {
        return createResult(insertItem(context, givenStack), -1);
    }

    /**
     * Performs a simple insertion attempt.
     *
     * <p>Returning {@code true} indicates success and will trigger
     * item consumption and stat increase logic.
     *
     * <p>This is intended as a simplified override point when
     * advanced decrement control is not required.
     *
     * @param context    interaction context
     * @param givenStack stack being inserted
     * @return true if insertion succeeded
     */
    default boolean insertItem(InteractionContext context, ItemStack givenStack) {
        return true;
    }

    /**
     * Attempts to extract an item from this inventory.
     *
     * <p>This is typically triggered when the player is sneaking
     * and interacting with an empty hand.
     *
     * @param context interaction context
     * @return true if extraction succeeded
     */
    default boolean extractItem(InteractionContext context) {
        return true;
    }

    /**
     * Handles interaction when the player right-clicks
     * with an empty hand and is not sneaking.
     *
     * <p>This allows defining custom behavior such as
     * collecting outputs or triggering special logic.
     *
     * @param context interaction context
     * @return true if the interaction was handled
     */
    default boolean useEmptyHanded(InteractionContext context) {
        return false;
    }

    /**
     * Entry point for handling player interaction with this inventory.
     *
     * <p>This method determines whether to insert, extract,
     * or perform empty-hand logic based on:
     * <ul>
     *     <li>Whether the player is holding an item</li>
     *     <li>Whether the player is sneaking</li>
     * </ul>
     *
     * <p>Uses {@link ItemDecrementBehaviour#createAllConsumed()} as default.
     *
     * @param entity  target inventory
     * @param context interaction context
     * @return action result
     */
    static ItemActionResult access(AccessibleInventory entity, InteractionContext context) {
        return access(entity, context, ItemDecrementBehaviour.createAllConsumed());
    }

    /**
     * Advanced interaction entry point with custom decrement behavior.
     *
     * <p>This method handles:
     * <ul>
     *     <li>Insertion attempts</li>
     *     <li>Item consumption</li>
     *     <li>Stat tracking</li>
     *     <li>Sound playback</li>
     * </ul>
     *
     * @param entity    target inventory
     * @param context   interaction context
     * @param behaviour custom decrement behavior
     * @return action result
     */
    static ItemActionResult access(AccessibleInventory entity, InteractionContext context, ItemDecrementBehaviour behaviour) {
        ItemStack heldStack = context.user.getStackInHand(context.hand);

        if (!heldStack.isEmpty()) {
            return handleInsertAction(entity, context, heldStack, behaviour);
        } else {
            return handleEmptyHandAction(entity, context);
        }
    }

    /**
     * Creates an {@link InteractionContext} object
     * encapsulating world, position, player and hand.
     *
     * @param world world instance
     * @param pos   block position
     * @param user  interacting player
     * @param hand  used hand
     * @return interaction context
     */
    static InteractionContext createContext(World world, BlockPos pos, PlayerEntity user, Hand hand) {
        return new InteractionContext(world, pos, user, hand);
    }

    /**
     * Creates an insertion result with default consumption enabled.
     *
     * @param result         whether insertion succeeded
     * @param decrementCount number of items to decrement (-1 = default behavior)
     * @return insertion result
     */
    static InsertResult createResult(boolean result, int decrementCount) {
        return createResult(result, decrementCount, true);
    }

    /**
     * Creates a fully customized insertion result.
     *
     * @param result         whether insertion succeeded
     * @param decrementCount number of items to decrement
     * @param consume        whether the held item should be consumed
     * @return insertion result
     */
    static InsertResult createResult(boolean result, int decrementCount, boolean consume) {
        return new InsertResult(result, decrementCount, consume);
    }

    private static ItemActionResult handleInsertAction(AccessibleInventory entity, InteractionContext context, ItemStack heldStack, ItemDecrementBehaviour behaviour) {
        InsertResult result = entity.insertItemSpecified(context, heldStack);
        if (result.isSuccess()) {
            playUsageSound(context, SoundEvents.ENTITY_ITEM_PICKUP, 1F, 2F);
            if (result.isConsumeItem()) {
                if (result.decrementCount > -1) {
                    ItemDecrementBehaviour.createDecreaseSpecified(result.decrementCount).effective(context.world, context.user, context.hand);
                } else {
                    behaviour.effective(context.world, context.user, context.hand);
                }
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

    /**
     * Plays a usage sound at the interaction position.
     *
     * @param context interaction context
     * @param sound   sound event
     * @param volume  sound volume
     * @param pitch   sound pitch
     */
    static void playUsageSound(InteractionContext context, SoundEvent sound, float volume, float pitch) {
        context.world.playSound(context.user, context.pos, sound, SoundCategory.BLOCKS, volume, pitch);
    }

    /**
     * Increments the player's usage statistic for the given item.
     *
     * @param user  player
     * @param stack used item stack
     */
    static void increaseUsageStat(PlayerEntity user, ItemStack stack) {
        user.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
    }

    /**
     * Encapsulates contextual information about a player interaction.
     *
     * <p>This object is immutable and passed throughout
     * the interaction pipeline.
     */
    class InteractionContext {
        public World world;
        public BlockPos pos;
        public PlayerEntity user;
        public Hand hand;

        private InteractionContext(World world, BlockPos pos, PlayerEntity user, Hand hand) {
            this.world = world;
            this.pos = pos;
            this.user = user;
            this.hand = hand;
        }

        /**
         * @return true if the player is currently sneaking
         */
        public boolean isSneaking() {
            return this.user.isSneaking();
        }
    }

    /**
     * Represents the outcome of an insertion attempt.
     *
     * <p>This controls:
     * <ul>
     *     <li>Whether the insertion succeeded</li>
     *     <li>How many items should be decremented</li>
     *     <li>Whether consumption should occur</li>
     * </ul>
     */
    class InsertResult {
        public boolean result;
        public int decrementCount;
        public boolean consume;

        private InsertResult(boolean result, int decrementCount, boolean consume) {
            this.result = result;
            this.decrementCount = decrementCount;
            this.consume = consume;
        }

        /**
         * @return true if insertion succeeded
         */
        public boolean isSuccess() {
            return this.result;
        }

        /**
         * @return true if the held item should be consumed
         */
        public boolean isConsumeItem() {
            return this.consume;
        }
    }
}
