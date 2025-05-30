package net.george.peony.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.SimpleVoxelShape;

public class ExtraCodecs {
    public static final MapCodec<BitSetVoxelSet> BIT_SET_VOXEL_SET = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.fieldOf("sizeX").forGetter(BitSetVoxelSet::getXSize),
                    Codec.INT.fieldOf("sizeY").forGetter(BitSetVoxelSet::getYSize),
                    Codec.INT.fieldOf("sizeZ").forGetter(BitSetVoxelSet::getZSize),
                    Codec.INT.fieldOf("minX").forGetter(set -> set.getMin(Direction.Axis.X)),
                    Codec.INT.fieldOf("minY").forGetter(set -> set.getMin(Direction.Axis.Y)),
                    Codec.INT.fieldOf("minZ").forGetter(set -> set.getMin(Direction.Axis.Z)),
                    Codec.INT.fieldOf("maxX").forGetter(set -> set.getMax(Direction.Axis.X)),
                    Codec.INT.fieldOf("maxY").forGetter(set -> set.getMax(Direction.Axis.Y)),
                    Codec.INT.fieldOf("maxZ").forGetter(set -> set.getMax(Direction.Axis.Z))
            ).apply(instance, BitSetVoxelSet::create));
    public static final MapCodec<BitSetVoxelSet> SIMPLE_BIT_SET_VOXEL_SET = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.fieldOf("sizeX").forGetter(BitSetVoxelSet::getXSize),
                    Codec.INT.fieldOf("sizeY").forGetter(BitSetVoxelSet::getYSize),
                    Codec.INT.fieldOf("sizeZ").forGetter(BitSetVoxelSet::getZSize)
            ).apply(instance, BitSetVoxelSet::new));
    public static final MapCodec<SimpleVoxelShape> SIMPLE_VOXEL_SHAPE = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BIT_SET_VOXEL_SET.fieldOf("voxelSet").forGetter(shape -> (BitSetVoxelSet) shape.voxels)
            ).apply(instance, SimpleVoxelShape::new));
}