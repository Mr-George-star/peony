package net.george.peony.util.math;

import java.util.Objects;

@SuppressWarnings("unused")
public class Size {
    protected final int width;
    protected final int height;

    protected Size() {
        this(16, 16);
    }

    protected Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static Size create() {
        return new Size();
    }

    public static Size create(int width, int height) {
        return new Size(width, height);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || getClass() != another.getClass()) {
            return false;
        }
        Size size = (Size) another;
        return this.width == size.width && this.height == size.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.width, this.height);
    }

    @Override
    public String toString() {
        return this.width + "x" + this.height;
    }
}
