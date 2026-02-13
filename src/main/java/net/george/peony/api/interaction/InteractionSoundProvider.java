package net.george.peony.api.interaction;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public interface InteractionSoundProvider {
    /**
     * Sound played when insertion succeeds.
     */
    default SoundEvent getInsertSound() {
        return SoundEvents.ENTITY_ITEM_PICKUP;
    }

    /**
     * Sound played when extraction succeeds.
     */
    default SoundEvent getExtractSound() {
        return SoundEvents.ENTITY_ITEM_PICKUP;
    }

    /**
     * Volume multiplier.
     */
    default float getSoundVolume() {
        return 0.5F;
    }

    /**
     * Pitch multiplier.
     */
    default float getSoundPitch() {
        return 1.0F;
    }
}
