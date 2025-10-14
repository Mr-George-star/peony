package net.george.peony.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.george.peony.block.MillstoneBlock;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.PotStandBlock;
import net.george.peony.block.PotStandWithCampfireBlock;
import net.george.peony.block.entity.MillstoneBlockEntity;
import net.george.peony.block.entity.PotStandBlockEntity;
import net.george.peony.util.ItemMatricesTransformers;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class PeonyItemRenderers {
    static final MillstoneMatrixApplier MILLSTONE_APPLIER = new MillstoneMatrixApplier();
    static final PotStandMatrixApplier POT_STAND_APPLIER = new PotStandMatrixApplier();

    public static final BuiltinItemRendererRegistry.DynamicItemRenderer MILLSTONE = (stack, mode, matrices, provider, light, overlay) -> {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof MillstoneBlock millstone) {
            BlockState state = millstone.getDefaultState().with(MillstoneBlock.FACING, Direction.NORTH);

            matrices.translate(0.5F, 0.5F, 0.5F);
            MILLSTONE_APPLIER.apply(matrices, mode);
            matrices.translate(-0.5F, -0.5F, -0.5F);
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, matrices, provider, light, overlay);
            MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(
                    new MillstoneBlockEntity(BlockPos.ORIGIN, state), matrices, provider, light, overlay);
        }
    };
    public static final BuiltinItemRendererRegistry.DynamicItemRenderer POT_STAND = (stack, mode, matrices, provider, light, overlay) -> {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof PotStandBlock potStand) {
            BlockState state = potStand.getDefaultState().with(PotStandBlock.FACING, Direction.NORTH);

            matrices.translate(0.5F, 0.5F, 0.5F);
            POT_STAND_APPLIER.apply(matrices, mode);
            matrices.translate(-0.5F, -0.5F, -0.5F);
            MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(
                    matrices.peek(),
                    provider.getBuffer(RenderLayers.getBlockLayer(state)), state,
                    MinecraftClient.getInstance().getBlockRenderManager().getModel(state), 1, 1, 1,
                    light, overlay
            );
            MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(
                    new PotStandBlockEntity(BlockPos.ORIGIN, state), matrices, provider, light, overlay);
        }
    };

    @Environment(EnvType.CLIENT)
    public static void register() {
        BuiltinItemRendererRegistry.INSTANCE.register(PeonyBlocks.MILLSTONE, MILLSTONE);
        Registries.BLOCK.stream().filter(block -> block instanceof PotStandBlock && !(block instanceof PotStandWithCampfireBlock)).forEach(potStand ->
                BuiltinItemRendererRegistry.INSTANCE.register(potStand.asItem(), POT_STAND));
    }

    @Environment(EnvType.CLIENT)
    static class MillstoneMatrixApplier implements ItemMatricesTransformers.Applier {
        MillstoneMatrixApplier() {}

        @Override
        public ItemMatricesTransformers.Transformer from(ModelTransformationMode mode) {
            return switch (mode) {
                case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> (leftHanded, matrices) -> ItemMatricesTransformers.deserialize(ItemMatricesTransformers.DEFAULT_ROTATION, new Vector3f(0, 4, 0), new Vector3f(0.5F)).apply(leftHanded, matrices);
                case GROUND -> (leftHanded, matrices) -> ItemMatricesTransformers.deserialize(ItemMatricesTransformers.DEFAULT_ROTATION, new Vector3f(0, 3, 0), new Vector3f(0.5F)).apply(leftHanded, matrices);
                case GUI -> (leftHanded, matrices) -> ItemMatricesTransformers.deserialize(new Vector3f(30, -135, 0), new Vector3f(0, 2.5F, 0), new Vector3f(0.625F)).apply(leftHanded, matrices);
                case HEAD -> (leftHanded, matrices) -> ItemMatricesTransformers.deserialize(ItemMatricesTransformers.DEFAULT_ROTATION, new Vector3f(0, 14.25F, 0), ItemMatricesTransformers.DEFAULT_SCALE).apply(leftHanded, matrices);
                case FIXED -> (leftHanded, matrices) -> ItemMatricesTransformers.deserialize(new Vector3f(-90, 0, 0), new Vector3f(0, 0, -4), new Vector3f(0.5F)).apply(leftHanded, matrices);
                default -> Transformation.IDENTITY::apply;
            };
        }
    }

    @Environment(EnvType.CLIENT)
    static class PotStandMatrixApplier implements ItemMatricesTransformers.Applier {
        PotStandMatrixApplier() {}

        @Override
        public ItemMatricesTransformers.Transformer from(ModelTransformationMode mode) {
            return switch (mode) {
                case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> (leftHanded, matrices) -> ItemMatricesTransformers.deserialize(new Vector3f(75, 45, 0), new Vector3f(0, 2.5F, 0), new Vector3f(0.375F)).apply(leftHanded, matrices);
                case FIRST_PERSON_RIGHT_HAND -> (leftHanded, matrices) -> ItemMatricesTransformers.deserialize(new Vector3f(0, 45, 0), ItemMatricesTransformers.DEFAULT_TRANSLATION, new Vector3f(0.4F)).apply(leftHanded, matrices);
                case FIRST_PERSON_LEFT_HAND -> (leftHanded, matrices) -> ItemMatricesTransformers.deserialize(new Vector3f(0, -135, 0), ItemMatricesTransformers.DEFAULT_TRANSLATION, new Vector3f(0.4F)).apply(leftHanded, matrices);
                case GROUND -> (leftHanded, matrices) -> ItemMatricesTransformers.deserialize(ItemMatricesTransformers.DEFAULT_ROTATION, new Vector3f(0, 3, 0), new Vector3f(0.25F)).apply(leftHanded, matrices);
                case GUI -> (leftHanded, matrices) -> ItemMatricesTransformers.deserialize(new Vector3f(30, -135, 0), ItemMatricesTransformers.DEFAULT_TRANSLATION, new Vector3f(0.5F)).apply(leftHanded, matrices);
                case HEAD -> (leftHanded, matrices) -> ItemMatricesTransformers.deserialize(ItemMatricesTransformers.DEFAULT_ROTATION, new Vector3f(0, 10.5F, 0), ItemMatricesTransformers.DEFAULT_SCALE).apply(leftHanded, matrices);
                case FIXED -> (leftHanded, matrices) -> ItemMatricesTransformers.deserialize(ItemMatricesTransformers.DEFAULT_ROTATION, ItemMatricesTransformers.DEFAULT_TRANSLATION, new Vector3f(0.5F)).apply(leftHanded, matrices);
                default -> Transformation.IDENTITY::apply;
            };
        }
    }
}
