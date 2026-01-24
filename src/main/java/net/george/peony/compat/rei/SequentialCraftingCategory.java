package net.george.peony.compat.rei;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Label;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.george.peony.Peony;
import net.george.peony.api.action.Action;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.data.CraftingSteps;
import net.george.peony.compat.rei.widghts.WidgetsGroup;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class SequentialCraftingCategory implements DisplayCategory<SequentialCraftingDisplay> {
    public static final Identifier TEXTURE_ID = Peony.id("textures/gui/sequential_crafting_gui.png");

    @Override
    public CategoryIdentifier<SequentialCraftingDisplay> getCategoryIdentifier() {
        return PeonyREIPlugin.SEQUENTIAL_CRAFTING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable(PeonyTranslationKeys.REI_CATEGORY_SEQUENTIAL_CRAFTING);
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(PeonyBlocks.OAK_CUTTING_BOARD);
    }

    @Override
    public int getDisplayWidth(SequentialCraftingDisplay display) {
        return 8 + (display.getSteps().size() * (58 + 6)) + (22 + 8 + 18) + 8;
    }

    @Override
    public int getDisplayHeight() {
        return 130;
    }

    @Override
    public List<Widget> setupDisplay(SequentialCraftingDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        int startX = bounds.getMinX();
        int startY = bounds.getMinY();

        widgets.add(Widgets.createRecipeBase(bounds));

        int stepX = startX + 8;
        int gap = 6;
        for (int index = 0; index < display.getSteps().size(); index++) {
            widgets.addAll(
                    createAtPoint(new Point(stepX, startY + 8), index, display.getSteps().get(index))
                            .createWidgets(display));
            stepX += 58 + gap;
        }

        widgets.add(Widgets.createArrow(new Point(stepX, startY + 47)));
        widgets.add(Widgets.createSlot(new Point(stepX + 22 + 8, startY + 47))
                .entries(display.getOutputEntries().getFirst())
                .markOutput());

        return widgets;
    }

    protected static WidgetsGroup<SequentialCraftingDisplay> createAtPoint(Point placementPoint, int index, CraftingSteps.Step step) {
        return WidgetsGroup.createGroup(placementPoint, (display, point) -> {
            TextRenderer text = MinecraftClient.getInstance().textRenderer;
            List<Widget> widgets = Lists.newArrayList();
            Action action = step.getAction();
            int pointX = point.x;
            int pointY = point.y;
            boolean noIngredient = step.getIngredient().test(PeonyItems.PLACEHOLDER.getDefaultStack());

            widgets.add(Widgets.createTexturedWidget(TEXTURE_ID, pointX, pointY, noIngredient ? 58 : 0, 0,
                    58, noIngredient ? 95 : 114, 128, 128));
            widgets.add(createCenteredLabel(text, new Point(pointX + 9, pointY + 9),
                    Text.translatable(PeonyTranslationKeys.REI_STEP, index + 1)));

            if (noIngredient) {
                widgets.add(createCenteredLabel(text, new Point(pointX + 9, pointY + 21),
                        Text.translatable(PeonyTranslationKeys.REI_NO_INGREDIENTS)));
                widgets.add(createCenteredLabel(text, new Point(pointX + 9, pointY + 33),
                        Text.translatable(PeonyTranslationKeys.REI_ACTION)));
                widgets.add(createCenteredLabel(text, new Point(pointX + 9, pointY + 45),
                        Text.translatable(action.getType().createTranslationKey())));
            } else {
                widgets.add(createCenteredLabel(text, new Point(pointX + 9, pointY + 21),
                        Text.translatable(PeonyTranslationKeys.REI_REQUIRED_INGREDIENTS)));
                widgets.add(Widgets.createSlot(new Point(pointX + 21, pointY + 32))
                        .entries(display.getInputEntries().get(index))
                        .markInput());
                widgets.add(createCenteredLabel(text, new Point(pointX + 9, pointY + 52),
                        Text.translatable(PeonyTranslationKeys.REI_ACTION)));
                widgets.add(createCenteredLabel(text, new Point(pointX + 9, pointY + 64),
                        Text.translatable(action.getType().createTranslationKey())));
            }

            int width = action.getType().getGuide().getRight().getWidth();
            int height = action.getType().getGuide().getRight().getHeight();

            widgets.add(Widgets.createTexturedWidget(action.getType().getGuide().getLeft(),
                    pointX + 14 + MathHelper.floor((float) (32 - width) / 2),
                    pointY + (noIngredient ? 57 : 76) + MathHelper.floor((float) (32 - height) / 2),
                    0, 0,
                    width,
                    height,
                    32, 32));

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
