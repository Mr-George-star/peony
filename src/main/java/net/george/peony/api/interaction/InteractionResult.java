package net.george.peony.api.interaction;

import com.google.common.collect.Lists;
import net.george.peony.api.interaction.effect.InteractionEffect;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public sealed interface InteractionResult permits InteractionResult.Success, InteractionResult.Fail {
    static Success success(Consumption consumption) {
        return new Success(consumption);
    }

    static InteractionResult fail() {
        return Fail.INSTANCE;
    }

    boolean isSuccess();

    final class Success implements InteractionResult {
        private final Consumption consumption;
        private InteractionEffect effects = null;

        private Success(Consumption consumption) {
            this.consumption = consumption;
        }

        public Success effect(InteractionEffect effect) {
            this.effects = effect;
            return this;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        public Consumption getConsumption() {
            return this.consumption;
        }

        @Nullable
        public InteractionEffect getEffects() {
            return this.effects;
        }
    }

    final class Fail implements InteractionResult {
        private static final Fail INSTANCE = new Fail();

        private Fail() {}

        @Override
        public boolean isSuccess() {
            return false;
        }
    }
}
