package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class FlourBlock extends FallingShapedBlock {
    public static final MapCodec<FlourBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(createSettingsCodec()).apply(instance, FlourBlock::new));

    public FlourBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<FlourBlock> getCodec() {
        return CODEC;
    }

    @NotNull
    @Override
    public VoxelShape getShapeFromDirection(@NotNull Direction direction) {
        return switch (direction) {
            case WEST, EAST -> Block.createCuboidShape(4, 0, 3, 12, 2, 13);
            default -> Block.createCuboidShape(3, 0, 4, 13, 2, 12);
        };
    }
}
