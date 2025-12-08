package net.george.peony.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.block.entity.BowlBlockEntity;
import net.george.peony.block.entity.NonBlockRenderingItems;
import net.george.peony.util.math.PositioningHelper;
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
        int pos = (int) entity.getPos().asLong();
        float yOffset = PositioningHelper.yOffsetFromFloor(1);

        for (int index = 0; index < entity.getItems().size(); index++) {
            ItemStack stack = entity.getStack(index);
            if (!stack.isEmpty()) {
                matrices.push();
                ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();

                matrices.push();
                BakedModel model = renderer.getModel(stack, entity.getWorld(), null, 0);
                model.getTransformation().getTransformation(ModelTransformationMode.FIXED).apply(false, matrices);
                matrices.pop();

                Item item = stack.getItem();
                boolean isBlockItem = item instanceof BlockItem && !NonBlockRenderingItems.getInstance().contains(item);
                yOffset += this.positionItem(matrices, direction, yOffset, isBlockItem);

                renderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV,
                        matrices, vertexConsumers, entity.getWorld(), pos);
                matrices.pop();
            }
        }
    }

    protected float positionItem(MatrixStack matrices, Direction direction, float yOffset, boolean isBlockItem) {
        float rotation = -direction.asRotation();


        if (isBlockItem) {
            matrices.translate(0.5F, yOffset, 0.5F);
            matrices.scale(0.7F, 0.7F, 0.7F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
            return PositioningHelper.yOffsetFromFloor(1);
        } else {
            matrices.translate(0.5F, yOffset, 0.5F);
            matrices.scale(0.5F, 0.5F, 0.5F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            return PositioningHelper.yOffsetFromFloor(1);
        }
    }
}
