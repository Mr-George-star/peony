package net.george.peony.util;

import net.george.peony.block.entity.NonBlockRenderingItems;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import java.util.List;

public class RenderUtils {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final ItemRenderer itemRenderer = client.getItemRenderer();

    public static void renderItemList(BlockEntity entity, List<ItemStack> stacks, MatrixStack matrices,
                                      VertexConsumerProvider vertexConsumers, float yOffset, int light, int overlay,
                                      Direction direction) {
        matrices.push();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                matrices.push();
                yOffset += renderSingleItem(entity, stack, matrices, vertexConsumers, light, overlay, yOffset, direction);
                matrices.pop();
            }
        }
        matrices.pop();
    }

    /**
     * Render a single item
     * @return Returns the height of the item, used to stack the next item
     */
    public static float renderSingleItem(BlockEntity entity, ItemStack stack, MatrixStack matrices,
                                         VertexConsumerProvider vertexConsumers, int light, int overlay,
                                         float yOffset, Direction direction) {
        Item item = stack.getItem();
        boolean isBlockItem = item instanceof BlockItem && !NonBlockRenderingItems.getInstance().contains(item);
        float rotation = -direction.asRotation();
        float scale = isBlockItem ? 0.7F : 0.5F;

        matrices.translate(0.5F, yOffset, 0.5F);
        matrices.scale(scale, scale, scale);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        if (!isBlockItem) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        }

        itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, overlay,
                matrices, vertexConsumers, entity.getWorld(), (int) entity.getPos().asLong());

        float itemHeight = isBlockItem ? 1.0F : 0.5F;
        return itemHeight / 16;
    }
}
