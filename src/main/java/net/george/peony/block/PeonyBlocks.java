package net.george.peony.block;

import net.fabricmc.fabric.api.transfer.v1.fluid.CauldronFluidContent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.george.peony.Peony;
import net.george.peony.PeonyItemGroups;
import net.george.peony.block.entity.ItemExchangeBehaviour;
import net.george.peony.event.PotStandFamilyRegistryCallback;
import net.george.peony.fluid.PeonyFluids;
import net.george.peony.item.PeonyItems;
import net.george.peony.item.PotStandItem;
import net.george.peony.item.SolidModelProvider;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.item.Items.BUCKET;
import static net.minecraft.item.Items.GLASS_BOTTLE;

public class PeonyBlocks {
    public static final Map<Block, Block> POT_STAND_FAMILIES;

    public static final Block MILLSTONE = register("millstone", MillstoneBlock::new,
            AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE).nonOpaque());
    // use log top map color
    public static final Block OAK_CUTTING_BOARD = register("oak_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.OAK_LOG), createDefaultWoodSettings(MapColor.OAK_TAN));
    public static final Block SPRUCE_CUTTING_BOARD = register("spruce_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.SPRUCE_LOG), createDefaultWoodSettings(MapColor.SPRUCE_BROWN));
    public static final Block BIRCH_CUTTING_BOARD = register("birch_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.BIRCH_LOG), createDefaultWoodSettings(MapColor.PALE_YELLOW));
    public static final Block JUNGLE_CUTTING_BOARD = register("jungle_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.JUNGLE_LOG), createDefaultWoodSettings(MapColor.DIRT_BROWN));
    public static final Block ACACIA_CUTTING_BOARD = register("acacia_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.ACACIA_LOG), createDefaultWoodSettings(MapColor.ORANGE));
    public static final Block CHERRY_CUTTING_BOARD = register("cherry_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.CHERRY_LOG), createDefaultWoodSettings(MapColor.TERRACOTTA_WHITE, BlockSoundGroup.CHERRY_WOOD));
    public static final Block DARK_OAK_CUTTING_BOARD = register("dark_oak_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.DARK_OAK_LOG), createDefaultWoodSettings(MapColor.BROWN));
    public static final Block MANGROVE_CUTTING_BOARD = register("mangrove_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.MANGROVE_LOG), createDefaultWoodSettings(MapColor.RED));
    public static final Block SKILLET = register("skillet", SkilletBlock::new,
            createDefaultSettings().nonOpaque().requiresTool().mapColor(MapColor.IRON_GRAY),
            PeonyItems.createDefaultSettings().maxCount(1));

    // use log top map color
    public static final Block OAK_LOG_STICK = register("oak_log_stick", settings ->
            new LogStickBlock(settings, Blocks.OAK_LOG), createDefaultWoodSettings(MapColor.OAK_TAN));

    // use log side map color
    public static final Block OAK_POT_STAND = register("oak_pot_stand", settings ->
            new PotStandBlock(settings, OAK_LOG_STICK), createDefaultWoodSettings(MapColor.SPRUCE_BROWN).nonOpaque(),
            PotStandItem::new, PeonyItems.createDefaultSettings().maxCount(16));

    // use log side map color
    public static final Block OAK_POT_STAND_WITH_CAMPFIRE = register("oak_pot_stand_with_campfire", settings ->
            new PotStandWithCampfireBlock(settings, OAK_LOG_STICK),
            createDefaultWoodSettings(MapColor.SPRUCE_BROWN).luminance(Blocks.createLightLevelFromLitBlockState(15)).nonOpaque(),
            false);

    public static final Block DOUGH = register("dough",
                DoughBlock::new, createDefaultSettings().breakInstantly(), DoughItem::new);
    public static final Block FLOUR = register("flour",
                FlourBlock::new, createDefaultSettings().breakInstantly(), FlourItem::new);

    /* CROPS */

    public static final Block BARLEY_CROP = register("barley_crop", BarleyCropBlock::new,
            createDefaultSettings().nonOpaque().noCollision().ticksRandomly().breakInstantly()
                    .mapColor(MapColor.DARK_GREEN).sounds(BlockSoundGroup.CROP).pistonBehavior(PistonBehavior.DESTROY),
            false);
    public static final Block PEANUT_CROP = register("peanut_crop", PeanutCropBlock::new,
            createDefaultSettings().nonOpaque().noCollision().ticksRandomly().breakInstantly()
                    .mapColor(MapColor.DARK_GREEN).sounds(BlockSoundGroup.CROP).pistonBehavior(PistonBehavior.DESTROY),
            false);

    /* FLUIDS */
    public static final Block NATURE_GAS = register("nature_gas", NatureGasBlock::new, createDefaultSettings()
            .mapColor(MapColor.LIGHT_GRAY).replaceable().noCollision().strength(100F)
            .pistonBehavior(PistonBehavior.DESTROY).dropsNothing().liquid().sounds(BlockSoundGroup.INTENTIONALLY_EMPTY), false);
    public static final Block LARD_FLUID = register("lard_fluid", LardFluidBlock::new, createDefaultSettings()
            .mapColor(MapColor.TERRACOTTA_YELLOW).replaceable().noCollision().strength(100F)
            .pistonBehavior(PistonBehavior.DESTROY).dropsNothing().liquid().sounds(BlockSoundGroup.INTENTIONALLY_EMPTY), false);

    public static final Block LARD_CAULDRON = register("lard_cauldron", LardCauldronBlock::new,
            AbstractBlock.Settings.copy(Blocks.CAULDRON), false);


    public static Block register(String name,
                                 Function<AbstractBlock.Settings, Block> blockFactory,
                                 AbstractBlock.Settings settings) {
        return register(name, blockFactory, settings, true);
    }

    public static Block register(String name,
                                 Function<AbstractBlock.Settings, Block> blockFactory,
                                 AbstractBlock.Settings settings,
                                 boolean shouldRegisterItem) {
        return register(name, blockFactory, settings, shouldRegisterItem ? BlockItem::new : null, shouldRegisterItem ? new Item.Settings() : null);
    }

    public static Block register(String name,
                                 Function<AbstractBlock.Settings, Block> blockFactory,
                                 AbstractBlock.Settings settings,
                                 Item.Settings itemSettings) {
        return register(name, blockFactory, settings, BlockItem::new, itemSettings);
    }

    public static Block register(String name,
                                 Function<AbstractBlock.Settings, Block> blockFactory,
                                 AbstractBlock.Settings settings,
                                 BiFunction<Block, Item.Settings, Item> blockItemFactory) {
        return register(name, blockFactory, settings, blockItemFactory, null);
    }

    public static Block register(String name,
                          Function<AbstractBlock.Settings, Block> blockFactory,
                          AbstractBlock.Settings settings,
                          @Nullable BiFunction<Block, Item.Settings, Item> blockItemFactory,
                          @Nullable Item.Settings itemSettings) {
        Block instance = Registry.register(Registries.BLOCK, Peony.id(name), blockFactory.apply(settings));
        Optional.ofNullable(blockItemFactory).ifPresent(factory -> {
            PeonyItems.register(name, factory, Optional.ofNullable(itemSettings).orElse(new Item.Settings()), instance);
            PeonyItemGroups.BLOCK_LIST.add(instance);
        });
        return instance;
    }

    public static AbstractBlock.Settings createDefaultSettings() {
        return AbstractBlock.Settings.create();
    }

    public static AbstractBlock.Settings createDefaultWoodSettings(MapColor color) {
        return createDefaultWoodSettings(color, BlockSoundGroup.WOOD);
    }

    public static AbstractBlock.Settings createDefaultWoodSettings(MapColor color, BlockSoundGroup soundGroup) {
        return createDefaultSettings()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASS)
                .sounds(soundGroup)
                .strength(2.0F)
                .burnable();
    }

    public static void register() {
        ItemExchangeBehaviour.registerBehaviours();
        CauldronFluidContent.registerCauldron(LARD_CAULDRON, PeonyFluids.STILL_LARD, FluidConstants.BOTTLE, LeveledCauldronBlock.LEVEL);
        FluidStorage.combinedItemApiProvider(PeonyItems.LARD_BUCKET).register(context ->
                new FullItemFluidStorage(context, bucket -> ItemVariant.of(BUCKET), FluidVariant.of(PeonyFluids.STILL_LARD), FluidConstants.BUCKET)
        );
        FluidStorage.combinedItemApiProvider(BUCKET).register(context ->
                new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(PeonyItems.LARD_BUCKET), PeonyFluids.STILL_LARD, FluidConstants.BUCKET)
        );
        FluidStorage.combinedItemApiProvider(PeonyItems.LARD_BOTTLE).register(context ->
                new FullItemFluidStorage(context, bottle -> ItemVariant.of(GLASS_BOTTLE), FluidVariant.of(PeonyFluids.STILL_LARD), FluidConstants.BOTTLE)
        );
        FluidStorage.combinedItemApiProvider(GLASS_BOTTLE).register(context ->
                new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(PeonyItems.LARD_BOTTLE), PeonyFluids.STILL_LARD, FluidConstants.BOTTLE)
        );
        LardCauldronBlock.addBehaviours();
        Peony.debug("Blocks");
    }

    static class DoughItem extends BlockItem implements SolidModelProvider {
        public DoughItem(Block dough, Settings settings) {
            super(dough, settings);
        }

        @Override
        public BlockState asRenderingState() {
            return DOUGH.getDefaultState();
        }
    }

    static class FlourItem extends BlockItem implements SolidModelProvider {
        public FlourItem(Block flour, Settings settings) {
            super(flour, settings);
        }

        @Override
        public BlockState asRenderingState() {
            return FLOUR.getDefaultState();
        }
    }

    static {
        POT_STAND_FAMILIES = new HashMap<>();
        Map<Block, Block> map = new HashMap<>();
        map.put(OAK_POT_STAND, OAK_POT_STAND_WITH_CAMPFIRE);
        PotStandFamilyRegistryCallback.EVENT.invoker().interact(map);
        POT_STAND_FAMILIES.putAll(map);
    }
}
