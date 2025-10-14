package net.george.peony.sound;

import net.george.peony.Peony;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class PeonySoundEvents {
    public static final SoundEvent BLOCK_SHEAR_USING = registerSound("shear_using");

    public static SoundEvent registerSound(String name) {
        Identifier id = Peony.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {
        Peony.debug("Sound Events");
    }
}
