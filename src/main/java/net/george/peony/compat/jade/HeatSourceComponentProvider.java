package net.george.peony.compat.jade;

import net.george.peony.api.heat.HeatProvider;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class HeatSourceComponentProvider implements IBlockComponentProvider {
    public static final HeatSourceComponentProvider DEFAULT = of(PeonyJadePlugin.DEFAULT_HEAT_SOURCE_ID);

    private final Identifier id;

    private HeatSourceComponentProvider(Identifier id) {
        this.id = id;
    }

    public static HeatSourceComponentProvider of(Identifier id) {
        return new HeatSourceComponentProvider(id);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig config) {
        if (blockAccessor.getBlock() instanceof HeatProvider provider) {
            tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_HEAT_SOURCE_AVAILABLE_HEAT_AMOUNT,
                    provider.getTemperature().getMin(), provider.getTemperature().getMax()));
            tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_HEAT_SOURCE_HEATING_LEVEL));
            tooltip.append(Text.translatable(provider.getLevel().getTranslationKey()).formatted(Formatting.YELLOW));
        }
    }

    @Override
    public Identifier getUid() {
        return this.id;
    }
}
