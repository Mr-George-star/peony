package net.george.peony.item;

import net.george.peony.block.PeonyBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class GarlicItem extends AliasedBlockItem {
    public GarlicItem(Settings settings) {
        super(PeonyBlocks.GARLIC_CROP, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack heldStack = user.getStackInHand(hand);
        Random random = world.random;
        ItemStack peanutKernels;

        if (random.nextBetween(0, 1000) == 0) {
            peanutKernels = new ItemStack(PeonyItems.GARLIC_CLOVE, 7);
        } else {
            peanutKernels = new ItemStack(PeonyItems.GARLIC_CLOVE, 6);
        }
        user.giveItemStack(peanutKernels);
        heldStack.decrement(1);
        return TypedActionResult.success(heldStack);
    }
}
