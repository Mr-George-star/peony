package net.george.peony.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.block.entity.BowlBlockEntity;
import net.george.peony.block.entity.NonBlockRenderingItems;
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
public class BowlBlockEntityRenderer implements BlockEntityRenderer<BowlBlockEntity> {
    protected final ItemRenderer itemRenderer;
    protected final BlockRenderManager blockRenderManager;

    public BowlBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.blockRenderManager = context.getRenderManager();
    }

    @Override
    public void render(BowlBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction direction = entity.getDirection();
//        ItemStack stack = entity.getStack(0);
        int pos = (int) entity.getPos().asLong();

        for (int index = 1; index < entity.getItems().size(); index++) {
            ItemStack stack = entity.getStack(index);
            if (stack != ItemStack.EMPTY) {
                matrices.push();
                ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();

                matrices.push();
                BakedModel model = renderer.getModel(stack, entity.getWorld(), null, 0);
                model.getTransformation().getTransformation(ModelTransformationMode.FIXED).apply(false, matrices);
                matrices.pop();

                Item item = stack.getItem();
                if (item instanceof BlockItem && !NonBlockRenderingItems.getInstance().contains(item)) {
                    this.block(matrices, direction, index);
                } else {
                    this.item(matrices, direction, index);
                }

                renderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV,
                        matrices, vertexConsumers, entity.getWorld(), pos);
                matrices.pop();
            }
        }
    }

    protected void block(MatrixStack matrices, Direction direction, int index) {
        float rotation = -direction.asRotation();

        matrices.translate(0.5D, 0.125 * index, 0.5D);
        matrices.scale(0.8F, 0.8F, 0.8F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
    }

    protected void item(MatrixStack matrices, Direction direction, int index) {
        float rotation = -direction.asRotation();

        matrices.translate(0.5, 0.125 * index, 0.5);
        matrices.scale(0.6F, 0.6F, 0.6F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
    }
}
