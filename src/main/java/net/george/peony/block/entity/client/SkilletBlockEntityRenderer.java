package net.george.peony.block.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.block.entity.NonBlockRenderingItems;
import net.george.peony.block.entity.SkilletBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.List;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class SkilletBlockEntityRenderer implements BlockEntityRenderer<SkilletBlockEntity> {
    protected final ItemRenderer itemRenderer;
    protected final BlockRenderManager blockRenderManager;

    public SkilletBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.blockRenderManager = context.getRenderManager();
    }

    @Override
    public void render(SkilletBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction direction = entity.getDirection();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        List<ItemStack> ingredients = entity.getAddedIngredients();

        if (ingredients.isEmpty()) {
            renderSingleItem(entity, entity.getOutputStack(), direction, 0, matrices, vertexConsumers, light, overlay);
        } else {
            for (int index = 0; index < ingredients.size(); index++) {
                ItemStack stack = ingredients.get(index);
                renderIngredient(entity, stack, direction, index, matrices, vertexConsumers, tickDelta, light, overlay);
            }
        }
    }

    private void renderIngredient(SkilletBlockEntity entity, ItemStack stack, Direction direction, int index,
                                  MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, int light, int overlay) {
        matrices.push();

        if (entity.allowOilBasedRecipes && SkilletBlockEntity.isCookingOil(stack)) {
            this.renderOil(entity, matrices, vertexConsumers, tickDelta, light, overlay);
        } else {
            renderSingleItem(entity, stack, direction, index, matrices, vertexConsumers, light, overlay);
        }

        matrices.pop();
    }

    private void renderSingleItem(SkilletBlockEntity entity, ItemStack stack, Direction direction, int index,
                                  MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        int pos = (int) entity.getPos().asLong();

        if (stack.getItem() instanceof BlockItem && !NonBlockRenderingItems.getInstance().contains(stack.getItem())) {
            this.block(matrices, direction, index);
        } else {
            this.item(matrices, direction, index);
        }

        renderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV,
                matrices, vertexConsumers, entity.getWorld(), pos);
    }

    protected void block(MatrixStack matrices, Direction direction, int index) {
        float rotation = -direction.asRotation();

        matrices.translate(0.5, (index + 1) / 16.0, 0.5);
        matrices.scale(0.8F, 0.8F, 0.8F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
    }

    protected void item(MatrixStack matrices, Direction direction, int index) {
        float rotation = -direction.asRotation();

        matrices.translate(0.5, (index + 1) / 16.0, 0.5);
        matrices.scale(0.6F, 0.6F, 0.6F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
    }

    protected void renderOil(SkilletBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, int light, int overlay) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        World world = blockEntity.getWorld();
        if (world == null) return;

        long time = world.getTime();
        float waveOffset = (float) Math.sin((time + tickDelta) * 0.1) * 0.01f;
        float liquidHeight = 0.075F;

        MinecraftClient client = MinecraftClient.getInstance();
        Sprite sprite = client.getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).getParticleSprite();
        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float startX = 0.1f;
        float endX = 0.9f;
        float startZ = 0.1f;
        float endZ = 0.9f;

        int color = 0xffffe39e;

        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();

        vertexConsumer.vertex(matrix, startX, liquidHeight + waveOffset, startZ)
                .color(color)
                .texture(minU, minV)
                .light(light)
                .overlay(overlay)
                .normal(0, 1, 0);

        vertexConsumer.vertex(matrix, endX, liquidHeight - waveOffset, startZ)
                .color(color)
                .texture(maxU, minV)
                .light(light)
                .overlay(overlay)
                .normal(0, 1, 0);

        vertexConsumer.vertex(matrix, endX, liquidHeight + waveOffset, endZ)
                .color(color)
                .texture(maxU, maxV)
                .light(light)
                .overlay(overlay)
                .normal(0, 1, 0);

        vertexConsumer.vertex(matrix, startX, liquidHeight - waveOffset, endZ)
                .color(color)
                .texture(minU, maxV)
                .light(light)
                .overlay(overlay)
                .normal(0, 1, 0);
    }
}
