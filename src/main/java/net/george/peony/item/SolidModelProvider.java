package net.george.peony.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

public interface SolidModelProvider {
    BlockState asRenderingState();

    @Environment(EnvType.CLIENT)
    static void render(SolidModelProvider provider, BlockRenderManager manager, BlockPos pos, BlockRenderView world, MatrixStack matrices,
                       VertexConsumerProvider consumer) {
        render(provider, manager, pos, world, matrices, consumer, true, Random.create());
    }

    @Environment(EnvType.CLIENT)
    static void render(SolidModelProvider provider, BlockRenderManager manager, BlockPos pos, BlockRenderView world, MatrixStack matrices,
                       VertexConsumerProvider consumer, boolean cull, Random random) {
        manager.renderBlock(provider.asRenderingState(), pos, world, matrices, consumer.getBuffer(RenderLayers.getBlockLayer(provider.asRenderingState())), cull, random);
    }
}
