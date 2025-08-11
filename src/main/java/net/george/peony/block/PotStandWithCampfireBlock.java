package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.api.heat.Heat;
import net.george.peony.api.heat.HeatLevel;
import net.george.peony.api.heat.HeatProvider;
import net.george.peony.block.entity.PeonyBlockEntities;
import net.george.peony.block.entity.PotStandWithCampfireBlockEntity;
import net.george.peony.util.math.Range;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotStandWithCampfireBlock extends PotStandBlock implements HeatProvider {
    public static final MapCodec<PotStandWithCampfireBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(createSettingsCodec(), createLogStickCodec(), HeatProvider.createHeatCodec()).apply(instance, PotStandWithCampfireBlock::new));
    public static final Heat HEAT = Heat.create(Range.create(500, 600), HeatLevel.HIGH);
    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty SIGNAL_FIRE = Properties.SIGNAL_FIRE;
    protected final Heat heat;
    public final boolean emitsParticles = true;

    public PotStandWithCampfireBlock(Settings settings, Block logStick) {
        this(settings, logStick, HEAT);
    }

    public PotStandWithCampfireBlock(Settings settings, Block logStick, Heat heat) {
        super(settings, logStick);
        this.heat = heat;
        this.setDefaultState(this.getDefaultState()
                .with(LIT, true)
                .with(SIGNAL_FIRE, false));
    }

    @Override
    protected MapCodec<PotStandWithCampfireBlock> getCodec() {
        return CODEC;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            if (random.nextInt(10) == 0) {
                world.playSound((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
            }
            if (this.emitsParticles && random.nextInt(5) == 0) {
                for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                    world.addParticle(ParticleTypes.LAVA, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, random.nextFloat() / 2.0F, 5.0E-5, random.nextFloat() / 2.0F);
                }
            }
        }
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
            if (state.get(LIT)) {
                if (!world.isClient()) {
                    world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                CampfireBlock.extinguish(null, world, pos, state);
            }

            world.setBlockState(pos, state.with(WATERLOGGED, true).with(LIT, false), 3);
            world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        BlockPos blockPos = hit.getBlockPos();
        if (!world.isClient && projectile.isOnFire() && projectile.canModifyAt(world, blockPos) && !state.get(LIT) && !state.get(WATERLOGGED)) {
            world.setBlockState(blockPos, state.with(Properties.LIT, true), 11);
        }
    }

    @NotNull
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context)
                .with(LIT, context.getWorld().getFluidState(context.getBlockPos()).getFluid() == Fluids.WATER)
                .with(SIGNAL_FIRE, this.isSignalFireBaseBlock(context.getWorld().getBlockState(context.getBlockPos().down())));
    }

    private boolean isSignalFireBaseBlock(BlockState state) {
        return state.isOf(Blocks.HAY_BLOCK);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LIT, SIGNAL_FIRE);
    }

    /* HEAT SYSTEM */

    @Override
    public Heat getHeat() {
        return this.heat;
    }

    /* BLOCK ENTITY */

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PotStandWithCampfireBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return state.get(LIT) ? validateTicker(type, PeonyBlockEntities.POT_STAND_WITH_CAMPFIRE, PotStandWithCampfireBlockEntity::clientTick) : null;
        } else {
            return null;
        }
    }
}
