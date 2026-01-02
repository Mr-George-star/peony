package net.george.peony.compat.jade;

import net.george.peony.block.data.CookingSteps;
import net.george.peony.block.entity.SkilletBlockEntity;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum SkilletComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig config) {
        if (blockAccessor.getBlockEntity() instanceof SkilletBlockEntity) {
            NbtCompound nbt = blockAccessor.getServerData();
            SkilletBlockEntity.CookingStates state;

            try {
                String stateName = nbt.getString("State");
                state = SkilletBlockEntity.CookingStates.valueOf(stateName);
            } catch (IllegalArgumentException exception) {
                state = SkilletBlockEntity.CookingStates.IDLE;
            }

            Text stateText = Text.translatable("jade.peony.state." + state.name().toLowerCase())
                    .formatted(Formatting.GOLD, Formatting.BOLD);
            tooltip.add(stateText);

            tooltip.add(Text.literal(" "));

            if (nbt.contains("CurrentStep") && nbt.contains("TotalSteps")) {
                int current = nbt.getInt("CurrentStep") + 1;
                int total = nbt.getInt("TotalSteps");
                tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_STEP, current, total)
                        .formatted(Formatting.GRAY));
            }

            switch (state) {
                case IDLE -> {
                    if (nbt.getBoolean("HasOil")) {
                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_HAS_OIL)
                                .formatted(Formatting.GREEN));
                    }
                    if (nbt.getBoolean("HasCommonIngredient")) {
                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_HAS_COMMON_INGREDIENT)
                                .formatted(Formatting.GREEN));
                    }
                }

                case OIL_PROCESSING -> {
                    int stage = this.getIntData(nbt, "OilProcessingStage");
                    if (stage == 0) {
                        int heatingTime = this.getIntData(nbt, "HeatingTime");
                        int requiredHeatingTime = this.getIntData(nbt, "RequiredHeatingTime");

                        this.addProgressBar(tooltip, heatingTime, requiredHeatingTime,
                                Text.translatable(PeonyTranslationKeys.JADE_SKILLET_MELTING_OIL));

                        if (heatingTime >= requiredHeatingTime) {
                            this.addToolUsageHint(tooltip, PeonyItems.SPATULA);
                        }
                    } else {
                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_ADD_INGREDIENT)
                                .formatted(Formatting.GREEN));
                    }
                }

                case COMMON_INGREDIENT_PROCESSING -> {
                    int stage = this.getIntData(nbt, "CommonIngredientStage");
                    if (stage == 0) {
                        int heatingTime = this.getIntData(nbt, "HeatingTime");
                        int requiredHeatingTime = this.getIntData(nbt, "RequiredHeatingTime");

                        this.addProgressBar(tooltip, heatingTime, requiredHeatingTime,
                                Text.translatable(PeonyTranslationKeys.JADE_SKILLET_PREPARING_INGREDIENT));
                    } else {
                        int waitingTime = this.getIntData(nbt, "WaitingTime");
                        int displayTime = 6 - Math.round((float) waitingTime / 20);
                        displayTime = Math.max(0, displayTime);

                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_CONTINUE,
                                displayTime, 6).formatted(Formatting.YELLOW));
                    }
                }

                case HEATING -> {
                    int heatingTime = this.getIntData(nbt, "HeatingTime");
                    int requiredHeatingTime = this.getIntData(nbt, "RequiredHeatingTime");

                    if (requiredHeatingTime > 0) {
                        this.addProgressBar(tooltip, heatingTime, requiredHeatingTime,
                                Text.translatable(PeonyTranslationKeys.JADE_SKILLET_HEATING));

                        int remainingTicks = Math.max(0, requiredHeatingTime - heatingTime);
                        int seconds = Math.round((float) remainingTicks / 20);
                        if (seconds > 0) {
                            tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_TIME_REMAINING, seconds)
                                    .formatted(Formatting.GRAY));
                        }
                    }
                }

                case STIR_FRYING -> {
                    int count = this.getIntData(nbt, "StirFryingCount");
                    int required = this.getIntData(nbt, "RequiredStirFryingCount");

                    this.addProgressBar(tooltip, count, required,
                            Text.translatable(PeonyTranslationKeys.JADE_SKILLET_STIR_FRYING));

                    int stirFryingTime = this.getIntData(nbt, "StirFryingTime");
                    int timeLimit = this.getIntData(nbt, "StirFryingTimeLimit");
                    if (timeLimit > 0) {
                        int remainingTime = Math.max(0, timeLimit - stirFryingTime);
                        int seconds = Math.round((float) remainingTime / 20);
                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_TIME_LIMIT, seconds)
                                .formatted(Formatting.RED));
                    }

                    this.addToolUsageHint(tooltip, PeonyItems.SPATULA);
                }

                case OVERFLOW -> {
                    int overflowTime = this.getIntData(nbt, "OverflowTime");
                    int maxOverflowTime = this.getIntData(nbt, "MaxOverflowTime");

                    tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_OVERFLOW_WARNING)
                            .formatted(Formatting.RED, Formatting.BOLD));

                    this.addProgressBar(tooltip, overflowTime, maxOverflowTime,
                            Text.translatable(PeonyTranslationKeys.JADE_SKILLET_OVERFLOW_TIME));

                    this.addToolUsageHint(tooltip, null);
                }

                case WAITING_FOR_INGREDIENT -> {
                    int countdown = this.getIntData(nbt, "IngredientPlacementCountdown");
                    int displayTime = Math.round((float) countdown / 20);

                    tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_WAITING_FOR_INGREDIENT,
                            displayTime).formatted(Formatting.YELLOW));

                    if (nbt.contains("RequiredIngredient")) {
                        String ingredientId = nbt.getString("RequiredIngredient");
                        Item ingredient = Registries.ITEM.get(Identifier.tryParse(ingredientId));
                        if (ingredient != Items.AIR) {
                            IElementHelper elements = IElementHelper.get();
                            IElement icon = elements.item(new ItemStack(ingredient), 0.75f)
                                    .size(new Vec2f(12, 12));
                            tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_REQUIRES)
                                    .append(": "));
                            tooltip.add(icon);
                        }
                    }
                }

                case COMPLETED -> {
                    tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_COMPLETED)
                            .formatted(Formatting.GREEN, Formatting.BOLD));

                    if (nbt.getBoolean("ContainerTooltip") && nbt.contains("Container")) {
                        Item container = Registries.ITEM.get(Identifier.tryParse(nbt.getString("Container")));
                        this.addContainerHint(tooltip, container);
                    } else {
                        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_READY_TO_EXTRACT)
                                .formatted(Formatting.GREEN));
                    }
                }

                case FAILED -> {
                    tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_FAILED)
                            .formatted(Formatting.RED, Formatting.BOLD));

                    if (nbt.getBoolean("ContainerTooltip") && nbt.contains("Container")) {
                        Item container = Registries.ITEM.get(Identifier.tryParse(nbt.getString("Container")));
                        this.addContainerHint(tooltip, container);
                    }
                }
            }

            boolean hasHeat = nbt.getBoolean("HasHeat");
            if (!hasHeat && state != SkilletBlockEntity.CookingStates.IDLE) {
                tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_NO_HEAT_SOURCE)
                        .formatted(Formatting.RED));
            }
        }
    }

    @Override
    public void appendServerData(NbtCompound nbt, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof SkilletBlockEntity skillet) {
            skillet.context.writeNbt(nbt, blockAccessor.getLevel().getRegistryManager());

            nbt.putBoolean("HasOil", skillet.context.hasOil);
            nbt.putBoolean("HasCommonIngredient", skillet.context.currentCommonIngredient != null);

            if (skillet.getCachedRecipe() != null) {
                CookingSteps steps = skillet.getCachedRecipe().value().getSteps();
                if (steps != null) {
                    nbt.putInt("CurrentStep", skillet.context.currentStepIndex);
                    nbt.putInt("TotalSteps", steps.getSteps().size());

                    if (skillet.context.currentStepIndex < steps.getSteps().size()) {
                        CookingSteps.Step currentStep = steps.getSteps().get(skillet.context.currentStepIndex);
                        for (ItemStack stack : currentStep.getIngredient().getMatchingStacks()) {
                            if (!stack.isEmpty()) {
                                nbt.putString("RequiredIngredient",
                                        Registries.ITEM.getId(stack.getItem()).toString());
                                break;
                            }
                        }
                    }
                }
            }

            if (skillet.context.state == SkilletBlockEntity.CookingStates.WAITING_FOR_INGREDIENT) {
                int remaining = skillet.context.countdownManager.getRemainingTicks("IngredientPlacement");
                nbt.putInt("IngredientPlacementCountdown", remaining);
            }

            if (skillet.context.state == SkilletBlockEntity.CookingStates.STIR_FRYING) {
                CookingSteps.Step step = skillet.context.getCurrentStep(skillet.getWorld());
                if (step != null) {
                    nbt.putInt("StirFryingTimeLimit", step.getRequiredTime());
                }
            }

            if (!skillet.getOutputStack().isEmpty()) {
                ItemConvertible requiredContainer = skillet.getRequiredContainer();
                nbt.putBoolean("ContainerTooltip", requiredContainer != null);
                if (requiredContainer != null) {
                    nbt.putString("Container",
                            Registries.ITEM.getId(requiredContainer.asItem()).toString());
                }
            }
        }
    }

    @Override
    public Identifier getUid() {
        return PeonyJadePlugin.SKILLET_ID;
    }

    private int getIntData(NbtCompound nbt, String key) {
        return nbt.contains(key) ? nbt.getInt(key) : 0;
    }

    private void addProgressBar(ITooltip tooltip, int current, int max, Text title) {
        IElementHelper elements = IElementHelper.get();

        if (max > 0) {
            float progress = Math.min(1.0f, (float) current / max);
            tooltip.add(elements.progress(progress,
                    title.copy().append(String.format(" (%d/%d)", current, max)),
                    elements.progressStyle().color(0xFF00FF00, 0xFF008800)
                            .textColor(0xFFFFFFFF), BoxStyle.getNestedBox(), true));
        } else {
            tooltip.add(title);
        }
    }

    private void addToolUsageHint(ITooltip tooltip, Item toolItem) {
        IElementHelper elements = IElementHelper.get();
        IElement icon;

        if (toolItem != null) {
            icon = elements.item(new ItemStack(toolItem), 0.5f)
                    .size(new Vec2f(10, 10)).translate(new Vec2f(0, -1));
        } else {
            icon = elements.text(Text.literal("⛏"));
        }

        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_TOOL_USAGE_TOOLTIP)
                .append(" "));
        tooltip.add(icon);
    }

    private void addContainerHint(ITooltip tooltip, Item container) {
        IElementHelper elements = IElementHelper.get();
        IElement icon = elements.item(new ItemStack(container), 0.5f)
                .size(new Vec2f(10, 10)).translate(new Vec2f(0, -1));

        tooltip.add(Text.translatable(PeonyTranslationKeys.JADE_SKILLET_CONTAINER_TOOLTIP)
                .append(" "));
        tooltip.add(icon);
    }
}
