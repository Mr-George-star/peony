package net.george.peony.api.heat;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.george.peony.util.math.Range;
import net.minecraft.block.Block;

@FunctionalInterface
public interface HeatProvider {
    Heat getHeat();

    default Range getTemperature() {
        return this.getHeat().getTemperature();
    }

    default HeatLevel getLevel() {
        return this.getHeat().getLevel();
    }

    static <B extends Block & HeatProvider> RecordCodecBuilder<B, Heat> createHeatCodec() {
        return Heat.CODEC.fieldOf("heat").forGetter(B::getHeat);
    }
}
