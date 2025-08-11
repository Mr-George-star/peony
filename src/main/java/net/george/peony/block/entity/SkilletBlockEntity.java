package net.george.peony.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SkilletBlockEntity extends BlockEntity {
    public SkilletBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.SKILLET, pos, state);
    }
}
