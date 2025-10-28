package net.george.peony.api.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public class CountdownManager implements NbtSerializable {
    Map<String, CountdownComponent> components = new HashMap<>();

    private CountdownManager() {}

    public static CountdownManager create() {
        return new CountdownManager();
    }

    public CountdownComponent add(String componentName, int maxTicks) {
        return this.add(componentName, maxTicks, 1);
    }

    public CountdownComponent add(String componentName, int maxTicks, int interval) {
        return this.components.put(componentName, new CountdownComponent(maxTicks, interval) {
            @Override
            public String toString() {
                return componentName;
            }

            @Override
            public String getName() {
                return componentName;
            }
        });
    }

    public void tick() {
        for (CountdownComponent component : this.components.values()) {
            component.tick();
        }
    }

    @Nullable
    public CountdownComponent get(String componentName) {
        return this.components.get(componentName);
    }

    public void start(String componentName) {
        Optional.ofNullable(this.get(componentName)).ifPresent(CountdownComponent::start);
    }

    public boolean isOver(String componentName) {
        return Optional.ofNullable(this.get(componentName)).map(CountdownComponent::isOver).orElse(false);
    }

    public void reset(String componentName) {
        Optional.ofNullable(this.get(componentName)).ifPresent(CountdownComponent::reset);
    }

    public boolean isActive(String componentName) {
        CountdownComponent component = this.components.get(componentName);
        return component != null && component.started && component.currentTick > 0;
    }

    public int getRemainingTicks(String componentName) {
        CountdownComponent component = this.components.get(componentName);
        return component != null && component.started ? component.currentTick : 0;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        for (CountdownComponent component : this.components.values()) {
            component.writeNbt(nbt, registryLookup);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        for (CountdownComponent component : this.components.values()) {
            component.readNbt(nbt, registryLookup);
        }
    }

    public static class CountdownComponent implements NbtSerializable {
        int maxTicks;
        int interval;
        int currentTick;
        boolean started;

        CountdownComponent(int maxTicks, int interval) {
            this.maxTicks = maxTicks;
            this.interval = interval;
            this.currentTick = 0;
            this.started = false;
        }

        public void start() {
            this.currentTick = this.maxTicks;
            this.started = true;
        }

        public void tick() {
            if (this.started) {
                this.currentTick -= this.interval;
                if (this.currentTick < 0) {
                    this.started = false;
                }
            }
        }

        public boolean isOver() {
            return this.currentTick < 0;
        }

        public void reset() {
            this.currentTick = 0;
            this.started = false;
        }

        public int getMaxTicks() {
            return this.maxTicks;
        }

        public int getInterval() {
            return this.interval;
        }

        public int getCurrentTick() {
            return this.currentTick;
        }

        public boolean isStarted() {
            return this.started;
        }

        public String getName() {
            return this.toString();
        }

        @Override
        public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            nbt.putInt(this.getName() + "_MaxTicks", this.maxTicks);
            nbt.putInt(this.getName() + "_Interval", this.interval);
            nbt.putInt(this.getName() + "_CurrentTick", this.currentTick);
            nbt.putBoolean(this.getName() + "_Started", this.started);
        }

        @Override
        public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            this.maxTicks = nbt.getInt(this.getName() + "_MaxTicks");
            this.interval = nbt.getInt(this.getName() + "_Interval");
            this.currentTick = nbt.getInt(this.getName() + "_CurrentTick");
            this.started = nbt.getBoolean(this.getName() + "_Started");
        }
    }
}
