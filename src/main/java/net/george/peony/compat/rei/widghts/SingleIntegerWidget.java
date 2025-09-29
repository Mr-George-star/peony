package net.george.peony.compat.rei.widghts;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class SingleIntegerWidget extends Widget {
    private final int integer;
    private final Point position;
    private final int color;

    protected SingleIntegerWidget(int integer, Point position) {
        this(integer, position, 1325400064);
    }

    protected SingleIntegerWidget(int integer, Point position, int color) {
        this.integer = integer;
        this.position = position;
        this.color = color;
    }

    public static SingleIntegerWidget create(int integer, Point position) {
        return new SingleIntegerWidget(integer, position);
    }

    public static SingleIntegerWidget create(int integer, Point position, int color) {
        return new SingleIntegerWidget(integer, position, color);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        context.drawText(renderer, Text.literal(String.valueOf(this.integer)),
                this.position.x, this.position.y, this.color, false);
    }

    @Override
    public List<? extends Element> children() {
        return Collections.emptyList();
    }
}
