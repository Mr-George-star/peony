package net.george.peony.block;

import net.george.peony.Peony;
import net.george.peony.fluid.PeonyFluids;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LardFluidBlock extends FluidBlock {
    public LardFluidBlock(Settings settings) {
        super(PeonyFluids.STILL_LARD, settings);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);

        for (Direction direction : Direction.values()) {
            if (world.getBlockState(pos.offset(direction)).isOf(Blocks.FIRE)) {
                world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                break;
            }
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.random.nextFloat() < 0.1F && entity instanceof LivingEntity living) {
            if (living instanceof PlayerEntity player && !player.isCreative()) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, Peony.getConfig().lardSlownessDurationTicks, 1));
            }
        }
        if (entity.isOnFire()) {
            entity.setFireTicks(entity.getFireTicks() + Peony.getConfig().lardFireExtensionTicks);
        }
    }
}
