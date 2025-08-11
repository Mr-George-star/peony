package net.george.peony.item;

import net.george.peony.block.PeonyBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class PotStandItem extends BlockItem {
    public PotStandItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        if (player != null && world.getBlockState(context.getBlockPos()).getBlock() instanceof CampfireBlock) {
            Hand hand = context.getHand();
            ItemStack stack = player.getStackInHand(hand);

            if (PeonyBlocks.POT_STAND_FAMILIES.containsKey(this.getBlock())) {
                world.setBlockState(context.getBlockPos(), PeonyBlocks.POT_STAND_FAMILIES.get(this.getBlock()).getDefaultState());
                stack.decrementUnlessCreative(1, player);
                player.setStackInHand(hand, stack);
                return ActionResult.SUCCESS;
            } else {
                return super.useOnBlock(context);
            }
        } else {
            return super.useOnBlock(context);
        }
    }
}
