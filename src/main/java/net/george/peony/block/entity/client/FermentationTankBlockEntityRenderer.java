package net.george.peony.block.entity.client;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.george.peony.block.entity.FermentationTankBlockEntity;
import net.george.peony.util.FluidRenderHelper;
import net.george.peony.util.RenderUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.commons.lang3.math.Fraction;

@SuppressWarnings("unused")
public class FermentationTankBlockEntityRenderer implements BlockEntityRenderer<FermentationTankBlockEntity> {
    public static final Fraction REND = Fraction.getFraction(1, 16);

    public FermentationTankBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(FermentationTankBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        float finalOffset = this.renderFluid(entity, entity.getFluidStorage(), tickDelta, matrices, vertexConsumers, light);

        if (entity.getOutputStack().isEmpty()) {
            RenderUtils.renderItemList(entity, entity.getItems(), matrices, vertexConsumers,
                    finalOffset, light, overlay, entity.getDirection());
        } else {
            RenderUtils.renderSingleItem(entity, entity.getOutputStack(), matrices, vertexConsumers,
                    light, overlay, finalOffset, entity.getDirection());
        }
    }
    
    private float renderFluid(FermentationTankBlockEntity entity, SingleVariantStorage<FluidVariant> fluidStorage,
                             float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        FluidVariant fluidVariant = fluidStorage.getResource();
        long amount = fluidStorage.getAmount();
        long capacity = fluidStorage.getCapacity();

        if (fluidVariant.isBlank() || amount <= 0) {
            return (float) 1 / 16;
        }

        float xMin = REND.floatValue();
        float xMax = 1 - REND.floatValue();
        float zMin = REND.floatValue();
        float zMax = 1 - REND.floatValue();
        float yMin = REND.floatValue();
        float yMax = entity.animatedFluidHeight.getValue(tickDelta);

        FluidRenderHelper.renderSimpleFluidCube(
                entity.getWorld(),
                entity.getPos(),
                fluidVariant.getFluid(),
                xMin, yMin, zMin,
                xMax, yMax, zMax,
                vertexConsumers,
                matrices,
                light
        );
        return yMax;
    }
}
