package net.george.peony.compat.rei;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.george.peony.api.fluid.FluidRecord;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.compat.rei.widghts.FluidLabelWidget;
import net.george.peony.compat.rei.widghts.WidgetsGroup;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class BrewingRecipeCategory implements DisplayCategory<BrewingRecipeDisplay> {
    @Override
    public CategoryIdentifier<BrewingRecipeDisplay> getCategoryIdentifier() {
        return PeonyREIPlugin.BREWING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable(PeonyTranslationKeys.REI_CATEGORY_BREWING);
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(PeonyBlocks.BREWING_BARREL.asItem().getDefaultStack());
    }

    @Override
    public int getDisplayHeight() {
        return 72 + 11 + 2;
    }

    @Override
    public int getDisplayWidth(BrewingRecipeDisplay display) {
        return 144 - (display.getContainer() == null ? 18 : 0);
    }

    @Override
    public List<Widget> setupDisplay(BrewingRecipeDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        int startX = bounds.getMinX();
        int startY = bounds.getMinY();

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createLabel(new Point(startX + 9, startY + 6),
                        Text.translatable(PeonyTranslationKeys.REI_BREWING_TIMES, display.getBrewingTime()))
                .noShadow().leftAligned().color(-12566464, -4473925));
        widgets.add(new FluidLabelWidget(new Point(startX + 9, startY + 17), FluidRecord.record(
                display.getBasicFluid(), FluidConstants.BUCKET
        )));
        widgets.add(Widgets.createLabel(new Point(startX + 9, startY + 17 + 11 + 3),
                        Text.translatable(display.getContainer() == null ? PeonyTranslationKeys.REI_NO_CONTAINER : PeonyTranslationKeys.REI_REQUIRES_CONTAINER))
                .noShadow().leftAligned().color(-12566464, -4473925));
        widgets.addAll(createCraftingArea(new Point(startX + 9, startY + 28 + 11 + 3))
                .createWidgets(display));
        widgets.add(Widgets.createArrow(new Point(startX + 71, startY + 29 + 11 + 3)));
        widgets.add(Widgets.createSlot(new Point(startX + 101, startY + 28 + 11 + 3))
                .entries(display.getOutputEntries().getFirst())
                .markOutput());

        if (display.getContainer() != null) {
            widgets.add(Widgets.createSlot(new Point(startX + 119, startY + 28 + 11 + 3))
                    .entry(EntryStacks.of(display.getContainer())));
        }

        return widgets;
    }

    protected static WidgetsGroup<BrewingRecipeDisplay> createCraftingArea(Point placementPoint) {
        return WidgetsGroup.createGroup(placementPoint, (display, point) -> {
            List<Widget> widgets = Lists.newArrayList();
            int pointX = point.x;
            int pointY = point.y;

            widgets.add(Widgets.createSlot(point).markInput());
            widgets.add(Widgets.createSlot(new Point(pointX + 19, pointY)).markInput());
            widgets.add(Widgets.createSlot(new Point(pointX + 37, pointY)).markInput());
            widgets.add(Widgets.createSlot(new Point(pointX + 10, pointY + 18)).markInput());
            widgets.add(Widgets.createSlot(new Point(pointX + 28, pointY + 18)).markInput());

            for (int index = 0; index < display.getInputEntries().size(); index++) {
                ((Slot) widgets.get(index)).entries(display.getInputEntries().get(index));
            }

            return widgets;
        });
    }
}
