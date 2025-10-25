package net.george.peony.sound;

import net.george.peony.Peony;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class PeonySoundEvents {
    public static final SoundEvent ITEM_PARING = registerSound("paring");

    public static final SoundEvent BLOCK_SHEAR_USING = registerSound("shear_using");

    public static final RegistryEntry.Reference<SoundEvent> MUSIC_DISC_SURPRISE = registerReference("music_disc_surprise");

    public static SoundEvent registerSound(String name) {
        Identifier id = Peony.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static RegistryEntry.Reference<SoundEvent> registerReference(String name) {
        return registerReference(Peony.id(name));
    }

    public static RegistryEntry.Reference<SoundEvent> registerReference(Identifier id) {
        return registerReference(id, id);
    }

    public static RegistryEntry.Reference<SoundEvent> registerReference(Identifier id, Identifier soundId) {
        return Registry.registerReference(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }

    public static void register() {
        Peony.debug("Sound Events");
    }
}
