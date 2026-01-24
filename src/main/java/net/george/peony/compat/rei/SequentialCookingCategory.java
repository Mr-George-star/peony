package net.george.peony.compat.rei;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Label;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.george.peony.Peony;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.compat.rei.widghts.WidgetsGroup;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class SequentialCookingCategory implements DisplayCategory<SequentialCookingDisplay> {
    public static final Identifier TEXTURE_ID = Peony.id("textures/gui/sequential_cooking_gui.png");

    @Override
    public CategoryIdentifier<SequentialCookingDisplay> getCategoryIdentifier() {
        return PeonyREIPlugin.SEQUENTIAL_COOKING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable(PeonyTranslationKeys.REI_CATEGORY_SEQUENTIAL_COOKING);
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(PeonyBlocks.SKILLET.asItem().getDefaultStack());
    }

    @Override
    public int getDisplayWidth(SequentialCookingDisplay display) {
        return 8 + (display.getSteps().size() * (58 + 6)) + (22 + 8 + 18) + (!display.getContainer().isEmpty() ? 18 : 0) + 8;
    }

    @Override
    public int getDisplayHeight() {
        return 124;
    }

    @Override
    public List<Widget> setupDisplay(SequentialCookingDisplay display, Rectangle bounds) {
        TextRenderer text = MinecraftClient.getInstance().textRenderer;
        List<Widget> widgets = new ArrayList<>();
        int startX = bounds.getMinX();
        int startY = bounds.getMinY();

        widgets.add(Widgets.createRecipeBase(bounds));

        Text title = Text.translatable(PeonyTranslationKeys.REI_TEMPERATURE, display.getTemperature())
                .formatted(Formatting.GOLD);
        widgets.add(Widgets.createLabel(new Point(
                startX + MathHelper.floor((float) (this.getDisplayWidth(display) - text.getWidth(title)) / 2),
                                bounds.getY() + 6), title)
                .noShadow().leftAligned().color(-12566464, -4473925));

        int stepX = startX + 8;
        int gap = 6;
        for (SequentialCookingDisplay.StepInfo step : display.getSteps()) {
            widgets.addAll(
                    createAtPoint(new Point(stepX, startY + 16), step)
                            .createWidgets(display));
            stepX += 58 + gap;
        }

        widgets.add(Widgets.createArrow(new Point(stepX, startY + 55)));
        widgets.add(Widgets.createSlot(new Point(stepX + 22 + 8, startY + 55))
                .entry(display.getOutput())
                .markOutput());
        if (!display.getContainer().isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(stepX + 22 + 8 + 18, startY + 55))
                    .entries(display.getContainer()));
            widgets.add(Widgets.createLabel(new Point(stepX + 8, startY + 76),
                    Text.translatable(PeonyTranslationKeys.REI_REQUIRES_CONTAINER)
            ).noShadow().leftAligned().color(-12566464, -4473925));
        } else {
            widgets.add(Widgets.createLabel(new Point(stepX, startY + 76),
                    Text.translatable(PeonyTranslationKeys.REI_NO_CONTAINER)
            ).noShadow().leftAligned().color(-12566464, -4473925));
        }
        return widgets;
    }

    protected static WidgetsGroup<SequentialCookingDisplay> createAtPoint(Point placementPoint, SequentialCookingDisplay.StepInfo step) {
        return WidgetsGroup.createGroup(placementPoint, (display, point) -> {
            List<EntryIngredient> ingredients = step.ingredients();
            Preconditions.checkState(ingredients.size() <= 2 && !ingredients.isEmpty(),
                    "The size should be less than 2 or equal 2!");
            TextRenderer text = MinecraftClient.getInstance().textRenderer;
            List<Widget> widgets = Lists.newArrayList();
            int pointX = point.x;
            int pointY = point.y;

            widgets.add(Widgets.createTexturedWidget(TEXTURE_ID, pointX, pointY, 0, 0,
                    58, 100, 128, 128));

            widgets.add(createCenteredLabel(text, new Point(pointX + 9, pointY + 9),
                    Text.translatable(PeonyTranslationKeys.REI_STEP, step.index())));
            widgets.add(createCenteredLabel(text, new Point(pointX + 9, pointY + 21),
                    Text.translatable(PeonyTranslationKeys.REI_REQUIRED_INGREDIENTS)));

            if (ingredients.size() == 1) {
                widgets.add(Widgets.createSlot(new Point(pointX + 21, pointY + 32))
                        .entries(step.ingredients().getFirst())
                        .markInput());
            } else {
                widgets.add(Widgets.createSlot(new Point(pointX + 12, pointY + 32))
                        .entries(step.ingredients().getFirst())
                        .markInput());
                widgets.add(Widgets.createSlot(new Point(pointX + 30, pointY + 32))
                        .entries(step.ingredients().get(1))
                        .markInput());
            }
            widgets.add(createCenteredLabel(text, new Point(pointX + 9, pointY + 52),
                    Text.translatable(PeonyTranslationKeys.REI_REQUIRED_TIME, (step.requiredTime()) / 20)));
            widgets.add(createCenteredLabel(text, new Point(pointX + 9, pointY + 64),
                    switch (step.cookingType()) {
                        case HEATING -> Text.translatable(PeonyTranslationKeys.REI_HEATING);
                        case STIR -> Text.translatable(PeonyTranslationKeys.REI_STIR_FRYING, step.fryingData().times());
                    }));

            if (!step.requiredTool().isEmpty()) {
                widgets.add(Widgets.createSlot(new Point(pointX + 21, pointY + 76))
                        .entries(step.requiredTool()));
            }

            return widgets;
        });
    }

    protected static Label createCenteredLabel(TextRenderer textRenderer, Point expectedPoint, Text text) {
        int textWidth = textRenderer.getWidth(text);
        return Widgets.createLabel(
                new Point(expectedPoint.x + MathHelper.floor((float) (42 - textWidth) / 2), expectedPoint.y),
                text
        ).noShadow().leftAligned().color(-12566464, -4473925);
    }
}
