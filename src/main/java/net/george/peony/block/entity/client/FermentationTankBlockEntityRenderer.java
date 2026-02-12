package net.george.peony.block.entity.client;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.george.peony.block.entity.FermentationTankBlockEntity;
import net.george.peony.util.FluidRenderHelper;
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
        FluidVariant fluidVariant = entity.getFluidStorage().getResource();
        long amount = entity.getFluidStorage().getAmount();
        long capacity = entity.getFluidStorage().getCapacity();

        if (fluidVariant.isBlank() || amount <= 0) {
            return;
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
    }

    @Override
    public boolean rendersOutsideBoundingBox(FermentationTankBlockEntity blockEntity) {
        return false;
    }
}
