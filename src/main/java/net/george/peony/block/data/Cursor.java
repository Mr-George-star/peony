package net.george.peony.block.data;

import net.george.peony.util.math.Range;
import net.minecraft.nbt.NbtCompound;

public interface Cursor {
    static Cursor create(int minIndex, int maxIndex, String name) {
        return create(Range.create(minIndex, maxIndex), name);
    }

    static Cursor create(Range range, String name) {
        return new Cursor() {
            private int cursoringIndex = range.getMin();

            @Override
            public Range getRange() {
                return range;
            }

            @Override
            public int getCursoringIndex() {
                return this.cursoringIndex;
            }

            @Override
            public void next() {
                this.cursoringIndex++;
            }

            @Override
            public void previous() {
                this.cursoringIndex--;
            }

            @Override
            public boolean overflowing() {
                return this.cursoringIndex >= range.getMax();
            }

            @Override
            public void reset() {
                this.cursoringIndex = range.getMin();
            }

            @Override
            public void writeNbt(NbtCompound nbt) {
                nbt.putInt(name + "CursoringIndex", this.cursoringIndex);
            }

            @Override
            public void readNbt(NbtCompound nbt) {
                this.cursoringIndex = nbt.getInt(name + "CursoringIndex");
            }
        };
    }

    Range getRange();

    int getCursoringIndex();

    void next();

    void previous();

    boolean overflowing();

    void reset();

    void writeNbt(NbtCompound nbt);

    void readNbt(NbtCompound nbt);
}
