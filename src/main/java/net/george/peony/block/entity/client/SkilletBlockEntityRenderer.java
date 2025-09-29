package net.george.peony.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.block.entity.SkilletBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class SkilletBlockEntityRenderer implements BlockEntityRenderer<SkilletBlockEntity> {
    public SkilletBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(SkilletBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        // 移动到方块中心
        matrices.translate(0.5f, 0.5f, 0.5f);

        // 渲染库存物品
        renderInventoryItems(entity, matrices, vertexConsumers, light, overlay);

        // 恢复矩阵状态
        matrices.pop();
    }

    private void renderInventoryItems(SkilletBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // 获取输入和输出物品
        ItemStack inputStack = entity.getInputStack();
        ItemStack outputStack = entity.getOutputStack();

        // 渲染输入物品（在平底锅内）
        if (!inputStack.isEmpty()) {
            matrices.push();
            matrices.translate(0, 0.1f, 0); // 稍微高于平底锅底部
            matrices.scale(0.5f, 0.5f, 0.5f); // 调整物品大小

            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    inputStack,
                    ModelTransformationMode.GROUND,
                    light,
                    overlay,
                    matrices,
                    vertexConsumers,
                    entity.getWorld(),
                    0
            );
            matrices.pop();
        }

        // 渲染输出物品（在平底锅内）
        if (!outputStack.isEmpty()) {
            matrices.push();
            matrices.translate(0, 0.1f, 0); // 稍微高于平底锅底部
            matrices.scale(0.5f, 0.5f, 0.5f); // 调整物品大小

            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    outputStack,
                    ModelTransformationMode.GROUND,
                    light,
                    overlay,
                    matrices,
                    vertexConsumers,
                    entity.getWorld(),
                    0
            );
            matrices.pop();
        }
    }
}
