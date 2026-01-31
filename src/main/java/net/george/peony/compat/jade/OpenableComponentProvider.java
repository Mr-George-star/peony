package net.george.peony.compat.jade;

import net.george.peony.block.data.Openable;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class OpenableComponentProvider implements IBlockComponentProvider {
    public static final OpenableComponentProvider DEFAULT = of(PeonyJadePlugin.DEFAULT_OPENABLE_ID);

    private final Identifier id;

    private OpenableComponentProvider(Identifier id) {
        this.id = id;
    }

    public static OpenableComponentProvider of(Identifier id) {
        return new OpenableComponentProvider(id);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig config) {
        if (blockAccessor.getBlockEntity() instanceof Openable openable) {
            tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_OPENING_STATE));
            if (openable.isOpened()) {
                tooltip.append(Text.translatable(PeonyTranslationKeys.JADE_OPENED));
            } else {
                tooltip.append(Text.translatable(PeonyTranslationKeys.JADE_CLOSED));
            }
        }
    }

    @Override
    public Identifier getUid() {
        return this.id;
    }
}
