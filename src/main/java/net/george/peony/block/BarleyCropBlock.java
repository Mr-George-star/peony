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

public class BarleyCropBlock extends CropBlock {
    public static final MapCodec<BarleyCropBlock> CODEC = createCodec(BarleyCropBlock::new);
    public static final int MAX_AGE = 7;
    public static final IntProperty AGE = Properties.AGE_7;

    public BarleyCropBlock(Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<BarleyCropBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return PeonyItems.BARLEY_SEEDS;
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
