package net.george.peony.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import net.minecraft.util.math.random.Random;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface Range {
    MapCodec<Range> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("min").forGetter(Range::getMin),
            Codec.INT.fieldOf("max").forGetter(Range::getMax)
    ).apply(instance, Impl::new));

    static Range create(int min, int max) {
        return new Impl(min, max);
    }

    int getMin();

    int getMax();

    int averageValue();

    int randomlyValue();

    int[] toArray();

    IntCollection toIntCollection();

    IntArrayList toList();

    Stream<Integer> stream();

    IntStream intStream();

    class Impl implements Range {
        protected final int min;
        protected final int max;
        protected final Random random;

        protected Impl(int min, int max) {
            this.min = min;
            this.max = max;
            this.random = Random.create();
        }

        @Override
        public int getMin() {
            return this.min;
        }

        @Override
        public int getMax() {
            return this.max;
        }

        @Override
        public int averageValue() {
            return (this.min + this.max) / 2;
        }

        @Override
        public int randomlyValue() {
            return this.random.nextBetween(this.min, this.max);
        }

        @Override
        public int[] toArray() {
            return this.intStream().toArray();
        }

        @Override
        public IntCollection toIntCollection() {
            return IntCollections.asCollection(this.toList());
        }

        @Override
        public IntArrayList toList() {
            return new IntArrayList(this.stream().toList());
        }

        @Override
        public Stream<Integer> stream() {
            return this.intStream().boxed();
        }

        @Override
        public IntStream intStream() {
            return IntStream.rangeClosed(this.min, this.max);
        }
    }
}
