package net.george.peony.block;

import com.mojang.serialization.MapCodec;
import net.george.peony.item.PeonyItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

public class PeanutCropBlock extends CropBlock {
    public static final MapCodec<PeanutCropBlock> CODEC = createCodec(PeanutCropBlock::new);
    public static final int MAX_AGE = Properties.AGE_7_MAX;
    public static final IntProperty AGE = Properties.AGE_7;

    public PeanutCropBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(AGE, 0));
    }

    @Override
    public MapCodec<PeanutCropBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return PeonyItems.PEANUT_KERNEL;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}
