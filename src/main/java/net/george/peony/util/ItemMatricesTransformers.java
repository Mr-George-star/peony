package net.george.peony.util;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.joml.Vector3f;

@NonExtendable
public class ItemMatricesTransformers {
    public static final Vector3f DEFAULT_ROTATION = new Vector3f(0.0F, 0.0F, 0.0F);
    public static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0F, 0.0F, 0.0F);
    public static final Vector3f DEFAULT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);
    public static final Applier DEFAULT_APPLIER = mode -> switch (mode) {
        case GUI -> (leftHanded, matrices) -> deserialize(new Vector3f(30, 225, 0), DEFAULT_TRANSLATION, new Vector3f(0.625F)).apply(leftHanded, matrices);
        case GROUND -> (leftHanded, matrices) -> deserialize(DEFAULT_ROTATION, new Vector3f(0, 3, 0), new Vector3f(0.25F)).apply(leftHanded, matrices);
        case FIXED -> (leftHanded, matrices) -> deserialize(DEFAULT_ROTATION, DEFAULT_TRANSLATION, new Vector3f(0.5F)).apply(leftHanded, matrices);
        case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> (leftHanded, matrices) -> deserialize(new Vector3f(75, 45, 0), new Vector3f(0, 2.5F, 0), new Vector3f(0.375F)).apply(leftHanded, matrices);
        case FIRST_PERSON_RIGHT_HAND -> (leftHanded, matrices) -> deserialize(new Vector3f(0, 45, 0), DEFAULT_TRANSLATION, new Vector3f(0.4F)).apply(leftHanded, matrices);
        case FIRST_PERSON_LEFT_HAND -> (leftHanded, matrices) -> deserialize(new Vector3f(0, 225, 0), DEFAULT_TRANSLATION, new Vector3f(0.4F)).apply(leftHanded, matrices);
        default -> Transformation.IDENTITY::apply;
    };

    public static Transformation deserialize(Vector3f rotation, Vector3f translation, Vector3f scale) {
        translation.mul(0.0625F);
        translation.set(MathHelper.clamp(translation.x, -5.0F, 5.0F), MathHelper.clamp(translation.y, -5.0F, 5.0F), MathHelper.clamp(translation.z, -5.0F, 5.0F));
        scale.set(MathHelper.clamp(scale.x, -4.0F, 4.0F), MathHelper.clamp(scale.y, -4.0F, 4.0F), MathHelper.clamp(scale.z, -4.0F, 4.0F));
        return new Transformation(rotation, translation, scale);
    }

    @FunctionalInterface
    public interface Applier {
        Transformer from(ModelTransformationMode mode);

        default void apply(MatrixStack matrices, ModelTransformationMode mode) {
            apply(matrices, mode, mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND);
        }

        default void apply(MatrixStack matrices, ModelTransformationMode mode, boolean leftHanded) {
            from(mode).apply(leftHanded, matrices);
        }
    }

    @FunctionalInterface
    public interface Transformer {
        void apply(boolean leftHanded, MatrixStack stack);
    }
}
