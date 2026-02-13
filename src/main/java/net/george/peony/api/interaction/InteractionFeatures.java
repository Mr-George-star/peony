package net.george.peony.api.interaction;

public interface InteractionFeatures {
    /**
     * Whether shift bulk insertion is enabled
     */
    default boolean supportsBulkInsert() {
        return false;
    }

    /**
     * Whether the entire stack should be consumed on insert
     */
    default boolean consumeAllOnInsert() {
        return false;
    }
}
