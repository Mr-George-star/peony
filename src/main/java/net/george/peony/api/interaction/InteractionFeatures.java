package net.george.peony.api.interaction;

import net.george.peony.api.interaction.effect.InteractionSound;

public interface InteractionFeatures {
    /**
     * Override to customize sound.
     */
    default InteractionSound getInteractionSound() {
        return InteractionSound.DEFAULT;
    }
}
