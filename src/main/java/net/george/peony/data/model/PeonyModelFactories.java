package net.george.peony.data.model;

import net.george.peony.block.CuttingBoardBlock;
import net.minecraft.block.Block;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.TexturedModel;
import net.minecraft.util.Identifier;

public class PeonyModelFactories {
    public static final TexturedModel.Factory CUTTING_BOARD = TexturedModel.makeFactory(block ->
                    new TextureMap()
                            .put(TextureKey.TEXTURE, TextureMap.getId(block))
                            .put(TextureKey.PARTICLE, getLogId(block)),
            PeonyModels.CUTTING_BOARD);

    private static Identifier getLogId(Block block) {
        if (block instanceof CuttingBoardBlock board) {
            return TextureMap.getId(board.getLogMadeFrom());
        } else {
            return TextureMap.getId(block);
        }
    }
}
