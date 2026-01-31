package net.george.peony.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.api.heat.Heat;
import net.george.peony.api.heat.HeatLevel;
import net.george.peony.api.heat.HeatProvider;
import net.george.peony.block.PotStandWithCampfireBlock;
import net.george.peony.util.math.Range;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class PotStandWithCampfireBlockEntity extends BlockEntity implements DirectionProvider, HeatProvider {
    public static final Heat HEAT = Heat.create(Range.create(500, 600), HeatLevel.HIGH);

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

    @Override
    public Heat getHeat() {
        return HEAT;
    }

    @SuppressWarnings("unused")
    @Environment(EnvType.CLIENT)
    public static void clientTick(World world, BlockPos pos, BlockState state, PotStandWithCampfireBlockEntity potStand) {
        Random random = world.random;
        int i;
        if (random.nextFloat() < 0.11F) {
            for (i = 0; i < random.nextInt(2) + 2; ++i) {
                CampfireBlock.spawnSmokeParticle(world, pos, state.get(CampfireBlock.SIGNAL_FIRE), false);
            }
        }
    }
}
