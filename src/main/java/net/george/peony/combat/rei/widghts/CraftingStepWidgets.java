package net.george.peony.combat.rei.widghts;

import me.shedaniel.math.Dimension;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.george.peony.Peony;
import net.george.peony.block.data.CraftingSteps;
import net.george.peony.combat.rei.SequentialCraftingDisplay;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.math.GuiHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CraftingStepWidgets {
    public static final Identifier TEXTURE = Peony.id("textures/gui/sequential_crafting_gui.png");
    protected final EntryIngredient ingredient;
    protected final CraftingSteps.Procedure procedure;

    protected CraftingStepWidgets(EntryIngredient ingredient, CraftingSteps.Procedure procedure) {
        this.ingredient = ingredient;
        this.procedure = procedure;
    }

    public static CraftingStepWidgets create(EntryIngredient ingredient, CraftingSteps.Procedure procedure) {
        return new CraftingStepWidgets(ingredient, procedure);
    }

    public static List<List<Widget>> createAll(SequentialCraftingDisplay display, Point basePoint) {
        List<List<Widget>> result = new ArrayList<>();
        List<CraftingSteps.Step> steps = display.getSteps();
        List<EntryIngredient> inputEntries = display.getInputEntries();
        int size = inputEntries.size();

        if (steps.size() != size) {
            Peony.LOGGER.warn("The length of the list Steps and the list InputEntries are different! The length of the list InputEntries will be used!");
        }
        for (int index = 0; index < size; index++) {
            CraftingStepWidgets widgets = create(inputEntries.get(index), steps.get(index).getProcedure());
            Point point = getPoint(basePoint, index);
            result.add(widgets.toWidgetListWithIndex(point, index));
        }
        return result;
    }

    protected static Point getPoint(Point basePoint, int index) {
        return new Point(basePoint.x + 5 + (index * 38) + (index * 5), basePoint.y + 5);
    }

    public static Widget createProcedureWidget(CraftingSteps.Procedure procedure, Point point) {
        return Widgets.createTexturedWidget(procedure.getTextureId(), point.x, point.y, 0, 0, 16, 16, 16, 16);
    }

    public static Widget createGuideWidget(CraftingSteps.Procedure procedure, Point point) {
        int guideWidth = procedure.getGuideSize().getWidth();
        int guideHeight = procedure.getGuideSize().getHeight();
        return Widgets.createTexturedWidget(procedure.getGuideTextureId(),
                point.x + GuiHelper.calculateCenterPos(32, guideWidth),
                point.y + GuiHelper.calculateCenterPos(32, guideHeight),
                0, 0, guideWidth, guideHeight, 32, 32);
    }

    public static int calculateTotalWidth(SequentialCraftingDisplay display) {
        return (display.getSteps().size() * 38) + ((display.getSteps().size() - 1) * 5);
    }

    public List<Widget> toWidgetListWithIndex(Point point, int index) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(SingleIntegerWidget.create(index + 1, new Point(
                point.x + GuiHelper.calculateTextCenterX(38, String.valueOf(index + 1).length()),
                point.y
        )));
        widgets.addAll(this.toWidgetList(
                new Point(point.x, point.y + 10)));
        return widgets;
    }

    public List<Widget> toWidgetList(Point point) {
        List<Widget> widgets = new ArrayList<>();

        if (this.ingredient.getFirst().getValue() instanceof ItemStack stack && !stack.isOf(PeonyItems.PLACEHOLDER)) {
            Point procedurePoint = new Point(point.x + 11, point.y + 34);

            widgets.add(Widgets.createTexturedWidget(TEXTURE, point.x, point.y, 0, 0, 38, 86));
            widgets.add(Widgets.createSlot(new Point(point.x + 11, point.y + 11)).entries(this.ingredient));
            widgets.add(createProcedureWidget(this.procedure, procedurePoint));
            widgets.add(createGuideWidget(this.procedure, new Point(point.x + 4, point.y + 52)));

            widgets.add(Widgets.createTooltip(new Rectangle(procedurePoint, new Dimension(16, 16)),
                    Text.translatable(this.procedure.getTranslationKey()),
                    Text.literal(StringUtils.capitalize(this.procedure.getId().getNamespace())).formatted(Formatting.BLUE, Formatting.ITALIC)
            ));
        } else {
            Point procedurePoint = new Point(point.x + 11, point.y + 7);

            widgets.add(Widgets.createTexturedWidget(TEXTURE, point.x, point.y, 38, 0, 38, 60));
            widgets.add(createProcedureWidget(this.procedure, procedurePoint));
            widgets.add(createGuideWidget(this.procedure, new Point(point.x + 4, point.y + 24)));
        }

        return widgets;
    }
}
