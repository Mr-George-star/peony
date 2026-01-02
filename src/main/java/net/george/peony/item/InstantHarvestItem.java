package net.george.peony.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.function.Function;

public abstract class InstantHarvestItem extends AliasedBlockItem {
    protected final Function<Random, ItemStack> resultFunction;

    protected InstantHarvestItem(Block crop, Settings settings, Function<Random, ItemStack> resultFunction) {
        super(crop, settings);
        this.resultFunction = resultFunction;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack heldStack = user.getStackInHand(hand);
        Random random = world.random;
        ItemStack result = this.resultFunction.apply(random);
        user.giveItemStack(result);
        heldStack.decrement(1);
        user.incrementStat(Stats.USED.getOrCreateStat(result.getItem()));
        return TypedActionResult.success(heldStack);
    }
}
