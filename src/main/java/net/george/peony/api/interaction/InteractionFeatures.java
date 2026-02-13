package net.george.peony.api.interaction;

public interface InteractionFeatures {
    /**
     * Whether shift bulk insertion is enabled
     */
    default boolean supportsBulkInsert() {
        return false;
    }
}
