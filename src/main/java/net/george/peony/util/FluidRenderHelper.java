package net.george.peony.util;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockRenderView;

@SuppressWarnings("SameParameterValue")
public class FluidRenderHelper {
    public static void renderSimpleFluidCube(
            BlockRenderView world, BlockPos pos, Fluid fluid,
            float xMin, float yMin, float zMin, float xMax, float yMax, float zMax,
            VertexConsumerProvider vertexConsumers, MatrixStack matrices, int light) {
        renderFluidCubeComplete(
                world,
                pos,
                fluid,
                xMin, yMin, zMin,
                xMax, yMax, zMax,
                vertexConsumers,
                matrices,
                light,
                true
        );
    }

    public static void renderFluidCubeComplete(
            BlockRenderView world, BlockPos pos, Fluid fluid,
            float xMin, float yMin, float zMin, float xMax, float yMax, float zMax,
            VertexConsumerProvider provider, MatrixStack matrices, int light, boolean renderBottom) {
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        if (handler == null) {
            return;
        }

        FluidState state = fluid.getDefaultState();
        Sprite fluidTexture = handler.getFluidSprites(world, null, state)[0];
        int color = handler.getFluidColor(world, pos, state) | 0xff000000;

        int blockLightIn = (light >> 4) & 0xF;
        int luminosity = Math.max(blockLightIn, state.getBlockState().getLuminance());
        light = (light & 0xF00000) | luminosity << 4;

        VertexConsumer vertexBuilder = provider.getBuffer(RenderLayer.getTranslucentMovingBlock());
        matrices.push();
        MatrixStack.Entry entry = matrices.peek();

        for (Direction side : Direction.values()) {
            if (side == Direction.DOWN && !renderBottom) {
                continue;
            }

            boolean positive = side.getDirection() == Direction.AxisDirection.POSITIVE;

            if (side.getAxis().isHorizontal()) {
                if (side.getAxis() == Direction.Axis.X) {
                    renderTiledFaceWithDetails(
                            side,
                            zMin, yMin,
                            zMax, yMax,
                            positive ? xMax : xMin,
                            vertexBuilder,
                            entry,
                            light,
                            color,
                            fluidTexture,
                            1.0F
                    );
                } else {
                    renderTiledFaceWithDetails(
                            side,
                            xMin, yMin,
                            xMax, yMax,
                            positive ? zMax : zMin,
                            vertexBuilder,
                            entry,
                            light,
                            color,
                            fluidTexture,
                            1.0f
                    );
                }
            } else {
                renderTiledFaceWithDetails(
                        side,
                        xMin, zMin,
                        xMax, zMax,
                        positive ? yMax : yMin,
                        vertexBuilder,
                        entry,
                        light,
                        color,
                        fluidTexture,
                        1.0f
                );
            }
        }

        matrices.pop();
    }

    private static void renderTiledFaceWithDetails(Direction direction,
                                                   float left, float down, float right, float up, float depth,
                                                   VertexConsumer vertexBuilder, MatrixStack.Entry entry,
                                                   int light, int color, Sprite texture, float textureScale) {
        boolean positive = direction.getDirection() == Direction.AxisDirection.POSITIVE;
        boolean horizontal = direction.getAxis().isHorizontal();
        boolean xAxis = direction.getAxis() == Direction.Axis.X;

        for (float x1 = left; x1 < right;) {
            float floorX = MathHelper.floor(x1);
            float x2 = Math.min(floorX + 1, right);

            float u1, u2;
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                float ceilX2 = MathHelper.ceil(x2);
                u1 = texture.getFrameU((ceilX2 - x2) * textureScale);
                u2 = texture.getFrameU((ceilX2 - x1) * textureScale);
            } else {
                u1 = texture.getFrameU((x1 - floorX) * textureScale);
                u2 = texture.getFrameU((x2 - floorX) * textureScale);
            }

            for (float y1 = down; y1 < up;) {
                float floorY = MathHelper.floor(y1);
                float y2 = Math.min(floorY + 1, up);

                float v1, v2;
                if (direction == Direction.UP) {
                    v1 = texture.getFrameV((y1 - floorY) * textureScale);
                    v2 = texture.getFrameV((y2 - floorY) * textureScale);
                } else {
                    float ceilY2 = MathHelper.ceil(y2);
                    v1 = texture.getFrameV((ceilY2 - y2) * textureScale);
                    v2 = texture.getFrameV((ceilY2 - y1) * textureScale);
                }

                if (horizontal) {
                    if (xAxis) {
                        createVertex(vertexBuilder, entry, depth, y2, positive ? x2 : x1, color, u1, v1, direction, light);
                        createVertex(vertexBuilder, entry, depth, y1, positive ? x2 : x1, color, u1, v2, direction, light);
                        createVertex(vertexBuilder, entry, depth, y1, positive ? x1 : x2, color, u2, v2, direction, light);
                        createVertex(vertexBuilder, entry, depth, y2, positive ? x1 : x2, color, u2, v1, direction, light);
                    } else {
                        createVertex(vertexBuilder, entry, positive ? x1 : x2, y2, depth, color, u1, v1, direction, light);
                        createVertex(vertexBuilder, entry, positive ? x1 : x2, y1, depth, color, u1, v2, direction, light);
                        createVertex(vertexBuilder, entry, positive ? x2 : x1, y1, depth, color, u2, v2, direction, light);
                        createVertex(vertexBuilder, entry, positive ? x2 : x1, y2, depth, color, u2, v1, direction, light);
                    }
                } else {
                    createVertex(vertexBuilder, entry, x1, depth, positive ? y1 : y2, color, u1, v1, direction, light);
                    createVertex(vertexBuilder, entry, x1, depth, positive ? y2 : y1, color, u1, v2, direction, light);
                    createVertex(vertexBuilder, entry, x2, depth, positive ? y2 : y1, color, u2, v2, direction, light);
                    createVertex(vertexBuilder, entry, x2, depth, positive ? y1 : y2, color, u2, v1, direction, light);
                }
                y1 = y2;
            }
            x1 = x2;
        }
    }

    private static void createVertex(VertexConsumer vertices, MatrixStack.Entry entry,
                                     float x, float y, float z, int color, float u, float v, Direction face, int light) {
        Vec3i vector = face.getVector();

        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        vertices.vertex(entry.getPositionMatrix(), x, y, z)
                .color(red, green, blue, alpha)
                .texture(u, v)
                .light(light)
                .normal(entry, vector.getX(), vector.getY(), vector.getZ());
    }
}
