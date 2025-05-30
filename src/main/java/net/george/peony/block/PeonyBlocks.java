package net.george.peony.block;

import net.george.peony.Peony;
import net.george.peony.PeonyItemGroups;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

import java.util.function.Function;

public class PeonyBlocks {
    public static final Block MILLSTONE = register("millstone", MillstoneBlock::new,
            AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE).nonOpaque());
    public static final Block BARLEY_CROP = register("barley_crop", BarleyCropBlock::new,
            createDefaultSettings().noCollision().ticksRandomly().breakInstantly()
                    .mapColor(MapColor.DARK_GREEN).sounds(BlockSoundGroup.CROP).pistonBehavior(PistonBehavior.DESTROY),
            false);
    public static final Block OAK_CUTTING_BOARD = register("oak_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.OAK_LOG), createDefaultCuttingBoardSettings());
    public static final Block SPRUCE_CUTTING_BOARD = register("spruce_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.SPRUCE_LOG), createDefaultCuttingBoardSettings());
    public static final Block BIRCH_CUTTING_BOARD = register("birch_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.BIRCH_LOG), createDefaultCuttingBoardSettings());
    public static final Block JUNGLE_CUTTING_BOARD = register("jungle_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.JUNGLE_LOG), createDefaultCuttingBoardSettings());
    public static final Block ACACIA_CUTTING_BOARD = register("acacia_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.ACACIA_LOG), createDefaultCuttingBoardSettings());
    public static final Block DARK_OAK_CUTTING_BOARD = register("dark_oak_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.DARK_OAK_LOG), createDefaultCuttingBoardSettings());
    public static final Block MANGROVE_CUTTING_BOARD = register("mangrove_cutting_board", settings ->
            new CuttingBoardBlock(settings, Blocks.MANGROVE_LOG), createDefaultCuttingBoardSettings());

    public static final Block DOUGH = register("dough", DoughBlock::new, createDefaultSettings().breakInstantly());
    public static final Block FLOUR = register("flour", FlourBlock::new, createDefaultSettings().breakInstantly());

    public static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings) {
        return register(name, blockFactory, settings, true);
    }

    public static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        Block instance = Registry.register(Registries.BLOCK, key(name), blockFactory.apply(settings));
        if (shouldRegisterItem) {
            PeonyItems.register(name, itemSettings -> new BlockItem(instance, itemSettings), new Item.Settings(), false);
            PeonyItemGroups.BLOCK_LIST.add(instance);
        }
        return instance;
    }

    public static AbstractBlock.Settings createDefaultSettings() {
        return AbstractBlock.Settings.create();
    }

    public static AbstractBlock.Settings createDefaultCuttingBoardSettings() {
        return createDefaultSettings().mapColor(MapColor.BROWN)
                .sounds(BlockSoundGroup.WOOD).instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .nonOpaque().burnable();
    }

    public static RegistryKey<Block> key(String name) {
        return Peony.key(RegistryKeys.BLOCK, name);
    }

    public static void register() {
        Peony.debug("Blocks");
    }
}
