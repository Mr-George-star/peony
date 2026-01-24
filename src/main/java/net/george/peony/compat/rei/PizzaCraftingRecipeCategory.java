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
import net.george.peony.Peony;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.compat.rei.widghts.WidgetsGroup;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PizzaCraftingRecipeCategory implements DisplayCategory<PizzaCraftingRecipeDisplay> {
    public static final Identifier TEXTURE_ID = Peony.id("textures/gui/pizza_crafting_gui.png");

    @Override
    public CategoryIdentifier<PizzaCraftingRecipeDisplay> getCategoryIdentifier() {
        return PeonyREIPlugin.PIZZA_CRAFTING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable(PeonyTranslationKeys.REI_CATEGORY_PIZZA_CRAFTING);
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(PeonyBlocks.RAW_MARGHERITA_PIZZA);
    }

    @Override
    public int getDisplayHeight() {
        return 86;
    }

    @Override
    public int getDisplayWidth(PizzaCraftingRecipeDisplay display) {
        return 126;
    }

    @Override
    public List<Widget> setupDisplay(PizzaCraftingRecipeDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        int startX = bounds.getMinX();
        int startY = bounds.getMinY();

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.addAll(createCraftingArea(new Point(startX + 9, startY + 9))
                .createWidgets(display));
        widgets.add(Widgets.createArrow(new Point(startX + 71, startY + 29)));
        widgets.add(Widgets.createSlot(new Point(startX + 101, startY + 28))
                .entries(display.getOutputEntries().getFirst())
                .markOutput());
        widgets.add(Widgets.createTexturedWidget(TEXTURE_ID, startX + 14, startY + 66,
                0, 0, 45, 15, 64, 64));

        return widgets;
    }

    protected static WidgetsGroup<PizzaCraftingRecipeDisplay> createCraftingArea(Point placementPoint) {
        return WidgetsGroup.createGroup(placementPoint, (display, point) -> {
            List<Widget> widgets = Lists.newArrayList();
            int pointX = point.x;
            int pointY = point.y;

            widgets.add(Widgets.createSlot(new Point(pointX, pointY)).markInput());
            widgets.add(Widgets.createSlot(new Point(pointX + 18, pointY)).markInput());
            widgets.add(Widgets.createSlot(new Point(pointX + 36, pointY)).markInput());
            widgets.add(Widgets.createSlot(new Point(pointX, pointY + 18)).markInput());
            widgets.add(Widgets.createSlot(new Point(pointX + 36, pointY + 18)).markInput());
            widgets.add(Widgets.createSlot(new Point(pointX, pointY + 36)).markInput());
            widgets.add(Widgets.createSlot(new Point(pointX + 18, pointY + 36)).markInput());
            widgets.add(Widgets.createSlot(new Point(pointX + 36, pointY + 36)).markInput());

            for (int index = 0; index < display.getInputEntries().size(); index++) {
                ((Slot) widgets.get(index)).entries(display.getInputEntries().get(index));
            }

            return widgets;
        });
    }
}
