package net.george.peony.compat.rei;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.george.peony.Peony;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.compat.rei.widghts.CraftingStepWidgets;
import net.george.peony.util.PeonyTranslationKeys;
import net.george.peony.util.math.GuiHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class SequentialCraftingCategory implements DisplayCategory<SequentialCraftingDisplay> {
    public static final CategoryIdentifier<SequentialCraftingDisplay> SEQUENTIAL_CRAFTING =
            CategoryIdentifier.of(Peony.id("sequential_crafting"));
    public static final Identifier TEXTURE = Peony.id("textures/gui/sequential_crafting_gui.png");

    @Override
    public CategoryIdentifier<SequentialCraftingDisplay> getCategoryIdentifier() {
        return SEQUENTIAL_CRAFTING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable(PeonyTranslationKeys.SEQUENTIAL_CRAFTING_RECIPE_CATEGORY_TITLE);
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(PeonyBlocks.OAK_CUTTING_BOARD);
    }

    @Override
    public List<Widget> setupDisplay(SequentialCraftingDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - (this.getDisplayWidth(display) / 2), bounds.getCenterY() - (this.getDisplayHeight() / 2));
        List<Widget> widgets = Lists.newArrayList();

        widgets.add(Widgets.createRecipeBase(bounds));

        int width = CraftingStepWidgets.calculateTotalWidth(display);
        int beginX = startPoint.x + GuiHelper.calculateCenterPos(this.getDisplayWidth(display), width + 5 + 22 + 5 + 18) - 5;

        for (List<Widget> step : CraftingStepWidgets.createAll(display, new Point(beginX, startPoint.y))) {
            widgets.addAll(step);
        }
        widgets.add(Widgets.createTexturedWidget(TEXTURE,
                beginX + width + 5 + 5, startPoint.y + GuiHelper.calculateCenterPos(96, 15),
                0, 86, 22, 15));
        widgets.add(Widgets.createSlot(
                new Point(beginX + width + 5 + 5 + 22 + 5, startPoint.y + GuiHelper.calculateCenterPos(96, 18)))
                .entries(display.getOutputEntries().getFirst()));
        return widgets;
    }

    @Override
    public int getDisplayWidth(SequentialCraftingDisplay display) {
        return 15 + CraftingStepWidgets.calculateTotalWidth(display) + 5 + 22 + 5 + 18 + 5;
    }

    @Override
    public int getDisplayHeight() {
        return 110;
    }
}
