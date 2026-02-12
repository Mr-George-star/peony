package net.george.peony.util.math.animation;

import com.google.common.collect.Maps;
import net.george.peony.api.util.nbt.NbtSerializable;
import net.george.peony.api.util.nbt.NbtWriteable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class LerpedFloat implements NbtSerializable {
    public static final Map<ChaserType, Chaser> CHASER_TYPES = Maps.newHashMap();
    public static final Map<Chaser, ChaserType> CHASERS = Maps.newHashMap();
    public static final ChaserType IDLE = registerChaser("idle", Chaser.IDLE);
    public static final ChaserType EXP = registerChaser("exp", Chaser.EXP);
    public static final ChaserType LINEAR = registerChaser("linear", Chaser.LINEAR);

    protected Interpolator interpolator;
    protected float previousValue;
    protected float value;

    @Nullable
    protected Chaser chaseFunction;
    protected float chaseTarget;
    protected float chaseSpeed;
    protected boolean angularChase;

    protected boolean forcedSync;

    public LerpedFloat(Interpolator interpolator) {
        this.interpolator = interpolator;
        this.startWithValue(0);
        this.forcedSync = true;
    }

    public static LerpedFloat linear() {
        return new LerpedFloat((progress, current, target) -> (float) MathHelper.lerp(progress, current, target));
    }

    public static LerpedFloat angular() {
        LerpedFloat lerpedFloat = new LerpedFloat(AngleHelper::angleLerp);
        lerpedFloat.angularChase = true;
        return lerpedFloat;
    }

    @Nullable
    public static Chaser fromNbt(NbtCompound nbt) {
        NbtCompound nbtCompound = nbt.getCompound("Chaser");
        ChaserType type = createType(nbtCompound.getString("Type"));
        if (CHASER_TYPES.containsKey(type)) {
            return CHASER_TYPES.get(type);
        }
        return null;
    }

    public static ChaserType registerChaser(String name, Chaser chaseFunction) {
        ChaserType type = createType(name);
        CHASER_TYPES.put(type, chaseFunction);
        CHASERS.put(chaseFunction, type);
        return type;
    }

    private static ChaserType createType(String name) {
        return new ChaserType(name);
    }

    @SuppressWarnings("UnusedReturnValue")
    public LerpedFloat startWithValue(double value) {
        float floated = (float) value;
        this.previousValue = floated;
        this.chaseTarget = floated;
        this.value = floated;
        return this;
    }

    public LerpedFloat chase(double value, double speed, Chaser chaseFunction) {
        this.updateChaseTarget((float) value);
        this.chaseSpeed = (float) speed;
        this.chaseFunction = chaseFunction;
        return this;
    }

    public LerpedFloat chaseTimed(double value, int ticks) {
        double difference = value - this.value;
        return chase(value, Math.abs(difference / ticks), Chaser.LINEAR);
    }

    public LerpedFloat disableSmartAngleChasing() {
        this.angularChase = false;
        return this;
    }

    public void updateChaseTarget(float target) {
        if (this.angularChase) {
            target = this.value + AngleHelper.getShortestAngleDiff(this.value, target);
        }
        this.chaseTarget = target;
    }

    public boolean updateChaseSpeed(double speed) {
        float prevSpeed = this.chaseSpeed;
        this.chaseSpeed = (float) speed;
        return !MathHelper.approximatelyEquals(prevSpeed, speed);
    }

    public void tickChaser() {
        float oldValue = this.value;
        this.previousValue = this.value;
        if (this.chaseFunction == null) {
            return;
        }
        if (MathHelper.approximatelyEquals((double) this.value, this.chaseTarget)) {
            this.value = this.chaseTarget;
            return;
        }
        this.value = this.chaseFunction.chase(this.value, this.chaseSpeed, this.chaseTarget);
    }

    public void setValueNoUpdate(double value) {
        this.value = (float) value;
    }

    public void setValue(double value) {
        this.previousValue = this.value;
        this.value = (float) value;
    }

    public float getValue() {
        return this.getValue(1);
    }

    public float getValue(float tickDelta) {
        return this.interpolator.interpolate(tickDelta, this.previousValue, this.value);
    }

    public boolean settled() {
        return MathHelper.approximatelyEquals((double) this.previousValue, this.value) &&
                (this.chaseFunction == null || MathHelper.approximatelyEquals((double) this.value, this.chaseTarget));
    }

    public float getChaseTarget() {
        return this.chaseTarget;
    }

    public void forceNextSync() {
        this.forcedSync = true;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.putFloat("Speed", this.chaseSpeed);
        nbt.putFloat("Target", this.chaseTarget);
        nbt.putFloat("Value", this.value);
        if (this.chaseFunction != null && CHASERS.containsKey(this.chaseFunction)) {
            CHASERS.get(this.chaseFunction).writeNbt(nbt, registries);
        }
        if (this.forcedSync) {
            nbt.putBoolean("Force", true);
        }
        this.forcedSync = false;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        if (nbt.getBoolean("Force")) {
            this.startWithValue(nbt.contains("Value") ? nbt.getFloat("Value") : 0);
        }
        this.readChaser(nbt);
    }

    protected void readChaser(NbtCompound nbt) {
        this.chaseSpeed = nbt.contains("Speed") ? nbt.getFloat("Speed") : 0;
        this.chaseTarget = nbt.contains("Target") ? nbt.getFloat("Target") : 0;
        Chaser chaser = fromNbt(nbt);
        if (chaser != null) {
            this.chaseFunction = chaser;
        }
    }

    public static void initialize() {
    }

    @FunctionalInterface
    public interface Interpolator {
        float interpolate(double progress, double current, double target);
    }

    @FunctionalInterface
    public interface Chaser {
        Chaser IDLE = (current, speed, target) -> (float) current;
        Chaser EXP = exp(Double.MAX_VALUE);
        Chaser LINEAR = (current, speed, target) ->
                (float) (current + MathHelper.clamp(target - current, -speed, speed));

        static Chaser exp(double maxEffectiveSpeed) {
            return (current, speed, target) ->
                    (float) (current + MathHelper.clamp((target - current) * speed, -maxEffectiveSpeed, maxEffectiveSpeed));
        }

        float chase(double current, double speed, double target);
    }

    public static final class ChaserType implements NbtWriteable {
        private final String name;

        private ChaserType(String name) {
            this.name = name;
        }

        @Override
        public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString("Type", this.name);
            nbt.put("Chaser", nbtCompound);
        }

        @Override
        public boolean equals(Object another) {
            if (this == another) {
                return true;
            }
            if (another == null || getClass() != another.getClass()) {
                return false;
            }
            ChaserType that = (ChaserType) another;
            return Objects.equals(this.name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name);
        }
    }
}
