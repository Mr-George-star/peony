package net.george.peony.data.model;

import net.george.peony.block.CuttingBoardBlock;
import net.george.peony.block.LogStickBlock;
import net.george.peony.block.PotStandBlock;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.TexturedModel;

public class PeonyModelFactories {
    public static final TexturedModel.Factory CUTTING_BOARD = TexturedModel.makeFactory(block ->
                    new TextureMap()
                            .put(TextureKey.TEXTURE, TextureMap.getId(block))
                            .put(TextureKey.PARTICLE,
                                    block instanceof CuttingBoardBlock board ? TextureMap.getId(board.getLog()) : TextureMap.getId(block)),
            PeonyModels.CUTTING_BOARD);
    public static final TexturedModel.Factory LOG_STICK = TexturedModel.makeFactory(block ->
                    new TextureMap()
                            .put(TextureKey.TEXTURE, TextureMap.getId(block))
                            .put(TextureKey.PARTICLE,
                                    block instanceof LogStickBlock logStick ? TextureMap.getId(logStick.getLog()) : TextureMap.getId(block)),
            PeonyModels.LOG_STICK);
    public static final TexturedModel.Factory POT_STAND = TexturedModel.makeFactory(block ->
                    new TextureMap()
                            .put(TextureKey.TEXTURE, TextureMap.getId(block))
                            .put(TextureKey.PARTICLE,
                                    (block instanceof PotStandBlock potStand && potStand.getLogStick() instanceof LogStickBlock logStick) ? TextureMap.getId(logStick.getLog()) : TextureMap.getId(block)),
            PeonyModels.POT_STAND);
}
