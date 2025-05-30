package net.george.peony.block.entity.client;

import net.george.peony.block.entity.CuttingBoardBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class CuttingBoardBlockEntityRenderer implements BlockEntityRenderer<CuttingBoardBlockEntity> {
    protected final ItemRenderer itemRenderer;

    public CuttingBoardBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(CuttingBoardBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack stack = entity.getInputStack();
        int pos = (int) entity.getPos().asLong();

        if (stack != ItemStack.EMPTY) {
            matrices.push();
            matrices.translate(0.5, 0.2, 0.5);
            matrices.scale(0.6F, 1, 0.6F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            this.itemRenderer.renderItem(stack, ModelTransformationMode.FIXED,
                    light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), pos);
            matrices.pop();
        }
    }
}
