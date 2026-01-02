package net.george.peony.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.function.Function;

public abstract class DirectConvertItem extends Item {
    protected final Function<Random, ItemStack> resultFunction;

    protected DirectConvertItem(Settings settings, Function<Random, ItemStack> resultFunction) {
        super(settings);
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
