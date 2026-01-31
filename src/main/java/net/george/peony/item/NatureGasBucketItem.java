package net.george.peony.item;

import net.george.peony.block.PeonyBlocks;
import net.george.peony.fluid.PeonyFluids;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class NatureGasBucketItem extends BucketItem {
    public NatureGasBucketItem(Settings settings) {
        super(PeonyFluids.STILL_NATURE_GAS, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);
        if (world.getBlockState(blockHitResult.getBlockPos()).isOf(PeonyBlocks.GAS_CYLINDER)) {
            return TypedActionResult.consume(itemStack);
        }
        return super.use(world, user, hand);
    }
}
