package net.george.peony.compat.rei.widghts;

import me.shedaniel.math.Dimension;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import net.george.peony.api.fluid.FluidRecord;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public class FluidLabelWidget extends WidgetWithBounds {
    private final Point point;
    private final FluidRecord fluid;
    private final TextRenderer textRenderer;

    public FluidLabelWidget(Point point, FluidRecord fluid) {
        this.point = point;
        this.fluid = fluid;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.point, new Dimension(100, 12));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int barWidth = 100;
        int barHeight = 12;
        int x = this.point.x;
        int y = this.point.y;

        Text displayText = buildDisplayText();
        int textX = x + (barWidth - this.textRenderer.getWidth(displayText)) / 2;
        int textY = y + (barHeight - 8) / 2;

        context.drawText(
                this.textRenderer,
                displayText,
                textX, textY, 0xFFFFFF,
                false
        );
    }

    private Text buildDisplayText() {
        String strippedName = this.fluid.getFluidDisplayName().getString()
                .replaceAll("§[0-9a-fk-or]", "");

        MutableText fluidNameText = Text.literal(strippedName + ": ");

        MutableText amountText = Text.literal(formatAmount(this.fluid.getAmount()) + " / " +
                formatAmount(this.fluid.getTotalCapacity()));

        return fluidNameText.append(amountText);
    }

    private String formatAmount(long amount) {
        if (amount < 1000) {
            return amount + " mB";
        } else if (amount < 1_000_000) {
            return String.format("%.2f B", amount / 1000.0);
        } else {
            return String.format("%.2f kB", amount / 1_000_000.0);
        }
    }

    @Override
    public List<? extends Element> children() {
        return Collections.emptyList();
    }
}
