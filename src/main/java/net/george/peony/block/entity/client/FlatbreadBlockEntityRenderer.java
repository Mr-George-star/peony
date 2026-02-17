package net.george.peony.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.block.entity.FlatbreadBlockEntity;
import net.george.peony.util.RenderUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class FlatbreadBlockEntityRenderer implements BlockEntityRenderer<FlatbreadBlockEntity> {
    public FlatbreadBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(FlatbreadBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        RenderUtils.renderItemList(entity, entity.getIngredients(), matrices, vertexConsumers,
                (float) 1 / 16, light, overlay, Direction.NORTH);
    }
}
