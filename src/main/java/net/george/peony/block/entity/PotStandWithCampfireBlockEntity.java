package net.george.peony.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.block.PotStandWithCampfireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class PotStandWithCampfireBlockEntity extends BlockEntity implements DirectionProvider {
    public PotStandWithCampfireBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.POT_STAND_WITH_CAMPFIRE, pos, state);
    }

    @Override
    public Direction getDirection() {
        if (this.world != null) {
            BlockState state = this.world.getBlockState(this.pos);
            if (state.getBlock() instanceof PotStandWithCampfireBlock) {
                return state.get(PotStandWithCampfireBlock.FACING);
            } else {
                return Direction.NORTH;
            }
        }
        return Direction.NORTH;
    }

    @Environment(EnvType.CLIENT)
    public static void clientTick(World world, BlockPos pos, BlockState state, PotStandWithCampfireBlockEntity campfire) {
        Random random = world.random;
        int i;
        if (random.nextFloat() < 0.11F) {
            for (i = 0; i < random.nextInt(2) + 2; ++i) {
                CampfireBlock.spawnSmokeParticle(world, pos, state.get(CampfireBlock.SIGNAL_FIRE), false);
            }
        }
    }
}
