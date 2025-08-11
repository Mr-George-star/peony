package net.george.peony.data.model;

import com.google.gson.JsonObject;
import net.george.peony.Peony;
import net.george.peony.block.LogStickBlock;
import net.george.peony.block.PotStandWithCampfireBlock;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class PeonyModels {
    public static final Model CUTTING_BOARD = new Model(Optional.of(Peony.id("block/cutting_board")),
            Optional.empty(), TextureKey.TEXTURE, TextureKey.PARTICLE);
    public static final Model LOG_STICK = new Model(Optional.of(Peony.id("block/log_stick")),
            Optional.empty(), TextureKey.TEXTURE, TextureKey.PARTICLE);
    public static final Model POT_STAND = new Model(Optional.of(Peony.id("block/pot_stand")),
            Optional.empty(), TextureKey.TEXTURE, TextureKey.PARTICLE);
    public static final Model POT_STAND_WITH_CAMPFIRE = new Model(Optional.of(Peony.id("block/pot_stand_with_campfire")),
            Optional.empty(), TextureKey.TEXTURE, TextureKey.PARTICLE);
    public static final Model POT_STAND_WITH_CAMPFIRE_OFF = new Model(Optional.of(Peony.id("block/pot_stand_with_campfire_off")),
            Optional.empty(), TextureKey.TEXTURE, TextureKey.PARTICLE);

    public static Identifier getUploaded(Block block, BlockStateModelGenerator generator, TexturedModel.Factory factory) {
        return factory.upload(block, generator.modelCollector);
    }

    private static Identifier getUploadedBoard(Block block, BlockStateModelGenerator generator) {
        return getUploaded(block, generator, PeonyModelFactories.CUTTING_BOARD);
    }

    private static Identifier getUploadedLogStick(Block block, BlockStateModelGenerator generator) {
        return getUploaded(block, generator, PeonyModelFactories.LOG_STICK);
    }

    private static Identifier getUploadedPotStand(Block block, BlockStateModelGenerator generator) {
        return getUploaded(block, generator, PeonyModelFactories.POT_STAND);
    }

    private static Identifier getUploadedPotStandWithCampfire(Block potStand, Block block, BlockStateModelGenerator generator) {
        TextureMap map = new TextureMap()
                .put(TextureKey.TEXTURE, TextureMap.getId(potStand))
                .put(TextureKey.PARTICLE,
                        (block instanceof PotStandWithCampfireBlock potStandWithCampfireBlock &&
                                potStandWithCampfireBlock.getLogStick() instanceof LogStickBlock logStick) ?
                                TextureMap.getId(logStick.getLog()) : TextureMap.getId(block));
        return POT_STAND_WITH_CAMPFIRE.upload(block, map, generator.modelCollector);
    }

    private static Identifier getUploadedPotStandWithCampfireOff(Block potStand, Block block, BlockStateModelGenerator generator) {
        TextureMap map = new TextureMap()
                .put(TextureKey.TEXTURE, TextureMap.getId(potStand))
                .put(TextureKey.PARTICLE,
                        (block instanceof PotStandWithCampfireBlock potStandWithCampfireBlock &&
                                potStandWithCampfireBlock.getLogStick() instanceof LogStickBlock logStick) ?
                                TextureMap.getId(logStick.getLog()) : TextureMap.getId(block));
        return POT_STAND_WITH_CAMPFIRE_OFF.upload(ModelIds.getBlockSubModelId(block, "_off"), map, generator.modelCollector);
    }

    public static void registerCuttingBoard(BlockStateModelGenerator generator, Block block) {
        Identifier board = getUploadedBoard(block, generator);
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block,
                BlockStateVariant.create().put(VariantSettings.MODEL, board))
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    public static void registerLogStick(BlockStateModelGenerator generator, Block block) {
        Identifier logStick = getUploadedLogStick(block, generator);
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block,
                BlockStateVariant.create().put(VariantSettings.MODEL, logStick))
                .coordinate(generator.createUpDefaultFacingVariantMap()));
    }

    public static void registerPotStand(BlockStateModelGenerator generator, Block potStand, Block potStandWithCampfire) {
        Identifier potStandId = getUploadedPotStand(potStand, generator);
        Identifier potStandWithCampfireId = getUploadedPotStandWithCampfire(potStand, potStandWithCampfire, generator);
        Identifier potStandWithCampfireOffId = getUploadedPotStandWithCampfireOff(potStand, potStandWithCampfire, generator);

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(potStand,
                BlockStateVariant.create().put(VariantSettings.MODEL, potStandId))
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
        generator.modelCollector.accept(ModelIds.getItemModelId(potStand.asItem()),
                () -> {
                    JsonObject object = new JsonObject();
                    object.addProperty("parent", Identifier.ofVanilla("builtin/entity").toString());
                    return object;
                });

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(potStandWithCampfire)
                .coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, potStandWithCampfireId, potStandWithCampfireOffId))
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }
}
