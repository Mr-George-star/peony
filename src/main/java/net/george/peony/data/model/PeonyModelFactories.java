package net.george.peony.data.model;

import net.george.peony.block.CuttingBoardBlock;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.TexturedModel;

public class PeonyModelFactories {
    public static final TexturedModel.Factory CUTTING_BOARD = TexturedModel.makeFactory(block ->
                    new TextureMap()
                            .put(TextureKey.TEXTURE, TextureMap.getId(block))
                            .put(TextureKey.PARTICLE,
                                    block instanceof CuttingBoardBlock board ? TextureMap.getId(board.getLogMadeFrom()) : TextureMap.getId(block)),
            PeonyModels.CUTTING_BOARD);
}
