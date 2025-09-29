package net.george.peony.compat.jade;

import net.george.peony.block.entity.SkilletBlockEntity;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum SkilletComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig config) {
        if (blockAccessor.getBlockEntity() instanceof SkilletBlockEntity) {
            NbtCompound data = blockAccessor.getServerData();
            boolean containsHasOil = data.contains("HasOil");
            if (!containsHasOil || !data.getBoolean("HasOil")) {
                if (data.contains("InOverflow")) {
                    if (data.getBoolean("InOverflow")) {
                        int overflowTime = this.getIntData(data, "OverflowTime");
                        int maxOverflowTime = this.getIntData(data, "MaxOverflowTime");
                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_COOKING_OVERFLOW_TIME,
                                        Math.round((float) overflowTime / 20), Math.round((float) maxOverflowTime / 20))
                                .formatted(Formatting.RED));
                    } else {
                        int heatingTime = this.getIntData(data, "HeatingTime");
                        int requiredHeatingTime = this.getIntData(data, "RequiredHeatingTime");
                        if (heatingTime > 0 && requiredHeatingTime > 0) {
                            tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_COOKING_TIME,
                                Math.round((float) heatingTime / 20), Math.round((float) requiredHeatingTime / 20)));
                        }
                    }
                }
            }
            if (containsHasOil) {
                if (data.getBoolean("HasOil")) {
                    int oilProcessingStage = this.getIntData(data, "OilProcessingStage");
                    int heatingTime = this.getIntData(data, "HeatingTime");
                    int requiredHeatingTime = this.getIntData(data, "RequiredHeatingTime");
                    if (oilProcessingStage == 0) {
                        int displayHeatingTime = Math.round((float) heatingTime / 20);
                        int displayRequiredHeatingTime = Math.round((float) requiredHeatingTime / 20);
                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_MELTING_OIL,
                                displayHeatingTime, displayRequiredHeatingTime));
                        if (displayHeatingTime >= 4 && displayRequiredHeatingTime >= 4) {
                            IElementHelper elements = IElementHelper.get();
                            IElement icon = elements.item(new ItemStack(PeonyItems.SPATULA), 0.5f).size(new Vec2f(10, 10)).translate(new Vec2f(0, -1));
                            tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_TOOL_USAGE_TOOLTIP));
                            tooltip.append(icon);
                        }
                    }
                }
            }
            if (data.contains("ContainerTooltip")) {
                if (data.getBoolean("ContainerTooltip") && data.contains("Container")) {
                    Item container = Registries.ITEM.get(Identifier.tryParse(data.getString("Container")));
                    IElementHelper elements = IElementHelper.get();
                    IElement icon = elements.item(new ItemStack(container), 0.5f).size(new Vec2f(10, 10)).translate(new Vec2f(0, -1));
                    tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_CONTAINER_TOOLTIP));
                    tooltip.append(icon);
                } else {
                    tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_NON_CONTAINER_TOOLTIP));
                }
            }
        }
    }

    @Override
    public void appendServerData(NbtCompound nbt, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof SkilletBlockEntity skillet) {
            skillet.writeDataToNbt(nbt);
            if (!skillet.getOutputStack().isEmpty()) {
                ItemConvertible requiredContainer = skillet.getRequiredContainer();

                nbt.putBoolean("ContainerTooltip", requiredContainer != null);
                if (requiredContainer != null) {
                    nbt.putString("Container", Registries.ITEM.getId(requiredContainer.asItem()).toString());
                }
            }
        }
    }

    @Override
    public Identifier getUid() {
        return PeonyJadePlugin.SKILLET_ID;
    }

    private int getIntData(NbtCompound nbt, String key) {
        if (nbt.contains(key)) {
            return nbt.getInt(key);
        } else {
            return 0;
        }
    }
}
