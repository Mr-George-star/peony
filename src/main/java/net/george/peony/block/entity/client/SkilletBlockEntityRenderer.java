package net.george.peony.block.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
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
        SkilletBlockEntity.AnimationData data = entity.context.getAnimationData();
        long seed = data.seed;
        Random random = Random.create(seed);

        long time = System.currentTimeMillis() - data.timestamp;
        if (data.preSeed != seed) {
            data.preSeed = seed;
            if (time > 1000) {
                data.timestamp = System.currentTimeMillis();
            }
            data.randomHeights = new float[9];
            for (int i = 0; i < 9; i++) {
                data.randomHeights[i] = 0.25f + random.nextFloat() * 1;
            }
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        List<ItemStack> ingredients = entity.getAddedIngredients();

        matrices.push();
        this.applyInitial(entity, matrices);

        if (ingredients.isEmpty()) {
            renderSingleItem(entity, entity.getOutputStack(), 0, matrices, vertexConsumers, light, overlay, random, time, data);
        } else {
            if (!entity.getOutputStack().isEmpty() && entity.getOutputStack() != null) {
                if (entity.getRequiredContainer() == null) {
                    renderSingleItem(entity, entity.getOutputStack(), 0, matrices, vertexConsumers, light, overlay, random, time, data);
                } else {
                    render(entity, ingredients, false, matrices, vertexConsumers, tickDelta, light, overlay, random, time, data);
                }
                matrices.pop();
                return;
            }
            render(entity, ingredients, true, matrices, vertexConsumers, tickDelta, light, overlay, random, time, data);
        }
        matrices.pop();
    }

    private void render(SkilletBlockEntity entity, List<ItemStack> ingredients, boolean renderOil,
                        MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, int light, int overlay,
                        Random random, long time, SkilletBlockEntity.AnimationData data) {
        int index = 0;
        int stackIndex = 0;
        while (stackIndex < ingredients.size()) {
            ItemStack stack = ingredients.get(stackIndex);
            if (SkilletBlockEntity.isCookingOil(stack) && renderOil) {
                if (renderIngredient(entity, stack, index, matrices, vertexConsumers, tickDelta, light, overlay, random, time, data)) {
                    index++;
                }
            } else if (!SkilletBlockEntity.isCookingOil(stack)){
                if (renderIngredient(entity, stack, index, matrices, vertexConsumers, tickDelta, light, overlay, random, time, data)) {
                    index++;
                }
            }
            stackIndex++;
            matrices.translate(0, 0, 0.025);
        }
    }

    private boolean renderIngredient(SkilletBlockEntity entity, ItemStack stack, int index,
                                     MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, int light, int overlay,
                                     Random random, long time, SkilletBlockEntity.AnimationData data) {
        boolean increaseIndex = true;
        matrices.push();

        if (entity.context.allowOilBasedRecipes && SkilletBlockEntity.isCookingOil(stack)) {
            this.renderOil(entity, matrices, vertexConsumers, tickDelta, light, overlay);
            increaseIndex = false;
        } else {
            renderSingleItem(entity, stack, index, matrices, vertexConsumers, light, overlay,
                    random, time, data);
        }

        matrices.pop();
        return increaseIndex;
    }

    private void renderSingleItem(SkilletBlockEntity entity, ItemStack stack, int index,
                                  MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay,
                                  Random random, long time, SkilletBlockEntity.AnimationData data) {
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        int pos = (int) entity.getPos().asLong();

        int count = 90 + random.nextInt(90);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(index * count));

        if (time < 1000) {
            matrices.translate(0, 0, data.randomHeights[index] * MathHelper.sin(MathHelper.PI * time / 1000f));
            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(720f / 1000 * time));
        }

        renderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV,
                matrices, vertexConsumers, entity.getWorld(), pos);
    }

    protected void applyInitial(SkilletBlockEntity skillet, MatrixStack matrices) {
        int rotation = skillet.getDirection().getHorizontal() * 90;
        matrices.translate(0.5, 0.1, 0.5);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(rotation));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
        matrices.scale(0.5f, 0.5f, 0.5f);
    }

    protected void renderOil(SkilletBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, int light, int overlay) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        matrices.pop();
        matrices.pop();
        matrices.push();

        World world = blockEntity.getWorld();
        if (world == null) return;

        long time = world.getTime();
        float waveOffset = (float) Math.sin((time + tickDelta) * 0.1) * 0.005f;
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
        matrices.pop();
        matrices.push();
        this.applyInitial(blockEntity, matrices);
        matrices.push();
    }
}
