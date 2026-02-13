package net.george.peony.api.interaction;

@SuppressWarnings("ClassCanBeRecord")
public class InteractionResult {
    private final boolean success;
    private final Consumption consumption;

    public InteractionResult(boolean success, Consumption consumption) {
        this.success = success;
        this.consumption = consumption;
    }

    public static InteractionResult fail() {
        return new InteractionResult(false, Consumption.none());
    }

    public static InteractionResult success(Consumption consumption) {
        return new InteractionResult(true, consumption);
    }

    public boolean isSuccess() {
        return this.success;
    }

    public Consumption getConsumption() {
        return this.consumption;
    }
}
