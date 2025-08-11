package net.george.peony.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.block.LogStickBlock;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.PotStandBlock;
import net.george.peony.block.PotStandWithCampfireBlock;
import net.george.peony.block.entity.DirectionProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class AbstractPotStandRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    protected static final List<ModelElement> NORTH_ELEMENTS = List.of(
            new ModelElement(new Vec3d(17, -1, -2), new Vec3d(19, 15, 0), 12.5F, Direction.Axis.Z, new Vec3d(17, -1, -2)),
            new ModelElement(new Vec3d(17, -1, 16), new Vec3d(19, 15, 18), 12.5F, Direction.Axis.Z, new Vec3d(17, -1, 16)),
            new ModelElement(new Vec3d(-3, -1, -2), new Vec3d(-1, 15, 0), -12.5F, Direction.Axis.Z, new Vec3d(-3, -1, -2)),
            new ModelElement(new Vec3d(-3, -1, 16), new Vec3d(-1, 15, 18), -12.5F, Direction.Axis.Z, new Vec3d(-3, -1, 16))
    );
    protected static final List<ModelElement> WEST_ELEMENTS = List.of(
            new ModelElement(new Vec3d(16, -1, -3), new Vec3d(18, 15, -1), 12.5F, Direction.Axis.X, new Vec3d(16, -1, -3)),
            new ModelElement(new Vec3d(16, -1, 17), new Vec3d(18, 15, 19), -12.5F, Direction.Axis.X, new Vec3d(16, -1, 17)),
            new ModelElement(new Vec3d(-2, -1, -3), new Vec3d(0, 15, -1), 12.5F, Direction.Axis.X, new Vec3d(-2, -1, -3)),
            new ModelElement(new Vec3d(-2, -1, 17), new Vec3d(0, 15, 19), -12.5F, Direction.Axis.X, new Vec3d(-2, -1, 17))
    );
    protected final BlockRenderManager manager;

    public AbstractPotStandRenderer(BlockEntityRendererFactory.Context context) {
        this.manager = context.getRenderManager();
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity instanceof DirectionProvider) {
            Direction direction = ((DirectionProvider) entity).getDirection();
            List<ModelElement> elements = (direction == Direction.NORTH || direction == Direction.SOUTH) ? NORTH_ELEMENTS : WEST_ELEMENTS;

            for (ModelElement element : elements) {
                matrices.push();
                applyTransformFromElement(matrices, element);

                BlockState stick = this.getLogStickState(entity);
                this.manager.renderBlockAsEntity(
                        stick.with(LogStickBlock.FACING, Direction.UP),
                        matrices, vertexConsumers, light, overlay
                );
                matrices.pop();
            }
        }
    }

    protected BlockState getLogStickState(T entity) {
        Block block = entity.getCachedState().getBlock();
        if (block instanceof PotStandBlock potStand) {
            return potStand.getLogStick().getDefaultState();
        } else if (block instanceof PotStandWithCampfireBlock potStandWithCampfire) {
            return potStandWithCampfire.getLogStick().getDefaultState();
        } else {
            return PeonyBlocks.OAK_LOG_STICK.getDefaultState();
        }
    }

    protected void applyTransformFromElement(MatrixStack matrices, ModelElement element) {
        float scale = 1.0f / 16.0f;

        matrices.translate(element.origin.x * scale, element.origin.y * scale, element.origin.z * scale);

        switch (element.axis) {
            case X -> matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(element.angle));
            case Y -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(element.angle));
            case Z -> matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(element.angle));
        }

        matrices.translate(-element.origin.x * scale, -element.origin.y * scale, -element.origin.z * scale);

        matrices.translate(element.from.x * scale, element.from.y * scale, element.from.z * scale);
        matrices.translate(-0.4375, 0, -0.4375);
    }

    protected record ModelElement(Vec3d from, Vec3d to, float angle, Direction.Axis axis, Vec3d origin) {}
}
