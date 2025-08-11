package net.george.peony.api.heat;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.util.math.Range;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("unused")
public interface Heat {
    MapCodec<Heat> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Range.CODEC.fieldOf("temperature").forGetter(Heat::getTemperature),
                    HeatLevel.CODEC.fieldOf("level").forGetter(Heat::getLevel)
            ).apply(instance, Impl::new));

    Range getTemperature();

    HeatLevel getLevel();

    static Heat create(Range temperature, HeatLevel level) {
        return new Impl(temperature, level);
    }

    static int getDamage(Heat provider, int temperature) {
        return provider.getLevel().causesDamage() ? getDamage(temperature) : 0;
    }

    static int getDamage(int temperature) {
        return MathHelper.floor((float) temperature / 200);
    }

    @SuppressWarnings("ClassCanBeRecord")
    class Impl implements Heat {
        protected final Range temperature;
        protected final HeatLevel level;

        public Impl(Range temperature, HeatLevel level) {
            this.temperature = temperature;
            this.level = level;
        }

        @Override
        public Range getTemperature() {
            return this.temperature;
        }

        @Override
        public HeatLevel getLevel() {
            return this.level;
        }
    }
}
