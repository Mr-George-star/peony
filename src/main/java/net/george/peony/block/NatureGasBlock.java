package net.george.peony.block;

import net.george.peony.fluid.PeonyFluids;
import net.george.peony.util.PeonyTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NatureGasBlock extends FluidBlock {
    public NatureGasBlock(Settings settings) {
        super(PeonyFluids.STILL_NATURE_GAS, settings);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        this.checkBurnableBlocks(world, pos);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        this.checkBurnableBlocks(world, pos);
    }

    protected void checkBurnableBlocks(World world, BlockPos center) {
        if (world.isClient) {
            return;
        }
        BlockPos.iterate(center.add(-1, -1, -1), center.add(1, 1, 1)).forEach(pos -> {
            if (world.getBlockState(pos).isIn(PeonyTags.Blocks.BURNABLE_BLOCKS)) {
                world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(),
                        5F, false, World.ExplosionSourceType.BLOCK);
                world.removeBlock(center, true);
            }
        });
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof PlayerEntity player) {
            if (!player.getAbilities().creativeMode) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 4));
            }
        }
    }
}
