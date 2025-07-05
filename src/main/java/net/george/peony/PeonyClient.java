package net.george.peony;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.entity.PeonyBlockEntities;
import net.george.peony.block.entity.client.CuttingBoardBlockEntityRenderer;
import net.george.peony.networking.PeonyNetworking;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class PeonyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PeonyNetworking.registerS2CPackets();
        BlockRenderLayerMap.INSTANCE.putBlock(PeonyBlocks.MILLSTONE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PeonyBlocks.BARLEY_CROP, RenderLayer.getCutout());

        BlockEntityRendererFactories.register(PeonyBlockEntities.CUTTING_BOARD, CuttingBoardBlockEntityRenderer::new);
    }
}
