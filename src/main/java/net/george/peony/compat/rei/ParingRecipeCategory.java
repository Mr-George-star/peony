package net.george.peony.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ParingRecipeCategory implements DisplayCategory<ParingRecipeDisplay> {
    @Override
    public CategoryIdentifier<ParingRecipeDisplay> getCategoryIdentifier() {
        return PeonyREIPlugin.PARING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable(PeonyTranslationKeys.REI_CATEGORY_PARING);
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(PeonyItems.IRON_PARING_KNIFE.getDefaultStack());
    }

    @Override
    public int getDisplayWidth(ParingRecipeDisplay display) {
        return 88;
    }

    @Override
    public int getDisplayHeight() {
        return 48;
    }

    @Override
    public List<Widget> setupDisplay(ParingRecipeDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        int startX = bounds.getMinX();
        int startY = bounds.getMinY();

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createSlot(new Point(startX + 8, startY + 18))
                .entries(display.getInputEntries().getFirst())
                .markInput());
        widgets.add(Widgets.createArrow(new Point(startX + 34, startY + 19)));
        widgets.add(Widgets.createSlot(new Point(startX + 64, startY + 18))
                .entries(display.getOutputEntries().getFirst())
                .markOutput());

        return widgets;
    }
}
