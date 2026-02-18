package net.george.peony.compat.jade;

import net.george.peony.block.entity.FermentationTankBlockEntity;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum FermentationTankComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig config) {
        if (blockAccessor.getBlockEntity() instanceof FermentationTankBlockEntity) {
            NbtCompound nbt = blockAccessor.getServerData();
            if (nbt.contains("IsFermenting") && nbt.getBoolean("IsFermenting")) {
                int fermentTime = nbt.contains("FermentTime") ? nbt.getInt("FermentTime") : 0;
                tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_FERMENT_REMAINING_TIME, Math.round((float) fermentTime / 20)));
            }
        }
    }

    @Override
    public void appendServerData(NbtCompound nbt, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof FermentationTankBlockEntity fermentationTank) {
            fermentationTank.writeFermentingData(nbt, blockAccessor.getLevel().getRegistryManager());
        }
    }

    @Override
    public Identifier getUid() {
        return PeonyJadePlugin.FERMENTATION_TANK_ID;
    }
}
