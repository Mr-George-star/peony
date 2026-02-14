package net.george.peony.api.interaction;

public interface InteractionFeatures {
    /**
     * Override to customize sound.
     */
    default InteractionSound getInteractionSound() {
        return InteractionSound.DEFAULT;
    }
}
