package net.george.peony;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.entity.PeonyBlockEntities;
import net.george.peony.block.entity.client.*;
import net.george.peony.client.PeonyItemRenderers;
import net.george.peony.client.PeonyModelLoaderPlugin;
import net.george.peony.fluid.PeonyFluids;
import net.george.peony.networking.PeonyNetworking;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class PeonyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(PeonyModelLoaderPlugin.INSTANCE);
        PeonyNetworking.registerS2CPackets();

        BlockRenderLayerMap.INSTANCE.putBlock(PeonyBlocks.MILLSTONE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PeonyBlocks.SKILLET, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), PeonyBlocks.BARLEY_CROP, PeonyBlocks.PEANUT_CROP, PeonyBlocks.TOMATO_VINES);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                PeonyBlocks.OAK_POT_STAND, PeonyBlocks.SPRUCE_POT_STAND, PeonyBlocks.BIRCH_POT_STAND,
                PeonyBlocks.JUNGLE_POT_STAND, PeonyBlocks.ACACIA_POT_STAND, PeonyBlocks.CHERRY_POT_STAND,
                PeonyBlocks.DARK_OAK_POT_STAND, PeonyBlocks.MANGROVE_POT_STAND);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                PeonyBlocks.OAK_POT_STAND_WITH_CAMPFIRE, PeonyBlocks.SPRUCE_POT_STAND_WITH_CAMPFIRE, PeonyBlocks.BIRCH_POT_STAND_WITH_CAMPFIRE,
                PeonyBlocks.JUNGLE_POT_STAND_WITH_CAMPFIRE, PeonyBlocks.ACACIA_POT_STAND_WITH_CAMPFIRE, PeonyBlocks.CHERRY_POT_STAND_WITH_CAMPFIRE,
                PeonyBlocks.DARK_OAK_POT_STAND_WITH_CAMPFIRE, PeonyBlocks.MANGROVE_POT_STAND_WITH_CAMPFIRE);

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), PeonyFluids.STILL_NATURE_GAS, PeonyFluids.FLOWING_NATURE_GAS);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), PeonyFluids.STILL_LARD, PeonyFluids.FLOWING_LARD);

        BlockEntityRendererFactories.register(PeonyBlockEntities.MILLSTONE, MillstoneBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(PeonyBlockEntities.CUTTING_BOARD, CuttingBoardBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(PeonyBlockEntities.POT_STAND, PotStandBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(PeonyBlockEntities.POT_STAND_WITH_CAMPFIRE, PotStandWithCampfireBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(PeonyBlockEntities.SKILLET, SkilletBlockEntityRenderer::new);

        FluidRenderHandlerRegistry.INSTANCE.register(PeonyFluids.STILL_NATURE_GAS, PeonyFluids.FLOWING_NATURE_GAS,
                SimpleFluidRenderHandler.coloredWater(10066329));
        FluidRenderHandlerRegistry.INSTANCE.register(PeonyFluids.STILL_LARD, PeonyFluids.FLOWING_LARD,
                SimpleFluidRenderHandler.coloredWater(0xffffe39e));

        PeonyItemRenderers.register();
    }
}
