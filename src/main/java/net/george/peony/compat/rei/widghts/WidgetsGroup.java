package net.george.peony.compat.rei.widghts;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.display.Display;

import java.util.List;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public interface WidgetsGroup<T extends Display> {
    static <T extends Display> WidgetsGroup<T> createGroup(Point placementPoint, BiFunction<T, Point, List<? extends Widget>> widgetsFunction) {
        return new WidgetsGroup<>() {
            @Override
            public Point getOrigin() {
                return placementPoint;
            }

            @Override
            public List<? extends Widget> createWidgets(T display) {
                return widgetsFunction.apply(display, this.getOrigin());
            }
        };
    }

    Point getOrigin();

    List<? extends Widget> createWidgets(T display);
}
