package net.george.peony.util.math;

@SuppressWarnings("unused")
public class GuiHelper {
    public static final int FONT_WIDTH = 5;
    public static final int FONT_HEIGHT = 7;

    /**
     * @param surfaceSize the x or y length of the parent surface where the widget is located
     * @param widgetSize the x or y length of the widget
     * @return the center position of the widget on the parent surface
     */
    public static int calculateCenterPos(int surfaceSize, int widgetSize) {
        return (surfaceSize - widgetSize) / 2;
    }

    public static int calculateTextCenterPos(int surfaceSize, int textLength, int singleTextSize) {
        return calculateCenterPos(surfaceSize, (singleTextSize * textLength) + (textLength - 1));
    }

    public static int calculateTextCenterX(int surfaceSize, int textLength) {
        return calculateTextCenterPos(surfaceSize, textLength, FONT_WIDTH);
    }

    public static int calculateTextCenterY(int surfaceSize, int textLength) {
        return calculateTextCenterPos(surfaceSize, textLength, FONT_HEIGHT);
    }
}
