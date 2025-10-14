package net.george.peony.api.heat;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;

public enum HeatLevel implements StringIdentifiable {
    NONE("none", 0, 0F),
    SMOLDERING("smoldering", 1, 0.05F),
    LOW("low", 2, 0.15F),
    HIGH("high", 3, 0.25F),
    BLAZING("blazing", 4, 0.35F);

    public static final Codec<HeatLevel> CODEC = StringIdentifiable.createBasicCodec(HeatLevel::values);
    private final String name;
    private final int intensity;
    private final float timeReduction;

    HeatLevel(String name, int intensity, float timeReduction) {
        this.name = name;
        this.intensity = intensity;
        this.timeReduction = timeReduction;
    }

    public int getIntensity() {
        return this.intensity;
    }

    public float getTimeReduction() {
        return this.timeReduction;
    }

    public boolean canHeatItems() {
        return this.intensity >= SMOLDERING.intensity;
    }

    public boolean causesDamage() {
        return this.intensity >= HIGH.intensity;
    }

    public static HeatLevel byIntensity(int intensity) {
        return Arrays.stream(values())
                .filter(level -> level.intensity == intensity)
                .findFirst()
                .orElse(NONE);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public String getTranslationKey() {
        return "heatLevel.peony." + this.asString();
    }
}
