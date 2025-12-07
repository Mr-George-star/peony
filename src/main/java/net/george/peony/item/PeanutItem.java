package net.george.peony.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class PeanutItem extends Item {
    public PeanutItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack heldStack = user.getStackInHand(hand);
        Random random = world.random;
        ItemStack peanutKernels;

        if (random.nextBetween(0, 1000) == 0) {
            peanutKernels = new ItemStack(PeonyItems.PEANUT_KERNEL, 3);
        } else {
            peanutKernels = new ItemStack(PeonyItems.PEANUT_KERNEL, 2);
        }
        user.giveItemStack(peanutKernels);
        heldStack.decrement(1);
        return TypedActionResult.success(heldStack);
    }
}
