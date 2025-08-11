package net.george.peony.api.heat;

import net.george.peony.util.math.Range;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class HeatCalculationUtils {
    public static final Heat HEAT = Heat.create(Range.create(500, 600), HeatLevel.HIGH);
    public static final HeatProvider DEFAULT_PROVIDER = () -> HEAT;
    public static final float REFERENCE_TEMPERATURE = 550F;
    public static final float TEMPERATURE_EFFECT_FACTOR = 0.005F;
    public static final float MIN_TIME_FACTOR = 0.3F;
    public static final float MAX_TIME_FACTOR = 3.0F;

    @Nullable
    public static HeatProvider get(BlockState state) {
        return state.getBlock() instanceof HeatProvider source ? source : null;
    }

    public static HeatProvider getOr(BlockState state, HeatProvider other) {
        @Nullable HeatProvider provider = get(state);
        return provider == null ? other : provider;
    }

    public static HeatProvider getOrDefault(BlockState state) {
        return getOr(state, DEFAULT_PROVIDER);
    }

    /**
     * Calculates the actual heating time
     *
     * @param baseTime: Base time (in game ticks)
     * @param provider: Heat provider
     * @return Actual required game ticks. If the heat source cannot to heat, returns -1
     */
    public static int calculateHeatingTime(int baseTime, HeatProvider provider) {
        if (!provider.getLevel().canHeatItems()) {
            return -1;
        }

        // 1. Get the temperature range and calculate the average temperature
        Range temperatureRange = provider.getTemperature();
        float averaged = temperatureRange.averageValue();

        // 2. Calculate temperature impact factor
        float temperatureEffect = 1.0f + (REFERENCE_TEMPERATURE - averaged) * TEMPERATURE_EFFECT_FACTOR;

        // 3. Time reduction brought by obtaining thermal level
        // 4. Calculate the total time coefficient
        float totalFactor = temperatureEffect * (1.0f - provider.getLevel().getTimeReduction());
        totalFactor = MathHelper.clamp(totalFactor, MIN_TIME_FACTOR, MAX_TIME_FACTOR);
        return Math.round(baseTime * totalFactor);
    }
}
