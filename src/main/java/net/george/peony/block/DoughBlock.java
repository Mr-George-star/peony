package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DoughBlock extends WaterloggedShapedBlock {
    public static final MapCodec<DoughBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(createSettingsCodec()).apply(instance, DoughBlock::new));

    public DoughBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<DoughBlock> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull VoxelShape getShapeFromDirection(@NotNull Direction direction) {
        return switch (direction) {
            case WEST, EAST -> Block.createCuboidShape(5, 0, 4, 11, 3, 12);
            default -> Block.createCuboidShape(4, 0, 5, 12, 3, 11);
        };
    }
}
