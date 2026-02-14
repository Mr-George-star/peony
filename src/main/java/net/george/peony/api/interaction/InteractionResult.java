package net.george.peony.api.interaction;

import org.jetbrains.annotations.Nullable;

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
        @Nullable
        private InteractionSound sound = null;

        private Success(Consumption consumption) {
            this.consumption = consumption;
        }

        public Success sound(InteractionSound sound) {
            this.sound = sound;
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
        public InteractionSound getSound() {
            return this.sound;
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
