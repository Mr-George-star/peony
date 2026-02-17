package net.george.peony.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.block.entity.BowlBlockEntity;
import net.george.peony.util.RenderUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class BowlBlockEntityRenderer implements BlockEntityRenderer<BowlBlockEntity> {
    public BowlBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(BowlBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getOutputStack().isEmpty()) {
            RenderUtils.renderItemList(entity, entity.getItems(), matrices, vertexConsumers,
                    (float) 1 / 16, light, overlay, entity.getDirection());
        } else {
            RenderUtils.renderSingleItem(entity, entity.getOutputStack(), matrices, vertexConsumers,
                    light, overlay, (float) 1 / 16, entity.getDirection());
        }
    }
}
