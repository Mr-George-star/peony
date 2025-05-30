package net.george.peony.combat.rei;

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
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class MillstoneCategory implements DisplayCategory<MillstoneDisplay> {
    public static final Identifier TEXTURE = Peony.id("textures/gui/millstone/millstone_gui.png");
    public static final CategoryIdentifier<MillstoneDisplay> MILLSTONE = CategoryIdentifier.of(Peony.id("millstone"));

    @Override
    public CategoryIdentifier<MillstoneDisplay> getCategoryIdentifier() {
        return MILLSTONE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable(PeonyTranslationKeys.MILLING_RECIPE_CATEGORY_TITLE);
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(PeonyBlocks.MILLSTONE.asItem().getDefaultStack());
    }

    @Override
    public List<Widget> setupDisplay(MillstoneDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 90, bounds.getCenterY() - 44);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        // millstone photo
        widgets.add(Widgets.createTexturedWidget(TEXTURE, startPoint.x + 48,  startPoint.y + 46, 0, 0, 30, 20, 64, 64));
        // rotation times tooltip
        widgets.add(Widgets.createTexturedWidget(TEXTURE, startPoint.x + 85, startPoint.y + 34, 22, 20, 9, 9, 64, 64));
        // rotation times tooltip
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            graphics.drawText(renderer,
                    Text.translatable(PeonyTranslationKeys.MILLING_RECIPE_MILLING_TIMES, display.getMillingTimes()),
                    startPoint.x + 100, startPoint.y + 35, 1325400064, false);
        }));
        // arrow
        widgets.add(Widgets.createTexturedWidget(TEXTURE, startPoint.x + 84, startPoint.y + 48, 0, 20, 22, 15, 64, 64));
        // input slot
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 54, startPoint.y + 20)).entries(display.getInputEntries().getFirst()).markInput());
        // output slot
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 116, startPoint.y + 48)).entries(display.getOutputEntries().getFirst()).markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 90;
    }
}
