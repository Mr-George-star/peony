package net.george.peony.item;

import net.george.peony.block.PeonyBlocks;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NatureGasDetectorItem extends Item {
    public NatureGasDetectorItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            BlockPos playerPos = user.getBlockPos();
            boolean found = false;

            for (int y = playerPos.getY() - 1; y >= world.getBottomY(); y--) {
                BlockPos checkPos = new BlockPos(playerPos.getX(), y, playerPos.getZ());
                BlockState state = world.getBlockState(checkPos);

                if (state.isOf(PeonyBlocks.NATURE_GAS)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                user.sendMessage(Text.translatable(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_FOUND), true);
            } else {
                user.sendMessage(Text.translatable(PeonyTranslationKeys.NATURE_GAS_DETECTOR_ITEM_NOTHING), true);
            }
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
