package net.george.peony.compat.jade;

import net.george.peony.Peony;
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
        if (blockAccessor.getBlockEntity() instanceof SkilletBlockEntity skillet) {
            NbtCompound nbt = blockAccessor.getServerData();
            SkilletBlockEntity.CookingStates state;
            if (nbt.contains("State")) {
                try {
                    String stateName = nbt.getString("State");
                    state = SkilletBlockEntity.CookingStates.valueOf(stateName);
                    Peony.LOGGER.debug("Restored state from NBT: {}", stateName);
                } catch (IllegalArgumentException exception) {
                    state = SkilletBlockEntity.CookingStates.IDLE;
                    Peony.LOGGER.warn("Failed to restore state from NBT, defaulting to IDLE");
                }
            } else {
                state = SkilletBlockEntity.CookingStates.IDLE;
            }

            switch (state) {
                case OIL_PROCESSING -> {
                    int oilProcessingStage = this.getIntData(nbt, "OilProcessingStage");
                    int heatingTime = this.getIntData(nbt, "HeatingTime");
                    int requiredHeatingTime = this.getIntData(nbt, "RequiredHeatingTime");
                    if (oilProcessingStage == 0) {
                        int displayHeatingTime = Math.round((float) heatingTime / 20);
                        int displayRequiredHeatingTime = Math.round((float) requiredHeatingTime / 20);
                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_MELTING_OIL,
                                displayHeatingTime, displayRequiredHeatingTime));
                        if (heatingTime >= requiredHeatingTime) {
                            IElementHelper elements = IElementHelper.get();
                            IElement icon = elements.item(new ItemStack(PeonyItems.SPATULA), 0.5f).size(new Vec2f(10, 10)).translate(new Vec2f(0, -1));
                            tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_TOOL_USAGE_TOOLTIP));
                            tooltip.append(icon);
                        }
                    } else {
                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_CONTINUE, 0, 0));
                    }
                }
                case HEATING -> {
                    int heatingTime = this.getIntData(nbt, "HeatingTime");
                    int requiredHeatingTime = this.getIntData(nbt, "RequiredHeatingTime");
                    if (heatingTime > 0 && requiredHeatingTime > 0) {
                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_HEATING_TIME,
                                Math.round((float) heatingTime / 20), Math.round((float) requiredHeatingTime / 20)));
                    }
                }
                case COMMON_INGREDIENT_PROCESSING -> {
                    int commonIngredientStage = this.getIntData(nbt, "CommonIngredientStage");
                    if (commonIngredientStage == 0) {
                        int heatingTime = this.getIntData(nbt, "HeatingTime");
                        int requiredHeatingTime = this.getIntData(nbt, "RequiredHeatingTime");
                        int displayHeatingTime = Math.round((float) heatingTime / 20);
                        int displayRequiredHeatingTime = Math.round((float) requiredHeatingTime / 20);
                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_HEATING_TIME,
                                displayHeatingTime, displayRequiredHeatingTime));
                    } else {
                        int waitingTime = this.getIntData(nbt, "WaitingTime");
                        int displayWaitingTime = Math.round((float) waitingTime / 20);
                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_CONTINUE,
                                displayWaitingTime, 6));
                    }
                }
                case STIR_FRYING -> {
                    int stirFryingCount = this.getIntData(nbt, "StirFryingCount");
                    int requiredStirFryingCount = this.getIntData(nbt, "RequiredStirFryingCount");
                    tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_STIR_FRYING_COUNT,
                            requiredStirFryingCount, stirFryingCount));
                }
                case OVERFLOW -> {
                    int overflowTime = this.getIntData(nbt, "OverflowTime");
                    int maxOverflowTime = this.getIntData(nbt, "MaxOverflowTime");
                    tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_OVERFLOW_TIME,
                                    Math.round((float) overflowTime / 20), Math.round((float) maxOverflowTime / 20))
                            .formatted(Formatting.RED));
                }
                case COMPLETED, FAILED -> {
                    if (nbt.getBoolean("ContainerTooltip") && nbt.contains("Container")) {
                        Item container = Registries.ITEM.get(Identifier.tryParse(nbt.getString("Container")));
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
    }

    @Override
    public void appendServerData(NbtCompound nbt, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof SkilletBlockEntity skillet) {
            skillet.context.writeNbt(nbt, blockAccessor.getLevel().getRegistryManager());
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
