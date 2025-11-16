package net.george.peony.block;

import net.george.peony.Peony;
import net.george.peony.fluid.LardFluid;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

import java.util.Map;

public class LardCauldronBlock extends LeveledCauldronBlock {
    public static final CauldronBehavior.CauldronBehaviorMap LARD_CAULDRON_BEHAVIOURS = CauldronBehavior.createMap("lard");
    static final CauldronBehavior FILL_WITH_LARD = (state, world, pos, player, hand, stack) ->
            CauldronBehavior.fillCauldron(world, pos, player, hand, stack, PeonyBlocks.LARD_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY);

    public LardCauldronBlock(Settings settings) {
        super(Biome.Precipitation.NONE, LARD_CAULDRON_BEHAVIOURS, settings);
    }

    static void addBehaviours() {
        Map<Item, CauldronBehavior> map = LARD_CAULDRON_BEHAVIOURS.map();
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.map().put(PeonyItems.LARD_BUCKET, FILL_WITH_LARD);
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.map().put(PeonyItems.LARD_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                CauldronBehavior.fillCauldron(world, pos, player, hand, stack, PeonyBlocks.LARD_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 1), SoundEvents.ITEM_BOTTLE_EMPTY);
            }
            return ItemActionResult.success(world.isClient);
        });

        map.put(PeonyItems.LARD_BUCKET, FILL_WITH_LARD);
        map.put(Items.BUCKET, (state, world, pos, player, hand, stack) ->
                CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(PeonyItems.LARD_BUCKET), cauldron -> cauldron.get(LeveledCauldronBlock.LEVEL) == 3, SoundEvents.ITEM_BUCKET_FILL));
        map.put(PeonyItems.LARD_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                CauldronBehavior.fillCauldron(world, pos, player, hand, stack, PeonyBlocks.LARD_CAULDRON.getDefaultState().cycle(LeveledCauldronBlock.LEVEL), SoundEvents.ITEM_BOTTLE_EMPTY);
            }
            return ItemActionResult.success(world.isClient);
        });
        map.put(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, PeonyItems.LARD_BOTTLE.getDefaultStack()));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }

            return ItemActionResult.success(world.isClient);
        });
        CauldronBehavior.registerBucketBehavior(map);
    }

    @Override
    protected boolean canBeFilledByDripstone(Fluid fluid) {
        return fluid instanceof LardFluid;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && isEntityTouchingFluid(state, pos, entity) && entity.canModifyAt(world, pos)) {
            if (world.random.nextFloat() < 0.1F && entity instanceof LivingEntity living) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, Peony.getConfig().lardSlownessDurationTicks, 1));
            }
            if (entity.isOnFire()) {
                entity.setFireTicks(entity.getFireTicks() + Peony.getConfig().lardFireExtensionTicks);
            }
        }
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return Items.CAULDRON.getDefaultStack();
    }
}
