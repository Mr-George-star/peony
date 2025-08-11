package net.george.peony.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.block.MillstoneBlock;
import net.george.peony.block.entity.MillstoneBlockEntity;
import net.george.peony.client.PeonyModelLoaderPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class MillstoneBlockEntityRenderer implements BlockEntityRenderer<MillstoneBlockEntity> {
    public static final BakedModel MILLSTONE_TOP = MinecraftClient.getInstance().getBakedModelManager().getModel(PeonyModelLoaderPlugin.MILLSTONE_TOP);
    protected final BlockRenderManager manager;
    protected final ItemRenderer itemRenderer;

    private static final float MODEL_CENTER_X = 8.0f / 16.0f;
    private static final float MODEL_CENTER_Y = 1.5f / 16.0f;
    private static final float MODEL_CENTER_Z = 8.0f / 16.0f;

    public MillstoneBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.manager = context.getRenderManager();
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(MillstoneBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        float degrees = -22.5F * entity.getCachedState().get(MillstoneBlock.ROTATION_TIMES);

        matrices.translate(0.5, 0.3125 + MODEL_CENTER_Y, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(this.getRotationAngle(entity.getDirection())));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(degrees));
        matrices.translate(-MODEL_CENTER_X, -MODEL_CENTER_Y, -MODEL_CENTER_Z);

        this.manager.getModelRenderer().render(matrices.peek(), vertexConsumers.getBuffer(RenderLayers.getEntityBlockLayer(entity.getCachedState(), false)),
                entity.getCachedState(), MILLSTONE_TOP, 1, 1, 1, light, overlay);

        matrices.pop();
    }

    protected float getRotationAngle(Direction facing) {
        return switch (facing) {
            case SOUTH -> 180F;
            case EAST -> 270F;
            case WEST -> 90F;
            default -> 0;
        };
    }
}
