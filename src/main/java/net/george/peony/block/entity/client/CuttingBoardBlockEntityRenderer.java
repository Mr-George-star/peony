package net.george.peony.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.block.entity.CarvedRenderingItems;
import net.george.peony.block.entity.CuttingBoardBlockEntity;
import net.george.peony.block.entity.NonBlockRenderingItems;
import net.george.peony.item.SolidModelProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class CuttingBoardBlockEntityRenderer implements BlockEntityRenderer<CuttingBoardBlockEntity> {
    protected final ItemRenderer itemRenderer;
    protected final BlockRenderManager blockRenderManager;

    public CuttingBoardBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.blockRenderManager = context.getRenderManager();
    }

    @Override
    public void render(CuttingBoardBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction direction = entity.getDirection();
        ItemStack stack = entity.getInputStack();
        int pos = (int) entity.getPos().asLong();

        if (stack != ItemStack.EMPTY) {
            matrices.push();
            ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();

            if (stack.getItem() instanceof SolidModelProvider provider) {
                matrices.translate(0.1, 0.2, 0.1);
                matrices.scale(0.8F, 0.8F, 0.8F);
                SolidModelProvider.render(provider, this.blockRenderManager, entity.getPos(), entity.getWorld(), matrices, vertexConsumers);
                matrices.pop();
                return;
            }

            matrices.push();
            BakedModel model = renderer.getModel(stack, entity.getWorld(), null, 0);
            model.getTransformation().getTransformation(ModelTransformationMode.FIXED).apply(false, matrices);
            matrices.pop();

            Item item = stack.getItem();
            if (item instanceof BlockItem && !NonBlockRenderingItems.getInstance().contains(item)) {
                this.block(matrices, direction);
            } else if (CarvedRenderingItems.getInstance().contains(item)) {
                this.carvedItem(matrices, direction, item);
            } else {
                this.item(matrices, direction);
            }

            renderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV,
                    matrices, vertexConsumers, entity.getWorld(), pos);
            matrices.pop();
        }
    }

    protected void block(MatrixStack matrices, Direction direction) {
        float rotation = -direction.asRotation();

        matrices.translate(0.5D, 0.39, 0.5D);
        matrices.scale(0.8F, 0.8F, 0.8F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
    }

    protected void item(MatrixStack matrices, Direction direction) {
        float rotation = -direction.asRotation();

        matrices.translate(0.5, 0.2, 0.5);
        matrices.scale(0.6F, 0.6F, 0.6F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
    }

    protected void carvedItem(MatrixStack matrices, Direction direction, Item item) {
        float rotation = -direction.asRotation();

        matrices.translate(0.5, 0.48, 0.5);
        matrices.scale(0.8F, 0.8F, 0.8F);

        float angle = CarvedRenderingItems.getInstance().get(item).orElse(180F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));
    }
}
